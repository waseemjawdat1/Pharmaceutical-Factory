package application;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class SalesManagement {
	private ComboBox<String> customers;
	private Label showOrders;
	static Button toFire;
	private ObservableList<String> forCombo = FXCollections.observableArrayList();
	private ObservableList<SalesOrder> ordersList = FXCollections.observableArrayList();

	private MyTableView<SalesOrder> ordersTable = new MyTableView<>();
	private TableColumn<SalesOrder, Integer> orderId, empId;
	private TableColumn<SalesOrder, Double> total;
	private TableColumn<SalesOrder, String> date;

	public SalesManagement() {
		showOrders = new MyLabel("Customers Orders", 2);
		for (int i = 0; i < Main.customers.size(); i++) {
			Customer c = Main.customers.get(i);
			forCombo.add("id : " + c.getCustomerId() + " , " + c.getName() + " ( " + c.getEmail() + " )");
		}
		toFire = new Button("asds");

		customers = new MyComboBox<String>(forCombo);
		customers.setPromptText("Select Customer to see orders");

		customers.setOnAction(e -> {
			if (customers.getValue() == null || customers.getValue().trim().isEmpty()) {
				Main.notValidAlert("Not Valid", "Select Customer from box");
				return;
			}
			String s = customers.getValue().substring(5, customers.getValue().indexOf(",")).trim();
			int id = Integer.parseInt(s);
			ordersList.clear();
			for (int i = 0; i < Main.salesOrder.size(); i++) {
				if (Main.salesOrder.get(i).getCustomerId() == id) {
					ordersList.add(Main.salesOrder.get(i));
				}
			}
		});
		toFire.setOnAction(e -> {
			if (customers.getValue() == null || customers.getValue().trim().isEmpty()) {
				// Main.notValidAlert("Not Valid", "Select Customer from box");
				return;
			}
			String s = customers.getValue().substring(5, customers.getValue().indexOf(",")).trim();
			int id = Integer.parseInt(s);
			ordersList.clear();
			for (int i = 0; i < Main.salesOrder.size(); i++) {
				if (Main.salesOrder.get(i).getCustomerId() == id) {
					ordersList.add(Main.salesOrder.get(i));
				}
			}
		});
		orderId = ordersTable.createStyledColumn("Order Id", "salesOrderId", Integer.class);
		empId = ordersTable.createStyledColumn("Employee Id", "employeeId", Integer.class);
		total = ordersTable.createStyledColumn("Total Amount", "totalAmount", Double.class);
		date = ordersTable.createStyledColumn("Date", "orderDate");
		date.setCellValueFactory(ee -> {
			SalesOrder ss = ee.getValue();
			Calendar c = Calendar.getInstance();
			c.setTime(ss.getOrderDate());
			return new SimpleStringProperty(
					c.get(Calendar.DAY_OF_MONTH) + "/" + (c.get(Calendar.MONTH) + 1) + "/" + c.get(Calendar.YEAR));
		});
		ordersTable.getColumns().addAll(orderId, empId, date, total);
		ordersTable.setItems(ordersList);
		ordersTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		ordersTable.setMinHeight(500);
		ordersTable.setMaxWidth(700);

		Button newOrder = new MyButton("New Order", 2);
		Button deleteOrder = new MyButton("Delete Order", 2);
		Button viewOrderDetails = new MyButton("View Order Details", 2);

		HBox orderButtonsHBox = new HBox(10);
		orderButtonsHBox.getChildren().addAll(newOrder, deleteOrder, viewOrderDetails);
		orderButtonsHBox.setAlignment(Pos.CENTER);

		VBox allLeft = new VBox(10);
		allLeft.getChildren().addAll(showOrders, customers, ordersTable, orderButtonsHBox);
		allLeft.setAlignment(Pos.CENTER);

		newOrder.setOnAction(e -> {
			if (customers.getValue() == null || customers.getValue().trim().isEmpty()) {
				Main.notValidAlert("Not Valid", "Select Customer from box");
				return;
			}
			String s = customers.getValue().substring(5, customers.getValue().indexOf(",")).trim();
			int id = Integer.parseInt(s);
			new ProductTableView(id);
		});

		deleteOrder.setOnAction(e -> {
			SalesOrder selectedOrder = ordersTable.getSelectionModel().getSelectedItem();
			if (selectedOrder == null) {
				Main.notValidAlert("Nothing Selected", "You must select the order row you want to delete!");
				return;
			}
			Alert remove = new Alert(AlertType.CONFIRMATION);
			remove.setTitle("Delete Order");
			remove.setHeaderText(null);
			remove.setContentText("Are you sure you want to delete the order with ID number "
					+ selectedOrder.getSalesOrderId() + "?");
			ButtonType res = remove.showAndWait().orElse(ButtonType.CANCEL);
			if (res == ButtonType.OK) {
				Main.salesOrder.remove(selectedOrder);
				String deleteOrderSql = "DELETE FROM sales_orders WHERE sales_order_id = ?";
				try (PreparedStatement stmt = Main.conn.prepareStatement(deleteOrderSql)) {
					stmt.setInt(1, selectedOrder.getSalesOrderId());
					stmt.executeUpdate();
				} catch (SQLException ex) {
					Main.notValidAlert("Invalid", ex.getMessage());
					return;
				}
				Main.validAlert("Delete Order", "The order with ID number " + selectedOrder.getSalesOrderId()
						+ " has been deleted successfully.");
			}
		});

		viewOrderDetails.setOnAction(e -> {
			SalesOrder selectedOrder = ordersTable.getSelectionModel().getSelectedItem();
			if (selectedOrder == null) {
				Main.notValidAlert("Nothing Selected", "You must select the order row you want to view it's details!");
				return;
			}

			Label selectedOrderLabel = new MyLabel("Order ID (" + selectedOrder.getSalesOrderId() + ") details");

//			MyTableView<Product> orderDetailsTableView = new MyTableView<Product>();
//			TableColumn<Product, Integer> productIDColumn = orderDetailsTableView.createStyledColumn("Product ID",
//					"productId", Integer.class);
//			TableColumn<Product, String> nameColumn = orderDetailsTableView.createStyledColumn("Product Name", "name");
//			TableColumn<Product, Integer> quantityColumn = orderDetailsTableView.createStyledColumn("Quantity",
//					"quantity", Integer.class);
//			TableColumn<Product, Double> priceColumn = orderDetailsTableView.createStyledColumn("Price", "price",
//					Double.class);
//			
//			orderDetailsTableView.getColumns().addAll(productIDColumn,nameColumn,quantityColumn,priceColumn);
//			//orderDetailsTableView.setItems();
//			orderDetailsTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
//			orderDetailsTableView.setMinHeight(500);
//			orderDetailsTableView.setMaxWidth(700);
		//	ObservableList<ViewOrderDetails> viewOrderDetailsList = FXCollections.
			String selectProductQuantitySql = "SELECT product_id, quantity,unit_price FROM sales_order_details WHERE sales_order_id = ?";
			try {
				PreparedStatement stmt = Main.conn.prepareStatement(selectProductQuantitySql);
				stmt.setInt(1, selectedOrder.getSalesOrderId());
				ResultSet rs = stmt.executeQuery();
				
				while (rs.next()) {
					int productID = rs.getInt("product_id");
					int quantity = rs.getInt("quantity");
					double price = rs.getDouble("unit_price");
					String productName = null;
					for (int i = 0; i < Main.products.size(); i++) {
						if(Main.products.get(i).getProductId() == productID) {
							productName = Main.products.get(i).getName();
						}
					}
					ViewOrderDetails orderDetails = new ViewOrderDetails(productID, productName, quantity, price);	
				}
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
		});

		Scene scene = new Scene(allLeft, 800, 750);
		scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());

		Stage stage = new Stage();
		stage.setScene(scene);
		stage.show();
	}
}
