package application;

import java.sql.PreparedStatement;
import java.sql.SQLException;
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

public class RawMaterialRequirementSelector {

    private final TableView<RawMaterial> table = new TableView<>();
    private final ObservableMap<RawMaterial, BooleanProperty> selectedMap = FXCollections.observableHashMap();
    private final ObservableMap<RawMaterial, IntegerProperty> quantityMap = FXCollections.observableHashMap();
    private final Label totalLabel = new Label("Total selected: 0");

    public RawMaterialRequirementSelector(int productId) {
        Label title = new Label("🔧 Select Required Raw Materials");
        title.setStyle("-fx-font-size: 32px; -fx-font-weight: bold; -fx-text-fill: #667eea; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 5, 0, 0, 2); -fx-padding: 10px;");

        ObservableList<RawMaterial> allMaterials = Main.materials;

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
                prop.addListener((obs, oldVal, newVal) -> updateCount());
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
                        updateCount();
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

        TableColumn<RawMaterial, Integer> qtyCol = new TableColumn<>("📦 Required Qty");
        qtyCol.setStyle("-fx-font-weight: bold; -fx-text-fill: #2d3748; -fx-alignment: center;");
        qtyCol.setCellFactory(col -> new TableCell<>() {
            private final Spinner<Integer> spinner = new Spinner<>();

            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                    return;
                }

                RawMaterial material = getTableView().getItems().get(getIndex());
                IntegerProperty quantityProp = quantityMap.computeIfAbsent(material, m -> new SimpleIntegerProperty(1));

                spinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 10000, quantityProp.get()));
                quantityProp.set(spinner.getValue());

                spinner.valueProperty().addListener((obs, oldVal, newVal) -> {
                    if (newVal != null) quantityProp.set(newVal);
                });

                spinner.setStyle("-fx-background-color: linear-gradient(to bottom, #ffffff, #f7fafc); -fx-border-color: #cbd5e0; -fx-border-radius: 8px; -fx-background-radius: 8px; -fx-effect: innershadow(gaussian, rgba(0,0,0,0.1), 2, 0, 0, 1);");
                spinner.getEditor().setStyle("-fx-text-fill: #2d3748; -fx-font-weight: bold; -fx-alignment: center;");

                setGraphic(spinner);
            }
        });

        table.setItems(allMaterials);
        table.getColumns().addAll(selectCol, nameCol, qtyCol);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPrefHeight(400);

        totalLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: #2d3748; -fx-padding: 10px; -fx-background-color: rgba(255,255,255,0.7); -fx-background-radius: 15px; -fx-border-radius: 15px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0, 0, 2);");

        Button saveBtn = new Button("✅ Save Requirements");
        saveBtn.setPrefWidth(200);
        saveBtn.setPrefHeight(50);
        saveBtn.setStyle("-fx-background-color: linear-gradient(to right, #56ab2f, #a8e6cf); -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 16px; -fx-background-radius: 25px; -fx-border-radius: 25px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 10, 0, 0, 5); -fx-cursor: hand;");
        
        Stage stage = new Stage();
        saveBtn.setOnAction(e -> {
            try {
                boolean anySelected = false;
                for (RawMaterial r : table.getItems()) {
                    BooleanProperty selected = selectedMap.get(r);
                    if (selected != null && selected.get()) {
                        anySelected = true;
                        break;
                    }
                }

                if (!anySelected) {
                    Main.notValidAlert("No Materials Selected", "Please select at least one material to save requirements.");
                    return;
                }
                
                String deleteSQL = "DELETE FROM product_requirements WHERE product_id = ?";
                try (PreparedStatement deleteStmt = Main.conn.prepareStatement(deleteSQL)) {
                    deleteStmt.setInt(1, productId);
                    deleteStmt.executeUpdate();
                }


                for (RawMaterial r : table.getItems()) {
                    BooleanProperty selected = selectedMap.get(r);
                    IntegerProperty qty = quantityMap.get(r);
                    if (selected != null && selected.get() && qty != null) {
                        String sql = "INSERT INTO product_requirements (product_id, material_id, quantity) VALUES (?, ?, ?)";
                        PreparedStatement stmt = Main.conn.prepareStatement(sql);
                        stmt.setInt(1, productId);
                        stmt.setInt(2, r.getMaterialId());
                        stmt.setInt(3, qty.get());
                        stmt.executeUpdate();
                    }
                }
                Main.validAlert("Success! 🎉", "Product requirements saved successfully.");
                stage.close();
            } catch (SQLException ex) {
                Main.notValidAlert("Error", ex.getMessage());
            }
        });

        VBox root = new VBox(25, title, table, totalLabel, saveBtn);
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
        stage.setTitle("🔧 Product Requirements Setup");
        stage.show();
    }

    private void updateCount() {
        long count = selectedMap.values().stream().filter(BooleanProperty::get).count();
        totalLabel.setText("📊 Total selected: " + count + " materials");
    }
}