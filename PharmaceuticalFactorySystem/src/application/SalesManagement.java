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
	private VBox allLeft;
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
			for (int i = 0; i < Main.salesOrders.size(); i++) {
				if (Main.salesOrders.get(i).getCustomerId() == id) {
					ordersList.add(Main.salesOrders.get(i));
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
			for (int i = 0; i < Main.salesOrders.size(); i++) {
				if (Main.salesOrders.get(i).getCustomerId() == id) {
					ordersList.add(Main.salesOrders.get(i));
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

		allLeft = new VBox(10);
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
				try {
					String sql = "SELECT product_id, quantity FROM sales_order_details WHERE sales_order_id = ?";
					PreparedStatement stmt = Main.conn.prepareStatement(sql);
					stmt.setInt(1, selectedOrder.getSalesOrderId());
					ResultSet rs = stmt.executeQuery();

					while (rs.next()) {
						int productId = rs.getInt("product_id");
						int qty = rs.getInt("quantity");

						int i = 0;
						while (i < Main.products.size()) {
							Product p = Main.products.get(i);
							if (p.getProductId() == productId) {
								int newQty = p.getQuantity() + qty;
								p.updateProduct(p.getName(), p.getCategory(), newQty, p.getWarehouseId(), p.getPrice());
								break;
							}
							i++;
						}
					}

					Main.salesOrders.remove(selectedOrder);
					toFire.fire();

					String deleteDetailsSql = "DELETE FROM sales_order_details WHERE sales_order_id = ?";
					PreparedStatement stmtDetails = Main.conn.prepareStatement(deleteDetailsSql);
					stmtDetails.setInt(1, selectedOrder.getSalesOrderId());
					stmtDetails.executeUpdate();

					String deleteOrderSql = "DELETE FROM sales_orders WHERE sales_order_id = ?";
					PreparedStatement stmtOrder = Main.conn.prepareStatement(deleteOrderSql);
					stmtOrder.setInt(1, selectedOrder.getSalesOrderId());
					stmtOrder.executeUpdate();

					Main.validAlert("Delete Order", "The order with ID number " + selectedOrder.getSalesOrderId() + " has been deleted and products have been restocked.");
				} catch (SQLException ex) {
					Main.notValidAlert("Error", ex.getMessage());
				}
			}
		});

		viewOrderDetails.setOnAction(e -> {
			SalesOrder selectedOrder = ordersTable.getSelectionModel().getSelectedItem();
			if (selectedOrder == null) {
				Main.notValidAlert("Nothing Selected", "You must select the order row you want to view it's details!");
				return;
			}

			ObservableList<ViewOrderDetails> viewOrderDetailsList = FXCollections.observableArrayList();
			String selectProductQuantitySql = "SELECT product_id, quantity,unit_price FROM sales_order_details WHERE sales_order_id = ?";
			try {
				PreparedStatement stmt = Main.conn.prepareStatement(selectProductQuantitySql);
				stmt.setInt(1, selectedOrder.getSalesOrderId());
				ResultSet rs = stmt.executeQuery();

				String productName = null;
				while (rs.next()) {
					int productID = rs.getInt("product_id");
					int quantity = rs.getInt("quantity");
					double price = rs.getDouble("unit_price");
					for (int i = 0; i < Main.products.size(); i++) {
						if (Main.products.get(i).getProductId() == productID) {
							productName = Main.products.get(i).getName();
						}
					}
					ViewOrderDetails orderDetails = new ViewOrderDetails(productID, productName, quantity, price);
					viewOrderDetailsList.add(orderDetails);
				}
			} catch (SQLException ex) {
				Main.notValidAlert("Invalid", ex.getMessage());
				return;
			}

			Label selectedOrderLabel = new MyLabel("Order ID (" + selectedOrder.getSalesOrderId() + ") details");
			MyTableView<ViewOrderDetails> orderDetailsTableView = new MyTableView<ViewOrderDetails>();
			TableColumn<ViewOrderDetails, Integer> productIDColumn = orderDetailsTableView
					.createStyledColumn("Product ID", "productID", Integer.class);
			TableColumn<ViewOrderDetails, String> nameColumn = orderDetailsTableView.createStyledColumn("Product Name",
					"productName");
			TableColumn<ViewOrderDetails, Integer> quantityColumn = orderDetailsTableView.createStyledColumn("Quantity",
					"quantity", Integer.class);
			TableColumn<ViewOrderDetails, Double> priceColumn = orderDetailsTableView.createStyledColumn("Price",
					"price", Double.class);

			orderDetailsTableView.getColumns().addAll(productIDColumn, nameColumn, quantityColumn, priceColumn);
			orderDetailsTableView.setItems(viewOrderDetailsList);
			orderDetailsTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
			orderDetailsTableView.setMinHeight(500);
			orderDetailsTableView.setMaxWidth(700);

			VBox all = new VBox(10);
			all.getChildren().addAll(selectedOrderLabel, orderDetailsTableView);
			all.setAlignment(Pos.CENTER);

			Scene orderDetailScene = new Scene(all, 800, 800);
			orderDetailScene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			Stage orderDetailStage = new Stage();
			orderDetailStage.setScene(orderDetailScene);
			orderDetailStage.show();

		});

//		Scene scene = new Scene(allLeft, 800, 750);
//		scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
//		Stage stage = new Stage();
//		stage.setScene(scene);
//		stage.show();
	}
	public VBox getAll() {
		return allLeft;
	}
}
