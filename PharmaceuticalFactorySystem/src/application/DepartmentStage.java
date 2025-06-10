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

public class DepartmentStage {
	static MyTableView<Department> deptTable;
	private TableColumn<Department, Integer> deptId;
	private TableColumn<Department, String> name, managerId;
	private Button add, update, remove;
	private VBox all;

	public DepartmentStage() {
		deptTable = new MyTableView<>();
		deptId = deptTable.createStyledColumn("Department Id", "deptId", Integer.class);
		name = deptTable.createStyledColumn("Name", "deptName");
		managerId = deptTable.createStyledColumn("Manager Id", "managerId");
		managerId.setCellValueFactory(cellData -> {
			Department dept = cellData.getValue();
			Integer managerId = dept.getManagerId();
			if (managerId == -1) {
				return new SimpleStringProperty("Unassigned");
			} else {
				return new SimpleStringProperty(dept.getManagerId() + "");
			}
		});
		deptTable.getColumns().addAll(deptId, name, managerId);
		deptTable.setItems(Main.departments);
		deptTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		deptTable.setMinHeight(500);
		deptTable.setMaxWidth(700);

		add = new MyButton("➕ Add ", 2);
		update = new MyButton("✎ Update ", 2);
		remove = new MyButton("➖ Delete ", 2);

		HBox buttons = new HBox(10, update, remove);
		buttons.setAlignment(Pos.CENTER);
		Label deptNameL = new MyLabel("Dept Name");
		TextField deptNameTF = new MyTextField();
		HBox addBox = new HBox(10, deptNameL, deptNameTF, add);
		addBox.setAlignment(Pos.CENTER);
		add.setOnAction(e -> {
			String deptNameS = deptNameTF.getText();
			if (deptNameS == null || deptNameS.isEmpty()) {
				Main.notValidAlert("Not Valid Input", "Department name is empty");
				return;
			}
			boolean isExist = false;
			for (int i = 0; i < Main.departments.size(); i++) {
				if (Main.departments.get(i).getDeptName().equalsIgnoreCase(deptNameS)) {
					isExist = true;
					break;
				}
			}
			if (isExist) {
				Main.notValidAlert("Not Valid Input", "there is department with name : " + deptNameS);
				return;
			}
			Department d;
			try {
				d = new Department(deptNameS, -1);
			} catch (SQLException e1) {
				Main.notValidAlert("Error", e1.getMessage());
				return;
			}
			Main.departments.add(d);
			Main.validAlert("Department Added !", "Department added to system successuflly");
			deptNameTF.clear();
		});

		update.setOnAction(e -> {
			Department selected = deptTable.getSelectionModel().getSelectedItem();
			if (selected == null) {
				Main.notValidAlert("Nohting selected", "Select row of department you want to update");
				return;
			}
			Label title = new MyLabel("Update Department", 1);
			Label deptName = new MyLabel("Department Name : ");
			TextField deptName1 = new MyTextField();
			deptName1.setText(selected.getDeptName());
			Label managerId = new MyLabel("Manager Id : ");
			TextField managerId1 = new MyTextField();
			if (selected.getManagerId() == -1) {
				managerId1.setText("null");
			} else
				managerId1.setText(selected.getManagerId() + "");
			String getDeptEmpCount = "SELECT COUNT(*) FROM employees WHERE department_id = ?";
			try (PreparedStatement stmt = Main.conn.prepareStatement(getDeptEmpCount)) {
				stmt.setInt(1, selected.getDeptId());
				ResultSet rs = stmt.executeQuery();
				if (rs.next()) {
					int count = rs.getInt(1);
					if (count == 0)
						managerId1.setEditable(false);
				}
			} catch (SQLException e3) {
				e3.printStackTrace();
			}
			GridPane g = new GridPane();
			g.addColumn(0, deptName, managerId);
			g.addColumn(1, deptName1, managerId1);
			g.setVgap(5);
			g.setHgap(5);
			g.setAlignment(Pos.CENTER);

			Button update = new MyButton("Update", 2);
			Button clear = new MyButton("Clear", 2);
			HBox buttons1 = new HBox(10, update, clear);
			buttons1.setAlignment(Pos.CENTER);
			VBox all1 = new VBox(10, title, g, buttons1);
			all1.setAlignment(Pos.CENTER);
			Scene updateDeptScene = new Scene(all1, 400, 400);
			Stage updateDeptStage = new Stage();
			updateDeptStage.setScene(updateDeptScene);
			updateDeptStage.setTitle("Update Department");
			updateDeptStage.show();

			clear.setOnAction(e1 -> {
				deptName1.clear();
				if (managerId1.isEditable())
					managerId1.clear();
			});
			update.setOnAction(e1 -> {
				String deptNameS = deptName1.getText();
				if (deptNameS == null || deptNameS.isEmpty()) {
					Main.notValidAlert("Not Valid Input", "Department number empty");
					return;
				}
				String managerIdS = managerId1.getText();
				if (managerIdS == null || managerIdS.isEmpty()) {
					Main.notValidAlert("Not Valid Input", "Manager id is empty");
					return;
				}

				int managerIdInt = -1;
				if (!managerIdS.equalsIgnoreCase("null")) {
					try {
						managerIdInt = Integer.parseInt(managerIdS.trim());
					} catch (Exception e2) {
						Main.notValidAlert("Not Valid Input", "Manager Id must be a number");
						return;
					}
					Employee isExist = null;
					for (int i = 0; i < Main.employees.size(); i++) {
						if (Main.employees.get(i).getEmployeeId() == managerIdInt) {
							isExist = Main.employees.get(i);
							break;
						}
					}
					if (isExist == null) {
						Main.notValidAlert("Not Valid Input", "No employee with this id in system");
						return;
					}
					if (isExist.getDepartmentId() != selected.getDeptId()) {
						Main.notValidAlert("Not Valid Input", "Manager cant belongs to another department");
						return;
					}
				}
				try {
					selected.updateDepartment(deptNameS, managerIdInt);
					deptTable.refresh();
				} catch (SQLException e2) {
					Main.notValidAlert("Error", e2.getMessage());
					return;
				}
				Main.validAlert("Department Updated",
						"Department with id " + selected.getDeptId() + " was updated successfully");
				updateDeptStage.close();
			});
		});

		remove.setOnAction(e -> {
			Department selected = deptTable.getSelectionModel().getSelectedItem();
			if (selected == null) {
				Main.notValidAlert("Nohting selected", "Select row of department you want to remove");
				return;
			}
			Alert remove = new Alert(AlertType.CONFIRMATION);
			remove.setTitle("Remove Department");
			remove.setHeaderText(null);
			remove.setContentText("Are u sure to remove department with id " + selected.getDeptId() + " ?");
			ButtonType res = remove.showAndWait().orElse(ButtonType.CANCEL);
			if (res == ButtonType.OK) {
				Main.departments.remove(selected);
				String removeSql = "DELETE FROM departments Where department_id = ?";
				try (PreparedStatement stmt = Main.conn.prepareStatement(removeSql)) {
					stmt.setInt(1, selected.getDeptId());
					stmt.executeUpdate();
				} catch (SQLException e1) {
					Main.notValidAlert("Not Valid", e1.getMessage());
					return;
				}
				for (int i = 0; i < Main.employees.size(); i++) {
					if (Main.employees.get(i).getDepartmentId() == selected.getDeptId()) {
						Main.employees.get(i).setDepartmentId(-1);
					}
				}
				Main.validAlert("Department Removed", "Department with id " + selected.getDeptId() + " was removed");
			}
		});

		all = new VBox(10, addBox, buttons, deptTable);
		all.setAlignment(Pos.CENTER);

	}

	public VBox getAll() {
		return all;
	}

}
