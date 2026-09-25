package com.jayaseelan.nexoramart.controller;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import javax.sql.DataSource;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.*;
import java.util.*;

@WebServlet("/admin")
public class AdminServlet extends HttpServlet {
    private DataSource ds;

    @Override public void init() {
        ds=(DataSource)getServletContext().getAttribute("dataSource");
    }

    private boolean isAdmin(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession s=req.getSession(false);
        if(s==null || !"ADMIN".equals(s.getAttribute("userRole"))) {
            resp.sendRedirect(req.getContextPath()+"/login");
            return false;
        }
        return true;
    }

    @Override protected void doGet(HttpServletRequest req,HttpServletResponse resp) throws ServletException,IOException {
        if(!isAdmin(req,resp)) return;
        try(Connection c=ds.getConnection()) {
            req.setAttribute("users", loadUsers(c));
            req.setAttribute("products", loadProducts(c));
            req.setAttribute("orders", loadOrders(c));
            req.setAttribute("audits", loadAudits(c));
            req.getRequestDispatcher("/WEB-INF/views/admin.jsp").forward(req,resp);
        } catch(Exception e) {
            getServletContext().log("Admin dashboard failed",e);
            resp.sendError(500,"Unable to load admin dashboard.");
        }
    }

    @Override protected void doPost(HttpServletRequest req,HttpServletResponse resp) throws ServletException,IOException {
        if(!isAdmin(req,resp)) return;
        HttpSession s=req.getSession(false);
        long actor=((Number)s.getAttribute("userId")).longValue();
        String action=req.getParameter("action");
        try {
            boolean ok;
            switch(action==null?"":action) {
                case "toggle-user" -> ok=toggleUser(actor,Long.parseLong(req.getParameter("id")));
                case "toggle-product" -> ok=toggleProduct(actor,Long.parseLong(req.getParameter("id")));
                case "delete-product" -> ok=deactivateProduct(actor,Long.parseLong(req.getParameter("id")));
                case "order-status" -> ok=updateOrderStatus(actor,Long.parseLong(req.getParameter("id")),req.getParameter("status"));
                default -> { resp.sendError(400,"Invalid admin action."); return; }
            }
            resp.sendRedirect(req.getContextPath()+"/admin?updated="+ok);
        } catch(NumberFormatException e) {
            resp.sendError(400,"Invalid ID.");
        } catch(IllegalArgumentException e) {
            resp.sendError(400,e.getMessage());
        } catch(Exception e) {
            getServletContext().log("Admin action failed",e);
            resp.sendError(500,"Unable to perform admin action.");
        }
    }

    private List<Map<String,Object>> loadUsers(Connection c)throws SQLException {
        List<Map<String,Object>> out=new ArrayList<>();
        String q="SELECT id,name,email,role,active,created_at FROM users ORDER BY created_at DESC,id DESC";
        try(PreparedStatement p=c.prepareStatement(q);ResultSet r=p.executeQuery()){
            while(r.next()){Map<String,Object> m=new LinkedHashMap<>();
                m.put("id",r.getLong("id"));m.put("name",r.getString("name"));m.put("email",r.getString("email"));
                m.put("role",r.getString("role"));m.put("active",r.getBoolean("active"));m.put("createdAt",r.getTimestamp("created_at"));out.add(m);}
        } return out;
    }

    private List<Map<String,Object>> loadProducts(Connection c)throws SQLException {
        List<Map<String,Object>> out=new ArrayList<>();
        String q="SELECT p.id,p.name,p.brand,p.price,p.stock_qty,p.category,p.active,COALESCE(u.name,'Campus Seller') seller_name " +
                "FROM products p LEFT JOIN users u ON u.id=p.seller_id ORDER BY p.created_at DESC,p.id DESC";
        try(PreparedStatement p=c.prepareStatement(q);ResultSet r=p.executeQuery()){
            while(r.next()){Map<String,Object> m=new LinkedHashMap<>();
                m.put("id",r.getLong("id"));m.put("name",r.getString("name"));m.put("brand",r.getString("brand"));
                m.put("price",r.getBigDecimal("price"));m.put("stock",r.getInt("stock_qty"));m.put("category",r.getString("category"));
                m.put("active",r.getBoolean("active"));m.put("seller",r.getString("seller_name"));out.add(m);}
        } return out;
    }

    private List<Map<String,Object>> loadOrders(Connection c)throws SQLException {
        List<Map<String,Object>> out=new ArrayList<>();
        String q="SELECT o.id,o.order_ref,o.total_amount,o.status,o.created_at,u.name buyer_name,u.email buyer_email " +
                "FROM orders o JOIN users u ON u.id=o.buyer_id ORDER BY o.created_at DESC,o.id DESC";
        try(PreparedStatement p=c.prepareStatement(q);ResultSet r=p.executeQuery()){
            while(r.next()){Map<String,Object> m=new LinkedHashMap<>();
                m.put("id",r.getLong("id"));m.put("ref",r.getString("order_ref"));m.put("total",r.getBigDecimal("total_amount"));
                m.put("status",r.getString("status"));m.put("createdAt",r.getTimestamp("created_at"));m.put("buyer",r.getString("buyer_name"));m.put("email",r.getString("buyer_email"));out.add(m);}
        } return out;
    }

    private List<Map<String,Object>> loadAudits(Connection c)throws SQLException {
        List<Map<String,Object>> out=new ArrayList<>();
        String q="SELECT a.id,a.action,a.entity_type,a.entity_id,a.details,a.created_at,COALESCE(u.name,'System') actor " +
                "FROM audit_logs a LEFT JOIN users u ON u.id=a.actor_user_id ORDER BY a.created_at DESC,a.id DESC LIMIT 50";
        try(PreparedStatement p=c.prepareStatement(q);ResultSet r=p.executeQuery()){
            while(r.next()){Map<String,Object> m=new LinkedHashMap<>();
                m.put("id",r.getLong("id"));m.put("action",r.getString("action"));m.put("entityType",r.getString("entity_type"));
                m.put("entityId",r.getLong("entity_id"));m.put("details",r.getString("details"));m.put("createdAt",r.getTimestamp("created_at"));m.put("actor",r.getString("actor"));out.add(m);}
        } return out;
    }

    private boolean toggleUser(long actor,long id)throws Exception {
        if(actor==id) throw new IllegalArgumentException("You cannot deactivate your own admin account.");
        try(Connection c=ds.getConnection()){
            c.setAutoCommit(false);
            try { boolean active; String email;
                try(PreparedStatement p=c.prepareStatement("SELECT active,email FROM users WHERE id=?")){p.setLong(1,id);try(ResultSet r=p.executeQuery()){if(!r.next())return rollbackFalse(c);active=r.getBoolean(1);email=r.getString(2);}}
                try(PreparedStatement p=c.prepareStatement("UPDATE users SET active=? WHERE id=?")){p.setBoolean(1,!active);p.setLong(2,id);if(p.executeUpdate()!=1)return rollbackFalse(c);}
                audit(c,actor,"USER_STATUS", "USER", id,(active?"Deactivated ":"Activated ")+email); c.commit(); return true;
            } catch(Exception e){c.rollback();throw e;} finally {c.setAutoCommit(true);}
        }
    }

    private boolean toggleProduct(long actor,long id)throws Exception {
        try(Connection c=ds.getConnection()){
            c.setAutoCommit(false);
            try { boolean active; String name;
                try(PreparedStatement p=c.prepareStatement("SELECT active,name FROM products WHERE id=?")){p.setLong(1,id);try(ResultSet r=p.executeQuery()){if(!r.next())return rollbackFalse(c);active=r.getBoolean(1);name=r.getString(2);}}
                try(PreparedStatement p=c.prepareStatement("UPDATE products SET active=? WHERE id=?")){p.setBoolean(1,!active);p.setLong(2,id);if(p.executeUpdate()!=1)return rollbackFalse(c);}
                audit(c,actor,"PRODUCT_STATUS", "PRODUCT", id,(active?"Deactivated ":"Activated ")+name); c.commit(); return true;
            } catch(Exception e){c.rollback();throw e;} finally {c.setAutoCommit(true);}
        }
    }

    private boolean deactivateProduct(long actor,long id)throws Exception {
        try(Connection c=ds.getConnection()){
            c.setAutoCommit(false);
            try { String name;
                try(PreparedStatement p=c.prepareStatement("SELECT name FROM products WHERE id=?")){p.setLong(1,id);try(ResultSet r=p.executeQuery()){if(!r.next())return rollbackFalse(c);name=r.getString(1);}}
                try(PreparedStatement p=c.prepareStatement("UPDATE products SET active=FALSE WHERE id=?")){p.setLong(1,id);if(p.executeUpdate()!=1)return rollbackFalse(c);}
                audit(c,actor,"PRODUCT_MODERATE", "PRODUCT", id,"Soft-deleted/deactivated "+name); c.commit(); return true;
            } catch(Exception e){c.rollback();throw e;} finally {c.setAutoCommit(true);}
        }
    }

    private boolean updateOrderStatus(long actor,long id,String newStatus)throws Exception {
        if(newStatus==null || !(newStatus.equals("PENDING")||newStatus.equals("CONFIRMED")||newStatus.equals("SHIPPED")||newStatus.equals("DELIVERED")||newStatus.equals("CANCELLED"))) throw new IllegalArgumentException("Unsupported order status.");
        try(Connection c=ds.getConnection()){
            c.setAutoCommit(false);
            try { String old,ref;
                try(PreparedStatement p=c.prepareStatement("SELECT status,order_ref FROM orders WHERE id=? FOR UPDATE")){p.setLong(1,id);try(ResultSet r=p.executeQuery()){if(!r.next())return rollbackFalse(c);old=r.getString(1);ref=r.getString(2);}}
                if(old.equals(newStatus)){c.rollback();return false;}
                if("CANCELLED".equals(old) && !"CANCELLED".equals(newStatus)) throw new IllegalArgumentException("Cancelled orders cannot be reopened.");
                if("CANCELLED".equals(newStatus) && !"CANCELLED".equals(old)){
                    try(PreparedStatement p=c.prepareStatement("UPDATE products p SET stock_qty=p.stock_qty+(SELECT oi.quantity FROM order_items oi WHERE oi.order_id=? AND oi.product_id=p.id) WHERE p.id IN (SELECT product_id FROM order_items WHERE order_id=?)")){p.setLong(1,id);p.setLong(2,id);p.executeUpdate();}
                }
                try(PreparedStatement p=c.prepareStatement("UPDATE orders SET status=? WHERE id=?")){p.setString(1,newStatus);p.setLong(2,id);p.executeUpdate();}
                audit(c,actor,"ORDER_STATUS","ORDER",id,ref+": "+old+" -> "+newStatus); c.commit(); return true;
            } catch(Exception e){c.rollback();throw e;} finally {c.setAutoCommit(true);}
        }
    }

    private void audit(Connection c,long actor,String action,String entityType,long entityId,String details)throws SQLException {
        try(PreparedStatement p=c.prepareStatement("INSERT INTO audit_logs(actor_user_id,action,entity_type,entity_id,details) VALUES(?,?,?,?,?)")){
            p.setLong(1,actor);p.setString(2,action);p.setString(3,entityType);p.setLong(4,entityId);p.setString(5,details);p.executeUpdate();
        }
    }
    private boolean rollbackFalse(Connection c)throws SQLException {c.rollback();return false;}
}
