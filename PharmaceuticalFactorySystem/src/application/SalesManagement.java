package application;

import java.util.Calendar;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class SalesManagement {
private ComboBox <String> customers;
private Label showOrders;
static Button toFire;
private ObservableList <String> forCombo = FXCollections.observableArrayList();
private ObservableList <SalesOrder> ordersList = FXCollections.observableArrayList();

private MyTableView <SalesOrder> ordersTable= new MyTableView<>();
private TableColumn <SalesOrder , Integer> orderId , empId;
private TableColumn <SalesOrder , Double> total;
private TableColumn <SalesOrder , String> date;


public SalesManagement () {
	showOrders = new MyLabel ("Customers Orders",2 );
	for (int i = 0 ; i  < Main.customers.size(); i++) {
		Customer c = Main.customers.get(i);
		forCombo.add("id : "+ c.getCustomerId() + " , "+c.getName() + " ( " + c.getEmail() +" )");
	}
	toFire = new Button("asds");

	customers = new MyComboBox<String>(forCombo);
	customers.setPromptText("Select Customer to see orders");
	
	customers.setOnAction(e->{
		if (customers.getValue() == null || customers.getValue().trim().isEmpty()) {
			Main.notValidAlert("Not Valid", "Select Customer from box");
			return;
		}
		String s = customers.getValue().substring(5, customers.getValue().indexOf(",")).trim();
		int id = Integer.parseInt(s);
		ordersList.clear();
		for (int i = 0;  i < Main.salesOrder.size(); i++) {
			if (Main.salesOrder.get(i).getCustomerId() == id) {
				ordersList.add(Main.salesOrder.get(i));
			}
		}
	});
	toFire.setOnAction(e->{
			if (customers.getValue() == null || customers.getValue().trim().isEmpty()) {
				//Main.notValidAlert("Not Valid", "Select Customer from box");
				return;
			}
			String s = customers.getValue().substring(5, customers.getValue().indexOf(",")).trim();
			int id = Integer.parseInt(s);
			ordersList.clear();
			for (int i = 0;  i < Main.salesOrder.size(); i++) {
				if (Main.salesOrder.get(i).getCustomerId() == id) {
					ordersList.add(Main.salesOrder.get(i));
				}
			}
	});
		orderId = ordersTable.createStyledColumn("Order Id", "salesOrderId", Integer.class);
		empId = ordersTable.createStyledColumn("Employee Id","employeeId" , Integer.class );
		total = ordersTable.createStyledColumn("Total Amount", "totalAmount" , Double.class);
		date = ordersTable.createStyledColumn("Date", "orderDate");
		date.setCellValueFactory(ee->{
			SalesOrder ss = ee.getValue();
			Calendar c=  Calendar.getInstance();
			c.setTime(ss.getOrderDate());
			return new SimpleStringProperty(
					c.get(Calendar.DAY_OF_MONTH) + "/" + (c.get(Calendar.MONTH) + 1) + "/" + c.get(Calendar.YEAR));
		});
		ordersTable.getColumns().addAll(orderId , empId , date , total);
		ordersTable.setItems(ordersList);
		ordersTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		ordersTable.setMinHeight(500);
		ordersTable.setMaxWidth(700);
		Button newOrder = new MyButton ("New Order",2);
		
		VBox allLeft = new VBox (10);
		allLeft.getChildren().addAll(showOrders,customers,ordersTable,newOrder);
		allLeft.setAlignment(Pos.CENTER);
		Scene scene = new Scene (allLeft , 800, 750);
		scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());

		Stage stage = new Stage();
		stage.setScene(scene);
		stage.show();
		newOrder.setOnAction(e->{
			if (customers.getValue() == null || customers.getValue().trim().isEmpty()) {
				Main.notValidAlert("Not Valid", "Select Customer from box");
				return;
			}
			String s = customers.getValue().substring(5, customers.getValue().indexOf(",")).trim();
			int id = Integer.parseInt(s);
			new ProductTableView(id);
		});
	
	
	
}
}
