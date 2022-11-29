package com.seekerscloud.pos.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
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

    private String searchText="";

    public void initialize(){
        colCode.setCellValueFactory(new PropertyValueFactory<>("code"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        colQtyOnHand.setCellValueFactory(new PropertyValueFactory<>("qtyOnHand"));
        colUnitPrice.setCellValueFactory(new PropertyValueFactory<>("unitPrice"));
        colOption.setCellValueFactory(new PropertyValueFactory<>("btn"));

        searchItem(searchText);

        tblItem.getSelectionModel().selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> {
                    if (null!=newValue)
                        setData(newValue);
                });
        txtSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            searchText=newValue;
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
        ObservableList<ItemTM> obList= FXCollections.observableArrayList();
        for (Item item:Database.itemTable
             ) {
            if (item.getCode().contains(text) || item.getDescription().contains(text)){
                Button btn=new Button("Delete");
                obList.add(new ItemTM(
                        item.getCode(),
                        item.getDescription(),
                        item.getQtyOnHand(),
                        item.getUnitPrice(),
                        btn
                ));
                btn.setOnAction(event -> {
                    Alert alert=new Alert(Alert.AlertType.CONFIRMATION,
                            "Are you sure whether do you want to delete this item?",
                            ButtonType.YES,ButtonType.NO);
                    Optional<ButtonType> buttonType = alert.showAndWait();
                    if (buttonType.get().equals(ButtonType.YES)){
                        boolean isDelete = Database.itemTable.remove(item);
                        if (isDelete){
                            new Alert(Alert.AlertType.CONFIRMATION,"Deleted!").show();
                            searchItem(searchText);
                        }
                    }
                });
            }
        }
        tblItem.setItems(obList);
    }

    public void saveItemOnAction(ActionEvent actionEvent) {
        if (btnSaveItem.getText().equalsIgnoreCase("Save Item")){
            boolean isSaved = Database.itemTable.add(new Item(
                    txtCode.getText(),
                    txtDescription.getText(),
                    Integer.parseInt(txtQtyOnHand.getText()),
                    Double.parseDouble(txtUnitPrice.getText())
            ));
            if (isSaved){
                new Alert(Alert.AlertType.CONFIRMATION,"Saved!").show();
                searchItem(searchText);
                clearFields();
            }
        }else {
            //Update
            for (int i = 0; i < Database.itemTable.size(); i++) {
                if (txtCode.getText().equalsIgnoreCase(Database.itemTable.get(i).getCode())){
                    Database.itemTable.get(i).setDescription(txtDescription.getText());
                    Database.itemTable.get(i).setQtyOnHand(Integer.parseInt(txtQtyOnHand.getText()));
                    Database.itemTable.get(i).setUnitPrice(Double.parseDouble(txtUnitPrice.getText()));
                    searchItem(searchText);
                    new Alert(Alert.AlertType.CONFIRMATION,"Updated!");
                    clearFields();
                }
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
        Stage stage =(Stage) itemContext.getScene().getWindow();
        stage.setScene(new Scene(FXMLLoader.load(
                getClass().getResource("../view/DashboardForm.fxml")
        )));
    }

    public void newItemOnAction(ActionEvent actionEvent) {
        btnSaveItem.setText("Save Item");
    }
}
