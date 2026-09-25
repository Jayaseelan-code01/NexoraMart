package com.jayaseelan.nexoramart.dao;
import com.jayaseelan.nexoramart.model.Product; import java.sql.SQLException; import java.util.List;
public interface ProductDAO {void create(Product p)throws SQLException; List<Product> findAll(String q,String category)throws SQLException; List<Product> findBySeller(long id)throws SQLException; Product findById(long id)throws SQLException; void update(Product p,long sellerId)throws SQLException; void delete(long id,long sellerId)throws SQLException;}
