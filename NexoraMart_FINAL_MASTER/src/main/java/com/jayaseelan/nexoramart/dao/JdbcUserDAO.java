package com.jayaseelan.nexoramart.dao;

import com.jayaseelan.nexoramart.model.User;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class JdbcUserDAO implements UserDAO {
    private final DataSource ds;

    public JdbcUserDAO(DataSource ds) {
        this.ds = ds;
    }

    @Override
    public void create(User u) throws SQLException {
        String q = "INSERT INTO users(name,email,password_hash,role) VALUES(?,?,?,?)";
        try (Connection c = ds.getConnection();
             PreparedStatement p = c.prepareStatement(q)) {
            p.setString(1, u.getName());
            p.setString(2, u.getEmail());
            p.setString(3, u.getPasswordHash());
            p.setString(4, u.getRole());
            p.executeUpdate();
        }
    }

    @Override
    public User findByEmail(String email) throws SQLException {
        String q = "SELECT id,name,email,password_hash,role,active FROM users WHERE email=?";
        try (Connection c = ds.getConnection();
             PreparedStatement p = c.prepareStatement(q)) {
            p.setString(1, email);
            try (ResultSet r = p.executeQuery()) {
                if (r.next()) {
                    User u = new User(
                            r.getLong(1),
                            r.getString(2),
                            r.getString(3),
                            r.getString(4),
                            r.getString(5)
                    );
                    u.setActive(r.getBoolean(6));
                    return u;
                }
                return null;
            }
        }
    }
}
