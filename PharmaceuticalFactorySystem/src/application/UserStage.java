package application;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.mysql.cj.conf.BooleanPropertyDefinition.AllowableValues;

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
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class UserStage {
	static MyTableView<User> userTable;
	private TableColumn<User, Integer> userId, empId;
	private TableColumn<User, String> userName, password, role;
	private Button add, update, remove;
	private TextField search;
	private Label searchByUserName;
	private VBox all;

	public UserStage() {
		userTable = new MyTableView<User>();
		userId = userTable.createStyledColumn("User Id", "userId", Integer.class);
		userName = userTable.createStyledColumn("User Name", "userName");
		password = userTable.createStyledColumn("Password", "password");
		role = userTable.createStyledColumn("Role", "role");
		empId = userTable.createStyledColumn("Employee Id", "employeeId", Integer.class);

		userTable.getColumns().addAll(userId, userName, password, role, empId);
		userTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		userTable.setMinHeight(500);
		userTable.setMaxWidth(700);
		userTable.setItems(Main.users);

		add = new MyButton("➕ Add", 2);
		add.setOnAction(e -> {
			Label addUserL = new MyLabel("Add User", 1);
			Label userNameL = new MyLabel("User Name : ");
			TextField userNameTF = new MyTextField();
			Label passwordL = new MyLabel("Passowrd : ");
			TextField passwordTF = new MyTextField();
			Label roleL = new MyLabel("Role : ");
			MyComboBox<String> roleCB = new MyComboBox<>("admin", "sales manager", "purchase manager");
			Label employeeIdL = new MyLabel("Employee Id : ");
			TextField employeeIdTF = new MyTextField();
			GridPane g = new GridPane();
			g.addColumn(0, userNameL, passwordL, roleL, employeeIdL);
			g.addColumn(1, userNameTF, passwordTF, roleCB, employeeIdTF);
			g.setVgap(5);
			g.setHgap(5);
			g.setAlignment(Pos.CENTER);
			Button clear = new MyButton("Clear", 2);
			Button add = new MyButton("Add", 2);
			HBox buttons = new HBox(10, add, clear);
			buttons.setAlignment(Pos.CENTER);
			VBox all = new VBox(10, addUserL, g, buttons);
			all.setAlignment(Pos.CENTER);
			Scene addScene = new Scene(all, 600, 600);
			Stage addStage = new Stage();
			addStage.setTitle("Add User");
			addStage.setScene(addScene);
			addStage.show();
			add.setOnAction(ee -> {
				String userName = userNameTF.getText();
				if (userName == null || userName.isEmpty()) {
					Main.notValidAlert("Not Valid Input", "User name is empty");
					return;
				}
				for (int i = 0; i < Main.users.size(); i++) {
					if (Main.users.get(i).getUserName().equalsIgnoreCase(userName)) {
						Main.notValidAlert("Not Valid Input", "User name is exist");
						return;
					}
				}
				String password = passwordTF.getText();
				if (password == null || password.isEmpty()) {
					Main.notValidAlert("Not Valid Input", "Password cant be empty");
					return;
				}
				String role = roleCB.getValue();
				if (role == null || role.isEmpty()) {
					Main.notValidAlert("Not Valid Input", "Select role for user");
					return;
				}
				String employeeId = employeeIdTF.getText();
				if (employeeId == null || employeeId.isEmpty()) {
					Main.notValidAlert("Not Valid Input", "Employee id cant be empty");
					return;
				}
				int empId = -1;
				try {
					empId = Integer.parseInt(employeeId);
				} catch (Exception eee) {
					Main.notValidAlert("Not Valid Input", "Employee id must be a number");
					return;
				}
				boolean isExist = false;
				for (int i = 0; i < Main.employees.size(); i++) {
					if (Main.employees.get(i).getEmployeeId() == empId) {
						isExist = true;
						break;
					}
				}
				if (!isExist) {
					Main.notValidAlert("Not Valid Input", "Employee id is not exist");
					return;
				}
				try {
					User u = new User(userName, password, role, empId);
					Main.users.add(u);
					Main.validAlert("User Added", "User added to system successfully");
					addStage.close();
				} catch (SQLException e1) {
					Main.notValidAlert("Not Valid", e1.getMessage());
					return;
				}
			});
			clear.setOnAction(ee -> {
				userNameTF.clear();
				passwordTF.clear();
				roleCB.setValue(null);
				employeeIdTF.clear();
			});
		});
		update = new MyButton("✎ Update", 2);
		update.setOnAction(e -> {
			User selected = userTable.getSelectionModel().getSelectedItem();
			if (selected == null) {
				Main.notValidAlert("Nothing Selected", "You must select a row of user you want to update");
				return;
			}

			Label updateUserL = new MyLabel("Update User", 1);
			Label userNameL = new MyLabel("User Name : ");
			TextField userNameTF = new MyTextField();
			userNameTF.setText(selected.getUserName());
			Label passwordL = new MyLabel("Passowrd : ");
			TextField passwordTF = new MyTextField();
			passwordTF.setText(selected.getPassword());
			Label roleL = new MyLabel("Role : ");
			MyComboBox<String> roleCB = new MyComboBox<>("admin", "sales manager", "purchase manager");
			roleCB.setValue(selected.getRole());
			Label employeeIdL = new MyLabel("Employee Id : ");
			TextField employeeIdTF = new MyTextField();
			employeeIdTF.setText(selected.getEmployeeId() + "");
			GridPane g = new GridPane();
			g.addColumn(0, userNameL, passwordL, roleL, employeeIdL);
			g.addColumn(1, userNameTF, passwordTF, roleCB, employeeIdTF);
			g.setVgap(5);
			g.setHgap(5);
			g.setAlignment(Pos.CENTER);
			Button clear = new MyButton("Clear", 2);
			Button update = new MyButton("Update", 2);
			HBox buttons = new HBox(10, update, clear);
			buttons.setAlignment(Pos.CENTER);
			VBox all = new VBox(10, updateUserL, g, buttons);
			all.setAlignment(Pos.CENTER);
			Scene updateScene = new Scene(all, 600, 600);
			Stage updateStage = new Stage();
			updateStage.setTitle("Add User");
			updateStage.setScene(updateScene);
			updateStage.show();
			update.setOnAction(ee -> {
				String userName = userNameTF.getText();
				if (userName == null || userName.isEmpty()) {
					Main.notValidAlert("Not Valid Input", "User name is empty");
					return;
				}
				if (!userName.equalsIgnoreCase(selected.getUserName())) {
					for (int i = 0; i < Main.users.size(); i++) {
						if (Main.users.get(i).getUserName().equalsIgnoreCase(userName)) {
							Main.notValidAlert("Not Valid Input", "User name is exist");
							return;
						}
					}
				}
				String password = passwordTF.getText();
				if (password == null || password.isEmpty()) {
					Main.notValidAlert("Not Valid Input", "Password cant be empty");
					return;
				}
				String role = roleCB.getValue();
				if (role == null || role.isEmpty()) {
					Main.notValidAlert("Not Valid Input", "Select role for user");
					return;
				}
				String employeeId = employeeIdTF.getText();
				if (employeeId == null || employeeId.isEmpty()) {
					Main.notValidAlert("Not Valid Input", "Employee id cant be empty");
					return;
				}
				int empId = -1;
				try {
					empId = Integer.parseInt(employeeId);
				} catch (Exception eee) {
					Main.notValidAlert("Not Valid Input", "Employee id must be a number");
					return;
				}
				boolean isExist = false;
				for (int i = 0; i < Main.employees.size(); i++) {
					if (Main.employees.get(i).getEmployeeId() == empId) {
						isExist = true;
						break;
					}
				}
				if (!isExist) {
					Main.notValidAlert("Not Valid Input", "Employee id is not exist");
					return;
				}
				try {
					selected.setUserName(userName);
					selected.setPassword(password);
					selected.setEmployeeId(empId);
					selected.setRole(role);
					userTable.refresh();
					Main.validAlert("User Updated",
							"User with id " + selected.getUserId() + " was updated successfully");
					updateStage.close();
				} catch (SQLException e1) {
					Main.notValidAlert("Not Valid", e1.getMessage());
					return;
				}
			});
			clear.setOnAction(ee -> {
				userNameTF.clear();
				passwordTF.clear();
				roleCB.setValue(null);
				employeeIdTF.clear();
			});
		});
		remove = new MyButton("➖ Delete", 2);
		remove.setOnAction(e -> {
			User selected = userTable.getSelectionModel().getSelectedItem();
			if (selected == null) {
				Main.notValidAlert("Nothing Selected", "You must select a row of user you want to remove");
				return;
			}
			Alert remove = new Alert(AlertType.CONFIRMATION);
			remove.setTitle("Remove User");
			remove.setHeaderText(null);
			remove.setContentText("Are u sure to remove user with id " + selected.getUserId() + " ?");
			ButtonType res = remove.showAndWait().orElse(ButtonType.CANCEL);
			if (res == ButtonType.OK) {
				Main.users.remove(selected);
				String removeSql = "DELETE FROM users Where user_id = ?";
				try (PreparedStatement stmt = Main.conn.prepareStatement(removeSql)) {
					stmt.setInt(1, selected.getUserId());
					stmt.executeUpdate();
				} catch (SQLException e1) {
					Main.notValidAlert("Not Valid", e1.getMessage());
					return;
				}
				Main.validAlert("User Removed", "User with id " + selected.getUserId() + " was removed");
			}
		});
		HBox buttons = new HBox(10, add, update, remove);
		buttons.setAlignment(Pos.CENTER);

		searchByUserName = new MyLabel("Search By User Name : ");
		search = new MyTextField();

		HBox searchBox = new HBox(10, searchByUserName, search);
		searchBox.setAlignment(Pos.CENTER);

		search.setOnKeyTyped(e -> {
			String searchS = search.getText();
			if (searchS == null || searchS.isEmpty()) {
				userTable.setItems(Main.users);
				return;
			}
			ObservableList<User> temp = FXCollections.observableArrayList();
			for (int i = 0; i < Main.users.size(); i++) {
				if (Main.users.get(i).getUserName().toLowerCase().startsWith(searchS.toLowerCase())) {
					temp.add(Main.users.get(i));
				}
			}
			if (temp.size() > 0) {
				userTable.setItems(temp);
			} else {
				userTable.setItems(Main.users);

			}

		});
		all = new VBox(10, searchBox, buttons, userTable);
		all.setAlignment(Pos.CENTER);

		// Scene scene = new Scene(all, 800, 700);
		// scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());

		// Stage stage = new Stage();
		/// stage.setScene(scene);
		// stage.setTitle("User");
		// stage.show();
	}

	public TableView<User> getUserTable() {
		return userTable;
	}

	public TableColumn<User, Integer> getUserId() {
		return userId;
	}

	public TableColumn<User, Integer> getEmpId() {
		return empId;
	}

	public TableColumn<User, String> getUserName() {
		return userName;
	}

	public TableColumn<User, String> getPassword() {
		return password;
	}

	public TableColumn<User, String> getRole() {
		return role;
	}

	public Button getAdd() {
		return add;
	}

	public Button getUpdate() {
		return update;
	}

	public Button getRemove() {
		return remove;
	}

	public TextField getSearch() {
		return search;
	}

	public Label getSearchByUserName() {
		return searchByUserName;
	}

	public VBox getAll() {
		return all;
	}

}
