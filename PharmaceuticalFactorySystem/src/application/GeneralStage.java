package application;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class GeneralStage {

	private Stage stage = new Stage();
	private VBox sidebar;
	private BorderPane contentArea;
	private Label pageTitle;
	private List<Button> navigationButtons;
	private String currentUserName;
	private String currentUserRole;

	public GeneralStage(String role, String name) {
		this.navigationButtons = new ArrayList<>();
		this.currentUserName = name;
		this.currentUserRole = role;
		setupStage(role);

		if (role.equalsIgnoreCase("admin")) {
			addNavigationTab("Overview", ()->{
				setPageTitle("Overview Page");
				setContent((VBox) (new OverviewDashboard().getOverviewDashboard()));
			});
			addNavigationTab("Users", () -> {
				setPageTitle("Users Management");
				setContent((VBox) (new UserStage().getAll()));
			});
			addNavigationTab("Departments", () -> {
				setPageTitle("Department Management");
				setContent((VBox) (new DepartmentStage1().getAll()));
			});
			addNavigationTab("Employees", () -> {
				setPageTitle("Employee Management");
				setContent((VBox) (new EmployeeStage().getAll()));
			});
			addNavigationTab("Customers", () -> {
				setPageTitle("Customer Management");
				setContent((VBox) (new CustomerStage().getAll()));
			});
			addNavigationTab("Sales Management", () -> {
				setPageTitle("Sales Management");
				setContent((VBox) (new SalesManagement().getAll()));
			});
			addNavigationTab("Sales Statistics", () -> {
				setPageTitle("📊 Sales Analytics Dashboard");
				setContent((VBox) (new SalesStatistics().getAll()));
			});
			addNavigationTab("Products", () -> {
				setPageTitle("Product Management");
				setContent((VBox) (new ProductStage().getAll()));
			});
			addNavigationTab("Production Batches", () -> {
				setPageTitle("Production Batches");
				setContent((VBox) (new ProductionBatchTableView().getAll()));
			});
			addNavigationTab("Production Statistics", () -> {
				setPageTitle("Production Statistics");
				setContent((VBox) (new ProductionStatistics().getAll()));
			});
			addNavigationTab("Warehouses", () -> {
				setPageTitle("Warehouse Management");
				setContent((VBox) (new WarehouseStage().getAll()));
			});
			addNavigationTab("Suppliers", () -> {
				setPageTitle("Suppliers Management");
				setContent((VBox) (new SupplierStage().getAll()));
			});
			addNavigationTab("Raw Materials", () -> {
				setPageTitle("Raw Materials Management");
				setContent((VBox) (new RawMaterialStage().getAll()));
			});
			addNavigationTab("Purchase Management", () -> {
				setPageTitle("Purchase Management");
				setContent((VBox) (new PurchaseManagement().getAll()));
			});
			addNavigationTab("Purchase Statistics", () -> {
				setPageTitle("📊 Purchase Analytics Dashboard");
				setContent((VBox) (new PurchaseStatistics().getAll()));
			});
			
			
			
			

		} else if (role.equalsIgnoreCase("Sales Manager")) {
			addNavigationTab("Customers", () -> {
				setPageTitle("Customer Management");
				setContent((VBox) (new CustomerStage().getAll()));
			});
			addNavigationTab("Sales Management", () -> {
				setPageTitle("Sales Management");
				setContent((VBox) (new SalesManagement().getAll()));
			});
			addNavigationTab("Products", () -> {
				setPageTitle("Product Management");
				ProductStage productStage = new ProductStage();
				productStage.removeBox();
				setContent((VBox) (productStage.getAll()));
			});
			addNavigationTab("Sales Statistics", () -> {
				setPageTitle("📊 Sales Analytics Dashboard");
				setContent((VBox) (new SalesStatistics().getAll()));
			});
			
			
		} else if (role.equalsIgnoreCase("Purchase Manager")) {
			addNavigationTab("Warehouses", () -> {
				setPageTitle("Warehouse Management");
				setContent((VBox) (new WarehouseStage().getAll()));
			});
			addNavigationTab("Suppliers", () -> {
				setPageTitle("Suppliers Management");
				setContent((VBox) (new SupplierStage().getAll()));
			});
			addNavigationTab("Raw Materials", () -> {
				setPageTitle("Raw Materials Management");
				setContent((VBox) (new RawMaterialStage().getAll()));
			});
			addNavigationTab("Purchase Management", () -> {
				setPageTitle("Purchase Management");
				setContent((VBox) (new PurchaseManagement().getAll()));
			});
			addNavigationTab("Purchase Statistics", () -> {
				setPageTitle("📊 Purchase Analytics Dashboard");
				setContent((VBox) (new PurchaseStatistics().getAll()));
			});

		}
		else if (role.equalsIgnoreCase("Pre Production")) {
			addNavigationTab("Products", () -> {
				setPageTitle("Product Management");
				setContent((VBox) (new ProductStage().getAllForPre()));
			});
			addNavigationTab("Raw Materials", () -> {
				setPageTitle("Raw Materials Management");
				setContent((VBox) (new RawMaterialStage().getAll()));
			});
			addNavigationTab("Production Batches", () -> {
				setPageTitle("Production Batches");
				setContent((VBox) (new ProductionBatchTableView().getAll()));
			});
			addNavigationTab("Production Statistics", () -> {
				setPageTitle("Production Statistics");
				setContent((VBox) (new ProductionStatistics().getAll()));
			});
		}
		else if (role.equalsIgnoreCase("Production")) {
			addNavigationTab("Products", () -> {
				setPageTitle("Product Management");
				setContent((VBox) (new ProductStage().getAllForProduction()));
			});
			addNavigationTab("Raw Materials", () -> {
				setPageTitle("Raw Materials Management");
				setContent((VBox) (new RawMaterialStage().getAll()));
			});
			addNavigationTab("Production Batches", () -> {
				setPageTitle("Production Batches");
				setContent((VBox) (new ProductionBatchTableView().getAll()));
			});
			addNavigationTab("Production Statistics", () -> {
				setPageTitle("Production Statistics");
				setContent((VBox) (new ProductionStatistics().getAll()));
			});
		}

		stage.show();
	}

	public GeneralStage(Stage stage, String userName, String userRole) {
		this.stage = stage;
		this.navigationButtons = new ArrayList<>();
		this.currentUserName = userName;
		this.currentUserRole = userRole;
		setupStage(userRole);
	}

	private void setupStage(String role) {
		stage.setTitle("Admin Dashboard - Management System");

		BorderPane root = new BorderPane();
		root.setStyle("-fx-background-color: linear-gradient(to bottom right, #fefeff, #fffeff);");

		sidebar = createSidebar(role);
		root.setLeft(sidebar);

		VBox mainContent = createMainContent();
		root.setCenter(mainContent);

		Scene scene = new Scene(root, 1530, 785);
		scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());

		stage.setScene(scene);
		stage.show();
	}

	private VBox createSidebar(String role) {
		VBox sidebarContainer = new VBox();
		sidebarContainer.setPrefWidth(280);
		sidebarContainer.setMinWidth(280);
		sidebarContainer.setMaxWidth(280);
		sidebarContainer.setStyle("-fx-background-color: rgba(255, 255, 255, 0.95); " + "-fx-border-width: 0 1 0 0; "
				+ "-fx-border-color: rgba(0, 0, 0, 0.1);");

		DropShadow dropShadow = new DropShadow();
		dropShadow.setColor(Color.rgb(0, 0, 0, 0.1));
		dropShadow.setOffsetX(8);
		dropShadow.setRadius(32);
		sidebarContainer.setEffect(dropShadow);

		VBox header = createSidebarHeader(role);

		ScrollPane scrollPane = new ScrollPane();
		scrollPane.setFitToWidth(true);
		scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
		scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
		scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent;");

		VBox navMenu = new VBox();
		navMenu.setPadding(new Insets(20, 0, 20, 0));
		navMenu.setSpacing(5);

		scrollPane.setContent(navMenu);

		sidebarContainer.getChildren().addAll(header, scrollPane);
		VBox.setVgrow(scrollPane, Priority.ALWAYS);

		return sidebarContainer;
	}

	private VBox createSidebarHeader(String role) {
		VBox header = new VBox();
		header.setPadding(new Insets(30, 25, 30, 25));
		header.setAlignment(Pos.CENTER_LEFT);

		LinearGradient gradient = new LinearGradient(0, 0, 1, 1, true, null, new Stop(0, Color.web("#E8E5FF")),
				new Stop(1, Color.web("#F0EDFF")));

		BackgroundFill bgFill = new BackgroundFill(gradient, null, null);
		header.setBackground(new Background(bgFill));

		Label title = new Label("Admin Panel");
		if (role.equalsIgnoreCase("Sales Manager"))
			title.setText("Sales Panel");
		else if (role.equalsIgnoreCase("Purchase Manager"))
			title.setText("Purchase Panel");
		else if (role.equalsIgnoreCase("Pre Production")) {
			title.setText("PreProduction Panel");
		}
		else if (role.equalsIgnoreCase("Production")) {
			title.setText("Production Panel");

		}

		title.setTextFill(Color.web("#4A4A4A"));
		title.setFont(Font.font("System", FontWeight.BOLD, 24));
		title.setStyle("-fx-font-family: 'Georgia';");
		Label subtitle = new Label("Management System");
		subtitle.setTextFill(Color.web("#6B6B6B"));
		subtitle.setFont(Font.font("Georgia", 14));

		header.getChildren().addAll(title, subtitle);
		return header;
	}

	private VBox createMainContent() {
		VBox mainContent = new VBox();

		HBox topHeader = createTopHeader();

		contentArea = new BorderPane();
		contentArea.setPadding(new Insets(30));
		contentArea.setStyle("-fx-background-color: transparent;");

		setDefaultContent();

		mainContent.getChildren().addAll(topHeader, contentArea);
		VBox.setVgrow(contentArea, Priority.ALWAYS);

		return mainContent;
	}

	private HBox createTopHeader() {
		HBox topHeader = new HBox();
		topHeader.setPadding(new Insets(20, 30, 20, 30));
		topHeader.setAlignment(Pos.CENTER_LEFT);
		topHeader.setStyle("-fx-background-color: rgba(255, 255, 255, 0.95); " + "-fx-border-width: 0 0 1 0; "
				+ "-fx-border-color: rgba(0, 0, 0, 0.1);");

		DropShadow dropShadow = new DropShadow();
		dropShadow.setColor(Color.rgb(0, 0, 0, 0.1));
		dropShadow.setOffsetY(2);
		dropShadow.setRadius(20);
		topHeader.setEffect(dropShadow);

		pageTitle = new Label("Dashboard");
		pageTitle.setFont(Font.font("Georgia", FontWeight.BOLD, 28));
		pageTitle.setTextFill(Color.web("#333333"));

		Region spacer = new Region();
		HBox.setHgrow(spacer, Priority.ALWAYS);

		HBox userInfo = createUserInfo();

		topHeader.getChildren().addAll(pageTitle, spacer, userInfo);
		return topHeader;
	}

	private HBox createUserInfo() {
		HBox userInfo = new HBox();
		userInfo.setAlignment(Pos.CENTER_RIGHT);
		userInfo.setSpacing(15);

		VBox userDetails = new VBox();
		userDetails.setAlignment(Pos.CENTER_RIGHT);

		Label userName = new Label(currentUserName);
		userName.setFont(Font.font("System", FontWeight.SEMI_BOLD, 16));
		userName.setTextFill(Color.web("#333333"));

		Label userRole = new Label(currentUserRole);
		userRole.setFont(Font.font("System", 13));
		userRole.setTextFill(Color.web("#666666"));

		userDetails.getChildren().addAll(userName, userRole);

		Circle avatar = new Circle(22.5);

		LinearGradient avatarGradient = new LinearGradient(0, 0, 1, 1, true, null, new Stop(0, Color.web("#8B5CF6")),
				new Stop(1, Color.web("#A855F7")));
		avatar.setFill(avatarGradient);

		DropShadow avatarShadow = new DropShadow();
		avatarShadow.setColor(Color.web("#8B5CF6", 0.3));
		avatarShadow.setRadius(15);
		avatar.setEffect(avatarShadow);

		StackPane avatarContainer = new StackPane();
		Label initials = new Label(getInitials(currentUserName));
		initials.setTextFill(Color.WHITE);
		initials.setFont(Font.font("System", FontWeight.BOLD, 18));
		avatarContainer.getChildren().addAll(avatar, initials);

		userInfo.getChildren().addAll(userDetails, avatarContainer);
		return userInfo;
	}

	private String getInitials(String name) {
		if (name == null || name.trim().isEmpty())
			return "U";
		String[] parts = name.trim().split("\\s+");
		if (parts.length >= 2) {
			return (parts[0].charAt(0) + "" + parts[1].charAt(0)).toUpperCase();
		}
		return name.substring(0, 1).toUpperCase();
	}

	private void setDefaultContent() {
		VBox defaultContent = new VBox();
		defaultContent.setAlignment(Pos.CENTER);
		defaultContent.setSpacing(20);
		defaultContent.setStyle("-fx-background-color: rgba(255, 255, 255, 0.95); " + "-fx-background-radius: 20; "
				+ "-fx-padding: 60;");

		DropShadow shadow = new DropShadow();
		shadow.setColor(Color.rgb(0, 0, 0, 0.1));
		shadow.setRadius(40);
		defaultContent.setEffect(shadow);

		Label welcomeLabel = new Label("Welcome to Admin Dashboard");
		welcomeLabel.setFont(Font.font("Georgia", FontWeight.BOLD, 32));
		welcomeLabel.setTextFill(Color.web("#8B5CF6"));

		Label instructionLabel = new Label("Select a tab from the sidebar to get started");
		instructionLabel.setFont(Font.font("Georgia", 16));
		instructionLabel.setTextFill(Color.web("#666666"));

		defaultContent.getChildren().addAll(welcomeLabel, instructionLabel);
		contentArea.setCenter(defaultContent);
	}

	/**
	 * Add a navigation tab to the sidebar
	 * 
	 * @param tabName       The name of the tab
	 * @param onClickAction The action to perform when clicked (Runnable)
	 * @return The created button for additional customization if needed
	 */
	public Button addNavigationTab(String tabName, Runnable onClickAction) {
		Button navButton = createNavigationButton(tabName, onClickAction);

		ScrollPane scrollPane = (ScrollPane) sidebar.getChildren().get(1);
		VBox navMenu = (VBox) scrollPane.getContent();
		navMenu.getChildren().add(navButton);

		navigationButtons.add(navButton);
		return navButton;
	}

	/**
	 * Set content for the main content area
	 * 
	 * @param content The content node to display
	 */
	public void setContent(Node content) {
		contentArea.setCenter(content);
	}

	/**
	 * Set the page title in the top header
	 * 
	 * @param title The title to display
	 */
	public void setPageTitle(String title) {
		pageTitle.setText(title);
	}

	/**
	 * Clear all navigation tabs
	 */
	public void clearNavigationTabs() {
		ScrollPane scrollPane = (ScrollPane) sidebar.getChildren().get(1);
		VBox navMenu = (VBox) scrollPane.getContent();
		navMenu.getChildren().clear();
		navigationButtons.clear();
	}

	/**
	 * Set active navigation tab
	 * 
	 * @param tabName The name of the tab to set as active
	 */
	public void setActiveTab(String tabName) {
		for (Button button : navigationButtons) {
			if (button.getText().equals(tabName)) {
				button.setStyle(getActiveTabStyle());
			} else {
				button.setStyle(getInactiveTabStyle());
			}
		}
	}

	/**
	 * Get the stage
	 * 
	 * @return The JavaFX Stage
	 */
	public Stage getStage() {
		return stage;
	}

	/**
	 * Update user information
	 * 
	 * @param userName The user name
	 * @param userRole The user role
	 */
	public void updateUserInfo(String userName, String userRole) {
		this.currentUserName = userName;
		this.currentUserRole = userRole;

		BorderPane root = (BorderPane) stage.getScene().getRoot();
		VBox mainContent = createMainContent();
		root.setCenter(mainContent);
	}

	private Button createNavigationButton(String text, Runnable onClickAction) {
		Button button = new Button(text);
		button.setPrefWidth(280);
		button.setPrefHeight(50);
		button.setAlignment(Pos.CENTER_LEFT);
		button.setPadding(new Insets(15, 25, 15, 25));
		button.setStyle(getInactiveTabStyle());

		button.setOnMouseEntered(e -> {
			if (!button.getStyle().contains("rgba(139, 92, 246, 0.15)")) {
				button.setStyle(getHoverTabStyle());
			}
		});

		button.setOnMouseExited(e -> {
			if (!button.getStyle().contains("rgba(139, 92, 246, 0.15)")) {
				button.setStyle(getInactiveTabStyle());
			}
		});

		button.setOnAction(e -> {
			setActiveTab(text);
			if (onClickAction != null) {
				onClickAction.run();
			}
		});

		return button;
	}

	private String getInactiveTabStyle() {
		return "-fx-background-color: transparent; " + "-fx-text-fill: #666666; " + "-fx-font-size: 16px; "
				+ "-fx-font-weight: 500; " + "-fx-border-width: 0 0 0 4; " + "-fx-border-color: transparent; "
				+ "-fx-cursor: hand;";
	}

	private String getHoverTabStyle() {
		return "-fx-background-color: rgba(139, 92, 246, 0.1); " + "-fx-text-fill: #8B5CF6; " + "-fx-font-size: 16px; "
				+ "-fx-font-weight: 500; " + "-fx-border-width: 0 0 0 4; " + "-fx-border-color: #8B5CF6; "
				+ "-fx-cursor: hand;";
	}

	private String getActiveTabStyle() {
		return "-fx-background-color: rgba(139, 92, 246, 0.15); " + "-fx-text-fill: #8B5CF6; " + "-fx-font-size: 16px; "
				+ "-fx-font-weight: 600; " + "-fx-border-width: 0 0 0 4; " + "-fx-border-color: #8B5CF6; "
				+ "-fx-cursor: hand;";
	}
}