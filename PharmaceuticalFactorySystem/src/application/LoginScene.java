package application;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class LoginScene {

    private Scene loginScene;
    private Stage loginStage;
    public LoginScene() {
        StackPane s = new StackPane();
        s.setBackground(createDiagonalGradient());

        VBox all = new VBox();
        all.setSpacing(10);
        all.setAlignment(Pos.TOP_CENTER);
        all.setPadding(new Insets(30));
        all.setStyle("-fx-background-color: rgba(255, 255, 255, 0.1); -fx-background-radius: 15;");
        all.setMaxWidth(500);

        ImageView logo = new ImageView(new Image("samaLogo.png"));
        logo.setFitHeight(160);
        logo.setFitWidth(160);

        Label title = new Label("Welcome to Sama Pharma System");
        styleTitle(title);
        VBox.setMargin(title, new Insets(5, 0, 15, 0));

        TextField userName = new TextField();
        userName.setPromptText("User Name");
        styleInput(userName);
        Label userIcon = createIcon("\uD83D\uDC64");
        HBox userBox = new HBox(10, userIcon, userName);
        userBox.setAlignment(Pos.CENTER);

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        styleInput(passwordField);
        Label lockIcon = createIcon("\uD83D\uDD12");
        HBox passwordBox = new HBox(10, lockIcon, passwordField);
        passwordBox.setAlignment(Pos.CENTER);

        Button login = createLoginButton();
        VBox.setMargin(login, new Insets(10, 0, 0, 25));
        login.setOnAction(e->{
        	String checkQuery = "SELECT * FROM users WHERE username = ? AND password = ?";
        	try (PreparedStatement checkdata = Main.conn.prepareStatement(checkQuery)){
        		checkdata.setString(1, userName.getText().trim());
        		checkdata.setString(2, passwordField.getText().trim());
        		
        		ResultSet r = checkdata.executeQuery();
        		if (r.next()) {
        			String name= r.getString("username");
        			String role = r.getString("role");
        			Alert successAlert = new Alert (AlertType.INFORMATION);
        			successAlert.setTitle("Success login");
        			successAlert.setHeaderText("Welcome " + name +",\nLogged in as " + role);
        			ImageView trueI =new ImageView (new Image("true.png"));
        			trueI.setFitWidth(50);
        			trueI.setFitHeight(50);
        			successAlert.setGraphic(trueI);
        			DialogPane dialogPane = successAlert.getDialogPane();
        			dialogPane.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
        			dialogPane.getStyleClass().add("dialog-pane");
        			Stage stage = (Stage) successAlert.getDialogPane().getScene().getWindow();
        			stage.getIcons().add(new Image("true.png"));

        			successAlert.showAndWait();
        		}
        		else {
        			Alert inValid = new Alert (AlertType.ERROR);
        			inValid.setTitle("Fail login");
        			inValid.setHeaderText("Invalid info");
        			ImageView falseI =new ImageView (new Image("false.png"));
        			falseI.setFitWidth(50);
        			falseI.setFitHeight(50);
        			DialogPane dialogPane = inValid.getDialogPane();
        			dialogPane.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
        			dialogPane.getStyleClass().add("dialog-pane");
        			Stage stage = (Stage) inValid.getDialogPane().getScene().getWindow();
        			stage.getIcons().add(new Image("false.png"));
        			inValid.setGraphic(falseI);
        			inValid.showAndWait();
        		}
        	}catch (Exception ee) {
        		
        	}
        });
        all.getChildren().addAll(logo, title, userBox, passwordBox, login);
        s.getChildren().add(all);

        loginScene = new Scene(s, 500, 500);
        loginStage = new Stage ();
        loginStage.setScene(loginScene);
        loginStage.setTitle("Login");
        loginStage.getIcons().add(new Image ("login.png"));
    }

    public Scene getLoginScene() {
        return loginScene;
    }
    public Stage getLoginStage() {
    	return loginStage;
    }

    private LinearGradient createGradient() {
        Stop[] stops = new Stop[]{
            new Stop(0, Color.web("#6a11cb")),
            new Stop(1, Color.web("#2575fc"))
        };
        return new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE, stops);
    }

    private void styleInput(TextField field) {
        field.setStyle("""
            -fx-background-color: transparent;
            -fx-text-fill: white;
            -fx-prompt-text-fill: #dddddd;
            -fx-border-color: white;
            -fx-border-width: 1;
            -fx-background-radius: 10;
            -fx-border-radius: 10;
            -fx-padding: 6;
        """);
        field.setPrefWidth(240);
    }

    private void styleTitle(Label title) {
        title.setTextFill(Color.WHITE);
        title.setFont(Font.font("Georgia", FontWeight.BOLD, 20));
    }

    private Label createIcon(String unicode) {
        Label icon = new Label(unicode);
        icon.setStyle("-fx-text-fill: white; -fx-font-size: 16px;");
        return icon;
    }
    
    private Button createLoginButton() {
        Button loginButton = new Button("LOG IN");
        loginButton.setStyle(defaultButtonStyle());
        loginButton.setPrefWidth(200);

        loginButton.setOnMouseEntered(e -> loginButton.setStyle(hoverButtonStyle()));
        loginButton.setOnMouseExited(e -> loginButton.setStyle(defaultButtonStyle()));
        loginButton.setOnAction(e -> System.out.println("Login clicked"));

        return loginButton;
    }
    public Stage showAlert (String name , String role) {
    	VBox alertPane = new VBox();
    	Scene scene = new Scene (alertPane , 200 , 200);
    	scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());

    	alertPane.setAlignment(Pos.CENTER);
    	Stage stage = new Stage ();
    	stage.setTitle("Success login");
    	stage.setScene(scene);
    	alertPane.getStyleClass().add("alert-pane");

    	Label title = new Label("Welcome " + name);
    	title.getStyleClass().add("alert-title");

    	Label subtitle = new Label("You signed in as " + role);
    	subtitle.getStyleClass().add("alert-subtitle");

    	alertPane.getChildren().addAll(title, subtitle);
    	
    	return stage;
    }

    private String defaultButtonStyle() {
        return """
            -fx-background-color: #b2e6a1;
            -fx-text-fill: black;
            -fx-font-weight: bold;
            -fx-background-radius: 20;
            -fx-padding: 8 0 8 0;
        """;
    }

    private String hoverButtonStyle() {
        return """
            -fx-background-color: #a1d490;
            -fx-text-fill: black;
            -fx-font-weight: bold;
            -fx-background-radius: 20;
            -fx-padding: 8 0 8 0;
        """;
    }
    private Background createDiagonalGradient() {
        LinearGradient diagonal = new LinearGradient(
            0, 0, 1, 1, true, CycleMethod.NO_CYCLE,
            new Stop(0, Color.web("#0f2027")),
            new Stop(0.5, Color.web("#203a43")),
            new Stop(1, Color.web("#2c5364"))
        );
        return new Background(new BackgroundFill(diagonal, CornerRadii.EMPTY, Insets.EMPTY));
    }
}
