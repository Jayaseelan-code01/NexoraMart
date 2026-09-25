package com.jayaseelan.nexoramart.controller;

import com.jayaseelan.nexoramart.dao.JdbcProductDAO;
import com.jayaseelan.nexoramart.dao.JdbcReviewDAO;
import com.jayaseelan.nexoramart.dao.ReviewDAO;
import com.jayaseelan.nexoramart.service.ProductService;
import com.jayaseelan.nexoramart.model.Product;
import com.jayaseelan.nexoramart.model.Review;
import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import javax.sql.DataSource;
import java.io.IOException;
import java.util.List;

@WebServlet("/product/details")
public class ProductDetailsServlet extends HttpServlet {
    private ProductService productService;
    private ReviewDAO reviewDAO;

    @Override public void init() {
        DataSource ds = (DataSource)getServletContext().getAttribute("dataSource");
        productService = new ProductService(new JdbcProductDAO(ds));
        reviewDAO = new JdbcReviewDAO(ds);
    }

    @Override protected void doGet(HttpServletRequest r,HttpServletResponse p)throws ServletException,IOException {
        try {
            long id=Long.parseLong(r.getParameter("id"));
            Product x=productService.byId(id);
            if(x==null){p.sendError(404);return;}
            List<Review> reviews=reviewDAO.findByProduct(id);
            double average=reviews.stream().mapToInt(Review::getRating).average().orElse(0.0);
            List<Product> similar=productService.browse(null,x.getCategory());
            similar.removeIf(item -> item.getId()==x.getId());
            if(similar.size()>8) similar=similar.subList(0,8);
            r.setAttribute("product",x);
            r.setAttribute("reviews",reviews);
            r.setAttribute("reviewAverage",average);
            r.setAttribute("similarProducts",similar);
            HttpSession s=r.getSession(false);
            if(s!=null && s.getAttribute("userId") instanceof Number){
                long uid=((Number)s.getAttribute("userId")).longValue();
                r.setAttribute("myReview",reviewDAO.findByBuyerAndProduct(uid,id));
                if("BUYER".equals(s.getAttribute("userRole"))) r.setAttribute("canReview",reviewDAO.canReview(uid,id));
                r.setAttribute("canReply", "SELLER".equals(s.getAttribute("userRole")) && uid==x.getSellerId());
            }
            r.getRequestDispatcher("/WEB-INF/views/product/details.jsp").forward(r,p);
        }catch(Exception e){getServletContext().log("Product details failed",e);p.sendError(400,"Invalid product.");}
    }
}
