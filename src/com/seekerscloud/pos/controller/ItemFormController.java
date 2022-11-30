package com.seekerscloud.pos.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import com.seekerscloud.pos.db.DBConnection;
import com.seekerscloud.pos.db.Database;
import com.seekerscloud.pos.modal.Item;
import com.seekerscloud.pos.view.tm.ItemTM;
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
import java.util.Optional;

public class ItemFormController {
    public JFXButton btnSaveItem;
    public JFXTextField txtCode;
    public JFXTextField txtDescription;
    public JFXTextField txtQtyOnHand;
    public JFXTextField txtUnitPrice;
    public TableView<ItemTM> tblItem;
    public TableColumn colCode;
    public TableColumn colDescription;
    public TableColumn colQtyOnHand;
    public TableColumn colUnitPrice;
    public TableColumn colOption;
    public AnchorPane itemContext;
    public TextField txtSearch;

    private String searchText = "";

    public void initialize() {
        colCode.setCellValueFactory(new PropertyValueFactory<>("code"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        colQtyOnHand.setCellValueFactory(new PropertyValueFactory<>("qtyOnHand"));
        colUnitPrice.setCellValueFactory(new PropertyValueFactory<>("unitPrice"));
        colOption.setCellValueFactory(new PropertyValueFactory<>("btn"));

        searchItem(searchText);

        tblItem.getSelectionModel().selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> {
                    if (null != newValue)
                        setData(newValue);
                });
        txtSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            searchText = newValue;
            searchItem(searchText);
        });
    }

    private void setData(ItemTM newValue) {
        txtCode.setText(newValue.getCode());
        txtDescription.setText(newValue.getDescription());
        txtQtyOnHand.setText(String.valueOf(newValue.getQtyOnHand()));
        txtUnitPrice.setText(String.valueOf(newValue.getUnitPrice()));
        btnSaveItem.setText("Update Item");
    }

    private void searchItem(String text) {
        String searchText = "%" + text + "%";
        try {
            ObservableList<ItemTM> tmList = FXCollections.observableArrayList();
            String sql = "SELECT * FROM Item WHERE description LIKE ?";
            PreparedStatement statement = DBConnection.getInstance().getConnection().prepareStatement(sql);
            statement.setString(1, searchText);
            ResultSet set = statement.executeQuery();
            while (set.next()) {
                Button btn = new Button("Delete");
                ItemTM tm = new ItemTM(
                        set.getString(1),
                        set.getString(2),
                        set.getInt(3),
                        set.getDouble(4),
                        btn
                );
                tmList.add(tm);
                btn.setOnAction(event -> {
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                            "Are you sure whether do you want to delete this item?",
                            ButtonType.YES, ButtonType.NO);
                    Optional<ButtonType> buttonType = alert.showAndWait();
                    if (buttonType.get().equals(ButtonType.YES)) {
                        try {
                            String sql1 = "DELETE FROM Item WHERE code=?";
                            PreparedStatement statement1 = DBConnection.getInstance().getConnection().prepareStatement(sql1);
                            statement1.setString(1, tm.getCode());
                            if (statement1.executeUpdate() > 0) {
                                new Alert(Alert.AlertType.CONFIRMATION, "Deleted!").show();
                                searchItem(searchText);
                            } else {
                                new Alert(Alert.AlertType.WARNING, "Try again!").show();
                            }
                        } catch (ClassNotFoundException | SQLException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
            tblItem.setItems(tmList);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    public void saveItemOnAction(ActionEvent actionEvent) {
        Item item = new Item(
                txtCode.getText(),
                txtDescription.getText(),
                Integer.parseInt(txtQtyOnHand.getText()),
                Double.parseDouble(txtUnitPrice.getText()));
        if (btnSaveItem.getText().equalsIgnoreCase("Save Item")) {
            try {
                String sql = "INSERT INTO Item VALUES (?,?,?,?)";
                PreparedStatement statement = DBConnection.getInstance().getConnection().prepareStatement(sql);
                statement.setString(1,item.getCode());
                statement.setString(2,item.getDescription());
                statement.setInt(3,item.getQtyOnHand());
                statement.setDouble(4,item.getUnitPrice());
                if (statement.executeUpdate()>0) {
                    new Alert(Alert.AlertType.CONFIRMATION, "Saved!").show();
                    searchItem(searchText);
                    clearFields();
                }else {
                    new Alert(Alert.AlertType.WARNING,"Try again!").show();
                }
            } catch (SQLException | ClassNotFoundException e) {
                e.printStackTrace();
            }

        } else {
            //Update
            try {
                String sql = "UPDATE Item SET description=?,qtyOnHand=?,unitPrice=? WHERE code=?";
                PreparedStatement statement = DBConnection.getInstance().getConnection().prepareStatement(sql);
                statement.setString(1,item.getDescription());
                statement.setInt(2,item.getQtyOnHand());
                statement.setDouble(3,item.getUnitPrice());
                statement.setString(4,item.getCode());
                if (statement.executeUpdate()>0) {
                    new Alert(Alert.AlertType.CONFIRMATION, "Updated!").show();
                    searchItem(searchText);
                    clearFields();
                }else {
                    new Alert(Alert.AlertType.WARNING,"Try again!").show();
                }
            } catch (SQLException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

    }

    private void clearFields() {
        txtCode.clear();
        txtDescription.clear();
        txtQtyOnHand.clear();
        txtUnitPrice.clear();
    }

    public void backToHomeOnAction(ActionEvent actionEvent) throws IOException {
        Stage stage = (Stage) itemContext.getScene().getWindow();
        stage.setScene(new Scene(FXMLLoader.load(
                getClass().getResource("../view/DashboardForm.fxml")
        )));
    }

    public void newItemOnAction(ActionEvent actionEvent) {
        btnSaveItem.setText("Save Item");
    }
}
