package com.seekerscloud.pos.controller;

import com.seekerscloud.pos.db.DBConnection;
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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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
        try {
            String sql="SELECT o.orderId,d.itemCode,d.orderId,d.unitPrice,d.qty" +
                    " FROM `Order` o INNER JOIN `Order Details` d ON o.orderId=d.orderId AND o.orderId=?";
            PreparedStatement statement= DBConnection.getInstance().getConnection().prepareStatement(sql);
            statement.setString(1,id);
            ResultSet set=statement.executeQuery();
            ObservableList<ItemDetailsTM> tmList= FXCollections.observableArrayList();
            while (set.next()){
                double tempUnitPrice=set.getDouble(4);
                int tempQtyOnhand=set.getInt(5);
                double tempTotal=tempQtyOnhand*tempUnitPrice;
                tmList.add(new ItemDetailsTM(
                        set.getString(2),
                        tempUnitPrice,
                        tempQtyOnhand,
                        tempTotal
                ));
            }
            tblItemDetails.setItems(tmList);
        }catch (SQLException | ClassNotFoundException e){
            e.printStackTrace();
        }
    }

    public void backToHomeOnAction(ActionEvent actionEvent) throws IOException {
        Stage stage = (Stage) itemDetailsContext.getScene().getWindow();
        stage.setScene(new Scene(
                FXMLLoader.load(getClass().getResource("../view/DashboardForm.fxml"))
        ));
    }
}
