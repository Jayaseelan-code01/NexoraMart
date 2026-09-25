package com.jayaseelan.nexoramart.service;

import com.jayaseelan.nexoramart.dao.NotificationDAO;
import com.jayaseelan.nexoramart.model.Notification;
import java.util.List;

public class NotificationService {
    private final NotificationDAO dao;
    public NotificationService(NotificationDAO dao) { this.dao = dao; }
    public List<Notification> forRole(long userId, String role) throws Exception {
        if ("BUYER".equals(role)) return dao.forBuyer(userId);
        if ("SELLER".equals(role)) return dao.forSeller(userId);
        if ("ADMIN".equals(role)) return dao.forAdmin();
        return java.util.Collections.emptyList();
    }
}
