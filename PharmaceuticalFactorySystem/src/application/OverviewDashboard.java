package application;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import java.sql.*;

public class OverviewDashboard {
    
    private VBox overviewContainer;
    
    private int customersCount = 0;
    private int employeesCount = 0;
    private int productsCount = 0;
    private int suppliersCount = 0;
    private int warehousesCount = 0;
    private int rawMaterialsCount = 0;
    
    public VBox getOverviewDashboard() {
        fetchDatabaseCounts();
        createOverviewContainer();
        return overviewContainer;
    }
    
    private void fetchDatabaseCounts() {
        try {
            customersCount = executeCountQuery("SELECT COUNT(*) FROM customers WHERE active = 1");
            
            employeesCount = executeCountQuery("SELECT COUNT(*) FROM employees WHERE active = 1");
            
            productsCount = executeCountQuery("SELECT COUNT(*) FROM products WHERE active = 1");
            
            suppliersCount = executeCountQuery("SELECT COUNT(*) FROM suppliers WHERE active = 1");
            
            warehousesCount = executeCountQuery("SELECT COUNT(*) FROM warehouses");
            
            rawMaterialsCount = executeCountQuery("SELECT COUNT(*) FROM raw_materials WHERE active = 1");
            
        } catch (SQLException e) {
            System.err.println("Error fetching database counts: " + e.getMessage());
            e.printStackTrace();
            customersCount = 0;
            employeesCount = 0;
            productsCount = 0;
            suppliersCount = 0;
            warehousesCount = 0;
            rawMaterialsCount = 0;
        }
    }
    
    private int executeCountQuery(String query) throws SQLException {
        try (PreparedStatement statement = Main.conn.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {
            
            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
            return 0;
        }
    }
    
    private void createOverviewContainer() {
        overviewContainer = new VBox();
        overviewContainer.setSpacing(30);
        overviewContainer.setPadding(new Insets(40));
        overviewContainer.setStyle("-fx-background-color: #f8fafc;");
        
        VBox headerSection = createHeaderSection();
        
        GridPane cardsGrid = createCardsGrid();
        
        overviewContainer.getChildren().addAll(cardsGrid);
    }
    
    private VBox createHeaderSection() {
        VBox headerSection = new VBox();
        headerSection.setSpacing(8);
        
        Label title = new Label("Overview Dashboard");
        title.setFont(Font.font("System", FontWeight.BOLD, 32));
        title.setTextFill(Color.web("#1e293b"));
        
        Label subtitle = new Label("Management System Overview");
        subtitle.setFont(Font.font("System", FontWeight.NORMAL, 16));
        subtitle.setTextFill(Color.web("#64748b"));
        
        headerSection.getChildren().addAll(title);
        return headerSection;
    }
    
    private GridPane createCardsGrid() {
        GridPane cardsGrid = new GridPane();
        cardsGrid.setHgap(30);
        cardsGrid.setVgap(30);
        cardsGrid.setAlignment(Pos.CENTER);
        
        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(33.33);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPercentWidth(33.33);
        ColumnConstraints col3 = new ColumnConstraints();
        col3.setPercentWidth(33.33);
        cardsGrid.getColumnConstraints().addAll(col1, col2, col3);
        
        VBox customersCard = createOverviewCard("Total Customers", String.valueOf(customersCount), "#ef4444", "👥");
        VBox employeesCard = createOverviewCard("Total Employees", String.valueOf(employeesCount), "#3b82f6", "👨‍💼");
        VBox productsCard = createOverviewCard("Total Products", String.valueOf(productsCount), "#10b981", "📦");
        VBox suppliersCard = createOverviewCard("Total Suppliers", String.valueOf(suppliersCount), "#f59e0b", "🏭");
        VBox warehousesCard = createOverviewCard("Total Warehouses", String.valueOf(warehousesCount), "#8b5cf6", "🏪");
        VBox materialsCard = createOverviewCard("Raw Materials", String.valueOf(rawMaterialsCount), "#06b6d4", "🔧");
        
        cardsGrid.add(customersCard, 0, 0);
        cardsGrid.add(employeesCard, 1, 0);
        cardsGrid.add(productsCard, 2, 0);
        cardsGrid.add(suppliersCard, 0, 1);
        cardsGrid.add(warehousesCard, 1, 1);
        cardsGrid.add(materialsCard, 2, 1);
        
        return cardsGrid;
    }
    
    private VBox createOverviewCard(String title, String count, String accentColor, String icon) {
        VBox card = new VBox();
        card.setAlignment(Pos.CENTER);
        card.setSpacing(16);
        card.setPrefWidth(320);
        card.setPrefHeight(180);
        card.setPadding(new Insets(32));
        card.setStyle(
            "-fx-background-color: white; " +
            "-fx-background-radius: 16; " +
            "-fx-border-radius: 16;"
        );
        
        DropShadow dropShadow = new DropShadow();
        dropShadow.setRadius(20);
        dropShadow.setOffsetX(0);
        dropShadow.setOffsetY(8);
        dropShadow.setColor(Color.web("#00000010"));
        card.setEffect(dropShadow);
        
        VBox iconContainer = new VBox();
        iconContainer.setAlignment(Pos.CENTER);
        iconContainer.setPrefWidth(64);
        iconContainer.setPrefHeight(64);
        iconContainer.setStyle(
            "-fx-background-color: " + accentColor + "20; " +
            "-fx-background-radius: 50;"
        );
        
        Label iconLabel = new Label(icon);
        iconLabel.setFont(Font.font(28));
        iconContainer.getChildren().add(iconLabel);
        
        Label countLabel = new Label(count);
        countLabel.setFont(Font.font("System", FontWeight.BOLD, 36));
        countLabel.setTextFill(Color.web("#1e293b"));

        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("System", FontWeight.MEDIUM, 16));
        titleLabel.setTextFill(Color.web("#64748b"));
        
        card.getChildren().addAll(iconContainer, countLabel, titleLabel);
        
        card.setOnMouseEntered(e -> {
            card.setStyle(
                "-fx-background-color: " + accentColor + "08; " +
                "-fx-background-radius: 16; " +
                "-fx-border-radius: 16; " +
                "-fx-border-color: " + accentColor + "40; " +
                "-fx-border-width: 2;"
            );
        });
        
        card.setOnMouseExited(e -> {
            card.setStyle(
                "-fx-background-color: white; " +
                "-fx-background-radius: 16; " +
                "-fx-border-radius: 16;"
            );
        });
        
        return card;
    }
    public VBox getAll() {
    	return overviewContainer;
    }
    
}