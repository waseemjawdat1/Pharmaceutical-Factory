package application;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Calendar;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class RawMaterialTableView {

    private final TableView<RawMaterial> table = new TableView<>();
    private final ObservableMap<RawMaterial, BooleanProperty> selectedMap = FXCollections.observableHashMap();
    private final ObservableMap<RawMaterial, IntegerProperty> quantityMap = FXCollections.observableHashMap();
    private final TextField totalField = new TextField();

    public RawMaterialTableView(int supplierId) {
        Label title = new Label("Raw Material Selection");
        title.setStyle("-fx-font-size: 32px; -fx-font-weight: bold; -fx-text-fill: #667eea; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 5, 0, 0, 2); -fx-padding: 10px;");

        ObservableList<RawMaterial> filteredMaterials = FXCollections.observableArrayList();
        for (RawMaterial m : Main.materials) {
            if (m.getSupplierId() == supplierId) {
                filteredMaterials.add(m);
            }
        }

        table.setEditable(true);
        table.setStyle("-fx-background-color: linear-gradient(to bottom, #ffecd2 0%, #fcb69f 100%); -fx-background-radius: 15px; -fx-border-radius: 15px; -fx-border-color: rgba(255,255,255,0.3); -fx-border-width: 2px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 10, 0, 0, 5);");

        table.setRowFactory(tv -> {
            TableRow<RawMaterial> row = new TableRow<>();
            row.setStyle("-fx-background-color: rgba(255,255,255,0.8); -fx-background-radius: 8px; -fx-padding: 5px; -fx-border-radius: 8px;");
            row.hoverProperty().addListener((obs, wasHovered, isNowHovered) -> {
                if (isNowHovered) {
                    row.setStyle("-fx-background-color: rgba(255,255,255,0.95); -fx-background-radius: 8px; -fx-padding: 5px; -fx-border-radius: 8px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0, 0, 2);");
                } else {
                    row.setStyle("-fx-background-color: rgba(255,255,255,0.8); -fx-background-radius: 8px; -fx-padding: 5px; -fx-border-radius: 8px;");
                }
            });
            return row;
        });

        TableColumn<RawMaterial, Boolean> selectCol = new TableColumn<>("✓ Select");
        selectCol.setCellValueFactory(cellData -> {
            RawMaterial material = cellData.getValue();
            return selectedMap.computeIfAbsent(material, m -> {
                BooleanProperty prop = new SimpleBooleanProperty(false);
                prop.addListener((obs, oldVal, newVal) -> updateTotal());
                return prop;
            });
        });
        selectCol.setCellFactory(col -> new TableCell<>() {
            private final CheckBox checkBox = new CheckBox();
            {
                checkBox.setOnAction(e -> {
                    RawMaterial m = getTableView().getItems().get(getIndex());
                    BooleanProperty prop = selectedMap.get(m);
                    if (prop != null) {
                        prop.set(checkBox.isSelected());
                        updateTotal();
                        table.refresh();
                    }
                });
            }

            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    RawMaterial m = getTableView().getItems().get(getIndex());
                    checkBox.setSelected(selectedMap.get(m).get());
                    setGraphic(checkBox);
                    setAlignment(Pos.CENTER);
                }
            }
        });
        selectCol.setPrefWidth(80);
        selectCol.setStyle("-fx-font-weight: bold; -fx-text-fill: #4a5568; -fx-alignment: center;");

        TableColumn<RawMaterial, String> nameCol = new TableColumn<>("🏷️ Material Name");
        nameCol.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getName()));
        nameCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item);
                    setStyle("-fx-font-weight: bold; -fx-text-fill: #2d3748; -fx-padding: 10px;");
                }
            }
        });

        TableColumn<RawMaterial, Integer> quantityCol = new TableColumn<>("📥 Quantity");
        quantityCol.setStyle("-fx-font-weight: bold; -fx-text-fill: #2d3748; -fx-alignment: center;");
        quantityCol.setCellFactory(col -> new TableCell<>() {
            private final Spinner<Integer> spinner = new Spinner<>();

            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                    return;
                }

                RawMaterial material = getTableView().getItems().get(getIndex());
                IntegerProperty quantityProp = quantityMap.computeIfAbsent(material, m -> {
                    IntegerProperty q = new SimpleIntegerProperty(1);
                    q.addListener((obs, oldVal, newVal) -> {
                        updateTotal();
                        table.refresh();
                    });
                    return q;
                });

                spinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, Integer.MAX_VALUE, quantityProp.get()));
                spinner.setEditable(true);
                quantityProp.set(spinner.getValue());

                spinner.valueProperty().addListener((obs, oldVal, newVal) -> {
                    if (newVal != null) quantityProp.set(newVal);
                });
                
                spinner.getEditor().focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
                    if (!isNowFocused) {
                        try {
                            int newValue = Integer.parseInt(spinner.getEditor().getText());
                            spinner.getValueFactory().setValue(newValue);
                        } catch (NumberFormatException e) {
                            spinner.getEditor().setText(String.valueOf(spinner.getValue()));
                        }
                    }
                });

                spinner.setStyle("-fx-background-color: linear-gradient(to bottom, #ffffff, #f7fafc); -fx-border-color: #cbd5e0; -fx-border-radius: 8px; -fx-background-radius: 8px; -fx-effect: innershadow(gaussian, rgba(0,0,0,0.1), 2, 0, 0, 1);");
                spinner.getEditor().setStyle("-fx-text-fill: #2d3748; -fx-font-weight: bold; -fx-alignment: center;");

                setGraphic(spinner);
            }
        });

        TableColumn<RawMaterial, Double> totalCol = new TableColumn<>("💰 Total");
        totalCol.setCellValueFactory(cell -> new ReadOnlyObjectWrapper<>(0.0));
        totalCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                    return;
                }
                RawMaterial m = getTableView().getItems().get(getIndex());
                boolean selected = selectedMap.getOrDefault(m, new SimpleBooleanProperty(false)).get();
                int qty = quantityMap.getOrDefault(m, new SimpleIntegerProperty(1)).get();
                double total = selected ? qty * m.getPrice() : 0.0;
                setText(String.format("$%.2f", total));
                setStyle("-fx-font-weight: bold; -fx-text-fill: " + (total > 0 ? "#38a169" : "#718096") + "; -fx-padding: 10px; -fx-alignment: center;");
            }
        });

        table.setItems(filteredMaterials);
        table.getColumns().addAll(selectCol, nameCol, quantityCol, totalCol);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPrefHeight(400);

        totalField.setEditable(false);
        totalField.setAlignment(Pos.CENTER);
        totalField.setPrefWidth(200);
        totalField.setPrefHeight(45);
        totalField.setStyle("-fx-background-color: linear-gradient(to bottom, #667eea, #764ba2); -fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold; -fx-background-radius: 15px; -fx-border-radius: 15px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 8, 0, 0, 3);");
        updateTotal();

        Label totalLabel = new Label("💵 Total Amount:");
        totalLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: #2d3748;");

        HBox totalBox = new HBox(15, totalLabel, totalField);
        totalBox.setAlignment(Pos.CENTER);
        totalBox.setPadding(new Insets(10));
        totalBox.setStyle("-fx-background-color: rgba(255,255,255,0.7); -fx-background-radius: 20px; -fx-border-radius: 20px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0, 0, 2);");

        Button placeOrderBtn = new Button("✅ Place Purchase");
        placeOrderBtn.setPrefWidth(200);
        placeOrderBtn.setPrefHeight(50);
        placeOrderBtn.setStyle("-fx-background-color: linear-gradient(to right, #56ab2f, #a8e6cf); -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 16px; -fx-background-radius: 25px; -fx-border-radius: 25px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 10, 0, 0, 5); -fx-cursor: hand;");
        Stage stage = new Stage();
        placeOrderBtn.setOnAction(e -> {
        	try {
        		boolean anySelected = false;
        		for (int i = 0; i < Main.materials.size(); i++) {
        			RawMaterial r = Main.materials.get(i);
        			BooleanProperty selected = selectedMap.get(r);
        			if (selected != null && selected.get()) {
        				anySelected = true;
        				break;
        			}
        		}

        		if (!anySelected) {
        			Main.notValidAlert("No Materials Selected", "Please select at least one material to place a purchase.");
        			return;
        		}

        		double total = Double.parseDouble(totalField.getText().substring(1));
        		Calendar c = Calendar.getInstance();
        		Date date = new Date(c.getTimeInMillis());

        		PurchaseOrder p = new PurchaseOrder(supplierId, Main.currentUser.getEmployeeId(), date, total);
        		Main.purchaseOrders.add(p);

        		int orderId = PurchaseOrder.getLastId();

        		for (int i = 0; i < Main.materials.size(); i++) {
        			RawMaterial r = Main.materials.get(i);
        			BooleanProperty selected = selectedMap.get(r);
        			IntegerProperty qty = quantityMap.get(r);

        			if (selected != null && selected.get()) {
        				int purchasedQty = qty.get();
        				r.setUnit(r.getUnit()+purchasedQty);
        				String sql = "INSERT INTO purchase_order_details (purchase_order_id, material_id, quantity, unit_price) VALUES (?, ?, ?, ?)";
        				PreparedStatement stmt = Main.conn.prepareStatement(sql);
        				stmt.setInt(1, orderId);
        				stmt.setInt(2, r.getMaterialId());
        				stmt.setInt(3, purchasedQty);
        				stmt.setDouble(4, r.getPrice());
        				stmt.executeUpdate();
        			}
        		}

        		Main.validAlert("Purchase Placed", "Purchase for supplier was placed successfully.");
        		PurchaseManagement.toFire.fire();
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
            ".table-view { -fx-selection-bar: rgba(102, 126, 234, 0.3); -fx-selection-bar-non-focused: rgba(102, 126, 234, 0.2); } " +
            ".table-view .column-header { -fx-background-color: linear-gradient(to bottom, #4a5568, #2d3748); -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10px; } " +
            ".table-view .column-header .label { -fx-text-fill: white; -fx-font-weight: bold; } " +
            ".spinner .increment-arrow-button, .spinner .decrement-arrow-button { -fx-background-color: linear-gradient(to bottom, #667eea, #764ba2); -fx-background-radius: 3px; } " +
            ".spinner .increment-arrow-button .increment-arrow, .spinner .decrement-arrow-button .decrement-arrow { -fx-background-color: white; }");

        stage.setScene(scene);
        stage.setTitle("📝 Raw Material Purchase");
        stage.show();
    }

    private void updateTotal() {
        double total = 0.0;
        for (RawMaterial m : table.getItems()) {
            BooleanProperty selected = selectedMap.get(m);
            IntegerProperty qty = quantityMap.get(m);
            if (selected != null && selected.get() && qty != null) {
                total += qty.get() * m.getPrice();
            }
        }
        totalField.setText(String.format("$%.2f", total));
    }
}
