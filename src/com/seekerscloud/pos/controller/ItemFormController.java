package com.seekerscloud.pos.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import com.seekerscloud.pos.bo.BoFactory;
import com.seekerscloud.pos.bo.BoType;
import com.seekerscloud.pos.bo.custom.ItemBo;
import com.seekerscloud.pos.dao.DaoFactory;
import com.seekerscloud.pos.dao.DaoTypes;
import com.seekerscloud.pos.dao.custom.ItemDao;
import com.seekerscloud.pos.dao.custom.impl.ItemDaoImpl;
import com.seekerscloud.pos.dto.ItemDto;
import com.seekerscloud.pos.entity.Item;
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

    private ItemBo itemBo= BoFactory.getInstance().getBo(BoType.ITEM);

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
            for (ItemDto item: itemBo.searchItem(searchText)) {
                Button btn = new Button("Delete");
                ItemTM tm = new ItemTM(
                        item.getCode(),
                        item.getDescription(),
                        item.getQtyOnHand(),
                        item.getUnitPrice(),
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
                            if (itemBo.deleteItem(tm.getCode())) {
                                new Alert(Alert.AlertType.INFORMATION, "Deleted!").show();
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
        if (btnSaveItem.getText().equalsIgnoreCase("Save Item")) {
            try {
                boolean isItemSaved = itemBo.saveItem(new ItemDto(
                        txtCode.getText(),
                        txtDescription.getText(),
                        Integer.parseInt(txtQtyOnHand.getText()),
                        Double.parseDouble(txtUnitPrice.getText())
                ));
                if (isItemSaved) {
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
                boolean isItemUpdated = itemBo.updateItem(new ItemDto(
                        txtCode.getText(),
                        txtDescription.getText(),
                        Integer.parseInt(txtQtyOnHand.getText()),
                        Double.parseDouble(txtUnitPrice.getText())
                ));
                if (isItemUpdated) {
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
