package com.jayaseelan.nexoramart.model;

import java.sql.Timestamp;

public class Review {
    private long id;
    private long buyerId;
    private String buyerName;
    private long productId;
    private long sellerId;
    private String productName;
    private int rating;
    private String comment;
    private String sellerReply;
    private Timestamp createdAt;
    private Timestamp repliedAt;

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public long getBuyerId() { return buyerId; }
    public void setBuyerId(long buyerId) { this.buyerId = buyerId; }
    public String getBuyerName() { return buyerName; }
    public void setBuyerName(String buyerName) { this.buyerName = buyerName; }
    public long getProductId() { return productId; }
    public void setProductId(long productId) { this.productId = productId; }
    public long getSellerId() { return sellerId; }
    public void setSellerId(long sellerId) { this.sellerId = sellerId; }
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }
    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
    public String getSellerReply() { return sellerReply; }
    public void setSellerReply(String sellerReply) { this.sellerReply = sellerReply; }
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
    public Timestamp getRepliedAt() { return repliedAt; }
    public void setRepliedAt(Timestamp repliedAt) { this.repliedAt = repliedAt; }
}
