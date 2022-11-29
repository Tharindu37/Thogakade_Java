package com.seekerscloud.pos.controller;

import com.seekerscloud.pos.db.Database;
import com.seekerscloud.pos.modal.ItemDetails;
import com.seekerscloud.pos.modal.Order;
import com.seekerscloud.pos.view.tm.ItemDetailsTM;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.IOException;

public class ItemDetailsFormController {
    public AnchorPane itemDetailsContext;
    public TableView<ItemDetailsTM> tblItemDetails;
    public TableColumn colCode;
    public TableColumn colUnitPrice;
    public TableColumn colQty;
    public TableColumn colTotal;

    public void initialize(){
        colCode.setCellValueFactory(new PropertyValueFactory<>("code"));
        colQty.setCellValueFactory(new PropertyValueFactory<>("qty"));
        colUnitPrice.setCellValueFactory(new PropertyValueFactory<>("unitPrice"));
        colTotal.setCellValueFactory(new PropertyValueFactory<>("total"));
    }

    public void loadOrderDetails(String id){
        for (Order order: Database.orderTable
             ) {
            if (order.getOrderId().equalsIgnoreCase(id)){
                ObservableList<ItemDetailsTM> tmList= FXCollections.observableArrayList();
                for (ItemDetails itmItemDetails: order.getItemDetails()
                     ) {
                    double tempUnitPrice=itmItemDetails.getUnitPrice();
                    int tempQtyOnhand=itmItemDetails.getQty();
                    double tempTotal=tempQtyOnhand*tempUnitPrice;
                        tmList.add(new ItemDetailsTM(
                            itmItemDetails.getCode(),
                                itmItemDetails.getUnitPrice(),
                                itmItemDetails.getQty(),
                                tempTotal
                        ));
                }
                tblItemDetails.setItems(tmList);
                return;
            }
        }
    }

    public void backToHomeOnAction(ActionEvent actionEvent) throws IOException {
        Stage stage = (Stage) itemDetailsContext.getScene().getWindow();
        stage.setScene(new Scene(
                FXMLLoader.load(getClass().getResource("../view/DashboardForm.fxml"))
        ));
    }
}
