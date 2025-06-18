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
import java.sql.Date;
import java.time.LocalDate;
import java.util.*;

public class SalesStatistics {

    private Stage mainStage;
    private final DatePicker fromDatePicker = new DatePicker();
    private final DatePicker toDatePicker = new DatePicker();
    private VBox root;
    
    public SalesStatistics() {
         root = new VBox(30);
        root.setPadding(new Insets(40));

        HBox datePanel = createDateSelectionPanel();
        GridPane cardsGrid = createCardsGrid();

        root.getChildren().addAll(datePanel, cardsGrid);
    }

    private Label createTitle() {
        Label titleLabel = new Label("📊 Sales Analytics Dashboard");
        titleLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 42));
        titleLabel.setTextFill(Color.WHITE);
        titleLabel.setAlignment(Pos.CENTER);
        
        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.rgb(0, 0, 0, 0.5));
        shadow.setRadius(15);
        shadow.setOffsetX(3);
        shadow.setOffsetY(3);
        titleLabel.setEffect(shadow);
        
        return titleLabel;
    }

    private HBox createDateSelectionPanel() {
        HBox datePanel = new HBox(20);
        datePanel.setAlignment(Pos.CENTER);
        datePanel.setPadding(new Insets(20));
        datePanel.setStyle("""
            -fx-background-color: rgba(255, 255, 255, 0.9);
            -fx-background-radius: 20;
            -fx-border-color: rgba(255, 255, 255, 0.5);
            -fx-border-radius: 20;
            -fx-border-width: 1;
            """);

        DropShadow panelShadow = new DropShadow();
        panelShadow.setColor(Color.rgb(0, 0, 0, 0.3));
        panelShadow.setRadius(15);
        panelShadow.setOffsetY(8);
        datePanel.setEffect(panelShadow);

        setupDatePickers();

        Label dateIcon = new Label("📅");
        dateIcon.setFont(Font.font(24));
        
        Label selectLabel = new Label("Select Analysis Period");
        selectLabel.setFont(Font.font("Georgia", FontWeight.SEMI_BOLD, 18));
        selectLabel.setTextFill(Color.BLACK);
        
        Label fromLabel = new Label("From:");
        fromLabel.setFont(Font.font("Georgia", FontWeight.MEDIUM, 16));
        fromLabel.setTextFill(Color.BLACK);
        
        Label toLabel = new Label("To:");
        toLabel.setFont(Font.font("Georgia", FontWeight.MEDIUM, 16));
        toLabel.setTextFill(Color.BLACK);

        datePanel.getChildren().addAll(dateIcon, selectLabel, fromLabel, fromDatePicker, toLabel, toDatePicker);
        
        return datePanel;
    }

    private void setupDatePickers() {
        // Set default values
        fromDatePicker.setValue(LocalDate.now().minusDays(30));
        toDatePicker.setValue(LocalDate.now());
        
        fromDatePicker.setPromptText("Start Date");
        toDatePicker.setPromptText("End Date");
        
        String datePickerStyle = """
            -fx-background-color: white;
            -fx-border-color: #cccccc;
            -fx-border-radius: 12;
            -fx-background-radius: 12;
            -fx-padding: 12 16;
            -fx-font-size: 14px;
            -fx-font-weight: bold;
            -fx-pref-width: 180;
            -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 8, 0, 0, 3);
            """;
        
        fromDatePicker.setStyle(datePickerStyle);
        toDatePicker.setStyle(datePickerStyle);
        
        // Add validation: ensure 'from' date is not after 'to' date
        fromDatePicker.valueProperty().addListener((obs, oldDate, newDate) -> {
            if (newDate != null && toDatePicker.getValue() != null && newDate.isAfter(toDatePicker.getValue())) {
                toDatePicker.setValue(newDate);
            }
        });
        
        toDatePicker.valueProperty().addListener((obs, oldDate, newDate) -> {
            if (newDate != null && fromDatePicker.getValue() != null && newDate.isBefore(fromDatePicker.getValue())) {
                fromDatePicker.setValue(newDate);
            }
        });
    }

    private GridPane createCardsGrid() {
        GridPane grid = new GridPane();
        grid.setHgap(20);
        grid.setVgap(20);
        grid.setAlignment(Pos.CENTER);
        grid.setPadding(new Insets(10));

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

        grid.add(pieCard, 0, 0);
        grid.add(barCard, 1, 0);
        grid.add(histCard, 2, 0);
        
        HBox secondRowContainer = new HBox(20);
        secondRowContainer.setAlignment(Pos.CENTER);
        secondRowContainer.getChildren().addAll(lineCard, stackedCard);
        
        grid.add(secondRowContainer, 0, 1, 3, 1);

        return grid;
    }

    private VBox createCard(String icon, String title, String description, String color, Runnable action) {
        VBox card = new VBox(20);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(30));
        card.setPrefSize(280, 200);
        card.setMaxSize(280, 200);
        
        card.setStyle(String.format("""
            -fx-background-color: white;
            -fx-background-radius: 20;
            -fx-border-color: rgba(255, 255, 255, 0.3);
            -fx-border-radius: 20;
            -fx-border-width: 1;
            -fx-cursor: hand;
            """));

        DropShadow cardShadow = new DropShadow();
        cardShadow.setColor(Color.rgb(0, 0, 0, 0.15));
        cardShadow.setRadius(20);
        cardShadow.setOffsetY(10);
        card.setEffect(cardShadow);

        Label iconLabel = new Label(icon);
        iconLabel.setFont(Font.font(48));
        iconLabel.setPrefSize(80, 80);
        iconLabel.setAlignment(Pos.CENTER);
        iconLabel.setStyle(String.format("""
            -fx-background-color: %s;
            -fx-background-radius: 40;
            -fx-text-fill: white;
            """, color));

        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 18));
        titleLabel.setTextFill(Color.rgb(60, 60, 60));
        titleLabel.setAlignment(Pos.CENTER);

        Label descLabel = new Label(description);
        descLabel.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 12));
        descLabel.setTextFill(Color.rgb(120, 120, 120));
        descLabel.setWrapText(true);
        descLabel.setAlignment(Pos.CENTER);
        descLabel.setMaxWidth(220);

        card.getChildren().addAll(iconLabel, titleLabel, descLabel);

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

        card.setOnMouseClicked(e -> action.run());

        return card;
    }

    private boolean validateInput() {
        if (fromDatePicker.getValue() == null || toDatePicker.getValue() == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Input Required");
            alert.setHeaderText("Please select both start and end dates");
            alert.setContentText("Both start date and end date must be selected to generate the chart.");
            
            DialogPane dialogPane = alert.getDialogPane();
            dialogPane.setStyle("""
                -fx-background-color: white;
                -fx-font-family: 'Segoe UI';
                """);
            
            alert.showAndWait();
            return false;
        }
        
        if (fromDatePicker.getValue().isAfter(toDatePicker.getValue())) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Invalid Date Range");
            alert.setHeaderText("Start date cannot be after end date");
            alert.setContentText("Please ensure the start date is before or equal to the end date.");
            
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
                     "WHERE d.name = 'Sales' AND so.order_date >= ? AND so.order_date <= ? " +
                     "GROUP BY e.name";
        
        try (PreparedStatement stmt = Main.conn.prepareStatement(sql)) {
            stmt.setDate(1, Date.valueOf(fromDatePicker.getValue()));
            stmt.setDate(2, Date.valueOf(toDatePicker.getValue()));
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                data.add(new PieChart.Data(rs.getString("name"), rs.getDouble("total_sales")));
            }
            
            PieChart chart = new PieChart(data);
            chart.setTitle("Sales Distribution by Employee");
            styleChart(chart);
            
            Label infoLabel = new Label("🥧 Pie chart showing total sales distribution per employee in Sales department from " + 
                            fromDatePicker.getValue() + " to " + toDatePicker.getValue());
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
                     "WHERE d.name = 'Sales' AND so.order_date >= ? AND so.order_date <= ? " +
                     "GROUP BY so.order_date ORDER BY so.order_date";
        
        try (PreparedStatement stmt = Main.conn.prepareStatement(sql)) {
            stmt.setDate(1, Date.valueOf(fromDatePicker.getValue()));
            stmt.setDate(2, Date.valueOf(toDatePicker.getValue()));
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                series.getData().add(new XYChart.Data<>(rs.getDate("order_date").toString(), rs.getInt("order_count")));
            }
            chart.getData().add(series);
            styleChart(chart);
            
            Label infoLabel = new Label("📊 Bar chart displaying daily order count by Sales department from " + 
                            fromDatePicker.getValue() + " to " + toDatePicker.getValue());
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
            WHERE dep.name = 'Sales' AND so.order_date >= ? AND so.order_date <= ?
            GROUP BY p.name ORDER BY total_quantity DESC LIMIT 5
        """;

        try (PreparedStatement stmt = Main.conn.prepareStatement(sql)) {
            stmt.setDate(1, Date.valueOf(fromDatePicker.getValue()));
            stmt.setDate(2, Date.valueOf(toDatePicker.getValue()));
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                series.getData().add(new XYChart.Data<>(rs.getString("name"), rs.getInt("total_quantity")));
            }

            chart.getData().add(series);
            styleChart(chart);
            
            Label infoLabel = new Label("🏆 Top 5 products by quantity sold by Sales department from " + 
                            fromDatePicker.getValue() + " to " + toDatePicker.getValue());
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
            WHERE d.name = 'Sales' AND so.order_date >= ? AND so.order_date <= ?
            GROUP BY so.order_date ORDER BY so.order_date
        """;

        try (PreparedStatement stmt = Main.conn.prepareStatement(sql)) {
            stmt.setDate(1, Date.valueOf(fromDatePicker.getValue()));
            stmt.setDate(2, Date.valueOf(toDatePicker.getValue()));
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                series.getData().add(new XYChart.Data<>(rs.getDate("order_date").toString(), rs.getDouble("daily_sales")));
            }

            chart.getData().add(series);
            styleChart(chart);
            
            Label infoLabel = new Label("📈 Daily sales trend showing total revenue by Sales department from " + 
                            fromDatePicker.getValue() + " to " + toDatePicker.getValue());
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
            WHERE d.name = 'Sales' AND so.order_date >= ? AND so.order_date <= ?
            GROUP BY e.name, so.order_date ORDER BY e.name, so.order_date
        """;

        try (PreparedStatement stmt = Main.conn.prepareStatement(sql)) {
            stmt.setDate(1, Date.valueOf(fromDatePicker.getValue()));
            stmt.setDate(2, Date.valueOf(toDatePicker.getValue()));
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
            
            Label infoLabel = new Label("👥 Employee performance comparison showing daily sales distribution from " + 
                            fromDatePicker.getValue() + " to " + toDatePicker.getValue());
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
    
    private void showDatabaseError(SQLException ex) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Database Error");
        alert.setHeaderText("Failed to load data");
        alert.setContentText("There was an error accessing the database: " + ex.getMessage());
        
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.setStyle("""
            -fx-background-color: white;
            -fx-font-family: 'Segoe UI';
            """);
        
        alert.showAndWait();
        ex.printStackTrace();
    }
    
    public VBox getAll() {
        return root;
    }
}