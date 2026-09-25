package com.jayaseelan.nexoramart.model;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class Order {
    private long id;
    private String orderRef;
    private long buyerId;
    private String buyerName;
    private BigDecimal totalAmount;
    private String status;
    private Timestamp createdAt;
    private final List<OrderItem> items = new ArrayList<>();

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public String getOrderRef() { return orderRef; }
    public void setOrderRef(String orderRef) { this.orderRef = orderRef; }
    public long getBuyerId() { return buyerId; }
    public void setBuyerId(long buyerId) { this.buyerId = buyerId; }
    public String getBuyerName() { return buyerName; }
    public void setBuyerName(String buyerName) { this.buyerName = buyerName; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
    public List<OrderItem> getItems() { return items; }
}
