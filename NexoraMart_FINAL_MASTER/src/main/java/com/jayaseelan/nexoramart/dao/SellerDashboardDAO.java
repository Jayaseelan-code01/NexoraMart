package com.jayaseelan.nexoramart.dao;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public interface SellerDashboardDAO {
    Map<String,Object> loadStats(long sellerId) throws SQLException;
    List<Map<String,Object>> loadTopProducts(long sellerId) throws SQLException;
    List<Map<String,Object>> loadRecentOrders(long sellerId) throws SQLException;
    List<Map<String,Object>> loadCategoryBreakdown(long sellerId) throws SQLException;
}
