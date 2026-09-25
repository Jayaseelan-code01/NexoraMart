package com.jayaseelan.nexoramart.controller;

import com.jayaseelan.nexoramart.dao.JdbcReviewDAO;
import com.jayaseelan.nexoramart.dao.ReviewDAO;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import javax.sql.DataSource;
import java.io.IOException;

@WebServlet("/reviews")
public class ReviewServlet extends HttpServlet {
    private ReviewDAO reviewDAO;

    @Override public void init() {
        reviewDAO = new JdbcReviewDAO((DataSource)getServletContext().getAttribute("dataSource"));
    }

    private Long userId(HttpServletRequest r) {
        HttpSession s = r.getSession(false);
        Object id = s == null ? null : s.getAttribute("userId");
        return id instanceof Number ? ((Number)id).longValue() : null;
    }

    @Override protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Long uid = userId(req);
        if (uid == null) { resp.sendRedirect(req.getContextPath()+"/login"); return; }
        String action = req.getParameter("action");
        try {
            if ("add".equals(action)) {
                if (!"BUYER".equals(req.getSession(false).getAttribute("userRole"))) {
                    resp.sendError(403,"Only buyers can post reviews."); return;
                }
                long productId = Long.parseLong(req.getParameter("productId"));
                int rating = Integer.parseInt(req.getParameter("rating"));
                reviewDAO.create(uid, productId, rating, req.getParameter("comment"));
                resp.sendRedirect(req.getContextPath()+"/product/details?id="+productId+"&review=success");
                return;
            }
            if ("reply".equals(action)) {
                if (!"SELLER".equals(req.getSession(false).getAttribute("userRole"))) {
                    resp.sendError(403,"Only sellers can reply to reviews."); return;
                }
                long reviewId = Long.parseLong(req.getParameter("reviewId"));
                long productId = Long.parseLong(req.getParameter("productId"));
                if (!reviewDAO.reply(uid, reviewId, req.getParameter("reply"))) {
                    resp.sendError(403,"You can only reply to reviews for your own products."); return;
                }
                resp.sendRedirect(req.getContextPath()+"/product/details?id="+productId+"&review=replied");
                return;
            }
            resp.sendError(400,"Invalid review action.");
        } catch (NumberFormatException e) {
            resp.sendError(400,"Invalid review data.");
        } catch (IllegalArgumentException e) {
            resp.sendError(400,e.getMessage());
        } catch (Exception e) {
            getServletContext().log("Review action failed",e);
            resp.sendError(500,"Unable to save the review right now.");
        }
    }
}
