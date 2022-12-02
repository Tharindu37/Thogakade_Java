package com.seekerscloud.pos.entity;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "item")
public class Item implements SupperEntity{
    @Id
    @Column(name = "item_code")
    private String code;
    @Column(name = "description",
    nullable = false)
    private String description;
    @Column(name = "qtyOnHand",
    nullable = false)
    private int qtyOnHand;
    @Column(name = "unitPrice",
    nullable = false)
    private double unitPrice;

//    ========================
    @OneToMany(mappedBy = "item",
    cascade = CascadeType.ALL)
    private List<OrderDetails> details=new ArrayList<>();
//    ========================

    public Item() {
    }

    public Item(String code, String description, int qtyOnHand, double unitPrice) {
        this.code = code;
        this.description = description;
        this.qtyOnHand = qtyOnHand;
        this.unitPrice = unitPrice;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getQtyOnHand() {
        return qtyOnHand;
    }

    public void setQtyOnHand(int qtyOnHand) {
        this.qtyOnHand = qtyOnHand;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }
}
