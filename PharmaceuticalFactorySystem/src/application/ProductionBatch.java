package application;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ProductionBatch {
    private int batchId;
    private int productId;
    private int quantityProduced;
    private int remaining;
    private Date productionDate;
    private Date expiryDate;

    public ProductionBatch(int batchId, int productId, int quantityProduced, int remaining, Date productionDate, Date expiryDate) {
        this.batchId = batchId;
        this.productId = productId;
        this.quantityProduced = quantityProduced;
        this.remaining = remaining;
        this.productionDate = productionDate;
        this.expiryDate = expiryDate;
    }

    public ProductionBatch(int productId, int quantityProduced, Date productionDate, Date expiryDate) throws SQLException {
        this.productId = productId;
        this.quantityProduced = quantityProduced;
        this.remaining = quantityProduced;
        this.productionDate = productionDate;
        this.expiryDate = expiryDate;
        addBatchToDB();
    }

    public void addBatchToDB() throws SQLException {
        String sql = "INSERT INTO production_batches (product_id, quantity_produced, remaining, production_date, expiry_date) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = Main.conn.prepareStatement(sql)) {
            stmt.setInt(1, productId);
            stmt.setInt(2, quantityProduced);
            stmt.setInt(3, remaining);
            stmt.setDate(4, productionDate);
            stmt.setDate(5, expiryDate);
            stmt.executeUpdate();
            this.batchId = getLastId();
        }
    }

    public void updateBatch(int newProductId, int newRemaining, Date newProductionDate, Date newExpiryDate) throws SQLException {
        String sql = "UPDATE production_batches SET product_id = ?, remaining = ?, production_date = ?, expiry_date = ? WHERE batch_id = ?";
        try (PreparedStatement stmt = Main.conn.prepareStatement(sql)) {
            stmt.setInt(1, newProductId);
            stmt.setInt(2, newRemaining);
            stmt.setDate(3, newProductionDate);
            stmt.setDate(4, newExpiryDate);
            stmt.setInt(5, batchId);
            stmt.executeUpdate();

            this.productId = newProductId;
            this.remaining = newRemaining;
            this.productionDate = newProductionDate;
            this.expiryDate = newExpiryDate;
        }
    }

    public int getLastId() throws SQLException {
        String sql = "SELECT MAX(batch_id) AS last_id FROM production_batches";
        Statement stmt = Main.conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql);
        if (rs.next()) {
            return rs.getInt("last_id");
        }
        return -1;
    }

    public int getBatchId() { return batchId; }
    public int getProductId() { return productId; }
    public int getQuantityProduced() { return quantityProduced; }
    public int getRemaining() { return remaining; }
    public Date getProductionDate() { return productionDate; }
    public Date getExpiryDate() { return expiryDate; }

    public void setRemaining(int remaining) {
        this.remaining = remaining;
    }
}
