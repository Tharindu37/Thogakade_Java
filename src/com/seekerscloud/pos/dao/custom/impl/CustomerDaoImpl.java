package com.seekerscloud.pos.dao.custom.impl;

import com.seekerscloud.pos.dao.CrudUtil;
import com.seekerscloud.pos.dao.custom.CustomerDao;
import com.seekerscloud.pos.db.DBConnection;
import com.seekerscloud.pos.entity.Customer;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class CustomerDaoImpl implements CustomerDao {
    @Override
    public boolean save(Customer c) throws SQLException, ClassNotFoundException {
        String sql = "INSERT INTO Customer VALUES (?, ?, ?, ?)";
        return CrudUtil.execute(sql,c.getId(),c.getName(),c.getAddress(),c.getSalary());
    }

    @Override
    public boolean delete(String s) throws SQLException, ClassNotFoundException {
        String sql1 = "DELETE FROM Customer WHERE id=?";
        return CrudUtil.execute(sql1,s);
    }

    @Override
    public boolean update(Customer c) throws SQLException, ClassNotFoundException {
        String sql1 = "UPDATE Customer SET name=?,address=?,salary=? WHERE id=?";
        return CrudUtil.execute(sql1,c.getName(),c.getAddress(),c.getSalary(),c.getId());
    }

    @Override
    public ArrayList<Customer> searchCustomer(String searchText) throws SQLException, ClassNotFoundException {
        String sql = "SELECT * FROM Customer WHERE name LIKE ? || address LIKE ?";
        ResultSet set = CrudUtil.execute(sql,searchText,searchText);

        ArrayList<Customer> list=new ArrayList<>();
        while (set.next()){
            list.add(new Customer(
                    set.getString(1),
                    set.getString(2),
                    set.getString(3),
                    set.getDouble(4)
            ));
        }
        return list;
    }
}
