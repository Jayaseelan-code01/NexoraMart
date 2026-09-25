package com.jayaseelan.nexoramart.dao;

import com.jayaseelan.nexoramart.model.Review;
import java.sql.SQLException;
import java.util.List;

public interface ReviewDAO {
    List<Review> findByProduct(long productId) throws SQLException;
    Review findByBuyerAndProduct(long buyerId, long productId) throws SQLException;
    boolean canReview(long buyerId, long productId) throws SQLException;
    void create(long buyerId, long productId, int rating, String comment) throws SQLException;
    boolean reply(long sellerId, long reviewId, String reply) throws SQLException;
}
