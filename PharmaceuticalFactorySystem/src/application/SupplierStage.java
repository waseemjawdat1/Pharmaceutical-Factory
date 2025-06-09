package application;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javafx.beans.property.SimpleStringProperty;
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

public class SupplierStage {

	static MyTableView<Supplier> supplierTable;
	private TableColumn<Supplier, Integer> supplierId;
	private TableColumn<Supplier, String> name, email, phone, address;
	private Button add, update, remove;

	public SupplierStage() {
		supplierTable = new MyTableView<>();
		supplierId = supplierTable.createStyledColumn("Supplier Id", "supplierId", Integer.class);
		name = supplierTable.createStyledColumn("Name", "name");
		email = supplierTable.createStyledColumn("Email", "email");
		phone = supplierTable.createStyledColumn("Phone", "phone");
		address = supplierTable.createStyledColumn("Address", "address");

		supplierTable.getColumns().addAll(supplierId, name, email, phone, address);
		supplierTable.setItems(Main.suppliers);
		supplierTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		supplierTable.setMinHeight(500);
		supplierTable.setMaxWidth(800);

		add = new MyButton("➕ Add", 2);
		update = new MyButton("✎ Edit", 2);
		remove = new MyButton("➖ Remove", 2);

		add.setOnAction(e -> {
			Label title = new MyLabel("Add Supplier", 1);
			Label nameL = new MyLabel("Name:");
			TextField nameTF = new MyTextField();
			Label emailL = new MyLabel("Email:");
			TextField emailTF = new MyTextField();
			Label phoneL = new MyLabel("Phone:");
			TextField phoneTF = new MyTextField();
			Label addressL = new MyLabel("Address:");
			TextField addressTF = new MyTextField();

			GridPane g = new GridPane();
			g.addColumn(0, nameL, emailL, phoneL, addressL);
			g.addColumn(1, nameTF, emailTF, phoneTF, addressTF);
			g.setVgap(5);
			g.setHgap(5);
			g.setAlignment(Pos.CENTER);

			Button addBtn = new MyButton("Add", 2);
			Button clearBtn = new MyButton("Clear", 2);
			HBox buttons1 = new HBox(10, addBtn, clearBtn);
			buttons1.setAlignment(Pos.CENTER);

			VBox all1 = new VBox(10, title, g, buttons1);
			all1.setAlignment(Pos.CENTER);

			Stage addStage = new Stage();
			addStage.setScene(new Scene(all1, 500, 400));
			addStage.setTitle("Add Supplier");
			addStage.show();

			clearBtn.setOnAction(e1 -> {
				nameTF.clear();
				emailTF.clear();
				phoneTF.clear();
				addressTF.clear();
			});

			addBtn.setOnAction(e1 -> {
				String nameS = nameTF.getText();
				String emailS = emailTF.getText();
				String phoneS = phoneTF.getText();
				String addressS = addressTF.getText();

				if (nameS == null || nameS.isEmpty()) {
					Main.notValidAlert("Not Valid Input", "Supplier name is empty");
					return;
				}

				if (emailS == null || emailS.isEmpty()) {
					Main.notValidAlert("Not Valid Input", "Email is empty");
					return;
				}

				for (int i = 0; i < Main.suppliers.size(); i++) {
					if (Main.suppliers.get(i).getEmail().equals(emailS)) {
						Main.notValidAlert("Invalid input", "This email already exists, please enter another email!");
						return;
					}
				}

				if (phoneS == null || phoneS.isEmpty()) {
					Main.notValidAlert("Not Valid Input", "Phone is empty");
					return;
				}

				for (int i = 0; i < Main.suppliers.size(); i++) {
					if (Main.suppliers.get(i).getPhone().equals(phoneS)) {
						Main.notValidAlert("Invalid input",
								"This phone number already exists, please enter another phone number!");
						return;
					}
				}

				if (addressS == null || addressS.isEmpty()) {
					Main.notValidAlert("Not Valid Input", "Address is empty");
					return;
				}

				try {
					Supplier s = new Supplier(nameS, emailS, phoneS, addressS);
					Main.suppliers.add(s);
					Main.validAlert("Supplier Added", "Supplier added to system successfully");
					addStage.close();
				} catch (SQLException ex) {
					Main.notValidAlert("Error", ex.getMessage());
				}
			});
		});

		update.setOnAction(e -> {
			Supplier selected = supplierTable.getSelectionModel().getSelectedItem();
			if (selected == null) {
				Main.notValidAlert("Nothing selected", "Select row of supplier you want to update");
				return;
			}

			Label title = new MyLabel("Update Supplier", 1);
			Label name1L = new MyLabel("Name:");
			TextField name1TF = new MyTextField(selected.getName());
			Label email1L = new MyLabel("Email:");
			TextField email1TF = new MyTextField(selected.getEmail());
			Label phone1L = new MyLabel("Phone:");
			TextField phone1TF = new MyTextField(selected.getPhone());
			Label address1L = new MyLabel("Address:");
			TextField address1TF = new MyTextField(selected.getAddress());

			GridPane g = new GridPane();
			g.addColumn(0, name1L, email1L, phone1L, address1L);
			g.addColumn(1, name1TF, email1TF, phone1TF, address1TF);
			g.setVgap(5);
			g.setHgap(5);
			g.setAlignment(Pos.CENTER);

			Button updateBtn = new MyButton("Update", 2);
			Button clearBtn = new MyButton("Clear", 2);
			HBox buttons1 = new HBox(10, updateBtn, clearBtn);
			buttons1.setAlignment(Pos.CENTER);

			VBox all1 = new VBox(10, title, g, buttons1);
			all1.setAlignment(Pos.CENTER);

			Stage updateStage = new Stage();
			updateStage.setScene(new Scene(all1, 500, 400));
			updateStage.setTitle("Update Supplier");
			updateStage.show();

			clearBtn.setOnAction(e1 -> {
				name1TF.clear();
				email1TF.clear();
				phone1TF.clear();
				address1TF.clear();
			});

			updateBtn.setOnAction(e1 -> {
				String nameS = name1TF.getText();
				String emailS = email1TF.getText();
				String phoneS = phone1TF.getText();
				String addressS = address1TF.getText();

				if (nameS == null || nameS.isEmpty()) {
					Main.notValidAlert("Not Valid Input", "Supplier name is empty");
					return;
				}

				if (emailS == null || emailS.isEmpty()) {
					Main.notValidAlert("Not Valid Input", "Email is empty");
					return;
				}

				for (int i = 0; i < Main.suppliers.size(); i++) {
					if (Main.suppliers.get(i).getEmail().equals(emailS)) {
						Main.notValidAlert("Invalid input", "This email already exists, please enter another email!");
						return;
					}
				}

				if (phoneS == null || phoneS.isEmpty()) {
					Main.notValidAlert("Not Valid Input", "Phone is empty");
					return;
				}

				for (int i = 0; i < Main.suppliers.size(); i++) {
					if (Main.suppliers.get(i).getPhone().equals(phoneS)) {
						Main.notValidAlert("Invalid input",
								"This phone number already exists, please enter another phone number!");
						return;
					}
				}

				if (addressS == null || addressS.isEmpty()) {
					Main.notValidAlert("Not Valid Input", "Address is empty");
					return;
				}

				try {
					selected.updateSupplier(nameS, emailS, phoneS, addressS);
					supplierTable.refresh();
					Main.validAlert("Supplier Updated", "Supplier updated successfully");
					updateStage.close();
				} catch (SQLException ex) {
					Main.notValidAlert("Error", ex.getMessage());
				}
			});
		});

		remove.setOnAction(e -> {
			Supplier selected = supplierTable.getSelectionModel().getSelectedItem();
			if (selected == null) {
				Main.notValidAlert("Nothing selected", "Select row of supplier you want to remove");
				return;
			}

			Alert remove = new Alert(AlertType.CONFIRMATION);
			remove.setTitle("Remove Supplier");
			remove.setHeaderText(null);
			remove.setContentText("Are you sure to remove supplier with id " + selected.getSupplierId() + " ?");
			ButtonType res = remove.showAndWait().orElse(ButtonType.CANCEL);
			if (res == ButtonType.OK) {
				for (int i = 0; i < Main.materials.size(); i++) {
					if (Main.materials.get(i).getSupplierId() == selected.getSupplierId()) {
						Main.materials.get(i).setSupplierId(-1);
					}
				}
				Main.suppliers.remove(selected);
				String removeSql = "UPDATE suppliers SET active = FALSE WHERE supplier_id = ?";
				try (PreparedStatement stmt = Main.conn.prepareStatement(removeSql)) {
					stmt.setInt(1, selected.getSupplierId());
					stmt.executeUpdate();
				} catch (SQLException e1) {
					Main.notValidAlert("Not Valid", e1.getMessage());
					return;
				}
				Main.validAlert("Supplier Removed", "Supplier removed successfully");
			}
		});

		HBox buttons = new HBox(10, add, update, remove);
		buttons.setAlignment(Pos.CENTER);

		VBox all = new VBox(10, buttons, supplierTable);
		all.setAlignment(Pos.CENTER);

		Scene scene = new Scene(all, 1000, 700);
		scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		Stage stage = new Stage();
		stage.setTitle("Supplier Stage");
		stage.setScene(scene);
		stage.show();
	}
}