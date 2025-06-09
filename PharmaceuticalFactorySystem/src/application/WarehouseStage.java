package application;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class WarehouseStage {
	static MyTableView<Warehouse> warehouseTable;
	private TableColumn<Warehouse, Integer> id, capacity;
	private TableColumn<Warehouse, String> location;
	private Button add, update, remove;

	public WarehouseStage() {
		warehouseTable = new MyTableView<>();
		id = warehouseTable.createStyledColumn("Warehouse ID", "warehouseId", Integer.class);
		location = warehouseTable.createStyledColumn("Location", "location");
		capacity = warehouseTable.createStyledColumn("Capacity", "capacity", Integer.class);

		warehouseTable.getColumns().addAll(id, location, capacity);
		warehouseTable.setItems(Main.warehouses);
		warehouseTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		warehouseTable.setMinHeight(500);
		warehouseTable.setMaxWidth(700);

		add = new MyButton("➕ Add", 2);
		update = new MyButton("✎ Edit", 2);
		remove = new MyButton("➖ Remove", 2);

		HBox buttons = new HBox(10, add, update, remove);
		buttons.setAlignment(Pos.CENTER);

		Label searchL = new MyLabel("Search Location");
		TextField searchTF = new MyTextField();
		HBox searchBox = new HBox(10, searchL, searchTF);
		searchBox.setAlignment(Pos.CENTER);

		searchTF.setOnKeyTyped(e -> {
			String searchS = searchTF.getText();
			if (searchS == null || searchS.isEmpty()) {
				warehouseTable.setItems(Main.warehouses);
				return;
			}
			ObservableList<Warehouse> temp = FXCollections.observableArrayList();
			for (int i = 0; i < Main.warehouses.size(); i++) {
				if (Main.warehouses.get(i).getLocation().toLowerCase().contains(searchS.toLowerCase())) {
					temp.add(Main.warehouses.get(i));
				}
			}
			if (temp.size() > 0)
				warehouseTable.setItems(temp);
			else
				warehouseTable.setItems(Main.warehouses);
		});

		add.setOnAction(e -> {
			Label title = new MyLabel("Add Warehouse", 1);
			Label locationL = new MyLabel("Location");
			Label capacityL = new MyLabel("Capacity");

			TextField locationTF = new MyTextField();
			TextField capacityTF = new MyTextField();

			GridPane g = new GridPane();
			g.addColumn(0, locationL, capacityL);
			g.addColumn(1, locationTF, capacityTF);
			g.setHgap(5);
			g.setVgap(5);
			g.setAlignment(Pos.CENTER);

			Button addBtn = new MyButton("Add", 2);
			Button clear = new MyButton("Clear", 2);
			HBox buttons1 = new HBox(10, addBtn, clear);
			buttons1.setAlignment(Pos.CENTER);

			VBox all = new VBox(10, title, g, buttons1);
			all.setAlignment(Pos.CENTER);

			Scene s = new Scene(all, 400, 300);
			Stage st = new Stage();
			st.setScene(s);
			st.setTitle("Add Warehouse");
			st.show();

			clear.setOnAction(e1 -> {
				locationTF.clear();
				capacityTF.clear();
			});

			addBtn.setOnAction(e1 -> {
				String locationS = locationTF.getText();
				String capacityS = capacityTF.getText();

				if (locationS == null || locationS.isEmpty()) {
					Main.notValidAlert("Not Valid", "Location is empty");
					return;
				}
				if (capacityS == null || capacityS.isEmpty()) {
					Main.notValidAlert("Not Valid", "Capacity is empty");
					return;
				}

				int capacityInt = -1;
				try {
					capacityInt = Integer.parseInt(capacityS);
				} catch (Exception e2) {
					Main.notValidAlert("Not Valid", "Capacity must be a number");
					return;
				}
				if (capacityInt <= 0) {
					Main.notValidAlert("Not Valid", "Capacity must be greater than 0");
					return;
				}

				Warehouse w;
				try {
					w = new Warehouse(locationS, capacityInt);
				} catch (SQLException e2) {
					Main.notValidAlert("Error", e2.getMessage());
					return;
				}
				Main.warehouses.add(w);
				Main.validAlert("Warehouse Added", "Warehouse added successfully");
				st.close();
			});
		});

		update.setOnAction(e -> {
			Warehouse selected = warehouseTable.getSelectionModel().getSelectedItem();
			if (selected == null) {
				Main.notValidAlert("Nothing Selected", "Select warehouse to update");
				return;
			}

			Label title = new MyLabel("Update Warehouse", 1);
			Label locationL = new MyLabel("Location");
			Label capacityL = new MyLabel("Capacity");

			TextField locationTF = new MyTextField();
			locationTF.setText(selected.getLocation());

			TextField capacityTF = new MyTextField();
			capacityTF.setText(selected.getCapacity() + "");

			GridPane g = new GridPane();
			g.addColumn(0, locationL, capacityL);
			g.addColumn(1, locationTF, capacityTF);
			g.setHgap(5);
			g.setVgap(5);
			g.setAlignment(Pos.CENTER);

			Button updateBtn = new MyButton("Update", 2);
			Button clear = new MyButton("Clear", 2);
			HBox buttons1 = new HBox(10, updateBtn, clear);
			buttons1.setAlignment(Pos.CENTER);

			VBox all = new VBox(10, title, g, buttons1);
			all.setAlignment(Pos.CENTER);

			Scene s = new Scene(all, 400, 300);
			Stage st = new Stage();
			st.setScene(s);
			st.setTitle("Update Warehouse");
			st.show();

			clear.setOnAction(e1 -> {
				locationTF.clear();
				capacityTF.clear();
			});

			updateBtn.setOnAction(e1 -> {
				String locationS = locationTF.getText();
				String capacityS = capacityTF.getText();

				if (locationS == null || locationS.isEmpty()) {
					Main.notValidAlert("Not Valid", "Location is empty");
					return;
				}
				if (capacityS == null || capacityS.isEmpty()) {
					Main.notValidAlert("Not Valid", "Capacity is empty");
					return;
				}

				int capacityInt = -1;
				try {
					capacityInt = Integer.parseInt(capacityS);
				} catch (Exception e2) {
					Main.notValidAlert("Not Valid", "Capacity must be a number");
					return;
				}
				if (capacityInt <= 0) {
					Main.notValidAlert("Not Valid", "Capacity must be greater than 0");
					return;
				}

				try {
					selected.updateWarehouse(locationS, capacityInt);
					warehouseTable.refresh();
				} catch (SQLException e2) {
					Main.notValidAlert("Error", e2.getMessage());
					return;
				}
				Main.validAlert("Warehouse Updated", "Warehouse updated successfully");
				st.close();
			});
		});

		remove.setOnAction(e -> {
			Warehouse selected = warehouseTable.getSelectionModel().getSelectedItem();
			if (selected == null) {
				Main.notValidAlert("Nothing Selected", "Select warehouse to remove");
				return;
			}
			Alert alert = new Alert(AlertType.CONFIRMATION);
			alert.setTitle("Remove Warehouse");
			alert.setHeaderText(null);
			alert.setContentText("Are you sure to remove warehouse with id " + selected.getWarehouseId() + " ?");
			ButtonType res = alert.showAndWait().orElse(ButtonType.CANCEL);
			if (res == ButtonType.OK) {
				for (int i = 0; i < Main.products.size(); i++) {
					if (Main.products.get(i).getWarehouseId() == selected.getWarehouseId()) {
						Main.products.get(i).setWarehouseId(-1);
					}
				}
				Main.warehouses.remove(selected);
				String sql = "DELETE FROM warehouses WHERE warehouse_id = ?";
				try (PreparedStatement stmt = Main.conn.prepareStatement(sql)) {
					stmt.setInt(1, selected.getWarehouseId());
					stmt.executeUpdate();
				} catch (SQLException e1) {
					Main.notValidAlert("Error", e1.getMessage());
					return;
				}
				Main.validAlert("Warehouse Removed", "Warehouse removed successfully");
			}
		});

		VBox all = new VBox(10, searchBox, buttons, warehouseTable);
		all.setAlignment(Pos.CENTER);
		Scene scene = new Scene(all, 800, 700);
		scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		Stage stage = new Stage();
		stage.setTitle("Warehouse Stage");
		stage.setScene(scene);
		stage.show();
	}
}
