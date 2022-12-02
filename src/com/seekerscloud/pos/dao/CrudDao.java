package com.seekerscloud.pos.dao;

import com.seekerscloud.pos.entity.SupperEntity;

import java.sql.SQLException;

public interface CrudDao<T extends SupperEntity,ID> {
    public boolean save(T t) throws SQLException, ClassNotFoundException;
    public boolean delete(ID id) throws SQLException, ClassNotFoundException;
    public boolean update(T t) throws SQLException, ClassNotFoundException;
}
