package com.seekerscloud.pos.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
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

    private String searchText="";

    public void initialize(){
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colAddress.setCellValueFactory(new PropertyValueFactory<>("address"));
        colSalary.setCellValueFactory(new PropertyValueFactory<>("salary"));
        colOption.setCellValueFactory(new PropertyValueFactory<>("btn"));
        searchCustomer(searchText);

        tblCustomer.getSelectionModel().
                selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> {
                    if (null!=newValue)
                    setData(newValue);
                });
        txtSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            searchText=newValue;
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
        ObservableList<CustomerTM> tmList= FXCollections
                .observableArrayList();
        for (Customer customer:Database.customerTable
             ) {
            if (customer.getName().contains(text) || customer.getAddress().contains(text)){
                Button btn=new Button("Delete");
                tmList.add(new CustomerTM(
                        customer.getId(),
                        customer.getName(),
                        customer.getAddress(),
                        customer.getSalary(),
                        btn
                ));
                btn.setOnAction(event -> {
                    Alert alert=new Alert(Alert.AlertType.CONFIRMATION,
                            "Are you sure whether do you want to delete this customer?",
                            ButtonType.YES,ButtonType.NO);
                    Optional<ButtonType> buttonType = alert.showAndWait();
                    if (buttonType.get().equals(ButtonType.YES)){
                        boolean isDeleted = Database.customerTable.remove(customer);
                        if (isDeleted){
                            new Alert(Alert.AlertType.INFORMATION,"Deleted!").show();
                            searchCustomer(searchText);
                        }else {
                            new Alert(Alert.AlertType.WARNING,"Try Again!").show();
                        }
                    }
                });
            }

        }
        tblCustomer.setItems(tmList);
    }

    public void saveCustomerOnAction(ActionEvent actionEvent) {
        if (btnSaveCustomer.getText().equalsIgnoreCase(
                "Save Customer"
        )){
            Customer c1=new Customer(txtId.getText(),
                    txtName.getText(),
                    txtAddress.getText(),
                    Double.parseDouble(txtSalary.getText()));
            boolean isSaved=Database.customerTable.add(c1);
            if (isSaved){
                new Alert(Alert.AlertType.INFORMATION,
                        "Customer Saved!").show();
                searchCustomer(searchText);
                clearFields();
            }else {
                new Alert(Alert.AlertType.WARNING,
                        "Try Again!").show();
            }
        }else {
            //update
            for (int i = 0; i < Database.customerTable.size(); i++) {
                if (txtId.getText().equalsIgnoreCase(Database.customerTable.get(i).getId())){
                    Database.customerTable.get(i).setName(txtName.getText());
                    Database.customerTable.get(i).setAddress(txtAddress.getText());
                    Database.customerTable.get(i).setSalary(Double.parseDouble(txtSalary.getText()));
                    searchCustomer(searchText);
                    new Alert(Alert.AlertType.CONFIRMATION,"Updated!").show();
                    clearFields();
                }
            }
        }

    }
    private void clearFields(){
        txtId.clear();
        txtName.clear();
        txtAddress.clear();
        txtSalary.clear();
    }

    public void newCustomerOnAction(ActionEvent actionEvent) {
        btnSaveCustomer.setText("Save Customer");
    }

    public void backToHomeOnAction(ActionEvent actionEvent) throws IOException {
        Stage stage =(Stage) customerFormContext.getScene().getWindow();
        stage.setScene(new Scene(
                FXMLLoader.load(getClass().getResource("../view/DashboardForm.fxml"))
        ));
    }
}
