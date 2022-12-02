package com.seekerscloud.pos.entity;

import com.seekerscloud.pos.modal.ItemDetails;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "orders")
public class Order {
    @Id
    @Column(name = "order_code")
    private String orderId;//D-1;
    private Date date;
    @Column(name = "total_cost")
    private double totalCost;

//    =====================
    @ManyToOne
    @JoinColumn(name = "Customer_Id")
    private Customer customer;

    @OneToMany(mappedBy = "order",
            cascade = CascadeType.ALL
    )
    private List<OrderDetails> details=new ArrayList<>();
//    =====================
    public Order() {
    }

    public Order(String orderId, Date date, double totalCost) {
        this.orderId = orderId;
        this.date = date;
        this.totalCost = totalCost;
    }

    public List<OrderDetails> getDetails() {
        return details;
    }

    public void setDetails(List<OrderDetails> details) {
        this.details = details;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public double getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(double totalCost) {
        this.totalCost = totalCost;
    }
}
