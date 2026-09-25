package com.jayaseelan.nexoramart.dao;

import com.jayaseelan.nexoramart.model.Order;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public interface OrderDAO {
    Order createOrder(long buyerId, Map<Long, Integer> cart) throws SQLException;
    Order findById(long orderId, long buyerId) throws SQLException;
    List<Order> findByBuyer(long buyerId) throws SQLException;
    List<Order> findBySeller(long sellerId) throws SQLException;
    boolean cancelPending(long orderId, long buyerId) throws SQLException;
    boolean updateSellerStatus(long orderId, long sellerId, String newStatus) throws SQLException;
}
