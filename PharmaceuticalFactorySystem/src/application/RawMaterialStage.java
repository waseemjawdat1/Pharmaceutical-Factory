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
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class RawMaterialStage {

	static MyTableView<RawMaterial> materialTable;
	private TableColumn<RawMaterial, Integer> materialId, unit;
	private TableColumn<RawMaterial, String> name, supplierId;
	private TableColumn<RawMaterial, Double> price;

	private Button add, update, remove;
	private TextField search;
	private Label searchByMaterialName;
	private VBox all;

	public RawMaterialStage() {

		materialTable = new MyTableView<>();
		materialId = materialTable.createStyledColumn("Material Id", "materialId", Integer.class);
		name = materialTable.createStyledColumn("Name", "name");
		unit = materialTable.createStyledColumn("Unit", "unit", Integer.class);
		supplierId = new TableColumn<>("Supplier Id");
		supplierId.setCellValueFactory(cellData -> {
			RawMaterial m = cellData.getValue();
			int sid = m.getSupplierId();
			if (sid == -1) {
				return new SimpleStringProperty("Unassigned");
			}
			return new SimpleStringProperty(sid + "");
		});
		price = materialTable.createStyledColumn("Price", "price", Double.class);
		materialTable.getColumns().addAll(materialId, name, unit, supplierId, price);

		materialTable.setItems(Main.materials);
		materialTable.setColumnResizePolicy(materialTable.CONSTRAINED_RESIZE_POLICY);
		materialTable.setMinHeight(500);
		materialTable.setMaxWidth(950);

		add = new MyButton("➕ Add", 2);
		update = new MyButton("✎ Edit", 2);
		remove = new MyButton("➖ Remove", 2);

		HBox buttons = new HBox(10, add, update, remove);
		buttons.setAlignment(Pos.CENTER);

		searchByMaterialName = new MyLabel("Search by Material Name:");
		search = new MyTextField();
		HBox searchBox = new HBox(10, searchByMaterialName, search);
		searchBox.setAlignment(Pos.CENTER);

		search.setOnKeyTyped(e -> {
			String searchS = search.getText();
			if (searchS == null || searchS.isEmpty()) {
				materialTable.setItems(Main.materials);
				return;
			}
			ObservableList<RawMaterial> temp = FXCollections.observableArrayList();
			for (int i = 0; i < Main.materials.size(); i++) {
				String matName = Main.materials.get(i).getName();
				if (matName.toLowerCase().startsWith(searchS.toLowerCase())) {
					temp.add(Main.materials.get(i));
				}
			}
			if (temp.size() > 0) {
				materialTable.setItems(temp);
			} else {
				materialTable.setItems(Main.materials);
			}
		});

		add.setOnAction(e -> {
			Label title = new MyLabel("Add Material", 1);
			Label nameL = new MyLabel("Name:");
			TextField nameTF = new MyTextField();
			Label unitL = new MyLabel("Unit:");
			TextField unitTF = new MyTextField();
			Label supplierL = new MyLabel("Supplier Id:");
			TextField supplierTF = new MyTextField();
			Label priceL = new MyLabel("Price:");
			TextField priceTF = new MyTextField();

			GridPane g = new GridPane();
			g.addColumn(0, nameL, unitL, supplierL,priceL);
			g.addColumn(1, nameTF, unitTF, supplierTF,priceTF);
			g.setVgap(5);
			g.setHgap(5);
			g.setAlignment(Pos.CENTER);

			Button addBtn = new MyButton("Add", 2);
			Button clear = new MyButton("Clear", 2);
			HBox buttons1 = new HBox(10, addBtn, clear);
			buttons1.setAlignment(Pos.CENTER);

			VBox all = new VBox(10, title, g, buttons1);
			all.setAlignment(Pos.CENTER);

			Stage addStage = new Stage();
			addStage.setScene(new Scene(all, 550, 400));
			addStage.setTitle("Add Material");
			addStage.show();

			clear.setOnAction(e1 -> {
				nameTF.clear();
				unitTF.clear();
				supplierTF.clear();
			});

			addBtn.setOnAction(e1 -> {
				String nameS = nameTF.getText();
				String unitS = unitTF.getText();
				String priceS = priceTF.getText();
				String supplierS = supplierTF.getText();
				if (nameS == null || nameS.isEmpty()) {
					Main.notValidAlert("Not Valid Input", "Material name is empty");
					return;
				}
				if (unitS == null || unitS.isEmpty()) {
					Main.notValidAlert("Not Valid Input", "Unit is empty");
					return;
				}
				int unit = 0;
				try {
					unit = Integer.parseInt(unitS);
				    if (unit < 0) throw new NumberFormatException();

				} catch (Exception ex) {
					Main.notValidAlert("Not Valid Input", "Unit must be a non-negative number");
					return;
				}
				if (priceS == null || priceS.isEmpty()) {
				    Main.notValidAlert("Not Valid Input", "Price is empty");
				    return;
				}
				
				
				double price = 0;
				try {
				    price = Double.parseDouble(priceS);
				    if (price < 0) throw new NumberFormatException();
				} catch (Exception ex) {
				    Main.notValidAlert("Not Valid Input", "Price must be a non-negative number");
				    return;
				}
				
				
				int supplierId = -1;
				if (!supplierS.equalsIgnoreCase("null")) {
					try {
						supplierId = Integer.parseInt(supplierS);
					} catch (Exception ex) {
						Main.notValidAlert("Not Valid Input", "Supplier Id must be a number or null");
						return;
					}
				}
				boolean isExist = false;
				for (int i = 0; i < Main.suppliers.size(); i++) {
					if (Main.suppliers.get(i).getSupplierId() == supplierId) {
						isExist = true;
						break;
					}
				}
				if (supplierId != -1 && !isExist) {
					Main.notValidAlert("Not Valid Input", "Supplier id is not exist in system");
					return;
				}
				try {
					RawMaterial r = new RawMaterial(nameS, unit, supplierId , price);
					Main.materials.add(r);
					Main.validAlert("Material Added", "Material added to system successfully");
					addStage.close();
				} catch (SQLException ex) {
					Main.notValidAlert("Error", ex.getMessage());
				}
			});
		});

		remove.setOnAction(e -> {
			RawMaterial selected = materialTable.getSelectionModel().getSelectedItem();
			if (selected == null) {
				Main.notValidAlert("Nothing selected", "Select material row to remove");
				return;
			}
			Alert remove = new Alert(AlertType.CONFIRMATION);
			remove.setTitle("Remove Material");
			remove.setHeaderText(null);
			remove.setContentText("Are you sure to remove material with id " + selected.getMaterialId() + " ?");
			ButtonType res = remove.showAndWait().orElse(ButtonType.CANCEL);
			if (res == ButtonType.OK) {
				Main.materials.remove(selected);
				String sql = "UPDATE raw_materials SET active = FALSE WHERE material_id = ?";
				try (PreparedStatement stmt = Main.conn.prepareStatement(sql)) {
					stmt.setInt(1, selected.getMaterialId());
					stmt.executeUpdate();
				} catch (SQLException ex) {
					Main.notValidAlert("Error", ex.getMessage());
					return;
				}
				Main.validAlert("Material Removed", "Material removed successfully");
			}
		});

		update.setOnAction(e -> {
			RawMaterial selected = materialTable.getSelectionModel().getSelectedItem();
			if (selected == null) {
				Main.notValidAlert("Nothing selected", "Select material row to update");
				return;
			}
			Label title = new MyLabel("Update Material", 1);
			Label nameL = new MyLabel("Name:");
			TextField nameTF = new MyTextField(selected.getName());
			Label unitL = new MyLabel("Unit:");
			TextField unitTF = new MyTextField(selected.getUnit() + "");
			Label supplierL = new MyLabel("Supplier Id:");
			Label priceL = new MyLabel("Price:");
			TextField priceTF = new MyTextField(selected.getPrice() + "");

			String sidStr = null;
			if (selected.getSupplierId() == -1)
				sidStr = "null";
			else
				sidStr = selected.getSupplierId() + "";
			TextField supplierTF = new MyTextField(sidStr);

			GridPane g = new GridPane();
			g.addColumn(0, nameL, unitL, supplierL,priceL);
			g.addColumn(1, nameTF, unitTF, supplierTF,priceTF);
			g.setVgap(5);
			g.setHgap(5);
			g.setAlignment(Pos.CENTER);

			Button updateBtn = new MyButton("Update", 2);
			Button clearBtn = new MyButton("Clear", 2);
			HBox buttons1 = new HBox(10, updateBtn, clearBtn);
			buttons1.setAlignment(Pos.CENTER);

			VBox all = new VBox(10, title, g, buttons1);
			all.setAlignment(Pos.CENTER);

			Stage updateStage = new Stage();
			updateStage.setScene(new Scene(all, 550, 400));
			updateStage.setTitle("Update Material");
			updateStage.show();

			clearBtn.setOnAction(e1 -> {
				nameTF.clear();
				unitTF.clear();
				supplierTF.clear();
			});

			updateBtn.setOnAction(e1 -> {
				String nameS = nameTF.getText();
				String unitS = unitTF.getText();
				String priceS = priceTF.getText();
				String supplierS = supplierTF.getText();
				if (nameS == null || nameS.isEmpty()) {
					Main.notValidAlert("Not Valid Input", "Material name is empty");
					return;
				}
				
				if (unitS == null || unitS.isEmpty()) {
					Main.notValidAlert("Not Valid Input", "Unit is empty");
					return;
				}
				
				int unit = 0;
				try {
					unit = Integer.parseInt(unitS);
				    if (unit < 0) throw new NumberFormatException();

				} catch (Exception ex) {
					Main.notValidAlert("Not Valid Input", "Unit must be a non-negative number");
					return;
				}
				
				if (priceS == null || priceS.isEmpty()) {
				    Main.notValidAlert("Not Valid Input", "Price is empty");
				    return;
				}
				double price = 0;
				try {
				    price = Double.parseDouble(priceS);
				    if (price < 0) throw new NumberFormatException();
				} catch (Exception ex) {
				    Main.notValidAlert("Not Valid Input", "Price must be a non-negative number");
				    return;
				}

				int supplierId = -1;
				if (!supplierS.equalsIgnoreCase("null")) {
					try {
						supplierId = Integer.parseInt(supplierS);
					} catch (Exception ex) {
						Main.notValidAlert("Not Valid Input", "Supplier Id must be a number or null");
						return;
					}
				}
				boolean isExist = false;
				for (int i = 0; i < Main.suppliers.size(); i++) {
					if (Main.suppliers.get(i).getSupplierId() == supplierId) {
						isExist = true;
						break;
					}
				}
				if (supplierId != -1 && !isExist) {
					Main.notValidAlert("Not Valid Input", "Supplier id is not exist in system");
					return;
				}
				try {
					selected.updateMaterial(nameS, unit, supplierId,price);
					materialTable.refresh();
					Main.validAlert("Material Updated", "Material updated successfully");
					updateStage.close();
				} catch (SQLException ex) {
					Main.notValidAlert("Error", ex.getMessage());
				}
			});
		});

		all = new VBox(10, searchBox, buttons, materialTable);
		all.setAlignment(Pos.CENTER);

		Scene scene = new Scene(all, 1050, 750);
		scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		Stage stage = new Stage();
		stage.setTitle("Raw Material Stage");
		stage.setScene(scene);
		stage.show();
	}

}
