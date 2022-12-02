package com.seekerscloud.pos.dao.custom;

import com.seekerscloud.pos.dao.CrudDao;
import com.seekerscloud.pos.entity.Customer;

import java.sql.SQLException;
import java.util.ArrayList;

public interface CustomerDao extends CrudDao<Customer,String> {
    public ArrayList<Customer> searchCustomer(String searchText) throws SQLException, ClassNotFoundException;
}
