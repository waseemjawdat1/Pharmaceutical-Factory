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

public class ProductionStatistics {

	private Stage mainStage;
	private final DatePicker fromDatePicker = new DatePicker();
	private final DatePicker toDatePicker = new DatePicker();
	private VBox all;

	public ProductionStatistics() {
		all = new VBox(30);
		all.setPadding(new Insets(40));

		HBox datePanel = createDateSelectionPanel();
		GridPane cardsGrid = createCardsGrid();

		all.getChildren().addAll(datePanel, cardsGrid);
	}
	
	public VBox getAll() {
		return all;
	}
	
	private Label createTitle() {
		Label titleLabel = new Label("🏭 Production Analytics Dashboard");
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

		Label selectLabel = new Label("Select Production Period");
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
		// Set default values - last 30 days
		toDatePicker.setValue(LocalDate.now());
		fromDatePicker.setValue(LocalDate.now().minusDays(30));

		fromDatePicker.setPromptText("Select Start Date");
		toDatePicker.setPromptText("Select End Date");

		String datePickerStyle = """
				-fx-background-color: white;
				-fx-border-color: #cccccc;
				-fx-border-radius: 12;
				-fx-background-radius: 12;
				-fx-padding: 12 16;
				-fx-font-size: 14px;
				-fx-font-weight: bold;
				-fx-pref-width: 210;
				-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 8, 0, 0, 3);
				""";

		fromDatePicker.setStyle(datePickerStyle);
		toDatePicker.setStyle(datePickerStyle);

		// Add hover effects
		String hoverStyle = """
				-fx-background-color: #f8f9fa;
				-fx-border-color: #4ECDC4;
				-fx-border-radius: 12;
				-fx-background-radius: 12;
				-fx-padding: 12 16;
				-fx-font-size: 14px;
				-fx-font-weight: bold;
				-fx-pref-width: 210;
				-fx-effect: dropshadow(three-pass-box, rgba(78,205,196,0.3), 12, 0, 0, 4);
				""";

		fromDatePicker.setOnMouseEntered(e -> fromDatePicker.setStyle(hoverStyle));
		fromDatePicker.setOnMouseExited(e -> fromDatePicker.setStyle(datePickerStyle));
		toDatePicker.setOnMouseEntered(e -> toDatePicker.setStyle(hoverStyle));
		toDatePicker.setOnMouseExited(e -> toDatePicker.setStyle(datePickerStyle));
	}

	private GridPane createCardsGrid() {
		GridPane grid = new GridPane();
		grid.setHgap(20);
		grid.setVgap(20);
		grid.setAlignment(Pos.CENTER);
		grid.setPadding(new Insets(10));

		// Create cards with production analytics
		VBox pieCard = createCard("🎯", "Category Distribution",
				"Production quantity distribution across product categories", "#FF6B6B",
				this::openProductionEfficiencyChart);

		VBox barCard = createCard("📊", "Batch Performance", "Average production per batch for each product", "#4ECDC4",
				this::openCategoryPerformanceChart);

		VBox histCard = createCard("⚡", "Production Velocity", "Daily production velocity scores and trends", "#45B7D1",
				this::openProductionVelocityChart);

		VBox lineCard = createCard("📈", "Material Requirements", "Total material consumption based on production",
				"#96CEB4", this::openMaterialConsumptionChart);

		VBox stackedCard = createCard("🏆", "Top Performers", "Highest performing products with complex scoring",
				"#FECA57", this::openTopPerformersChart);

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
			alert.setContentText("Both from and to dates must be selected to generate the chart.");

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
			alert.setHeaderText("Start date must be before end date");
			alert.setContentText("Please ensure the 'from' date is earlier than the 'to' date.");

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

	private void openProductionEfficiencyChart() {
		if (!validateInput())
			return;

		Stage stage = createChartStage("Production Distribution by Category", "🎯");
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

		String sql = """
				SELECT p.category, SUM(pb.quantity_produced) AS total_production
				FROM products p
				JOIN production_batches pb ON p.product_id = pb.product_id
				WHERE pb.production_date BETWEEN ? AND ?
				AND p.active = 1
				GROUP BY p.category
				ORDER BY total_production DESC
				""";

		try (PreparedStatement stmt = Main.conn.prepareStatement(sql)) {
			stmt.setDate(1, Date.valueOf(fromDatePicker.getValue()));
			stmt.setDate(2, Date.valueOf(toDatePicker.getValue()));
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				data.add(new PieChart.Data(rs.getString("category"), rs.getInt("total_production")));
			}

			PieChart chart = new PieChart(data);
			chart.setTitle("Production Distribution by Category");
			styleChart(chart);

			Label infoLabel = new Label("🎯 Total production quantity distributed across product categories from "
					+ fromDatePicker.getValue() + " to " + toDatePicker.getValue());
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

	private void openCategoryPerformanceChart() {
		if (!validateInput())
			return;

		Stage stage = createChartStage("Average Production Performance", "📊");
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
		xAxis.setLabel("Product");
		yAxis.setLabel("Average Production per Batch");

		BarChart<String, Number> chart = new BarChart<>(xAxis, yAxis);
		chart.setTitle("Average Production Quantity per Batch");
		XYChart.Series<String, Number> series = new XYChart.Series<>();
		series.setName("Avg Production");

		String sql = """
				SELECT p.name, AVG(pb.quantity_produced) AS avg_production, COUNT(pb.batch_id) AS batch_count
				FROM products p
				JOIN production_batches pb ON p.product_id = pb.product_id
				WHERE pb.production_date BETWEEN ? AND ?
				AND p.active = 1
				GROUP BY p.product_id, p.name
				HAVING batch_count >= 2
				ORDER BY avg_production DESC
				""";

		try (PreparedStatement stmt = Main.conn.prepareStatement(sql)) {
			stmt.setDate(1, Date.valueOf(fromDatePicker.getValue()));
			stmt.setDate(2, Date.valueOf(toDatePicker.getValue()));
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				series.getData().add(new XYChart.Data<>(rs.getString("name"), rs.getDouble("avg_production")));
			}
			chart.getData().add(series);
			styleChart(chart);

			Label infoLabel = new Label(
					"📊 Average production quantity per batch for products with multiple batches from "
							+ fromDatePicker.getValue() + " to " + toDatePicker.getValue());
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

	private void openProductionVelocityChart() {
		if (!validateInput())
			return;

		Stage stage = createChartStage("Daily Production Velocity", "⚡");
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
		xAxis.setLabel("Date");
		yAxis.setLabel("Production Velocity Score");

		BarChart<String, Number> chart = new BarChart<>(xAxis, yAxis);
		chart.setTitle("Daily Production Velocity (Batches × Avg Quantity)");
		XYChart.Series<String, Number> series = new XYChart.Series<>();
		series.setName("Velocity Score");

		String sql = """
				SELECT pb.production_date,
				       COUNT(pb.batch_id) * AVG(pb.quantity_produced) AS velocity_score
				FROM production_batches pb
				JOIN products p ON pb.product_id = p.product_id
				WHERE pb.production_date BETWEEN ? AND ?
				AND p.active = 1
				GROUP BY pb.production_date
				ORDER BY pb.production_date
				""";

		try (PreparedStatement stmt = Main.conn.prepareStatement(sql)) {
			stmt.setDate(1, Date.valueOf(fromDatePicker.getValue()));
			stmt.setDate(2, Date.valueOf(toDatePicker.getValue()));
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				series.getData().add(
						new XYChart.Data<>(rs.getDate("production_date").toString(), rs.getDouble("velocity_score")));
			}

			chart.getData().add(series);
			styleChart(chart);

			Label infoLabel = new Label("⚡ Daily production velocity calculated as batch count × average quantity from "
					+ fromDatePicker.getValue() + " to " + toDatePicker.getValue());
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

	private void openMaterialConsumptionChart() {
		if (!validateInput())
			return;

		Stage stage = createChartStage("Material Requirements Analysis", "📈");
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
		xAxis.setLabel("Material");
		yAxis.setLabel("Total Required Quantity");

		LineChart<String, Number> chart = new LineChart<>(xAxis, yAxis);
		chart.setTitle("Material Consumption Based on Production");
		XYChart.Series<String, Number> series = new XYChart.Series<>();
		series.setName("Required Quantity");

		String sql = """
				    SELECT rm.name,
				           SUM(pr.quantity * pb.quantity_produced) AS total_material_needed
				    FROM raw_materials rm
				    JOIN product_requirements pr ON rm.material_id = pr.material_id
				    JOIN products p ON pr.product_id = p.product_id
				    JOIN production_batches pb ON p.product_id = pb.product_id
				    WHERE pb.production_date BETWEEN ? AND ?
				    AND rm.active = 1 AND p.active = 1
				    GROUP BY rm.material_id, rm.name
				    ORDER BY total_material_needed DESC
				    LIMIT 10
				""";

		try (PreparedStatement stmt = Main.conn.prepareStatement(sql)) {
			stmt.setDate(1, Date.valueOf(fromDatePicker.getValue()));
			stmt.setDate(2, Date.valueOf(toDatePicker.getValue()));
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				series.getData().add(new XYChart.Data<>(rs.getString("name"), rs.getInt("total_material_needed")));
			}

			chart.getData().add(series);
			styleChart(chart);

			Label infoLabel = new Label("📈 Top 10 materials by total required quantity based on actual production from "
					+ fromDatePicker.getValue() + " to " + toDatePicker.getValue());
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

	private void openTopPerformersChart() {
		if (!validateInput())
			return;

		Stage stage = createChartStage("Top Production Performers", "🏆");
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
		xAxis.setLabel("Product");
		yAxis.setLabel("Performance Score");

		BarChart<String, Number> chart = new BarChart<>(xAxis, yAxis);
		chart.setTitle("Top Products by Performance Score");
		XYChart.Series<String, Number> series = new XYChart.Series<>();
		series.setName("Performance Score");

		String sql = """
				    SELECT p.name,
				           SUM(pb.quantity_produced) + (COUNT(pb.batch_id) * 10) AS performance_score,
				           COUNT(pb.batch_id) AS batch_count,
				           SUM(pb.quantity_produced) AS total_produced
				    FROM products p
				    JOIN production_batches pb ON p.product_id = pb.product_id
				    WHERE pb.production_date BETWEEN ? AND ?
				    AND p.active = 1
				    AND pb.quantity_produced > (
				        SELECT AVG(quantity_produced)
				        FROM production_batches pb2
				        JOIN products p2 ON pb2.product_id = p2.product_id
				        WHERE pb2.production_date BETWEEN ? AND ?
				        AND p2.active = 1
				    )
				    GROUP BY p.product_id, p.name
				    HAVING batch_count >= 2 AND total_produced > 50
				    ORDER BY performance_score DESC
				    LIMIT 8
				""";

		try (PreparedStatement stmt = Main.conn.prepareStatement(sql)) {
			stmt.setDate(1, Date.valueOf(fromDatePicker.getValue()));
			stmt.setDate(2, Date.valueOf(toDatePicker.getValue()));
			stmt.setDate(3, Date.valueOf(fromDatePicker.getValue()));
			stmt.setDate(4, Date.valueOf(toDatePicker.getValue()));
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				series.getData().add(new XYChart.Data<>(rs.getString("name"), rs.getInt("performance_score")));
			}

			chart.getData().add(series);
			styleChart(chart);

			Label infoLabel = new Label(
					"🏆 Top performing products with above-average production and multiple batches from "
							+ fromDatePicker.getValue() + " to " + toDatePicker.getValue());
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

	private void showDatabaseError(SQLException ex) {
		Alert alert = new Alert(Alert.AlertType.ERROR);
		alert.setTitle("Database Error");
		alert.setHeaderText("An error occurred while accessing the database");
		alert.setContentText("Error: " + ex.getMessage());

		DialogPane dialogPane = alert.getDialogPane();
		dialogPane.setStyle("""
				-fx-background-color: white;
				-fx-font-family: 'Segoe UI';
				""");

		alert.showAndWait();
		ex.printStackTrace(); 
	}

	private void styleChart(Chart chart) {
		chart.setStyle("""
				-fx-background-color: transparent;
				-fx-border-color: transparent;
				""");

		chart.setTitleSide(javafx.geometry.Side.TOP);
		chart.getStylesheets()
				.add("data:text/css," + ".chart-title { " + "    -fx-font-size: 18px; " + "    -fx-font-weight: bold; "
						+ "    -fx-text-fill: #333333; " + "    -fx-font-family: 'Segoe UI'; " + "} "
						+ ".chart-legend { " + "    -fx-background-color: transparent; " + "    -fx-font-size: 12px; "
						+ "    -fx-font-family: 'Segoe UI'; " + "} " + ".axis { " + "    -fx-font-size: 11px; "
						+ "    -fx-font-family: 'Segoe UI'; " + "} " + ".axis-label { " + "    -fx-font-size: 12px; "
						+ "    -fx-font-weight: bold; " + "    -fx-font-family: 'Segoe UI'; " + "}");
	}

	public Scene getScene() {
		all.setStyle("-fx-background-color: linear-gradient(to bottom right, #667eea, #764ba2);");

		Label titleLabel = createTitle();
		all.getChildren().add(0, titleLabel); 

		return new Scene(all, 1200, 800);
	}
}