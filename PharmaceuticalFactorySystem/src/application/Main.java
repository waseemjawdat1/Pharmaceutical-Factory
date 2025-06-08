package application;
	
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;


public class Main extends Application {
	static Connection conn = DBConnect.getConnection();
	static ObservableList <User> users=  FXCollections.observableArrayList();
	static ObservableList <Employee> employees=  FXCollections.observableArrayList();
	static ObservableList <Department> departments=  FXCollections.observableArrayList();
	static ObservableList <Supplier> suppliers=  FXCollections.observableArrayList();
	static ObservableList <RawMaterial> materials=  FXCollections.observableArrayList();
	static ObservableList <Customer> customers=  FXCollections.observableArrayList();
	static ObservableList <Warehouse> warehouses=  FXCollections.observableArrayList();


	@Override
	public void start(Stage primaryStage) {
		try {			
		        if (conn != null) {
		            System.out.println("Connected!");
		        } else {
		            System.out.println("X");
		        }
		     
		        Timeline autoReload = new Timeline(
		        	    new KeyFrame(Duration.seconds(3), e -> {
		        	        Main.loadUsers();
		        	        Main.loadDepartments();
		        	        Main.loadSuppliers();
		        	        Main.loadMaterials();
		        	        Main.loadEmployees();
		        	        loadCustomers();
		        	        loadWarehouses();
		        	    })
		        	);
		        	autoReload.setCycleCount(Timeline.INDEFINITE);
		        	autoReload.play();

			new LoginScene().getLoginStage().show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
	
	public static void loadUsers() {
	    Main.users.clear(); 

	    String loadAllUsers = "SELECT * FROM users";

	    try (PreparedStatement stmt = Main.conn.prepareStatement(loadAllUsers);
	         ResultSet rs = stmt.executeQuery()) {

	        while (rs.next()) {
	            int id = rs.getInt("user_id");
	            String username = rs.getString("username");
	            String password = rs.getString("password");
	            String role = rs.getString("role");
	            int employeeId = rs.getInt("employee_id");

	            User user = new User(id, username, password, role, employeeId);
	            Main.users.add(user);
	        }
	        if (UserStage.userTable != null)
	        UserStage.userTable.setItems(users);

	    } catch (SQLException e) {
	        Main.notValidAlert("Database Error", e.getMessage());
	    }
	}
	public static void loadDepartments() {
	    Main.departments.clear(); 

	    String loadAllUsers = "SELECT * FROM departments";

	    try (PreparedStatement stmt = Main.conn.prepareStatement(loadAllUsers);
	         ResultSet rs = stmt.executeQuery()) {

	        while (rs.next()) {
	            int id = rs.getInt("department_id");
	            String name = rs.getString("name");
	            int manager_id = rs.getInt("manager_id");
	            if (rs.wasNull()) {
	            	manager_id = -1;
	            }
	            Department d = new Department(id, name, manager_id);
	            departments.add(d);
	        }
	        if (DepartmentStage.deptTable != null)
	        	DepartmentStage.deptTable.setItems(departments);

	    } catch (SQLException e) {
	        Main.notValidAlert("Database Error", e.getMessage());
	    }
	}
	public static void loadSuppliers() {
	    Main.suppliers.clear();

	    String sql = "SELECT * FROM suppliers";

	    try (PreparedStatement stmt = Main.conn.prepareStatement(sql);
	         ResultSet rs = stmt.executeQuery()) {

	        while (rs.next()) {
	            int id = rs.getInt("supplier_id");
	            String name = rs.getString("name");
	            String email = rs.getString("email");
	            String address = rs.getString("address");
	            String phone = rs.getString("phone");

	            Supplier s = new Supplier(id, name,email, phone, address);
	            Main.suppliers.add(s);
	        }

	        if (SupplierStage.supplierTable != null)
	            SupplierStage.supplierTable.setItems(Main.suppliers);

	    } catch (SQLException e) {
	        Main.notValidAlert("Database Error", e.getMessage());
	    }
	}
	public static void loadMaterials() {
		Main.materials.clear();

		String sql = "SELECT * FROM raw_materials";

		try (PreparedStatement stmt = Main.conn.prepareStatement(sql);
		     ResultSet rs = stmt.executeQuery()) {

			while (rs.next()) {
				int id = rs.getInt("material_id");
				String name = rs.getString("name");
				int unit = rs.getInt("unit");
				int supplierId = rs.getInt("supplier_id");
				if (rs.wasNull())
					supplierId = -1;

				RawMaterial r = new RawMaterial(id, name, unit, supplierId);
				Main.materials.add(r);
			}
			if (RawMaterialStage.materialTable != null)
				RawMaterialStage.materialTable.setItems(Main.materials);

		} catch (SQLException e) {
			Main.notValidAlert("Database Error", e.getMessage());
		}
	}
	public static void loadEmployees() {
		Main.employees.clear();

		String sql = "SELECT * FROM employees";

		try (PreparedStatement stmt = Main.conn.prepareStatement(sql);
		     ResultSet rs = stmt.executeQuery()) {

			while (rs.next()) {
				int id = rs.getInt("employee_id");
				String name = rs.getString("name");
				String email = rs.getString("email");
				String phone = rs.getString("phone");
				int deptId = rs.getInt("department_id");
				if (rs.wasNull())
					deptId = -1;
				String title = rs.getString("job_title");
				double salary = rs.getDouble("salary");
				int age = rs.getInt("age");
				Date joinedAt = rs.getDate("joined_at");
				Calendar joinedCalendar = Calendar.getInstance();
				joinedCalendar.setTime(joinedAt);
				Employee e = new Employee(id, name, email, phone, deptId, title, salary, age, joinedCalendar);
				Main.employees.add(e);
			}

			if (EmployeeStage.employeeTableView != null)
				EmployeeStage.employeeTableView.setItems(Main.employees);

		} catch (SQLException e) {
			Main.notValidAlert("Database Error", e.getMessage());
		}
	}

	public static void loadCustomers() {
	    Main.customers.clear();

	    String sql = "SELECT * FROM customers";

	    try (PreparedStatement stmt = Main.conn.prepareStatement(sql);
	         ResultSet rs = stmt.executeQuery()) {

	        while (rs.next()) {
	            int id = rs.getInt("customer_id");
	            String name = rs.getString("name");
	            String email = rs.getString("email");
	            String phone = rs.getString("phone");
	            String address = rs.getString("address");

	            Customer c = new Customer(id, name, email, phone, address);
	            Main.customers.add(c);
	        }

	        if (CustomerStage.customerTable != null)
	            CustomerStage.customerTable.setItems(Main.customers);

	    } catch (SQLException e) {
	        Main.notValidAlert("Database Error", e.getMessage());
	    }
	}
	public static void loadWarehouses() {
	    Main.warehouses.clear();

	    String sql = "SELECT * FROM warehouses";

	    try (PreparedStatement stmt = Main.conn.prepareStatement(sql);
	         ResultSet rs = stmt.executeQuery()) {

	        while (rs.next()) {
	            int id = rs.getInt("warehouse_id");
	            String location = rs.getString("location");
	            int capacity = rs.getInt("capacity");

	            Warehouse w = new Warehouse(id, location, capacity);
	            Main.warehouses.add(w);
	        }

	        if (WarehouseStage.warehouseTable != null)
	            WarehouseStage.warehouseTable.setItems(Main.warehouses);

	    } catch (SQLException e) {
	        Main.notValidAlert("Database Error", e.getMessage());
	    }
	}



	
	public static void validAlert (String title , String content) {
		Alert v = new Alert (AlertType.INFORMATION);
		v.setTitle(title);
		v.setContentText(content);
		ImageView i = new ImageView (new Image ("true.png"));
		i.setFitHeight(50);
		i.setFitWidth(50);
		v.setGraphic(i);
		v.setHeaderText(null);
		v.showAndWait();
	}
	public static void notValidAlert (String title , String content) {
		Alert v = new Alert (AlertType.ERROR);
		v.setTitle(title);
		v.setContentText(content);
		ImageView i = new ImageView (new Image ("false.png"));
		i.setFitHeight(50);
		i.setFitWidth(50);
		v.setGraphic(i);
		v.setHeaderText(null);
		v.showAndWait();
	}
}
