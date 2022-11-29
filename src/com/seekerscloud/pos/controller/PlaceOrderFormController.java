package com.seekerscloud.pos.controller;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import com.seekerscloud.pos.db.Database;
import com.seekerscloud.pos.modal.Customer;
import com.seekerscloud.pos.modal.Item;
import com.seekerscloud.pos.modal.ItemDetails;
import com.seekerscloud.pos.modal.Order;
import com.seekerscloud.pos.view.tm.CartTM;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.Window;

import javax.xml.crypto.Data;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Optional;

public class PlaceOrderFormController {
    public TextField txtDate;
    public AnchorPane placeOrderFormContext;
    public JFXComboBox<String> cmbCustomerId;
    public JFXComboBox<String> cmbItemCode;
    public JFXTextField txtName;
    public JFXTextField txtAddress;
    public JFXTextField txtSalary;
    public JFXTextField txtQty;
    public JFXTextField txtDescription;
    public JFXTextField txtQtyOnHand;
    public JFXTextField txtUnitPrice;
    public TableView<CartTM> tblCart;
    public TableColumn colCode;
    public TableColumn colDescription;
    public TableColumn colUnitPrice;
    public TableColumn colQty;
    public TableColumn colTotal;
    public TableColumn colOption;
    public Label lblTotal;
    public TextField txtOrderId;

    public void initialize(){
        colCode.setCellValueFactory(new PropertyValueFactory<>("code"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        colUnitPrice.setCellValueFactory(new PropertyValueFactory<>("unitPrice"));
        colQty.setCellValueFactory(new PropertyValueFactory<>("qty"));
        colTotal.setCellValueFactory(new PropertyValueFactory<>("total"));
        colOption.setCellValueFactory(new PropertyValueFactory<>("btn"));
//        Date date=new Date();
//        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyy-MM-dd");
//        String format = simpleDateFormat.format(date);
//        txtDate.setText(format);
        setDateAndOrderId();
        loadAllCustomerIds();
        loadAllItemCodes();
        setOrderId();
        
        cmbCustomerId.getSelectionModel().selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> {
                    if (null!=newValue)
                    setCustomerDetails();
                });
        cmbItemCode.getSelectionModel().selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> {
                    if (null!=newValue)
                        setItemDetails();
                });
    }

    private void setOrderId() {
        if (Database.orderTable.isEmpty()){
            txtOrderId.setText("D-1");
            return;
        }
        String tempOrderId=Database.orderTable.get(Database.orderTable.size()-1).getOrderId();
        String[] array = tempOrderId.split("-");//[D,3]
        int tempNumber=Integer.parseInt(array[1]);
        int finalizeOrderId=tempNumber+1;
        txtOrderId.setText("D-"+finalizeOrderId);
    }

    private void setItemDetails() {
        for (Item item:Database.itemTable
             ) {
            if (item.getCode().equalsIgnoreCase(cmbItemCode.getValue())){
                txtDescription.setText(item.getDescription());
                txtQtyOnHand.setText(String.valueOf(item.getQtyOnHand()));
                txtUnitPrice.setText(String.valueOf(item.getUnitPrice()));
            }
        }
    }

    private void setCustomerDetails() {
        for (Customer customer:Database.customerTable
             ) {
            if (customer.getId().equalsIgnoreCase(cmbCustomerId.getValue())){
                txtName.setText(customer.getName());
                txtAddress.setText(customer.getAddress());
                txtSalary.setText(String.valueOf(customer.getSalary()));
            }
        }
    }

    private void loadAllItemCodes() {
        for (Item item:Database.itemTable
             ) {
            cmbItemCode.getItems().add(item.getCode());
        }
    }

    private void loadAllCustomerIds() {
        for (Customer customer:Database.customerTable
             ) {
            cmbCustomerId.getItems().add(customer.getId());
        }

    }

    private void setDateAndOrderId() {
        txtDate.setText(new SimpleDateFormat("yyy-MM-dd").format(new Date()));
    }

    public void backToHomeOnAction(ActionEvent actionEvent) throws IOException {
        Stage stage =(Stage) placeOrderFormContext.getScene().getWindow();
        stage.setScene(new Scene(FXMLLoader.load(
                getClass().getResource("../view/DashboardForm.fxml")
        )));
    }

    private boolean checkQty(String code,int qty){
        for (Item item:Database.itemTable
             ) {
            if (code.equalsIgnoreCase(item.getCode())){
                if (item.getQtyOnHand()>=qty){
                    return true;
                }
            }
        }
        return false;
    }

    ObservableList<CartTM> obList=FXCollections.observableArrayList();
    public void addToCartOnAction(ActionEvent actionEvent) {
        if (!checkQty(cmbItemCode.getValue(),Integer.parseInt(txtQty.getText()))){
            new Alert(Alert.AlertType.WARNING,"Invalid Qty").show();
            return;
        }
        double unitPrice = Double.parseDouble(txtUnitPrice.getText());
        int qty = Integer.parseInt(txtQty.getText());
        double total = unitPrice * qty;
        Button btn = new Button("Delete");

        int row = isAlreadyExists(cmbItemCode.getValue());
        if (row == -1) {
            CartTM tm = new CartTM(cmbItemCode.getValue(),
                    txtDescription.getText(),
                    unitPrice,
                    qty,
                    total,
                    btn
            );
            obList.add(tm);
            tblCart.setItems(obList);
            btn.setOnAction(event -> {
                Alert alert=new Alert(Alert.AlertType.CONFIRMATION,
                        "Are you sure?",ButtonType.YES,ButtonType.NO);
                Optional<ButtonType> buttonType = alert.showAndWait();
                if (buttonType.get().equals(ButtonType.YES)){
                    for (CartTM cartTM:obList
                    ) {
                        if (cartTM.getCode().equalsIgnoreCase(tm.getCode())){
                            obList.remove(cartTM);
                            calculateTotal();
                            tblCart.refresh();
                            return;
                        }
                    }
                }
            });
        } else {
            int tempQty=obList.get(row).getQty()+qty;
            double tempTotal=unitPrice*tempQty;
            if (!checkQty(cmbItemCode.getValue(),tempQty)){
                new Alert(Alert.AlertType.WARNING,"Invalid Qty").show();
                return;
            }
            obList.get(row).setQty(tempQty);
            obList.get(row).setTotal(tempTotal);
            tblCart.refresh();
        }
        calculateTotal();
        clearFields();
        cmbItemCode.requestFocus();
//        btn.setOnAction(event -> {
//            Alert alert=new Alert(Alert.AlertType.CONFIRMATION,
//                    "Are you sure?",ButtonType.YES,ButtonType.NO);
//            Optional<ButtonType> buttonType = alert.showAndWait();
//            if (buttonType.get().equals(ButtonType.YES)){
//                for (CartTM cartTM:obList
//                ) {
//                    if (cartTM.getCode().equalsIgnoreCase(cartTM.getCode())){
//                        obList.remove(cartTM);
//                        calculateTotal();
//                        tblCart.refresh();
//                        return;
//                    }
//                }
//            }
//        });

    }

    private void clearFields() {
        txtDescription.clear();
        txtUnitPrice.clear();
        txtQtyOnHand.clear();
        txtQty.clear();
        cmbItemCode.setValue(null);
    }

    private int isAlreadyExists(String code) {
        for (int i = 0; i < obList.size(); i++) {
            if (obList.get(i).getCode().equalsIgnoreCase(code)){
                return i;
            }
        }
        return -1;
    }
    
    private void calculateTotal(){
        double total=0;
        for (CartTM tm:obList
             ) {
            total+=tm.getTotal();
        }
        lblTotal.setText(String.valueOf(total));
    }

    public void placeOrderOnAction(ActionEvent actionEvent) {
        if (obList.isEmpty()) return;
        ArrayList<ItemDetails> details=new ArrayList<>();
        for (CartTM tm:obList
             ) {
            details.add(new ItemDetails(
                    tm.getCode(),
                    tm.getUnitPrice(),
                    tm.getQty()
            ));
        }
        Order order=new Order(
                txtOrderId.getText(),
                new Date(),
                Double.parseDouble(lblTotal.getText()),
                cmbCustomerId.getValue(),
                details
        );
        Database.orderTable.add(order);
        manageQty();
        clearAll();
    }

    private void manageQty(){
        for (CartTM tm:obList
             ) {
            for (Item item:Database.itemTable
                 ) {
                if (item.getCode().equalsIgnoreCase(tm.getCode())){
                    item.setQtyOnHand(item.getQtyOnHand()-tm.getQty());
                    break;
                }
            }
        }
    }

    private void clearAll() {
        obList.clear();
        calculateTotal();

        txtName.clear();
        txtAddress.clear();
        txtSalary.clear();


        clearFields();
        cmbCustomerId.requestFocus();
        setOrderId();
    }
}
