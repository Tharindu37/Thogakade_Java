package com.seekerscloud.pos.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import com.seekerscloud.pos.db.DBConnection;
import com.seekerscloud.pos.db.Database;
import com.seekerscloud.pos.modal.Customer;
import com.seekerscloud.pos.view.tm.CustomerTM;
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

import javax.swing.*;
import java.io.IOException;
import java.sql.*;
import java.util.Optional;

public class CustomerFormController {
    public JFXTextField txtId;
    public JFXTextField txtName;
    public JFXTextField txtAddress;
    public JFXTextField txtSalary;
    public TableView<CustomerTM> tblCustomer;
    public TableColumn colId;
    public TableColumn colName;
    public TableColumn colAddress;
    public TableColumn colSalary;
    public TableColumn colOption;
    public JFXButton btnSaveCustomer;
    public AnchorPane customerFormContext;
    public TextField txtSearch;

    private String searchText = "";

    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colAddress.setCellValueFactory(new PropertyValueFactory<>("address"));
        colSalary.setCellValueFactory(new PropertyValueFactory<>("salary"));
        colOption.setCellValueFactory(new PropertyValueFactory<>("btn"));
        searchCustomer(searchText);

        tblCustomer.getSelectionModel().
                selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> {
                    if (null != newValue)
                        setData(newValue);
                });
        txtSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            searchText = newValue;
            searchCustomer(searchText);
        });
    }

    private void setData(CustomerTM tm) {
        txtId.setText(tm.getId());
        txtName.setText(tm.getName());
        txtAddress.setText(tm.getAddress());
        txtSalary.setText(String.valueOf(tm.getSalary()));
        btnSaveCustomer.setText("Update Customer");
    }

    private void searchCustomer(String text) {
        String searchText="%"+text+"%";
        try {
            ObservableList<CustomerTM> tmList = FXCollections.observableArrayList();

            Connection connection = DBConnection.getInstance().getConnection();
            String sql = "SELECT * FROM Customer WHERE name LIKE ? || address LIKE ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1,searchText);
            statement.setString(2,searchText);
            ResultSet set = statement.executeQuery();

            while (set.next()) {
                Button btn = new Button("Delete");
                CustomerTM tm = new CustomerTM(
                        set.getString(1),
                        set.getString(2),
                        set.getString(3),
                        set.getDouble(4),
                        btn
                );
                tmList.add(tm);
                    btn.setOnAction(event -> {
                        Alert alert=new Alert(Alert.AlertType.CONFIRMATION,
                                "Are you sure whether do you want to delete this customer?",
                                ButtonType.YES,ButtonType.NO);
                        Optional<ButtonType> buttonType = alert.showAndWait();
                        if (buttonType.get().equals(ButtonType.YES)){

                            try {
                                String sql1 = "DELETE FROM Customer WHERE id=?";
                                PreparedStatement statement1 = DBConnection.getInstance().getConnection().prepareStatement(sql1);
                                statement1.setString(1,tm.getId());
                                if (statement1.executeUpdate()>0){
                                    new Alert(Alert.AlertType.INFORMATION,"Deleted!").show();
                                    searchCustomer(searchText);
                                }else {
                                    new Alert(Alert.AlertType.WARNING,"Try Again!").show();
                                }
                            }catch (ClassNotFoundException | SQLException e){
                                e.printStackTrace();
                            }
                        }
                    });
            }
            tblCustomer.setItems(tmList);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    public void saveCustomerOnAction(ActionEvent actionEvent) {
        Customer c1 = new Customer(txtId.getText(),
                txtName.getText(),
                txtAddress.getText(),
                Double.parseDouble(txtSalary.getText()));

        if (btnSaveCustomer.getText().equalsIgnoreCase(
                "Save Customer")) {
            try {
                //database
                String sql = "INSERT INTO Customer VALUES (?, ?, ?, ?)";
                PreparedStatement statement = DBConnection.getInstance().getConnection().prepareStatement(sql);
                statement.setString(1, c1.getId());
                statement.setString(2, c1.getName());
                statement.setString(3, c1.getAddress());
                statement.setDouble(4, c1.getSalary());
                //Statement statement=connection.createStatement();
                //String sql="INSERT INTO Customer VALUES('"+c1.getId()+"','"+c1.getName()+"','"+c1.getAddress()+"','"+c1.getSalary()+"')";
                //int isSaved = statement.executeUpdate(sql);
                if (statement.executeUpdate() > 0) {
                    new Alert(Alert.AlertType.INFORMATION,
                            "Customer Saved!").show();
                    searchCustomer(searchText);
                    clearFields();
                } else {
                    new Alert(Alert.AlertType.WARNING,
                            "Try Again!").show();
                }
            } catch (ClassNotFoundException | SQLException e) {
                e.printStackTrace();
            }

        } else {
            //update

            try {
                String sql1 = "UPDATE Customer SET name=?,address=?,salary=? WHERE id=?";
                PreparedStatement statement1 = DBConnection.getInstance().getConnection().prepareStatement(sql1);
                statement1.setString(1,c1.getName());
                statement1.setString(2,c1.getAddress());
                statement1.setDouble(3,c1.getSalary());
                statement1.setString(4,c1.getId());
                if (statement1.executeUpdate()>0){
                    new Alert(Alert.AlertType.CONFIRMATION, "Updated!").show();
                    searchCustomer(searchText);
                    clearFields();
                }else {
                    new Alert(Alert.AlertType.WARNING,"Try again!").show();
                }
            }catch (ClassNotFoundException | SQLException e){
                e.printStackTrace();
            }
        }

    }

    private void clearFields() {
        txtId.clear();
        txtName.clear();
        txtAddress.clear();
        txtSalary.clear();
    }

    public void newCustomerOnAction(ActionEvent actionEvent) {
        btnSaveCustomer.setText("Save Customer");
    }

    public void backToHomeOnAction(ActionEvent actionEvent) throws IOException {
        Stage stage = (Stage) customerFormContext.getScene().getWindow();
        stage.setScene(new Scene(
                FXMLLoader.load(getClass().getResource("../view/DashboardForm.fxml"))
        ));
    }
}
