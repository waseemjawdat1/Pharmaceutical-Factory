package application;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.sql.*;
import java.util.*;

public class SalesStatistics {

    private Stage mainStage;
    private final ComboBox<Integer> monthBox = new ComboBox<>();
    private final ComboBox<Integer> yearBox = new ComboBox<>();

    public SalesStatistics() {
        mainStage = new Stage();
        mainStage.setTitle("📊 Sales Analytics Dashboard");
        mainStage.setMaximized(true);

        // Main container with solid gradient-like background
        VBox root = new VBox(30);
        root.setPadding(new Insets(40));
        root.setStyle("-fx-background-color: linear-gradient(to bottom right, #667eea, #764ba2);");

        // Title section
        Label titleLabel = createTitle();
        
        // Date selection panel
        HBox datePanel = createDateSelectionPanel();
        
        // Cards container
        GridPane cardsGrid = createCardsGrid();

        root.getChildren().addAll(titleLabel, datePanel, cardsGrid);

        Scene scene = new Scene(root);
        mainStage.setScene(scene);
        mainStage.show();
    }

    private Label createTitle() {
        Label titleLabel = new Label("📊 Sales Analytics Dashboard");
        titleLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 42));
        titleLabel.setTextFill(Color.WHITE);
        titleLabel.setAlignment(Pos.CENTER);
        
        // Enhanced drop shadow
        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.rgb(0, 0, 0, 0.5));
        shadow.setRadius(15);
        shadow.setOffsetX(3);
        shadow.setOffsetY(3);
        titleLabel.setEffect(shadow);
        
        return titleLabel;
    }

    private HBox createDateSelectionPanel() {
        HBox datePanel = new HBox(30);
        datePanel.setAlignment(Pos.CENTER);
        datePanel.setPadding(new Insets(25));
        datePanel.setStyle("""
            -fx-background-color: rgba(255, 255, 255, 0.9);
            -fx-background-radius: 20;
            -fx-border-color: rgba(255, 255, 255, 0.5);
            -fx-border-radius: 20;
            -fx-border-width: 1;
            """);

        // Add shadow to date panel
        DropShadow panelShadow = new DropShadow();
        panelShadow.setColor(Color.rgb(0, 0, 0, 0.3));
        panelShadow.setRadius(15);
        panelShadow.setOffsetY(8);
        datePanel.setEffect(panelShadow);

        // Setup combo boxes
        setupComboBoxes();

        Label dateIcon = new Label("📅");
        dateIcon.setFont(Font.font(24));
        
        Label selectLabel = new Label("Select Analysis Period");
        selectLabel.setFont(Font.font("Segoe UI", FontWeight.SEMI_BOLD, 18));
        selectLabel.setTextFill(Color.BLACK);
        
        Label monthLabel = new Label("Month:");
        monthLabel.setFont(Font.font("Segoe UI", FontWeight.MEDIUM, 14));
        monthLabel.setTextFill(Color.BLACK);
        
        Label yearLabel = new Label("Year:");
        yearLabel.setFont(Font.font("Segoe UI", FontWeight.MEDIUM, 14));
        yearLabel.setTextFill(Color.BLACK);

        datePanel.getChildren().addAll(dateIcon, selectLabel, monthLabel, monthBox, yearLabel, yearBox);
        
        return datePanel;
    }

    private void setupComboBoxes() {
        // Populate combo boxes
        for (int i = 1; i <= 12; i++) monthBox.getItems().add(i);
        for (int y = 2020; y <= 2030; y++) yearBox.getItems().add(y);

        monthBox.setPromptText("Select Month");
        yearBox.setPromptText("Select Year");
        
        // Enhanced styling for combo boxes
        String comboStyle = """
            -fx-background-color: white;
            -fx-border-color: #cccccc;
            -fx-border-radius: 12;
            -fx-background-radius: 12;
            -fx-padding: 12 16;
            -fx-font-size: 14px;
            -fx-font-weight: bold;
            -fx-pref-width: 160;
            -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 8, 0, 0, 3);
            """;
        
        monthBox.setStyle(comboStyle);
        yearBox.setStyle(comboStyle);
    }

    private GridPane createCardsGrid() {
        GridPane grid = new GridPane();
        grid.setHgap(30);
        grid.setVgap(30);
        grid.setAlignment(Pos.CENTER);
        grid.setPadding(new Insets(20));

        // Create cards
        VBox pieCard = createCard("🥧", "Sales Distribution", 
            "View sales performance distribution across all sales employees", 
            "#FF6B6B", this::openPieChartStage);
            
        VBox barCard = createCard("📊", "Daily Orders", 
            "Analyze daily order count trends and patterns", 
            "#4ECDC4", this::openBarChartStage);
            
        VBox histCard = createCard("🏆", "Top Products", 
            "Discover your best-selling products and quantities", 
            "#45B7D1", this::openHistogramStage);
            
        VBox lineCard = createCard("📈", "Sales Trend", 
            "Track daily sales revenue trends over time", 
            "#96CEB4", this::openLineChartStage);
            
        VBox stackedCard = createCard("👥", "Employee Performance", 
            "Compare individual employee sales performance", 
            "#FECA57", this::openStackedChartStage);

        // Arrange cards in grid with proper centering
        grid.add(pieCard, 0, 0);
        grid.add(barCard, 1, 0);
        grid.add(histCard, 2, 0);
        
        // Create a container for the second row to center it
        HBox secondRowContainer = new HBox(30);
        secondRowContainer.setAlignment(Pos.CENTER);
        secondRowContainer.getChildren().addAll(lineCard, stackedCard);
        
        // Add the second row container spanning all columns
        grid.add(secondRowContainer, 0, 1, 3, 1);

        return grid;
    }

    private VBox createCard(String icon, String title, String description, String color, Runnable action) {
        VBox card = new VBox(20);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(30));
        card.setPrefSize(280, 200);
        card.setMaxSize(280, 200);
        
        // Card styling with gradient
        card.setStyle(String.format("""
            -fx-background-color: white;
            -fx-background-radius: 20;
            -fx-border-color: rgba(255, 255, 255, 0.3);
            -fx-border-radius: 20;
            -fx-border-width: 1;
            -fx-cursor: hand;
            """));

        // Card shadow
        DropShadow cardShadow = new DropShadow();
        cardShadow.setColor(Color.rgb(0, 0, 0, 0.15));
        cardShadow.setRadius(20);
        cardShadow.setOffsetY(10);
        card.setEffect(cardShadow);

        // Icon with colored background
        Label iconLabel = new Label(icon);
        iconLabel.setFont(Font.font(48));
        iconLabel.setPrefSize(80, 80);
        iconLabel.setAlignment(Pos.CENTER);
        iconLabel.setStyle(String.format("""
            -fx-background-color: %s;
            -fx-background-radius: 40;
            -fx-text-fill: white;
            """, color));

        // Title
        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 18));
        titleLabel.setTextFill(Color.rgb(60, 60, 60));
        titleLabel.setAlignment(Pos.CENTER);

        // Description
        Label descLabel = new Label(description);
        descLabel.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 12));
        descLabel.setTextFill(Color.rgb(120, 120, 120));
        descLabel.setWrapText(true);
        descLabel.setAlignment(Pos.CENTER);
        descLabel.setMaxWidth(220);

        card.getChildren().addAll(iconLabel, titleLabel, descLabel);

        // Hover effects
        card.setOnMouseEntered(e -> {
            card.setStyle(String.format("""
                -fx-background-color: %s;
                -fx-background-radius: 20;
                -fx-border-color: rgba(255, 255, 255, 0.3);
                -fx-border-radius: 20;
                -fx-border-width: 1;
                -fx-cursor: hand;
                -fx-scale-x: 1.05;
                -fx-scale-y: 1.05;
                """, color));
            
            titleLabel.setTextFill(Color.WHITE);
            descLabel.setTextFill(Color.rgb(240, 240, 240));
            
            // Enhanced shadow on hover
            DropShadow hoverShadow = new DropShadow();
            hoverShadow.setColor(Color.rgb(0, 0, 0, 0.25));
            hoverShadow.setRadius(25);
            hoverShadow.setOffsetY(15);
            card.setEffect(hoverShadow);
        });

        card.setOnMouseExited(e -> {
            card.setStyle("""
                -fx-background-color: white;
                -fx-background-radius: 20;
                -fx-border-color: rgba(255, 255, 255, 0.3);
                -fx-border-radius: 20;
                -fx-border-width: 1;
                -fx-cursor: hand;
                -fx-scale-x: 1.0;
                -fx-scale-y: 1.0;
                """);
            
            titleLabel.setTextFill(Color.rgb(60, 60, 60));
            descLabel.setTextFill(Color.rgb(120, 120, 120));
            card.setEffect(cardShadow);
        });

        // Click action
        card.setOnMouseClicked(e -> action.run());

        return card;
    }

    private boolean validateInput() {
        if (monthBox.getValue() == null || yearBox.getValue() == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Input Required");
            alert.setHeaderText("Please select both month and year");
            alert.setContentText("Both month and year must be selected to generate the chart.");
            
            // Style the alert
            DialogPane dialogPane = alert.getDialogPane();
            dialogPane.setStyle("""
                -fx-background-color: white;
                -fx-font-family: 'Segoe UI';
                """);
            
            alert.showAndWait();
            return false;
        }
        return true;
    }

    private Stage createChartStage(String title, String icon) {
        Stage chartStage = new Stage();
        chartStage.setTitle(icon + " " + title);
        chartStage.setWidth(1000);
        chartStage.setHeight(700);
        
        return chartStage;
    }

    private void openPieChartStage() {
        if (!validateInput()) return;
        
        Stage stage = createChartStage("Sales Distribution", "🥧");
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: linear-gradient(to bottom right, #667eea, #764ba2);");

        // Chart container
        VBox chartContainer = new VBox(15);
        chartContainer.setAlignment(Pos.CENTER);
        chartContainer.setPadding(new Insets(20));
        chartContainer.setStyle("""
            -fx-background-color: white;
            -fx-background-radius: 15;
            """);

        ObservableList<PieChart.Data> data = FXCollections.observableArrayList();
        String sql = "SELECT e.name, SUM(so.total_amount) AS total_sales FROM sales_orders so " +
                     "JOIN employees e ON so.employee_id = e.employee_id " +
                     "JOIN departments d ON e.department_id = d.department_id " +
                     "WHERE d.name = 'Sales' AND MONTH(so.order_date) = ? AND YEAR(so.order_date) = ? " +
                     "GROUP BY e.name";
        
        try (PreparedStatement stmt = Main.conn.prepareStatement(sql)) {
            stmt.setInt(1, monthBox.getValue());
            stmt.setInt(2, yearBox.getValue());
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                data.add(new PieChart.Data(rs.getString("name"), rs.getDouble("total_sales")));
            }
            
            PieChart chart = new PieChart(data);
            chart.setTitle("Sales Distribution by Employee");
            styleChart(chart);
            
            Label infoLabel = new Label("🥧 Pie chart showing total sales distribution per employee in Sales department for " + 
                            getMonthName(monthBox.getValue()) + " " + yearBox.getValue());
            infoLabel.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 14));
            infoLabel.setWrapText(true);
            infoLabel.setTextFill(Color.rgb(100, 100, 100));
            infoLabel.setAlignment(Pos.CENTER);
            
            chartContainer.getChildren().addAll(chart, infoLabel);
            root.setCenter(chartContainer);
            
        } catch (SQLException ex) {
            showDatabaseError(ex);
            return;
        }

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    private void openBarChartStage() {
        if (!validateInput()) return;
        
        Stage stage = createChartStage("Daily Orders", "📊");
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: linear-gradient(to bottom right, #4ECDC4, #44A08D);");

        VBox chartContainer = new VBox(15);
        chartContainer.setAlignment(Pos.CENTER);
        chartContainer.setPadding(new Insets(20));
        chartContainer.setStyle("""
            -fx-background-color: white;
            -fx-background-radius: 15;
            """);

        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Date");
        yAxis.setLabel("Number of Orders");
        
        BarChart<String, Number> chart = new BarChart<>(xAxis, yAxis);
        chart.setTitle("Daily Orders Count");
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Orders");
        
        String sql = "SELECT so.order_date, COUNT(*) AS order_count FROM sales_orders so " +
                     "JOIN employees e ON so.employee_id = e.employee_id " +
                     "JOIN departments d ON e.department_id = d.department_id " +
                     "WHERE d.name = 'Sales' AND MONTH(so.order_date) = ? AND YEAR(so.order_date) = ? " +
                     "GROUP BY so.order_date ORDER BY so.order_date";
        
        try (PreparedStatement stmt = Main.conn.prepareStatement(sql)) {
            stmt.setInt(1, monthBox.getValue());
            stmt.setInt(2, yearBox.getValue());
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                series.getData().add(new XYChart.Data<>(rs.getDate("order_date").toString(), rs.getInt("order_count")));
            }
            chart.getData().add(series);
            styleChart(chart);
            
            Label infoLabel = new Label("📊 Bar chart displaying daily order count by Sales department for " + 
                            getMonthName(monthBox.getValue()) + " " + yearBox.getValue());
            infoLabel.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 14));
            infoLabel.setWrapText(true);
            infoLabel.setTextFill(Color.rgb(100, 100, 100));
            infoLabel.setAlignment(Pos.CENTER);
            
            chartContainer.getChildren().addAll(chart, infoLabel);
            root.setCenter(chartContainer);
            
        } catch (SQLException ex) {
            showDatabaseError(ex);
            return;
        }

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    private void openHistogramStage() {
        if (!validateInput()) return;
        
        Stage stage = createChartStage("Top Products", "🏆");
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: linear-gradient(to bottom right, #45B7D1, #2980B9);");

        VBox chartContainer = new VBox(15);
        chartContainer.setAlignment(Pos.CENTER);
        chartContainer.setPadding(new Insets(20));
        chartContainer.setStyle("""
            -fx-background-color: white;
            -fx-background-radius: 15;
            """);

        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Product");
        yAxis.setLabel("Quantity Sold");
        
        BarChart<String, Number> chart = new BarChart<>(xAxis, yAxis);
        chart.setTitle("Top 5 Best Selling Products");
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Quantity");

        String sql = """
            SELECT p.name, SUM(d.quantity) AS total_quantity
            FROM sales_order_details d
            JOIN sales_orders so ON d.sales_order_id = so.sales_order_id
            JOIN employees e ON so.employee_id = e.employee_id
            JOIN departments dep ON e.department_id = dep.department_id
            JOIN products p ON d.product_id = p.product_id
            WHERE dep.name = 'Sales' AND MONTH(so.order_date) = ? AND YEAR(so.order_date) = ?
            GROUP BY p.name ORDER BY total_quantity DESC LIMIT 5
        """;

        try (PreparedStatement stmt = Main.conn.prepareStatement(sql)) {
            stmt.setInt(1, monthBox.getValue());
            stmt.setInt(2, yearBox.getValue());
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                series.getData().add(new XYChart.Data<>(rs.getString("name"), rs.getInt("total_quantity")));
            }

            chart.getData().add(series);
            styleChart(chart);
            
            Label infoLabel = new Label("🏆 Top 5 products by quantity sold by Sales department in " + 
                            getMonthName(monthBox.getValue()) + " " + yearBox.getValue());
            infoLabel.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 14));
            infoLabel.setWrapText(true);
            infoLabel.setTextFill(Color.rgb(100, 100, 100));
            infoLabel.setAlignment(Pos.CENTER);
            
            chartContainer.getChildren().addAll(chart, infoLabel);
            root.setCenter(chartContainer);

        } catch (SQLException ex) {
            showDatabaseError(ex);
            return;
        }

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    private void openLineChartStage() {
        if (!validateInput()) return;
        
        Stage stage = createChartStage("Sales Trend", "📈");
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: linear-gradient(to bottom right, #96CEB4, #2ECC71);");

        VBox chartContainer = new VBox(15);
        chartContainer.setAlignment(Pos.CENTER);
        chartContainer.setPadding(new Insets(20));
        chartContainer.setStyle("""
            -fx-background-color: white;
            -fx-background-radius: 15;
            """);

        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Date");
        yAxis.setLabel("Sales Amount ($)");
        
        LineChart<String, Number> chart = new LineChart<>(xAxis, yAxis);
        chart.setTitle("Daily Sales Trend");
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Sales Total");

        String sql = """
            SELECT so.order_date, SUM(so.total_amount) AS daily_sales
            FROM sales_orders so
            JOIN employees e ON so.employee_id = e.employee_id
            JOIN departments d ON e.department_id = d.department_id
            WHERE d.name = 'Sales' AND MONTH(so.order_date) = ? AND YEAR(so.order_date) = ?
            GROUP BY so.order_date ORDER BY so.order_date
        """;

        try (PreparedStatement stmt = Main.conn.prepareStatement(sql)) {
            stmt.setInt(1, monthBox.getValue());
            stmt.setInt(2, yearBox.getValue());
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                series.getData().add(new XYChart.Data<>(rs.getDate("order_date").toString(), rs.getDouble("daily_sales")));
            }

            chart.getData().add(series);
            styleChart(chart);
            
            Label infoLabel = new Label("📈 Daily sales trend showing total revenue by Sales department for " + 
                            getMonthName(monthBox.getValue()) + " " + yearBox.getValue());
            infoLabel.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 14));
            infoLabel.setWrapText(true);
            infoLabel.setTextFill(Color.rgb(100, 100, 100));
            infoLabel.setAlignment(Pos.CENTER);
            
            chartContainer.getChildren().addAll(chart, infoLabel);
            root.setCenter(chartContainer);
            
        } catch (SQLException ex) {
            showDatabaseError(ex);
            return;
        }

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    private void openStackedChartStage() {
        if (!validateInput()) return;
        
        Stage stage = createChartStage("Employee Performance", "👥");
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: linear-gradient(to bottom right, #FECA57, #F39C12);");

        VBox chartContainer = new VBox(15);
        chartContainer.setAlignment(Pos.CENTER);
        chartContainer.setPadding(new Insets(20));
        chartContainer.setStyle("""
            -fx-background-color: white;
            -fx-background-radius: 15;
            """);

        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Date");
        yAxis.setLabel("Sales Amount ($)");
        
        StackedBarChart<String, Number> chart = new StackedBarChart<>(xAxis, yAxis);
        chart.setTitle("Daily Sales Performance by Employee");

        String sql = """
            SELECT e.name, so.order_date, SUM(so.total_amount) AS daily_employee_sales
            FROM sales_orders so
            JOIN employees e ON so.employee_id = e.employee_id
            JOIN departments d ON e.department_id = d.department_id
            WHERE d.name = 'Sales' AND MONTH(so.order_date) = ? AND YEAR(so.order_date) = ?
            GROUP BY e.name, so.order_date ORDER BY e.name, so.order_date
        """;

        try (PreparedStatement stmt = Main.conn.prepareStatement(sql)) {
            stmt.setInt(1, monthBox.getValue());
            stmt.setInt(2, yearBox.getValue());
            ResultSet rs = stmt.executeQuery();

            Map<String, XYChart.Series<String, Number>> seriesMap = new LinkedHashMap<>();
            while (rs.next()) {
                String emp = rs.getString("name");
                String date = rs.getDate("order_date").toString();
                double total = rs.getDouble("daily_employee_sales");

                seriesMap.putIfAbsent(emp, new XYChart.Series<>());
                seriesMap.get(emp).setName(emp);
                seriesMap.get(emp).getData().add(new XYChart.Data<>(date, total));
            }

            chart.getData().addAll(seriesMap.values());
            styleChart(chart);
            
            Label infoLabel = new Label("👥 Employee performance comparison showing daily sales distribution for " + 
                            getMonthName(monthBox.getValue()) + " " + yearBox.getValue());
            infoLabel.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 14));
            infoLabel.setWrapText(true);
            infoLabel.setTextFill(Color.rgb(100, 100, 100));
            infoLabel.setAlignment(Pos.CENTER);
            
            chartContainer.getChildren().addAll(chart, infoLabel);
            root.setCenter(chartContainer);

        } catch (SQLException ex) {
            showDatabaseError(ex);
            return;
        }

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    private void styleChart(Chart chart) {
        chart.setAnimated(true);
        chart.setLegendVisible(true);
        chart.setPrefSize(800, 500);
    }
    
    private String getMonthName(int month) {
        String[] months = {"", "January", "February", "March", "April", "May", "June",
                          "July", "August", "September", "October", "November", "December"};
        return months[month];
    }
    
    private void showDatabaseError(SQLException ex) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Database Error");
        alert.setHeaderText("Failed to load data");
        alert.setContentText("There was an error accessing the database: " + ex.getMessage());
        
        // Style the alert
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.setStyle("""
            -fx-background-color: white;
            -fx-font-family: 'Segoe UI';
            """);
        
        alert.showAndWait();
        ex.printStackTrace();
    }

    public void show() {
        mainStage.show();
    }
}