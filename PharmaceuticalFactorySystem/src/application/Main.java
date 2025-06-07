package application;
	
import java.sql.Connection;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;


public class Main extends Application {
	static  Connection conn = DBConnect.getConnection();
	@Override
	public void start(Stage primaryStage) {
		try {
			
		        if (conn != null) {
		            System.out.println("Connected!");
		        } else {
		            System.out.println("X");
		        }
			new LoginScene().getLoginStage().show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
