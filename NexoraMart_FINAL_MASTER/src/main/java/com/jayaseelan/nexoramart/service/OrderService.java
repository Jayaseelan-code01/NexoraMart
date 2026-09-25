package com.jayaseelan.nexoramart.service;

import com.jayaseelan.nexoramart.dao.OrderDAO;
import com.jayaseelan.nexoramart.model.Order;

import java.util.List;
import java.util.Map;

public class OrderService {
    private final OrderDAO orderDAO;
    public OrderService(OrderDAO orderDAO) { this.orderDAO = orderDAO; }
    public Order checkout(long buyerId, Map<Long, Integer> cart) throws Exception {
        if (buyerId <= 0) throw new IllegalArgumentException("Invalid buyer session.");
        return orderDAO.createOrder(buyerId, cart);
    }
    public Order byId(long orderId, long buyerId) throws Exception { return orderDAO.findById(orderId, buyerId); }
    public List<Order> buyerHistory(long buyerId) throws Exception { return orderDAO.findByBuyer(buyerId); }
    public List<Order> sellerOrders(long sellerId) throws Exception { return orderDAO.findBySeller(sellerId); }
    public boolean cancelPending(long orderId, long buyerId) throws Exception { return orderDAO.cancelPending(orderId, buyerId); }
    public boolean updateSellerStatus(long orderId, long sellerId, String status) throws Exception { return orderDAO.updateSellerStatus(orderId, sellerId, status); }
}
