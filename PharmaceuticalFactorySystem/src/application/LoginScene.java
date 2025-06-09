package application;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.util.Duration;

public class LoginScene {

    private Scene loginScene;
    private Stage loginStage;
    private VBox loginContainer;
    private TextField userName;
    private PasswordField passwordField;
    
    public LoginScene() {
        StackPane root = new StackPane();
        root.setBackground(createModernGradient());

        loginContainer = createLoginContainer();
        
        addEntranceAnimation(loginContainer);

        root.getChildren().add(loginContainer);

        loginScene = new Scene(root, 450, 650);
        loginStage = new Stage();
        loginStage.setScene(loginScene);
        loginStage.setTitle("Sama Pharma - Login");
        loginStage.getIcons().add(new Image("login.png"));
        loginStage.setResizable(false);
    }

    private VBox createLoginContainer() {
        VBox container = new VBox();
        container.setSpacing(25);
        container.setAlignment(Pos.CENTER);
        container.setPadding(new Insets(40, 50, 40, 50));
        container.setMaxWidth(450);
        
        container.setStyle("""
            -fx-background-color: rgba(255, 255, 255, 0.15);
            -fx-background-radius: 25;
            -fx-border-color: rgba(255, 255, 255, 0.3);
            -fx-border-width: 1;
            -fx-border-radius: 25;
        """);

        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.rgb(0, 0, 0, 0.3));
        shadow.setRadius(20);
        shadow.setOffsetY(10);
        container.setEffect(shadow);

        ImageView logo = createAnimatedLogo();
        
        VBox headerSection = createHeaderSection();
        
        VBox inputSection = createInputSection();
        
        Button loginButton = createModernLoginButton();

        container.getChildren().addAll(logo, headerSection, inputSection, loginButton);
        return container;
    }

    private ImageView createAnimatedLogo() {
        ImageView logo = new ImageView(new Image("samaLogo.png"));
        logo.setFitHeight(120);
        logo.setFitWidth(120);
        logo.setPreserveRatio(true);
        
        TranslateTransition floatAnimation = new TranslateTransition(Duration.seconds(3), logo);
        floatAnimation.setFromY(-5);
        floatAnimation.setToY(5);
        floatAnimation.setCycleCount(TranslateTransition.INDEFINITE);
        floatAnimation.setAutoReverse(true);
        floatAnimation.play();
        
        return logo;
    }

    private VBox createHeaderSection() {
        VBox header = new VBox(8);
        header.setAlignment(Pos.CENTER);
        
        Label title = new Label("Welcome Back");
        title.setStyle("""
            -fx-text-fill: white;
            -fx-font-family: 'Segoe UI', 'Helvetica Neue', Arial, sans-serif;
            -fx-font-size: 28px;
            -fx-font-weight: bold;
        """);
        
        Label subtitle = new Label("Sign in to Sama Pharma System");
        subtitle.setStyle("""
            -fx-text-fill: rgba(255, 255, 255, 0.8);
            -fx-font-family: 'Segoe UI', 'Helvetica Neue', Arial, sans-serif;
            -fx-font-size: 14px;
        """);
        
        header.getChildren().addAll(title, subtitle);
        VBox.setMargin(header, new Insets(0, 0, 20, 0));
        return header;
    }

    private VBox createInputSection() {
        VBox inputSection = new VBox(20);
        inputSection.setAlignment(Pos.CENTER);
        
        userName = new TextField();
        userName.setPromptText("Enter your username");
        styleModernInput(userName);
        HBox userBox = createInputBox("👤", userName);
        
        passwordField = new PasswordField();
        passwordField.setPromptText("Enter your password");
        styleModernInput(passwordField);
        HBox passwordBox = createInputBox("🔒", passwordField);
        
        
        
        inputSection.getChildren().addAll(userBox, passwordBox);
        return inputSection;
    }

    private HBox createInputBox(String icon, TextField field) {
        HBox box = new HBox(15);
        box.setAlignment(Pos.CENTER_LEFT);
        box.setPadding(new Insets(12, 16, 12, 16));
        box.setStyle("""
            -fx-background-color: rgba(255, 255, 255, 0.1);
            -fx-background-radius: 15;
            -fx-border-color: rgba(255, 255, 255, 0.2);
            -fx-border-width: 1;
            -fx-border-radius: 15;
        """);
        
        Label iconLabel = new Label(icon);
        iconLabel.setStyle("""
            -fx-text-fill: rgba(255, 255, 255, 0.7);
            -fx-font-size: 16px;
        """);
        
        field.setStyle("""
            -fx-background-color: transparent;
            -fx-text-fill: white;
            -fx-prompt-text-fill: rgba(255, 255, 255, 0.6);
            -fx-border-width: 0;
            -fx-font-size: 14px;
        """);
        field.setPrefWidth(280);
        
        field.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                box.setStyle("""
                    -fx-background-color: rgba(255, 255, 255, 0.15);
                    -fx-background-radius: 15;
                    -fx-border-color: rgba(178, 230, 161, 0.8);
                    -fx-border-width: 2;
                    -fx-border-radius: 15;
                """);
            } else {
                box.setStyle("""
                    -fx-background-color: rgba(255, 255, 255, 0.1);
                    -fx-background-radius: 15;
                    -fx-border-color: rgba(255, 255, 255, 0.2);
                    -fx-border-width: 1;
                    -fx-border-radius: 15;
                """);
            }
        });
        
        box.getChildren().addAll(iconLabel, field);
        return box;
    }

    private Button createModernLoginButton() {
        Button loginButton = new Button("Sign In");
        loginButton.setPrefWidth(350);
        loginButton.setPrefHeight(50);
        
        loginButton.setStyle("""
            -fx-background-color: linear-gradient(to right, #b2e6a1, #a1d490);
            -fx-text-fill: #2c5364;
            -fx-font-weight: bold;
            -fx-font-size: 16px;
            -fx-background-radius: 25;
            -fx-border-radius: 25;
            -fx-cursor: hand;
        """);

        loginButton.setOnMouseEntered(e -> {
            loginButton.setStyle("""
                -fx-background-color: linear-gradient(to right, #a1d490, #90c47f);
                -fx-text-fill: #2c5364;
                -fx-font-weight: bold;
                -fx-font-size: 16px;
                -fx-background-radius: 25;
                -fx-border-radius: 25;
                -fx-cursor: hand;
            """);
            
            ScaleTransition scale = new ScaleTransition(Duration.millis(100), loginButton);
            scale.setToX(1.05);
            scale.setToY(1.05);
            scale.play();
        });

        loginButton.setOnMouseExited(e -> {
            loginButton.setStyle("""
                -fx-background-color: linear-gradient(to right, #b2e6a1, #a1d490);
                -fx-text-fill: #2c5364;
                -fx-font-weight: bold;
                -fx-font-size: 16px;
                -fx-background-radius: 25;
                -fx-border-radius: 25;
                -fx-cursor: hand;
            """);
            
            ScaleTransition scale = new ScaleTransition(Duration.millis(100), loginButton);
            scale.setToX(1.0);
            scale.setToY(1.0);
            scale.play();
        });

        loginButton.setOnAction(e -> handleLogin());

        VBox.setMargin(loginButton, new Insets(20, 0, 0, 0));
        return loginButton;
    }

    private void handleLogin() {
        Button loginButton = (Button) loginContainer.getChildren().get(3);
        loginButton.setText("Signing in...");
        loginButton.setDisable(true);
        
        String checkQuery = "SELECT * FROM users WHERE username = ? AND password = ?";
        try (PreparedStatement checkdata = Main.conn.prepareStatement(checkQuery)) {
            checkdata.setString(1, userName.getText().trim());
            checkdata.setString(2, passwordField.getText().trim());
            
            ResultSet r = checkdata.executeQuery();
            if (r.next()) {
            	int id = r.getInt("user_id");
                String name = r.getString("username");
                String role = r.getString("role");
                showModernAlert(true, "Welcome " + name, "Logged in as " + role);
                for (int i = 0; i  < Main.users.size(); i++) {
                	if (Main.users.get(i).getUserId() == id) {
                		Main.currentUser = Main.users.get(i);
                		break;
                	}
                }
              //  new UserStage();
               new SalesManagement();
            } else {
                showModernAlert(false, "Login Failed", "Invalid username or password");
            }
        } catch (Exception ex) {
            showModernAlert(false, "Error", "Connection failed. Please try again.");
        } finally {
            loginButton.setText("Sign In");
            loginButton.setDisable(false);
        }
    }

    private void showModernAlert(boolean isSuccess, String title, String message) {
        Alert alert = new Alert(isSuccess ? AlertType.INFORMATION : AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.setStyle("""
            -fx-background-color: #2c5364;
            -fx-border-color: #ffffff4d;
            -fx-border-width: 1;
            -fx-border-radius: 15;
            -fx-background-radius: 15;
        """);
        
        if (dialogPane.lookup(".content.label") != null) {
            dialogPane.lookup(".content.label").setStyle("""
                -fx-text-fill: white;
                -fx-font-size: 14px;
            """);
        }
        
        dialogPane.lookupAll(".button").forEach(node -> {
            if (node instanceof Button) {
                node.setStyle("""
                    -fx-background-color: #b2e6a1;
                    -fx-text-fill: #2c5364;
                    -fx-font-weight: bold;
                    -fx-background-radius: 10;
                """);
            }
        });
        
        Stage stage = (Stage) dialogPane.getScene().getWindow();
        stage.getIcons().add(new Image(isSuccess ? "true.png" : "false.png"));
        
        alert.showAndWait();
    }

    private void addEntranceAnimation(VBox container) {
        container.setOpacity(0);
        container.setTranslateY(50);
        
        FadeTransition fade = new FadeTransition(Duration.millis(800), container);
        fade.setFromValue(0);
        fade.setToValue(1);
        
        TranslateTransition slide = new TranslateTransition(Duration.millis(800), container);
        slide.setFromY(50);
        slide.setToY(0);
        
        fade.play();
        slide.play();
    }

    private void styleModernInput(TextField field) {
        field.setStyle("""
            -fx-background-color: transparent;
            -fx-text-fill: white;
            -fx-prompt-text-fill: rgba(255, 255, 255, 0.6);
            -fx-border-width: 0;
            -fx-font-size: 14px;
        """);
    }

    private Background createModernGradient() {
        LinearGradient gradient = new LinearGradient(
            0, 0, 1, 1, true, CycleMethod.NO_CYCLE,
            new Stop(0, Color.web("#0f2027")),
            new Stop(0.3, Color.web("#203a43")),
            new Stop(0.7, Color.web("#2c5364")),
            new Stop(1, Color.web("#0f2027"))
        );
        return new Background(new BackgroundFill(gradient, CornerRadii.EMPTY, Insets.EMPTY));
    }

    public Scene getLoginScene() {
        return loginScene;
    }

    public Stage getLoginStage() {
        return loginStage;
    }
}