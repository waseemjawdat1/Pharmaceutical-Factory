package application;

import javafx.geometry.Pos;
import javafx.scene.layout.VBox;
import javafx.scene.control.TableColumn;
import javafx.beans.property.SimpleStringProperty;
import java.text.SimpleDateFormat;

public class ProductionBatchTableView {

    private VBox all;
    private MyTableView<ProductionBatch> table;

    public ProductionBatchTableView() {
        MyLabel title = new MyLabel("Production Batches", 1);

        table = new MyTableView<>();

        TableColumn<ProductionBatch, Integer> idCol = table.createStyledColumn("Batch ID", "batchId", Integer.class);
        TableColumn<ProductionBatch, Integer> productIdCol = table.createStyledColumn("Product ID", "productId", Integer.class);
        TableColumn<ProductionBatch, Integer> producedCol = table.createStyledColumn("Produced", "quantityProduced", Integer.class);
        TableColumn<ProductionBatch, Integer> remainingCol = table.createStyledColumn("Remaining", "remaining", Integer.class);

        TableColumn<ProductionBatch, String> prodDateCol = table.createStyledColumn("Production Date", "remaining");
        prodDateCol.setCellValueFactory(cell -> {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            return new SimpleStringProperty(sdf.format(cell.getValue().getProductionDate()));
        });

        TableColumn<ProductionBatch, String> expDateCol = table.createStyledColumn("Expirey Date", "remaining");
        expDateCol.setCellValueFactory(cell -> {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            return new SimpleStringProperty(sdf.format(cell.getValue().getExpiryDate()));
        });

        table.getColumns().addAll(idCol, productIdCol, producedCol, remainingCol, prodDateCol, expDateCol);
        table.setItems(Main.batches);
        table.setColumnResizePolicy(table.CONSTRAINED_RESIZE_POLICY);

        all = new VBox(15, title, table);
        all.setAlignment(Pos.CENTER);
        all.setMinWidth(900);
    }

    public VBox getAll() {
        return all;
    }
}
