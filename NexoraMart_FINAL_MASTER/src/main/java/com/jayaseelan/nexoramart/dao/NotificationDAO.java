package com.jayaseelan.nexoramart.dao;

import com.jayaseelan.nexoramart.model.Notification;
import java.sql.SQLException;
import java.util.List;

public interface NotificationDAO {
    List<Notification> forBuyer(long buyerId) throws SQLException;
    List<Notification> forSeller(long sellerId) throws SQLException;
    List<Notification> forAdmin() throws SQLException;
}
