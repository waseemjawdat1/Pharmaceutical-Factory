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

public class CustomerStage {
	static MyTableView<Customer> customerTable;
	private TableColumn<Customer, Integer> id;
	private TableColumn<Customer, String> name, email, phone, address;
	private Button add, update, remove;

	public CustomerStage() {
		customerTable = new MyTableView<>();
		id = customerTable.createStyledColumn("ID", "customerId", Integer.class);
		name = customerTable.createStyledColumn("Name", "name");
		email = customerTable.createStyledColumn("Email", "email");
		phone = customerTable.createStyledColumn("Phone", "phone");
		address = customerTable.createStyledColumn("Address", "address");

		customerTable.getColumns().addAll(id, name, email, phone, address);
		customerTable.setItems(Main.customers);
		customerTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		customerTable.setMinHeight(500);
		customerTable.setMaxWidth(900);
		
		Label searchL = new MyLabel("Search Name");
		TextField searchTF = new MyTextField();
		Button search = new MyButton("🔍 Search", 2);
		
		HBox searchBox = new HBox(10, searchL, searchTF, search);
		searchBox.setAlignment(Pos.CENTER);
		
		search.setOnAction(e -> {
			String nameS = searchTF.getText();
			if (nameS == null || nameS.isEmpty()) {
				customerTable.setItems(Main.customers);
				return;
			}
			ObservableList<Customer> temp = FXCollections.observableArrayList();
			for (int i = 0; i < Main.customers.size(); i++) {
				if (Main.customers.get(i).getName().toLowerCase().contains(nameS.toLowerCase())) {
					temp.add(Main.customers.get(i));
				}
			}
			if (temp.size() > 0)
			customerTable.setItems(temp);
			else customerTable.setItems(Main.customers);
		});
		
		add = new MyButton("➕ Add", 2);
		update = new MyButton("✎ Edit", 2);
		remove = new MyButton("➖ Remove", 2);

		HBox buttons = new HBox(10, add, update, remove);
		buttons.setAlignment(Pos.CENTER);

		add.setOnAction(e -> {
			Label title = new MyLabel("Add New Customer", 1);
			Label name = new MyLabel("Name");
			Label email = new MyLabel("Email");
			Label phone = new MyLabel("Phone");
			Label address = new MyLabel("Address");

			TextField name1 = new MyTextField();
			TextField email1 = new MyTextField();
			TextField phone1 = new MyTextField();
			TextField address1 = new MyTextField();

			GridPane g = new GridPane();
			g.addColumn(0, name, email, phone, address);
			g.addColumn(1, name1, email1, phone1, address1);
			g.setHgap(5);
			g.setVgap(5);
			g.setAlignment(Pos.CENTER);

			Button addBtn = new MyButton("Add", 2);
			Button clear = new MyButton("Clear", 2);
			HBox buttons1 = new HBox(10, addBtn, clear);
			buttons1.setAlignment(Pos.CENTER);

			VBox all = new VBox(10, title, g, buttons1);
			all.setAlignment(Pos.CENTER);
			Scene s = new Scene(all, 500, 400);
			Stage st = new Stage();
			st.setScene(s);
			st.setTitle("Add Customer");
			st.show();

			clear.setOnAction(e1 -> {
				name1.clear();
				email1.clear();
				phone1.clear();
				address1.clear();
			});

			addBtn.setOnAction(e1 -> {
				String nameS = name1.getText();
				String emailS = email1.getText();
				String phoneS = phone1.getText();
				String addressS = address1.getText();

				if (nameS == null || nameS.isEmpty()) {
					Main.notValidAlert("Not Valid", "Name is empty");
					return;
				}
				if (emailS == null || emailS.isEmpty()) {
					Main.notValidAlert("Not Valid", "Email is empty");
					return;
				}
				if (phoneS == null || phoneS.isEmpty()) {
					Main.notValidAlert("Not Valid", "Phone is empty");
					return;
				}
				if (addressS == null || addressS.isEmpty()) {
					Main.notValidAlert("Not Valid", "Address is empty");
					return;
				}

				boolean isDigit = true;
				for (int i = 0; i < phoneS.length(); i++) {
					char ch = phoneS.charAt(i);
					if (!Character.isDigit(ch)) {
						isDigit = false;
						break;
					}
				}
				if (!isDigit) {
					Main.notValidAlert("Not Valid", "Phone must contain only digits");
					return;
				}

				Customer c;
				try {
					c = new Customer(nameS, emailS, phoneS, addressS);
				} catch (SQLException e2) {
					Main.notValidAlert("Error", e2.getMessage());
					return;
				}
				Main.customers.add(c);
				Main.validAlert("Customer Added", "Customer added successfully");
				st.close();
			});
		});

		update.setOnAction(e -> {
			Customer selected = customerTable.getSelectionModel().getSelectedItem();
			if (selected == null) {
				Main.notValidAlert("Nothing Selected", "Select a customer to update");
				return;
			}
			Label title = new MyLabel("Update Customer", 1);
			Label name = new MyLabel("Name");
			Label email = new MyLabel("Email");
			Label phone = new MyLabel("Phone");
			Label address = new MyLabel("Address");

			TextField name1 = new MyTextField();
			name1.setText(selected.getName());

			TextField email1 = new MyTextField();
			email1.setText(selected.getEmail());

			TextField phone1 = new MyTextField();
			phone1.setText(selected.getPhone());

			TextField address1 = new MyTextField();
			address1.setText(selected.getAddress());

			GridPane g = new GridPane();
			g.addColumn(0, name, email, phone, address);
			g.addColumn(1, name1, email1, phone1, address1);
			g.setHgap(5);
			g.setVgap(5);
			g.setAlignment(Pos.CENTER);

			Button updateBtn = new MyButton("Update", 2);
			Button clear = new MyButton("Clear", 2);
			HBox buttons1 = new HBox(10, updateBtn, clear);
			buttons1.setAlignment(Pos.CENTER);

			VBox all = new VBox(10, title, g, buttons1);
			all.setAlignment(Pos.CENTER);
			Scene s = new Scene(all, 500, 400);
			Stage st = new Stage();
			st.setScene(s);
			st.setTitle("Update Customer");
			st.show();

			clear.setOnAction(e1 -> {
				name1.clear();
				email1.clear();
				phone1.clear();
				address1.clear();
			});

			updateBtn.setOnAction(e1 -> {
				String nameS = name1.getText();
				String emailS = email1.getText();
				String phoneS = phone1.getText();
				String addressS = address1.getText();

				if (nameS == null || nameS.isEmpty()) {
					Main.notValidAlert("Not Valid", "Name is empty");
					return;
				}
				if (emailS == null || emailS.isEmpty()) {
					Main.notValidAlert("Not Valid", "Email is empty");
					return;
				}
				if (phoneS == null || phoneS.isEmpty()) {
					Main.notValidAlert("Not Valid", "Phone is empty");
					return;
				}
				if (addressS == null || addressS.isEmpty()) {
					Main.notValidAlert("Not Valid", "Address is empty");
					return;
				}

				boolean isDigit = true;
				for (int i = 0; i < phoneS.length(); i++) {
					char ch = phoneS.charAt(i);
					if (!Character.isDigit(ch)) {
						isDigit = false;
						break;
					}
				}
				if (!isDigit) {
					Main.notValidAlert("Not Valid", "Phone must contain only digits");
					return;
				}

				try {
					selected.updateCustomer(nameS, emailS, phoneS, addressS);
					customerTable.refresh();
				} catch (SQLException e2) {
					Main.notValidAlert("Error", e2.getMessage());
					return;
				}
				Main.validAlert("Customer Updated", "Customer updated successfully");
				st.close();
			});
		});

		remove.setOnAction(e -> {
			Customer selected = customerTable.getSelectionModel().getSelectedItem();
			if (selected == null) {
				Main.notValidAlert("Nothing Selected", "Select a customer to remove");
				return;
			}
			Alert alert = new Alert(AlertType.CONFIRMATION);
			alert.setTitle("Remove Customer");
			alert.setHeaderText(null);
			alert.setContentText("Are you sure to remove customer with id " + selected.getCustomerId() + " ?");
			ButtonType res = alert.showAndWait().orElse(ButtonType.CANCEL);
			if (res == ButtonType.OK) {
				Main.customers.remove(selected);
				String sql = "DELETE FROM customers WHERE customer_id = ?";
				try (PreparedStatement stmt = Main.conn.prepareStatement(sql)) {
					stmt.setInt(1, selected.getCustomerId());
					stmt.executeUpdate();
				} catch (SQLException e1) {
					Main.notValidAlert("Error", e1.getMessage());
					return;
				}
				Main.validAlert("Customer Removed", "Customer removed successfully");
			}
		});

		VBox all = new VBox(10, searchBox,buttons, customerTable);
		all.setAlignment(Pos.CENTER);
		Scene scene = new Scene(all, 1000, 700);
		scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		Stage stage = new Stage();
		stage.setTitle("Customer Stage");
		stage.setScene(scene);
		stage.show();
	}
}
