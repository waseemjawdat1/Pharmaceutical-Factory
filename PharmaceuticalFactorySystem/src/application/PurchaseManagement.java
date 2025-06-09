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
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class PurchaseManagement {
	private ComboBox<String> suppliers;
	private Label showPurchases;
	static Button toFire;
	private ObservableList<String> forCombo = FXCollections.observableArrayList();
	private ObservableList<PurchaseOrder> purchaseList = FXCollections.observableArrayList();

	private MyTableView<PurchaseOrder> purchaseTable = new MyTableView<>();
	private TableColumn<PurchaseOrder, Integer> orderId, empId;
	private TableColumn<PurchaseOrder, Double> total;
	private TableColumn<PurchaseOrder, String> date;

	public PurchaseManagement() {
		showPurchases = new MyLabel("Suppliers Purchases", 2);
		for (int i = 0 ;i < Main.suppliers.size(); i++) {
			Supplier s = Main.suppliers.get(i);
			forCombo.add("id : " + s.getSupplierId() + " , " + s.getName() + " ( " + s.getEmail() + " )");
		}
		suppliers = new MyComboBox<>(forCombo);
		suppliers.setPromptText("Select Supplier to see purchases");
		toFire = new Button("fire");

		suppliers.setOnAction(e -> {
			if (suppliers.getValue() == null || suppliers.getValue().trim().isEmpty()) {
				Main.notValidAlert("Not Valid", "Select Supplier from box");
				return;
			}
			String s = suppliers.getValue().substring(5, suppliers.getValue().indexOf(",")).trim();
			int id = Integer.parseInt(s);
			purchaseList.clear();
			for (int i= 0 ; i<  Main.purchaseOrders.size(); i++) {
				if (Main.purchaseOrders.get(i).getSupplierId() == id) {
					purchaseList.add(Main.purchaseOrders.get(i));
				}
			}
		});

		toFire.setOnAction(e -> {
			if (suppliers.getValue() == null || suppliers.getValue().trim().isEmpty()) {
				return;
			}
			String s = suppliers.getValue().substring(5, suppliers.getValue().indexOf(",")).trim();
			int id = Integer.parseInt(s);
			purchaseList.clear();
			for (int i= 0 ; i<  Main.purchaseOrders.size(); i++) {
				if (Main.purchaseOrders.get(i).getSupplierId() == id) {
					purchaseList.add(Main.purchaseOrders.get(i));
				}
			}
		});

		orderId = purchaseTable.createStyledColumn("Order Id", "purchaseOrderId", Integer.class);
		empId = purchaseTable.createStyledColumn("Employee Id", "employeeId", Integer.class);
		total = purchaseTable.createStyledColumn("Total Amount", "totalAmount", Double.class);
		date = purchaseTable.createStyledColumn("Date", "orderDate");
		date.setCellValueFactory(ee -> {
			PurchaseOrder ss = ee.getValue();
			Calendar c = Calendar.getInstance();
			c.setTime(ss.getOrderDate());
			return new SimpleStringProperty(c.get(Calendar.DAY_OF_MONTH) + "/" + (c.get(Calendar.MONTH) + 1) + "/"
					+ c.get(Calendar.YEAR));
		});

		purchaseTable.getColumns().addAll(orderId, empId, date, total);
		purchaseTable.setItems(purchaseList);
		purchaseTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		purchaseTable.setMinHeight(500);
		purchaseTable.setMaxWidth(700);

		Button newPurchase = new MyButton("New Purchase", 2);
		Button deletePurchase = new MyButton("Delete Purchase", 2);
		Button viewPurchaseDetails = new MyButton("View Purchase Details", 2);

		HBox purchaseButtonsHBox = new HBox(10);
		purchaseButtonsHBox.getChildren().addAll(newPurchase, deletePurchase, viewPurchaseDetails);
		purchaseButtonsHBox.setAlignment(Pos.CENTER);

		VBox allLeft = new VBox(10);
		allLeft.getChildren().addAll(showPurchases, suppliers, purchaseTable, purchaseButtonsHBox);
		allLeft.setAlignment(Pos.CENTER);

		newPurchase.setOnAction(e -> {
			if (suppliers.getValue() == null || suppliers.getValue().trim().isEmpty()) {
				Main.notValidAlert("Not Valid", "Select Supplier from box");
				return;
			}
			String s = suppliers.getValue().substring(5, suppliers.getValue().indexOf(",")).trim();
			int id = Integer.parseInt(s);
			new RawMaterialTableView(id);
		});
		viewPurchaseDetails.setOnAction(e -> {
			PurchaseOrder selectedOrder = purchaseTable.getSelectionModel().getSelectedItem();
			if (selectedOrder == null) {
				Main.notValidAlert("Nothing Selected", "You must select the order row you want to view its details!");
				return;
			}

			ObservableList<ViewMaterialDetails> viewMaterialDetailsList = FXCollections.observableArrayList();
			String sql = "SELECT material_id, quantity, unit_price FROM purchase_order_details WHERE purchase_order_id = ?";
			try {
				PreparedStatement stmt = Main.conn.prepareStatement(sql);
				stmt.setInt(1, selectedOrder.getPurchaseOrderId());
				ResultSet rs = stmt.executeQuery();

				while (rs.next()) {
					int materialID = rs.getInt("material_id");
					int quantity = rs.getInt("quantity");
					double price = rs.getDouble("unit_price");
					String name = "";
					for (int i = 0 ; i < Main.materials.size(); i++) {
						if (Main.materials.get(i).getMaterialId() == materialID) {
							name = Main.materials.get(i).getName();
							break;
						}
					}
					ViewMaterialDetails detail = new ViewMaterialDetails(materialID, name, quantity, price);
					viewMaterialDetailsList.add(detail);
				}
			} catch (SQLException ex) {
				Main.notValidAlert("Invalid", ex.getMessage());
				return;
			}

			Label title = new MyLabel("Order ID (" + selectedOrder.getPurchaseOrderId() + ") details");
			MyTableView<ViewMaterialDetails> detailTable = new MyTableView<>();

			TableColumn<ViewMaterialDetails, Integer> idCol = detailTable.createStyledColumn("Material ID", "materialId", Integer.class);
			TableColumn<ViewMaterialDetails, String> nameCol = detailTable.createStyledColumn("Material Name", "materialName");
			TableColumn<ViewMaterialDetails, Integer> qtyCol = detailTable.createStyledColumn("Quantity", "quantity", Integer.class);
			TableColumn<ViewMaterialDetails, Double> priceCol = detailTable.createStyledColumn("Unit price", "price", Double.class);

			detailTable.getColumns().addAll(idCol, nameCol, qtyCol, priceCol);
			detailTable.setItems(viewMaterialDetailsList);
			detailTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
			detailTable.setMinHeight(500);
			detailTable.setMaxWidth(700);

			VBox box = new VBox(10);
			box.getChildren().addAll(title, detailTable);
			box.setAlignment(Pos.CENTER);

			Scene detailScene = new Scene(box, 800, 800);
			detailScene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			Stage detailStage = new Stage();
			detailStage.setScene(detailScene);
			detailStage.show();
		});
		deletePurchase.setOnAction(e -> {
			PurchaseOrder selectedOrder = purchaseTable.getSelectionModel().getSelectedItem();
			if (selectedOrder == null) {
				Main.notValidAlert("Nothing Selected", "You must select the order row you want to delete!");
				return;
			}
			Alert remove = new Alert(AlertType.CONFIRMATION);
			remove.setTitle("Delete Purchase");
			remove.setHeaderText(null);
			remove.setContentText("Are you sure you want to delete the purchase with ID number " + selectedOrder.getPurchaseOrderId() + "?");
			ButtonType res = remove.showAndWait().orElse(ButtonType.CANCEL);
			if (res == ButtonType.OK) {
				Main.purchaseOrders.remove(selectedOrder);
				toFire.fire();
				String deleteOrderSql = "DELETE FROM purchase_orders WHERE purchase_order_id = ?";
				try {
					PreparedStatement stmt = Main.conn.prepareStatement(deleteOrderSql);
					stmt.setInt(1, selectedOrder.getPurchaseOrderId());
					stmt.executeUpdate();
				} catch (SQLException ex) {
					Main.notValidAlert("Invalid", ex.getMessage());
					return;
				}
				Main.validAlert("Delete Purchase", "The purchase with ID number " + selectedOrder.getPurchaseOrderId() + " has been deleted successfully.");
			}
		});

		Scene scene = new Scene(allLeft, 800, 750);
		scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		Stage stage = new Stage();
		stage.setScene(scene);
		stage.show();
	}
}
