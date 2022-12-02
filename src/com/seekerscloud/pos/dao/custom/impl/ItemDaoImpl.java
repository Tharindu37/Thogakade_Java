package com.seekerscloud.pos.dao.custom.impl;

import com.seekerscloud.pos.dao.CrudUtil;
import com.seekerscloud.pos.dao.custom.ItemDao;
import com.seekerscloud.pos.db.DBConnection;
import com.seekerscloud.pos.entity.Item;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class ItemDaoImpl implements ItemDao {
    @Override
    public boolean save(Item i) throws SQLException, ClassNotFoundException {
        String sql = "INSERT INTO Item VALUES (?,?,?,?)";
        return CrudUtil.execute(sql,i.getCode(),i.getDescription(),i.getQtyOnHand(),i.getUnitPrice());
    }

    @Override
    public boolean delete(String s) throws SQLException, ClassNotFoundException {
        String sql1 = "DELETE FROM Item WHERE code=?";
        return CrudUtil.execute(sql1,s);
    }

    @Override
    public boolean update(Item i) throws SQLException, ClassNotFoundException {
        String sql = "UPDATE Item SET description=?,qtyOnHand=?,unitPrice=? WHERE code=?";
        return CrudUtil.execute(sql,i.getDescription(),i.getQtyOnHand(),i.getUnitPrice(),i.getCode());
    }

    @Override
    public ArrayList<Item> searchItem(String searchText) throws SQLException, ClassNotFoundException {
        String sql = "SELECT * FROM Item WHERE description LIKE ?";
        ResultSet set = CrudUtil.execute(sql,searchText);

        ArrayList<Item> list=new ArrayList<>();
        while (set.next()){
            list.add(
                    new Item(
                            set.getString(1),
                            set.getString(2),
                            set.getInt(3),
                            set.getDouble(4)
                    )
            );
        }
        return list;
    }
}
