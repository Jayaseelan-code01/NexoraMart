package com.jayaseelan.nexoramart.dao;

import com.jayaseelan.nexoramart.model.Review;
import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JdbcReviewDAO implements ReviewDAO {
    private final DataSource dataSource;
    public JdbcReviewDAO(DataSource dataSource) { this.dataSource = dataSource; }

    private Review map(ResultSet r) throws SQLException {
        Review x = new Review();
        x.setId(r.getLong("id"));
        x.setBuyerId(r.getLong("buyer_id"));
        x.setBuyerName(r.getString("buyer_name"));
        x.setProductId(r.getLong("product_id"));
        x.setSellerId(r.getLong("seller_id"));
        x.setProductName(r.getString("product_name"));
        x.setRating(r.getInt("rating"));
        x.setComment(r.getString("comment"));
        x.setSellerReply(r.getString("seller_reply"));
        x.setCreatedAt(r.getTimestamp("created_at"));
        x.setRepliedAt(r.getTimestamp("replied_at"));
        return x;
    }

    private String baseSelect() {
        return "SELECT r.id,r.buyer_id,u.name AS buyer_name,r.product_id,p.seller_id,p.name AS product_name," +
               "r.rating,r.comment,r.seller_reply,r.created_at,r.replied_at " +
               "FROM reviews r JOIN users u ON u.id=r.buyer_id JOIN products p ON p.id=r.product_id";
    }

    @Override
    public List<Review> findByProduct(long productId) throws SQLException {
        List<Review> out = new ArrayList<>();
        String q = baseSelect() + " WHERE r.product_id=? ORDER BY r.created_at DESC,r.id DESC";
        try (Connection c = dataSource.getConnection(); PreparedStatement ps = c.prepareStatement(q)) {
            ps.setLong(1, productId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(map(rs));
            }
        }
        return out;
    }

    @Override
    public Review findByBuyerAndProduct(long buyerId, long productId) throws SQLException {
        String q = baseSelect() + " WHERE r.buyer_id=? AND r.product_id=?";
        try (Connection c = dataSource.getConnection(); PreparedStatement ps = c.prepareStatement(q)) {
            ps.setLong(1, buyerId);
            ps.setLong(2, productId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        }
    }

    @Override
    public boolean canReview(long buyerId, long productId) throws SQLException {
        String q = "SELECT EXISTS(SELECT 1 FROM order_items oi JOIN orders o ON o.id=oi.order_id " +
                   "WHERE o.buyer_id=? AND oi.product_id=? AND o.status<>'CANCELLED') " +
                   "AND NOT EXISTS(SELECT 1 FROM reviews r WHERE r.buyer_id=? AND r.product_id=?)";
        try (Connection c = dataSource.getConnection(); PreparedStatement ps = c.prepareStatement(q)) {
            ps.setLong(1, buyerId);
            ps.setLong(2, productId);
            ps.setLong(3, buyerId);
            ps.setLong(4, productId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getBoolean(1);
            }
        }
    }

    @Override
    public void create(long buyerId, long productId, int rating, String comment) throws SQLException {
        if (rating < 1 || rating > 5) throw new IllegalArgumentException("Rating must be between 1 and 5.");
        if (comment == null || comment.isBlank()) throw new IllegalArgumentException("Please write a short review.");
        if (comment.length() > 1000) throw new IllegalArgumentException("Review must be 1000 characters or less.");
        if (!canReview(buyerId, productId)) throw new IllegalArgumentException("You can review this product only once after purchasing it.");
        String q = "INSERT INTO reviews(buyer_id,product_id,rating,comment) VALUES(?,?,?,?)";
        try (Connection c = dataSource.getConnection(); PreparedStatement ps = c.prepareStatement(q)) {
            ps.setLong(1, buyerId);
            ps.setLong(2, productId);
            ps.setInt(3, rating);
            ps.setString(4, comment.trim());
            ps.executeUpdate();
        } catch (SQLIntegrityConstraintViolationException e) {
            throw new IllegalArgumentException("You have already reviewed this product.");
        }
    }

    @Override
    public boolean reply(long sellerId, long reviewId, String reply) throws SQLException {
        if (reply == null || reply.isBlank()) throw new IllegalArgumentException("Reply cannot be empty.");
        if (reply.length() > 1000) throw new IllegalArgumentException("Reply must be 1000 characters or less.");
        String q = "UPDATE reviews r SET seller_reply=?,replied_at=CURRENT_TIMESTAMP " +
                   "WHERE r.id=? AND EXISTS(SELECT 1 FROM products p WHERE p.id=r.product_id AND p.seller_id=?)";
        try (Connection c = dataSource.getConnection(); PreparedStatement ps = c.prepareStatement(q)) {
            ps.setString(1, reply.trim());
            ps.setLong(2, reviewId);
            ps.setLong(3, sellerId);
            return ps.executeUpdate() == 1;
        }
    }
}
