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
	private VBox allLeft;
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

	    allLeft = new VBox(10);
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
				try {
					String sql = "SELECT material_id, quantity FROM purchase_order_details WHERE purchase_order_id = ?";
					PreparedStatement stmt = Main.conn.prepareStatement(sql);
					stmt.setInt(1, selectedOrder.getPurchaseOrderId());
					ResultSet rs = stmt.executeQuery();

					while (rs.next()) {
						int materialId = rs.getInt("material_id");
						int qtyPurchased = rs.getInt("quantity");

						int i = 0;
						boolean found = false;
						while (i < Main.materials.size()) {
							RawMaterial m = Main.materials.get(i);
							if (m.getMaterialId() == materialId) {
								found = true;
								if (m.getUnit() < qtyPurchased) {
									Main.notValidAlert("Cannot Delete", "Material ID " + materialId + " has only " + m.getUnit() + " units, but this purchase added " + qtyPurchased + ". Purchase more before deletion.");
									return;
								}
								break;
							}
							i++;
						}

						if (!found) {
							Main.notValidAlert("Error", "Material ID " + materialId + " not found in system.");
							return;
						}
					}

					rs.beforeFirst(); 
					while (rs.next()) {
						int materialId = rs.getInt("material_id");
						int qtyPurchased = rs.getInt("quantity");

						int i = 0;
						while (i < Main.materials.size()) {
							RawMaterial m = Main.materials.get(i);
							if (m.getMaterialId() == materialId) {
								int newQty = m.getUnit() - qtyPurchased;
								m.updateMaterial(m.getName(), newQty, m.getSupplierId(), m.getPrice());
								break;
							}
							i++;
						}
					}

					Main.purchaseOrders.remove(selectedOrder);
					toFire.fire();

					String deleteDetailsSql = "DELETE FROM purchase_order_details WHERE purchase_order_id = ?";
					PreparedStatement stmtDetails = Main.conn.prepareStatement(deleteDetailsSql);
					stmtDetails.setInt(1, selectedOrder.getPurchaseOrderId());
					stmtDetails.executeUpdate();

					String deleteOrderSql = "DELETE FROM purchase_orders WHERE purchase_order_id = ?";
					PreparedStatement stmtOrder = Main.conn.prepareStatement(deleteOrderSql);
					stmtOrder.setInt(1, selectedOrder.getPurchaseOrderId());
					stmtOrder.executeUpdate();

					Main.validAlert("Delete Purchase", "The purchase with ID number " + selectedOrder.getPurchaseOrderId() + " has been deleted successfully.");
				} catch (SQLException ex) {
					Main.notValidAlert("Error", ex.getMessage());
				}
			}

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
