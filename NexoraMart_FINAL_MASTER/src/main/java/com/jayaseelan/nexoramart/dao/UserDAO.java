package com.jayaseelan.nexoramart.dao; import com.jayaseelan.nexoramart.model.User; import java.sql.SQLException;
public interface UserDAO {void create(User u)throws SQLException; User findByEmail(String email)throws SQLException;}
