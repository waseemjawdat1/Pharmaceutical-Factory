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
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class EmployeeStageAssignmentStage {

	static MyTableView<EmployeeStageAssignment> assignmentTable;
	private TableColumn<EmployeeStageAssignment, Integer> empId, stageId;
	private Button add, remove;
	private TextField searchTF;
	private RadioButton byEmp, byStage;

	public EmployeeStageAssignmentStage() {
		assignmentTable = new MyTableView<>();
		empId = assignmentTable.createStyledColumn("Employee Id", "employeeId", Integer.class);
		stageId = assignmentTable.createStyledColumn("Stage Id", "stageId", Integer.class);
		assignmentTable.getColumns().addAll(empId, stageId);
		assignmentTable.setItems(Main.employeeStageAssignments);
		assignmentTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		assignmentTable.setMinHeight(500);
		assignmentTable.setMaxWidth(600);

		add = new MyButton("➕ Add", 2);
		remove = new MyButton("➖ Remove", 2);

		HBox buttons = new HBox(10, add, remove);
		buttons.setAlignment(Pos.CENTER);

		Label searchL = new MyLabel("Search : ");
		searchTF = new MyTextField();
		byEmp = new RadioButton("By Employee Id");
		byStage = new RadioButton("By Stage Id");
		byEmp.setStyle("-fx-text-fill: #6C3483;-fx-font-size: 14px;-fx-font-weight: bold;-fx-padding: 4 10 4 10;");
		byStage.setStyle("-fx-text-fill: #6C3483;-fx-font-size: 14px;-fx-font-weight: bold;-fx-padding: 4 10 4 10;");
		ToggleGroup g = new ToggleGroup();
		byEmp.setToggleGroup(g);
		byStage.setToggleGroup(g);
		byEmp.setSelected(true);
		HBox searchBox = new HBox(10, searchL, searchTF, byEmp, byStage);
		searchBox.setAlignment(Pos.CENTER);

		searchTF.setOnKeyTyped(e -> {
			String key = searchTF.getText().trim();
			if (key.isEmpty()) {
				assignmentTable.setItems(Main.employeeStageAssignments);
				return;
			}
			ObservableList<EmployeeStageAssignment> temp = FXCollections.observableArrayList();
			for (int i = 0; i < Main.employeeStageAssignments.size(); i++) {
				if (byEmp.isSelected() && (Main.employeeStageAssignments.get(i).getEmployeeId() + "").contains(key)) {
					temp.add(Main.employeeStageAssignments.get(i));
				} else if (byStage.isSelected() && (Main.employeeStageAssignments.get(i).getStageId() + "").contains(key)) {
					temp.add(Main.employeeStageAssignments.get(i));
				}
			}
			if (temp.size() > 0)
				assignmentTable.setItems(temp);
			else
				assignmentTable.setItems(Main.employeeStageAssignments);
		});

		add.setOnAction(e -> {
			Label title = new MyLabel("Assign Stage", 1);
			Label empIdL = new MyLabel("Employee Id : ");
			TextField empIdTF = new MyTextField();
			Label stageIdL = new MyLabel("Stage Id : ");
			TextField stageIdTF = new MyTextField();

			GridPane g1 = new GridPane();
			g1.addColumn(0, empIdL, stageIdL);
			g1.addColumn(1, empIdTF, stageIdTF);
			g1.setVgap(5);
			g1.setHgap(5);
			g1.setAlignment(Pos.CENTER);

			Button addBtn = new MyButton("Add", 2);
			Button clear = new MyButton("Clear", 2);
			HBox btns = new HBox(10, addBtn, clear);
			btns.setAlignment(Pos.CENTER);

			VBox all = new VBox(10, title, g1, btns);
			all.setAlignment(Pos.CENTER);
			Scene s = new Scene(all, 400, 250);
			Stage st = new Stage();
			st.setScene(s);
			st.setTitle("Assign Stage");
			st.show();

			clear.setOnAction(e1 -> {
				empIdTF.clear();
				stageIdTF.clear();
			});

			addBtn.setOnAction(e1 -> {
				String empS = empIdTF.getText().trim();
				String stageS = stageIdTF.getText().trim();
				if (empS.isEmpty() || stageS.isEmpty()) {
					Main.notValidAlert("Not Valid Input", "Fields can't be empty");
					return;
				}
				int empInt = -1;
				int stageInt = -1;
				try {
					empInt = Integer.parseInt(empS);
					stageInt = Integer.parseInt(stageS);
				} catch (Exception ex) {
					Main.notValidAlert("Not Valid Input", "IDs must be numbers");
					return;
				}
				boolean empExists = false;
				boolean stageExists = false;
				for (int i = 0; i < Main.employees.size(); i++) {
					if (Main.employees.get(i).getEmployeeId() == empInt) {
						empExists = true;
						break;
					}
				}
				for (int i = 0; i < Main.manufacturingStages.size(); i++) {
					if (Main.manufacturingStages.get(i).getStageId() == stageInt) {
						stageExists = true;
						break;
					}
				}
				if (!empExists || !stageExists) {
					Main.notValidAlert("Invalid IDs", "Employee or Stage does not exist");
					return;
				}
				EmployeeStageAssignment a = new EmployeeStageAssignment(empInt, stageInt);
				Main.employeeStageAssignments.add(a);
				Main.validAlert("Added", "Stage assigned to employee successfully");
				st.close();
			});
		});

		remove.setOnAction(e -> {
			EmployeeStageAssignment selected = assignmentTable.getSelectionModel().getSelectedItem();
			if (selected == null) {
				Main.notValidAlert("Nothing selected", "Select row to remove");
				return;
			}
			Alert remove = new Alert(AlertType.CONFIRMATION);
			remove.setTitle("Remove Assignment");
			remove.setHeaderText(null);
			remove.setContentText("Are you sure to remove this assignment?");
			ButtonType res = remove.showAndWait().orElse(ButtonType.CANCEL);
			if (res == ButtonType.OK) {
				Main.employeeStageAssignments.remove(selected);
				String sql = "DELETE FROM employee_stage_assignments WHERE employee_id = ? AND stage_id = ?";
				try (PreparedStatement stmt = Main.conn.prepareStatement(sql)) {
					stmt.setInt(1, selected.getEmployeeId());
					stmt.setInt(2, selected.getStageId());
					stmt.executeUpdate();
					Main.validAlert("Removed", "Assignment removed successfully");
				} catch (SQLException e1) {
					Main.notValidAlert("Error", e1.getMessage());
				}
			}
		});

		VBox all = new VBox(10, searchBox, buttons, assignmentTable);
		all.setAlignment(Pos.CENTER);
		Scene scene = new Scene(all, 750, 650);
		scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		Stage stage = new Stage();
		stage.setTitle("Employee Stage Assignments");
		stage.setScene(scene);
		stage.show();
	}
}
