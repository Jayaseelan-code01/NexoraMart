package com.jayaseelan.nexoramart.service;

import com.jayaseelan.nexoramart.dao.SellerDashboardDAO;
import java.util.List;
import java.util.Map;

public class SellerDashboardService {
    private final SellerDashboardDAO dao;
    public SellerDashboardService(SellerDashboardDAO dao) { this.dao = dao; }
    public Map<String,Object> stats(long sellerId) throws Exception { return dao.loadStats(sellerId); }
    public List<Map<String,Object>> topProducts(long sellerId) throws Exception { return dao.loadTopProducts(sellerId); }
    public List<Map<String,Object>> recentOrders(long sellerId) throws Exception { return dao.loadRecentOrders(sellerId); }
    public List<Map<String,Object>> categoryBreakdown(long sellerId) throws Exception { return dao.loadCategoryBreakdown(sellerId); }
}
