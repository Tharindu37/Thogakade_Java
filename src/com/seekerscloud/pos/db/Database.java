package com.seekerscloud.pos.db;

import com.seekerscloud.pos.modal.Customer;
import com.seekerscloud.pos.modal.Item;
import com.seekerscloud.pos.modal.Order;

import java.util.ArrayList;

public class Database {
    public static ArrayList<Customer> customerTable=
            new ArrayList<Customer>();
    public static ArrayList<Item> itemTable=
            new ArrayList<Item>();
    public static ArrayList<Order> orderTable=
            new ArrayList<>();
    static {
        customerTable.add(new Customer("C001","Bandara","Colombo",25000));
        customerTable.add(new Customer("C002","Jayantha","Colombo",26000));
        customerTable.add(new Customer("C003","Kruwan","Panadura",89000));
        customerTable.add(new Customer("C004","Lakshan","Galle",80000));
        customerTable.add(new Customer("C005","Sahan","Kurunegala",67000));

        itemTable.add(new Item("I-001","Description 1",25,173));
        itemTable.add(new Item("I-002","Description 2",15,450));
        itemTable.add(new Item("I-003","Description 3",5,500));
        itemTable.add(new Item("I-004","Description 4",45,1300));
        itemTable.add(new Item("I-005","Description 5",4,60));
    }
}
