package com.seekerscloud.pos.controller;

import com.seekerscloud.pos.db.Database;
import com.seekerscloud.pos.modal.Order;
import com.seekerscloud.pos.view.tm.OrderTM;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.IOException;

public class OrderDetailsFormController {
    public AnchorPane orderDetailsContext;
    public TableColumn colId;
    public TableColumn colName;
    public TableColumn colDate;
    public TableColumn colTotal;
    public TableColumn colOption;
    public TableView tblOrder;

    public void initialize(){
        colId.setCellValueFactory(new PropertyValueFactory<>("orderId"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        colTotal.setCellValueFactory(new PropertyValueFactory<>("total"));
        colOption.setCellValueFactory(new PropertyValueFactory<>("btn"));
        loadOrders();
    }

    private void loadOrders() {
        ObservableList<OrderTM> tmList= FXCollections.observableArrayList();
        for (Order order: Database.orderTable
             ) {
            Button btn=new Button("View More");
            tmList.add(new OrderTM(
                    order.getOrderId(),
                    order.getCustomer(),
                    order.getDate(),
                    order.getTotalCost(),
                    btn
            ));
            btn.setOnAction(event -> {
                try {
                    FXMLLoader loader=new FXMLLoader(
                            getClass().getResource("../view/ItemDetailsForm.fxml")
                    );
                    Parent parent = loader.load();
                    ItemDetailsFormController controller = loader.getController();
                    controller.loadOrderDetails(order.getOrderId());
                    Stage stage=new Stage();
                    stage.setScene(new Scene(
                            parent
                    ));
                    stage.show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
        tblOrder.setItems(tmList);
    }

    public void backToHomeOnAction(ActionEvent actionEvent) throws IOException {
        Stage stage =(Stage) orderDetailsContext.getScene().getWindow();
        stage.setScene(new Scene(
                FXMLLoader.load(getClass().getResource("../view/DashboardForm.fxml"))
        ));
    }
}
