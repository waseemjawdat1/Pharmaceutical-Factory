package application;
	
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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
	static ObservableList <Department> depatments=  FXCollections.observableArrayList();


	@Override
	public void start(Stage primaryStage) {
		try {
			
		        if (conn != null) {
		            System.out.println("Connected!");
		        } else {
		            System.out.println("X");
		        }
		        //Test
		        //Mariam Barghouthi
		        loadUsers();
			new LoginScene().getLoginStage().show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
	
	public void loadUsers() {
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
