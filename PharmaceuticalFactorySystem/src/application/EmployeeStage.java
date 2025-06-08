package application;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Observable;

import javax.naming.directory.AttributeModificationException;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class EmployeeStage {
	private Label searchL;
	private TextField searchTF;

	private Button addEmpB;
	private Button updateEmpB;
	private Button deleteEmpB;

	static MyTableView<Employee> employeeTableView;
	private TableColumn<Employee, Integer> employeeIDColumn;
	private TableColumn<Employee, String> nameColumn;
	private TableColumn<Employee, String> emailColumn;
	private TableColumn<Employee, String> phoneColumn;
	private TableColumn<Employee, Integer> departmentIDColumn;
	private TableColumn<Employee, String> jobTitleColumn;
	private TableColumn<Employee, Double> salaryColumn;
	private TableColumn<Employee, Integer> ageColumn;
	private TableColumn<Employee, String> joinedAtColumn;

	private HBox searchLTF;
	private HBox buttons;
	private VBox all;

	private Scene employeeScene;
	private Stage employeeStage;

	public EmployeeStage() {
		searchL = new MyLabel("Search by employee Name : ");
		searchTF = new MyTextField();

		addEmpB = new MyButton("Add", 2);
		updateEmpB = new MyButton("Update", 2);
		deleteEmpB = new MyButton("Delete", 2);

		employeeTableView = new MyTableView<Employee>();
		employeeIDColumn = employeeTableView.createStyledColumn("Employee ID", "employeeId", Integer.class);
		nameColumn = employeeTableView.createStyledColumn("Name", "name");
		emailColumn = employeeTableView.createStyledColumn("Email", "email");
		phoneColumn = employeeTableView.createStyledColumn("Phone Number", "phone");
		departmentIDColumn = employeeTableView.createStyledColumn("Department ID", "departmentId", Integer.class);
		jobTitleColumn = employeeTableView.createStyledColumn("Job Title", "jobTitle");
		salaryColumn = employeeTableView.createStyledColumn("Salary", "salary", Double.class);
		ageColumn = employeeTableView.createStyledColumn("Age", "age", Integer.class);
		joinedAtColumn = employeeTableView.createStyledColumn("Joined Date", "name");
		joinedAtColumn.setCellValueFactory(e->{
			Calendar c= e.getValue().getJoinedAt();
			return new SimpleStringProperty(c.get(Calendar.DAY_OF_MONTH) + "/" +( c.get(Calendar.MONTH) + 1) + "/" + c.get(Calendar.YEAR));
		});
		employeeTableView.getColumns().addAll(employeeIDColumn, nameColumn, emailColumn, phoneColumn,
				departmentIDColumn, jobTitleColumn, salaryColumn, ageColumn, joinedAtColumn);
		employeeTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		employeeTableView.setMinHeight(500);
		employeeTableView.setMaxWidth(1300);
		employeeTableView.setItems(Main.employees);

		searchLTF = new HBox(10);
		searchLTF.getChildren().addAll(searchL, searchTF);
		searchLTF.setAlignment(Pos.CENTER);

		searchTF.setOnKeyTyped(e->{
			String searchS = searchTF.getText();
			if (searchS == null || searchS.isEmpty()) {
				employeeTableView.setItems(Main.employees);
				return;
			}
			ObservableList<Employee> temp = FXCollections.observableArrayList();
			for (int i = 0  ;i < Main.employees.size(); i++) {
				if (Main.employees.get(i).getName().toLowerCase().startsWith(searchS.toLowerCase()))
					temp.add(Main.employees.get(i));
			}
			if (temp.size() == 0) employeeTableView.setItems(Main.employees);
			else employeeTableView.setItems(temp);
			}); 
		buttons = new HBox(10);
		buttons.getChildren().addAll(addEmpB, updateEmpB, deleteEmpB);
		buttons.setAlignment(Pos.CENTER);

		all = new VBox(10);
		all.getChildren().addAll(searchLTF, buttons, employeeTableView);
		all.setAlignment(Pos.CENTER);

		employeeScene = new Scene(all, 1350, 700);
		employeeScene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());

		employeeStage = new Stage();
		employeeStage.setScene(employeeScene);
		employeeStage.setTitle("Employee");
		employeeStage.show();

		addEmpB.setOnAction(e -> {
			Label nameL = new MyLabel("Name : ");
			Label emailL = new MyLabel("Email : ");
			Label phoneL = new MyLabel("Phone Number : ");
			Label departmentIDL = new MyLabel("Department ID : ");
			Label jobTitleL = new MyLabel("Job Title : ");
			Label salaryL = new MyLabel("Salary : ");
			Label ageL = new MyLabel("Age : ");
			Label joinedAtL = new MyLabel("Joined Date : ");

			TextField nameTF = new MyTextField();
			TextField emailTF = new MyTextField();
			TextField phoneTF = new MyTextField();
			TextField departmentTF = new MyTextField();
			TextField jobTitleTF = new MyTextField();
			TextField salaryTF = new MyTextField();
			TextField ageTF = new MyTextField();
			DatePicker joinedDatePicker = new DatePicker();

			GridPane gridPane = new GridPane();
			gridPane.addColumn(0, nameL, emailL, phoneL, departmentIDL, jobTitleL, salaryL, ageL, joinedAtL);
			gridPane.addColumn(1, nameTF, emailTF, phoneTF, departmentTF, jobTitleTF, salaryTF, ageTF,
					joinedDatePicker);
			gridPane.setVgap(5);
			gridPane.setHgap(5);
			gridPane.setAlignment(Pos.CENTER);

			Button clear = new MyButton("Clear", 2);
			Button add = new MyButton("Add Employee", 2);
			HBox buttons = new HBox(10);
			buttons.getChildren().addAll(add, clear);
			buttons.setAlignment(Pos.CENTER);

			VBox all = new VBox(10);
			all.getChildren().addAll(gridPane, buttons);
			all.setAlignment(Pos.CENTER);

			Scene addEmployeeScene = new Scene(all, 600, 600);
			Stage addEmployeeStage = new Stage();
			addEmployeeStage.setTitle("Add Employee");
			addEmployeeStage.setScene(addEmployeeScene);
			addEmployeeStage.show();

			add.setOnAction(e1 -> {
				String name = nameTF.getText();
				if (name == null || name.isEmpty()) {
					Main.notValidAlert("Invalid Input", "Name field is empty!");
					return;
				}

				String email = emailTF.getText();
				if (email == null || email.isEmpty()) {
					Main.notValidAlert("Invalid Input", "Email field is empty!");
					return;
				}

				String phone = phoneTF.getText();
				if (phone == null || phone.isEmpty()) {
					Main.notValidAlert("Invalid Input", "Phone field is empty!");
					return;
				}

				String departmentIdS = departmentTF.getText();
				if (departmentIdS == null || departmentIdS.isEmpty()) {
					Main.notValidAlert("Invalid Input", "Department ID field is empty! , type null if Unassigned");
					return;
				}

				int departmentId = -1;
				if (!departmentIdS.equalsIgnoreCase("null")) {
				try {
					departmentId = Integer.parseInt(departmentIdS);
				} catch (Exception ex) {
					Main.notValidAlert("Invalid Input", "Department ID must be a number!");
					return;
				}

				boolean isExist = false;
				for (int i = 0; i < Main.departments.size(); i++) {
					if (Main.departments.get(i).getDeptId() == departmentId) {
						isExist = true;
						break;
					}
				}

				if (!isExist) {
					Main.notValidAlert("Invalid Input", "Department ID does not exist!");
					return;
				}
				}
				String jobTitle = jobTitleTF.getText();
				if (jobTitle == null || jobTitle.isEmpty()) {
					Main.notValidAlert("Invalid Input", "Job Title field is empty!");
					return;
				}

				String salaryS = salaryTF.getText();
				if (salaryS == null || salaryS.isEmpty()) {
					Main.notValidAlert("Invalid Input", "Salary field is empty!");
					return;
				}

				double salary = -1;
				try {
					salary = Double.parseDouble(salaryS);
				} catch (Exception ex) {
					Main.notValidAlert("Invalid Input", "Salary must be a number!");
					return;
				}

				if (salary < 0) {
					Main.notValidAlert("Invalid Input", "Salary must be a positive number!");
					return;
				}

				String ageS = ageTF.getText();
				if (ageS == null || ageS.isEmpty()) {
					Main.notValidAlert("Invalid Input", "Age field is empty!");
					return;
				}

				int age = -1;
				try {
					age = Integer.parseInt(ageS);
				} catch (Exception ex) {
					Main.notValidAlert("Invalid Input", "Age must be a number!");
					return;
				}

				if (age < 18) {
					Main.notValidAlert("Invalid Input", "Age must be over 18!");
					return;
				}

				LocalDate joinedDate = joinedDatePicker.getValue();
				if (joinedDate == null) {
					Main.notValidAlert("Invalid Input", "Select joined date!");
					return;
				}
				Calendar joinedDateCalendar = new GregorianCalendar(joinedDate.getYear(),
						joinedDate.getMonthValue() - 1, joinedDate.getDayOfMonth());

				try {
					Employee employee = new Employee(name, email, phone, departmentId, jobTitle, salary, age,
							joinedDateCalendar);
					Main.employees.add(employee);
					Main.validAlert("Add Employee", "An Employee has been added successfully.");
					addEmployeeStage.close();
				} catch (Exception ex) {
					Main.notValidAlert("Invalid", ex.getMessage());
					return;
				}
			});

			clear.setOnAction(e1 -> {
				nameTF.clear();
				emailTF.clear();
				phoneTF.clear();
				departmentTF.clear();
				jobTitleTF.clear();
				salaryTF.clear();
				ageTF.clear();
				joinedDatePicker.setValue(null);
			});
		});

		updateEmpB.setOnAction(e -> {
			Employee selectedEmployee = employeeTableView.getSelectionModel().getSelectedItem();
			if (selectedEmployee == null) {
				Main.notValidAlert("Nothing Selected", "You must select the employee row you want to update!");
				return;
			}

			Label nameL = new MyLabel("Name : ");
			Label emailL = new MyLabel("Email : ");
			Label phoneL = new MyLabel("Phone Number : ");
			Label departmentIDL = new MyLabel("Department ID : ");
			Label jobTitleL = new MyLabel("Job Title : ");
			Label salaryL = new MyLabel("Salary : ");
			Label ageL = new MyLabel("Age : ");
			Label joinedAtL = new MyLabel("Joined Date : ");

			TextField nameTF = new MyTextField();
			TextField emailTF = new MyTextField();
			TextField phoneTF = new MyTextField();
			TextField departmentIDTF = new MyTextField();
			TextField jobTitleTF = new MyTextField();
			TextField salaryTF = new MyTextField();
			TextField ageTF = new MyTextField();
			DatePicker joinedDatePicker = new DatePicker();

			nameTF.setText(selectedEmployee.getName());
			emailTF.setText(selectedEmployee.getEmail());
			phoneTF.setText(selectedEmployee.getPhone());
			if (selectedEmployee.getDepartmentId() != 1)
			departmentIDTF.setText(selectedEmployee.getDepartmentId() + "");
			else 
			departmentIDTF.setText("null");
			jobTitleTF.setText(selectedEmployee.getJobTitle());
			salaryTF.setText(selectedEmployee.getSalary() + "");
			ageTF.setText(selectedEmployee.getAge() + "");
			Calendar c = selectedEmployee.getJoinedAt();
			LocalDate jDate = LocalDate.of(c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1,
					c.get(Calendar.DAY_OF_MONTH));
			joinedDatePicker.setValue(jDate);

			GridPane gridPane = new GridPane();
			gridPane.addColumn(0, nameL, emailL, phoneL, departmentIDL, jobTitleL, salaryL, ageL, joinedAtL);
			gridPane.addColumn(1, nameTF, emailTF, phoneTF, departmentIDTF, jobTitleTF, salaryTF, ageTF,
					joinedDatePicker);
			gridPane.setVgap(5);
			gridPane.setHgap(5);
			gridPane.setAlignment(Pos.CENTER);

			Button clear = new MyButton("Clear", 2);
			Button update = new MyButton("Update Employee", 2);
			HBox buttons = new HBox(10);
			buttons.getChildren().addAll(update, clear);
			buttons.setAlignment(Pos.CENTER);

			VBox all = new VBox(10);
			all.getChildren().addAll(gridPane, buttons);
			all.setAlignment(Pos.CENTER);

			Scene updateEmployeeScene = new Scene(all, 600, 600);
			Stage updateEmployeeStage = new Stage();
			updateEmployeeStage.setTitle("Add Employee");
			updateEmployeeStage.setScene(updateEmployeeScene);
			updateEmployeeStage.show();

			update.setOnAction(e1 -> {
				String name = nameTF.getText();
				if (name == null || name.isEmpty()) {
					Main.notValidAlert("Invalid Input", "Name field is empty!");
					return;
				}

				String email = emailTF.getText();
				if (email == null || email.isEmpty()) {
					Main.notValidAlert("Invalid Input", "Email field is empty!");
					return;
				}

				String phone = phoneTF.getText();
				if (phone == null || phone.isEmpty()) {
					Main.notValidAlert("Invalid Input", "Phone field is empty!");
					return;
				}

				String departmentIdS = departmentIDTF.getText();
				if (departmentIdS == null || departmentIdS.isEmpty()) {
					Main.notValidAlert("Invalid Input", "Department ID field is empty!, type null if Unassigned");
					return;
				}

				int departmentId = -1;
				if (!departmentIdS.equalsIgnoreCase("null")) {
				try {
					departmentId = Integer.parseInt(departmentIdS);
				} catch (Exception ex) {
					Main.notValidAlert("Invalid Input", "Department ID must be a number!");
					return;
				}

				boolean isExist = false;
				for (int i = 0; i < Main.departments.size(); i++) {
					if (Main.departments.get(i).getDeptId() == departmentId) {
						isExist = true;
						break;
					}
				}

				if (!isExist) {
					Main.notValidAlert("Invalid Input", "Department ID does not exist!");
					return;
				}
				}

				String jobTitle = jobTitleTF.getText();
				if (jobTitle == null || jobTitle.isEmpty()) {
					Main.notValidAlert("Invalid Input", "Job Title field is empty!");
					return;
				}

				String salaryS = salaryTF.getText();
				if (salaryS == null || salaryS.isEmpty()) {
					Main.notValidAlert("Invalid Input", "Salary field is empty!");
					return;
				}

				double salary = -1;
				try {
					salary = Double.parseDouble(salaryS);
				} catch (Exception ex) {
					Main.notValidAlert("Invalid Input", "Salary must be a number!");
					return;
				}

				if (salary < 0) {
					Main.notValidAlert("Invalid Input", "Salary must be a positive number!");
					return;
				}

				String ageS = ageTF.getText();
				if (ageS == null || ageS.isEmpty()) {
					Main.notValidAlert("Invalid Input", "Age field is empty!");
					return;
				}

				int age = -1;
				try {
					age = Integer.parseInt(ageS);
				} catch (Exception ex) {
					Main.notValidAlert("Invalid Input", "Age must be a number!");
					return;
				}

				if (age < 18) {
					Main.notValidAlert("Invalid Input", "Age must be over 18!");
					return;
				}

				LocalDate joinedDate = joinedDatePicker.getValue();
				if (joinedDate == null) {
					Main.notValidAlert("Invalid Input", "Select joined date");
					return;
				}
				Calendar joinedDateCalendar = new GregorianCalendar(joinedDate.getYear(),
						joinedDate.getMonthValue() - 1, joinedDate.getDayOfMonth());

				try {
					selectedEmployee.updateEmployee(name, email, phone, departmentId, jobTitle, salary, age,
							joinedDateCalendar);
					employeeTableView.refresh();
					Main.validAlert("Update Employee", "An employee with ID number " + selectedEmployee.getEmployeeId()
							+ " has been updated successfully.");
					updateEmployeeStage.close();
				} catch (Exception ex) {
					Main.notValidAlert("Invalid", ex.getMessage());
					return;
				}
			});

			clear.setOnAction(e1 -> {
				nameTF.clear();
				emailTF.clear();
				phoneTF.clear();
				departmentIDTF.clear();
				jobTitleTF.clear();
				salaryTF.clear();
				ageTF.clear();
				joinedDatePicker.setValue(null);
			});
		});

		deleteEmpB.setOnAction(e -> {
			Employee selectedEmployee = employeeTableView.getSelectionModel().getSelectedItem();
			if (selectedEmployee == null) {
				Main.notValidAlert("Nothing Selected", "You must select the employee row you want to update!");
				return;
			}

			Alert remove = new Alert(AlertType.CONFIRMATION);
			remove.setTitle("Delete Employee");
			remove.setHeaderText(null);
			remove.setContentText("Are you sure you want to delete the employee with ID number "
					+ selectedEmployee.getEmployeeId() + "?");
			ButtonType res = remove.showAndWait().orElse(ButtonType.CANCEL);
			if (res == ButtonType.OK) {
				Main.employees.remove(selectedEmployee);
				String removeEmpSql = "DELETE FROM employees where employee_id = ?";
				try (PreparedStatement stmt = Main.conn.prepareStatement(removeEmpSql)) {
					stmt.setInt(1, selectedEmployee.getEmployeeId());
					stmt.executeUpdate();
				} catch (SQLException ex) {
					Main.notValidAlert("Invalid", ex.getMessage());
					return;
				}
				Main.validAlert("Remove Employee", "The employee with ID number " + selectedEmployee.getEmployeeId()
						+ " has been deleted successfully.");
			}
		});
	}

	public Label getSearchL() {
		return searchL;
	}

	public void setSearchL(Label searchL) {
		this.searchL = searchL;
	}

	public TextField getSearchTF() {
		return searchTF;
	}

	public void setSearchTF(TextField searchTF) {
		this.searchTF = searchTF;
	}

	public Button getAddEmpB() {
		return addEmpB;
	}

	public void setAddEmpB(Button addEmpB) {
		this.addEmpB = addEmpB;
	}

	public Button getUpdateEmpB() {
		return updateEmpB;
	}

	public void setUpdateEmpB(Button updateEmpB) {
		this.updateEmpB = updateEmpB;
	}

	public Button getDeleteEmpB() {
		return deleteEmpB;
	}

	public void setDeleteEmpB(Button deleteEmpB) {
		this.deleteEmpB = deleteEmpB;
	}

	public MyTableView<Employee> getEmployeeTableView() {
		return employeeTableView;
	}

	public void setEmployeeTableView(MyTableView<Employee> employeeTableView) {
		this.employeeTableView = employeeTableView;
	}

	public TableColumn<Employee, Integer> getEmployeeIDColumn() {
		return employeeIDColumn;
	}

	public void setEmployeeIDColumn(TableColumn<Employee, Integer> employeeIDColumn) {
		this.employeeIDColumn = employeeIDColumn;
	}

	public TableColumn<Employee, String> getNameColumn() {
		return nameColumn;
	}

	public void setNameColumn(TableColumn<Employee, String> nameColumn) {
		this.nameColumn = nameColumn;
	}

	public TableColumn<Employee, String> getEmailColumn() {
		return emailColumn;
	}

	public void setEmailColumn(TableColumn<Employee, String> emailColumn) {
		this.emailColumn = emailColumn;
	}

	public TableColumn<Employee, String> getPhoneColumn() {
		return phoneColumn;
	}

	public void setPhoneColumn(TableColumn<Employee, String> phoneColumn) {
		this.phoneColumn = phoneColumn;
	}

	public TableColumn<Employee, Integer> getDepartmentIDColumn() {
		return departmentIDColumn;
	}

	public void setDepartmentIDColumn(TableColumn<Employee, Integer> departmentIDColumn) {
		this.departmentIDColumn = departmentIDColumn;
	}

	public TableColumn<Employee, String> getJobTitleColumn() {
		return jobTitleColumn;
	}

	public void setJobTitleColumn(TableColumn<Employee, String> jobTitleColumn) {
		this.jobTitleColumn = jobTitleColumn;
	}

	public TableColumn<Employee, Double> getSalaryColumn() {
		return salaryColumn;
	}

	public void setSalaryColumn(TableColumn<Employee, Double> salaryColumn) {
		this.salaryColumn = salaryColumn;
	}

	public TableColumn<Employee, String> getJoinedAtColumn() {
		return joinedAtColumn;
	}

	public TableColumn<Employee, Integer> getAgeColumn() {
		return ageColumn;
	}

	public void setAgeColumn(TableColumn<Employee, Integer> ageColumn) {
		this.ageColumn = ageColumn;
	}

	public void setJoinedAtColumn(TableColumn<Employee, String> joinedAtColumn) {
		this.joinedAtColumn = joinedAtColumn;
	}

	public HBox getSearchLTF() {
		return searchLTF;
	}

	public void setSearchLTF(HBox searchLTF) {
		this.searchLTF = searchLTF;
	}

	public HBox getButtons() {
		return buttons;
	}

	public void setButtons(HBox buttons) {
		this.buttons = buttons;
	}

	public VBox getAll() {
		return all;
	}

	public void setAll(VBox all) {
		this.all = all;
	}

	public Scene getEmployeeScene() {
		return employeeScene;
	}

	public void setEmployeeScene(Scene employeeScene) {
		this.employeeScene = employeeScene;
	}

	public Stage getEmployeeStage() {
		return employeeStage;
	}

	public void setEmployeeStage(Stage employeeStage) {
		this.employeeStage = employeeStage;
	}
}
