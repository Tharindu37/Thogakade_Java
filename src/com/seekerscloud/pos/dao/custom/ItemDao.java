package com.seekerscloud.pos.dao.custom;

import com.seekerscloud.pos.dao.CrudDao;
import com.seekerscloud.pos.entity.Item;

import java.sql.SQLException;
import java.util.ArrayList;

public interface ItemDao extends CrudDao<Item,String> {
    public ArrayList<Item> searchItem(String searchText) throws SQLException, ClassNotFoundException;
}
