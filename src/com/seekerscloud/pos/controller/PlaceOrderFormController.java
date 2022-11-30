package com.seekerscloud.pos.controller;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import com.seekerscloud.pos.db.DBConnection;
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
import java.sql.*;
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
        try{
            String sql="SELECT orderId FROM `Order` ORDER BY orderId DESC LIMIT 1";
            PreparedStatement statement = DBConnection.getInstance().getConnection().prepareStatement(sql);
            ResultSet set = statement.executeQuery();
            if (!set.next()){
                txtOrderId.setText("D-1");
                return;
            }else {
                String tempOrderId=set.getString(1);
                String[] array = tempOrderId.split("-");//[D,3]
                int tempNumber = Integer.parseInt(array[1]);
                int finalizeOrderId = tempNumber + 1;
                txtOrderId.setText("D-" + finalizeOrderId);
            }
        }catch (ClassNotFoundException | SQLException e){
            e.printStackTrace();
        }
    }

    private void setItemDetails() {
        try {
            String sql="SELECT * FROM Item WHERE code=?";
            PreparedStatement statement = DBConnection.getInstance().getConnection().prepareStatement(sql);
            statement.setString(1,cmbItemCode.getValue());
            ResultSet set = statement.executeQuery();
            if (set.next()){
                    txtDescription.setText(set.getString(2));
                    txtQtyOnHand.setText(set.getString(3));
                    txtUnitPrice.setText(set.getString(4));
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void setCustomerDetails() {
        try {
            String sql="SELECT * FROM Customer WHERE id=?";
            PreparedStatement statement = DBConnection.getInstance().getConnection().prepareStatement(sql);
            statement.setString(1,cmbCustomerId.getValue());
            ResultSet set = statement.executeQuery();
            if (set.next()){
                txtName.setText(set.getString(2));
                txtAddress.setText(set.getString(3));
                txtSalary.setText(set.getString(4));
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void loadAllItemCodes() {
        try {
            String sql="SELECT code FROM Item";
            PreparedStatement statement = DBConnection.getInstance().getConnection().prepareStatement(sql);
            ResultSet set = statement.executeQuery();
            while (set.next()){
                cmbItemCode.getItems().add(set.getString(1));
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadAllCustomerIds() {
        try {
            String sql="SELECT id FROM Customer";
            PreparedStatement statement = DBConnection.getInstance().getConnection().prepareStatement(sql);
            ResultSet set = statement.executeQuery();
            ArrayList<String> idList=new ArrayList<>();
            while (set.next()){
                idList.add(set.getString(1));
            }
            ObservableList<String> obList=FXCollections.observableArrayList(idList);
            cmbCustomerId.setItems(obList);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
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
        try {
            String sql="SELECT qtyOnHand FROM Item WHERE code=?";
            PreparedStatement statement = DBConnection.getInstance().getConnection().prepareStatement(sql);
            statement.setString(1,code);
            ResultSet set = statement.executeQuery();
            if (set.next()){
                if (set.getInt(1)>=qty){
                    return true;
                }
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
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

    public void placeOrderOnAction(ActionEvent actionEvent) throws SQLException {
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

        //place order
        Connection con=null;
        try {

            con=DBConnection.getInstance().getConnection();
            con.setAutoCommit(false);

            String sql="INSERT INTO `Order` VALUES (?,?,?,?)";
            PreparedStatement statement = con.prepareStatement(sql);
            statement.setString(1,order.getOrderId());
            statement.setString(2,String.valueOf(order.getDate()));
            statement.setDouble(3,order.getTotalCost());
            statement.setString(4,order.getCustomer());
            if (statement.executeUpdate()>0){
                //update qty
                boolean isAllUpdated = manageQty(details);
                if (isAllUpdated){
                    con.commit();
                    new Alert(Alert.AlertType.CONFIRMATION,"Order Placed!").show();
                }else {
                    con.setAutoCommit(true);
                    con.rollback();
                    new Alert(Alert.AlertType.WARNING,"Try Again!").show();
                }
            }else {
                con.setAutoCommit(true);
                con.rollback();
                new Alert(Alert.AlertType.WARNING,"Try again!").show();
            }
        }catch (ClassNotFoundException | SQLException e){
            e.printStackTrace();
        }finally {
            con.setAutoCommit(true);
        }
        clearAll();
    }

    private boolean manageQty(ArrayList<ItemDetails> details){
        try{
            for (ItemDetails d:details
            ) {
                String sql="INSERT INTO `Order Details` VALUES (?,?,?,?)";
                PreparedStatement statement = DBConnection.getInstance().getConnection().prepareStatement(sql);
                statement.setString(1,d.getCode());
                statement.setString(2,txtOrderId.getText());
                statement.setDouble(3,d.getUnitPrice());
                statement.setInt(4,d.getQty());

                boolean isOrderDetailsSaved=statement.executeUpdate()>0;
                if (isOrderDetailsSaved){
                    boolean isQtyUpdated = update(d);
                    if (!isQtyUpdated){
                        return false;
                    }
                }
            }
        }catch (SQLException | ClassNotFoundException e){
            e.printStackTrace();
        }
        return true;
    }

    private boolean update(ItemDetails d) {
        try {
            String sql="UPDATE Item SET qtyOnHand=(qtyOnHand-?) WHERE code=?";
            PreparedStatement statement = DBConnection.getInstance().getConnection().prepareStatement(sql);
            statement.setInt(1,d.getQty());
            statement.setString(2,d.getCode());
            return statement.executeUpdate()>0;
        }catch (ClassNotFoundException | SQLException e){
            e.printStackTrace();
        }
        return false;
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
