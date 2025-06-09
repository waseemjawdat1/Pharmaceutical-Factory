package application;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Calendar;

import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class ProductTableView {

    private final TableView<Product> table = new TableView<>();
    private final ObservableMap<Product, BooleanProperty> selectedMap = FXCollections.observableHashMap();
    private final ObservableMap<Product, IntegerProperty> quantityMap = FXCollections.observableHashMap();
    private final TextField totalField = new TextField();

    public ProductTableView(int customerId) {
        Label title = new Label("📦 Product Selection");
        title.setStyle(
            "-fx-font-size: 32px; " +
            "-fx-font-weight: bold; " +
            "-fx-text-fill: #667eea; " +  
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 5, 0, 0, 2); " +
            "-fx-padding: 10px;"
        );

        table.setEditable(true);
        table.setStyle(
            "-fx-background-color: linear-gradient(to bottom, #ffecd2 0%, #fcb69f 100%); " +
            "-fx-background-radius: 15px; " +
            "-fx-border-radius: 15px; " +
            "-fx-border-color: rgba(255,255,255,0.3); " +
            "-fx-border-width: 2px; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 10, 0, 0, 5);"
        );

        table.setRowFactory(tv -> {
            TableRow<Product> row = new TableRow<>();
            row.setStyle(
                "-fx-background-color: rgba(255,255,255,0.8); " +
                "-fx-background-radius: 8px; " +
                "-fx-padding: 5px; " +
                "-fx-border-radius: 8px;"
            );
            row.hoverProperty().addListener((obs, wasHovered, isNowHovered) -> {
                if (isNowHovered) {
                    row.setStyle(
                        "-fx-background-color: rgba(255,255,255,0.95); " +  // Fixed: Use solid color
                        "-fx-background-radius: 8px; " +
                        "-fx-padding: 5px; " +
                        "-fx-border-radius: 8px; " +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0, 0, 2);"
                    );
                } else {
                    row.setStyle(
                        "-fx-background-color: rgba(255,255,255,0.8); " +
                        "-fx-background-radius: 8px; " +
                        "-fx-padding: 5px; " +
                        "-fx-border-radius: 8px;"
                    );
                }
            });
            return row;
        });

        TableColumn<Product, Boolean> selectCol = new TableColumn<>("✓ Select");
        selectCol.setEditable(true);
        selectCol.setStyle(
            "-fx-font-weight: bold; " +
            "-fx-text-fill: #4a5568; " +
            "-fx-alignment: center;"
        );
        selectCol.setCellValueFactory(cellData -> {
            Product product = cellData.getValue();
            return selectedMap.computeIfAbsent(product, p -> {
                BooleanProperty prop = new SimpleBooleanProperty(false);
                prop.addListener((obs, oldVal, newVal) -> updateTotal());
                return prop;
            });
        });
        selectCol.setCellFactory(CheckBoxTableCell.forTableColumn(selectCol));
        selectCol.setPrefWidth(80);

        TableColumn<Product, String> nameCol = new TableColumn<>("🏷️ Product Name");
        nameCol.setStyle(
            "-fx-font-weight: bold; " +
            "-fx-text-fill: #2d3748;"
        );
        nameCol.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getName()));
        nameCol.setCellFactory(col -> {
            TableCell<Product, String> cell = new TableCell<>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(item);
                        setStyle(
                            "-fx-font-weight: bold; " +
                            "-fx-text-fill: #2d3748; " +
                            "-fx-padding: 10px;"
                        );
                    }
                }
            };
            return cell;
        });

        TableColumn<Product, Integer> quantityCol = new TableColumn<>("📊 Quantity");
        quantityCol.setStyle(
            "-fx-font-weight: bold; " +
            "-fx-text-fill: #2d3748; " +
            "-fx-alignment: center;"
        );
        quantityCol.setCellFactory(col -> new TableCell<>() {
            private final Spinner<Integer> spinner = new Spinner<>();

            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                    return;
                }

                Product product = getTableView().getItems().get(getIndex());
                int max = Math.max(1, product.getQuantity());
                IntegerProperty quantityProp = quantityMap.computeIfAbsent(product, p -> {
                    IntegerProperty q = new SimpleIntegerProperty(1);
                    q.addListener((obs, oldVal, newVal) -> updateTotal());
                    return q;
                });

                spinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, max, quantityProp.get()));
                spinner.valueProperty().addListener((obs, oldVal, newVal) -> {
                    if (newVal != null) quantityProp.set(newVal);
                });

                spinner.setStyle(
                    "-fx-background-color: linear-gradient(to bottom, #ffffff, #f7fafc); " +
                    "-fx-border-color: #cbd5e0; " +
                    "-fx-border-radius: 8px; " +
                    "-fx-background-radius: 8px; " +
                    "-fx-effect: innershadow(gaussian, rgba(0,0,0,0.1), 2, 0, 0, 1);"
                );
                spinner.getEditor().setStyle(
                    "-fx-text-fill: #2d3748; " +
                    "-fx-font-weight: bold; " +
                    "-fx-alignment: center;"
                );

                setGraphic(spinner);
            }
        });

        TableColumn<Product, Double> totalCol = new TableColumn<>("💰 Total");
        totalCol.setStyle(
            "-fx-font-weight: bold; " +
            "-fx-text-fill: #2d3748; " +
            "-fx-alignment: center;"
        );
        totalCol.setCellValueFactory(cell -> {
            Product product = cell.getValue();
            DoubleBinding total = new DoubleBinding() {
                {
                    bind(selectedMap.computeIfAbsent(product, p -> new SimpleBooleanProperty(false)),
                         quantityMap.computeIfAbsent(product, p -> new SimpleIntegerProperty(1)));
                }

                @Override
                protected double computeValue() {
                    boolean isSelected = selectedMap.get(product).get();
                    int qty = quantityMap.get(product).get();
                    return isSelected ? qty * product.getPrice() : 0.0;
                }
            };
            return total.asObject();
        });
        totalCol.setCellFactory(col -> {
            TableCell<Product, Double> cell = new TableCell<>() {
                @Override
                protected void updateItem(Double item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(String.format("$%.2f", item));
                        setStyle(
                            "-fx-font-weight: bold; " +
                            "-fx-text-fill: " + (item > 0 ? "#38a169" : "#718096") + "; " +
                            "-fx-padding: 10px; " +
                            "-fx-alignment: center;"
                        );
                    }
                }
            };
            return cell;
        });

        table.setItems(Main.products);
        table.getColumns().addAll(selectCol, nameCol, quantityCol, totalCol);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPrefHeight(400);

        totalField.setEditable(false);
        totalField.setAlignment(Pos.CENTER);
        totalField.setPrefWidth(200);
        totalField.setPrefHeight(45);
        totalField.setStyle(
            "-fx-background-color: linear-gradient(to bottom, #667eea, #764ba2); " +
            "-fx-text-fill: white; " +
            "-fx-font-size: 18px; " +
            "-fx-font-weight: bold; " +
            "-fx-background-radius: 15px; " +
            "-fx-border-radius: 15px; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 8, 0, 0, 3);"
        );
        updateTotal();

        Label totalLabel = new Label("💵 Total Amount:");
        totalLabel.setStyle(
            "-fx-font-weight: bold; " +
            "-fx-font-size: 16px; " +
            "-fx-text-fill: #2d3748;"
        );

        HBox totalBox = new HBox(15, totalLabel, totalField);
        totalBox.setAlignment(Pos.CENTER);
        totalBox.setPadding(new Insets(10));
        totalBox.setStyle(
            "-fx-background-color: rgba(255,255,255,0.7); " +
            "-fx-background-radius: 20px; " +
            "-fx-border-radius: 20px; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0, 0, 2);"
        );

        Button placeOrderBtn = new Button("✅ Place Order");
        placeOrderBtn.setPrefWidth(200);
        placeOrderBtn.setPrefHeight(50);
        placeOrderBtn.setStyle(
            "-fx-background-color: linear-gradient(to right, #56ab2f, #a8e6cf); " +
            "-fx-text-fill: white; " +
            "-fx-font-weight: bold; " +
            "-fx-font-size: 16px; " +
            "-fx-background-radius: 25px; " +
            "-fx-border-radius: 25px; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 10, 0, 0, 5); " +
            "-fx-cursor: hand;"
        );

        placeOrderBtn.setOnMouseEntered(e -> placeOrderBtn.setStyle(
            "-fx-background-color: linear-gradient(to right, #4CAF50, #8BC34A); " +
            "-fx-text-fill: white; " +
            "-fx-font-weight: bold; " +
            "-fx-font-size: 16px; " +
            "-fx-background-radius: 25px; " +
            "-fx-border-radius: 25px; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.4), 15, 0, 0, 8); " +
            "-fx-cursor: hand; " +
            "-fx-scale-x: 1.05; " +
            "-fx-scale-y: 1.05;"
        ));

        placeOrderBtn.setOnMouseExited(e -> placeOrderBtn.setStyle(
            "-fx-background-color: linear-gradient(to right, #56ab2f, #a8e6cf); " +
            "-fx-text-fill: white; " +
            "-fx-font-weight: bold; " +
            "-fx-font-size: 16px; " +
            "-fx-background-radius: 25px; " +
            "-fx-border-radius: 25px; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 10, 0, 0, 5); " +
            "-fx-cursor: hand; " +
            "-fx-scale-x: 1.0; " +
            "-fx-scale-y: 1.0;"
        ));
        Stage stage = new Stage();
        placeOrderBtn.setOnAction(e -> {
            try {
            	boolean anySelected = false;

            	for (int i = 0; i < Main.products.size(); i++) {
            	    Product p = Main.products.get(i);
            	    BooleanProperty selected = selectedMap.get(p);
            	    if (selected != null && selected.get()) {
            	        anySelected = true;
            	        break;
            	    }
            	}

            	if (!anySelected) {
            	    Main.notValidAlert("No Products Selected", "Please select at least one product to place an order.");
            	    return; 
            	}
                double total = Double.parseDouble(totalField.getText().substring(1)); 

                Calendar c = Calendar.getInstance();
                Date date = new Date(c.getTimeInMillis());

                SalesOrder s = new SalesOrder(customerId, Main.currentUser.getEmployeeId(), date, total);
                Main.salesOrder.add(s); 
                
                int orderId = SalesOrder.getLastId(); 

                for (Product p : Main.products) {
                    BooleanProperty selected = selectedMap.get(p);
                    IntegerProperty qty = quantityMap.get(p);

                    if (selected != null && selected.get()) {
                        int purchasedQty = qty.get();
                        int newQty = p.getQuantity() - purchasedQty;
                        p.updateProduct(p.getName(), p.getCategory(), p.getExpiryDate(),
                                newQty, p.getWarehouseId(), p.getCreatedAt(), p.getPrice());

                        String sql = "INSERT INTO sales_order_details (sales_order_id, product_id, quantity,unit_price) VALUES (?, ?, ?, ?)";
                        try (PreparedStatement stmt = Main.conn.prepareStatement(sql)) {
                            stmt.setInt(1, orderId);
                            stmt.setInt(2, p.getProductId());
                            stmt.setInt(3, purchasedQty);
                            stmt.setDouble(4, p.getPrice());
                            stmt.executeUpdate();
                        }
                    }
                }

                Main.validAlert("Order Placed", "Order for customer was placed successfully.");
                SalesManagement.toFire.fire(); 
                stage.close();

            } catch (Exception ex) {
                Main.notValidAlert("Error", ex.getMessage());
            }
        });


        VBox root = new VBox(25, title, table, totalBox, placeOrderBtn);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(40));

        Scene scene = new Scene(root, 900, 700);
        scene.getStylesheets().add("data:text/css," +
            ".table-view { " +
            "    -fx-selection-bar: rgba(102, 126, 234, 0.3); " +  
            "    -fx-selection-bar-non-focused: rgba(102, 126, 234, 0.2); " +
            "} " +
            ".table-view .column-header { " +
            "    -fx-background-color: linear-gradient(to bottom, #4a5568, #2d3748); " +
            "    -fx-text-fill: white; " +
            "    -fx-font-weight: bold; " +
            "    -fx-padding: 10px; " +
            "} " +
            ".table-view .column-header .label { " +
            "    -fx-text-fill: white; " +
            "    -fx-font-weight: bold; " +
            "} " +
            ".spinner .increment-arrow-button, .spinner .decrement-arrow-button { " +
            "    -fx-background-color: linear-gradient(to bottom, #667eea, #764ba2); " +
            "    -fx-background-radius: 3px; " +
            "} " +
            ".spinner .increment-arrow-button .increment-arrow, " +
            ".spinner .decrement-arrow-button .decrement-arrow { " +
            "    -fx-background-color: white; " +
            "}"
        );

        stage.setScene(scene);
        stage.setTitle("🛍️ Product Selection");
        stage.show();
    }

    private void updateTotal() {
        double total = 0.0;
        for (Product p : Main.products) {
            BooleanProperty selected = selectedMap.get(p);
            IntegerProperty quantity = quantityMap.get(p);
            if (selected != null && selected.get() && quantity != null) {
                total += quantity.get() * p.getPrice();
            }
        }
        totalField.setText(String.format("$%.2f", total));
    }
}