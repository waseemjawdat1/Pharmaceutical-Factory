package application;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ProductRequirement {

    private int productId;
    private int materialId;
    private int quantity;

    public ProductRequirement(int productId, int materialId, int quantity) throws SQLException {
        this.productId = productId;
        this.materialId = materialId;
        this.quantity = quantity;
        addToDatabase();
    }

    public ProductRequirement(int productId, int materialId, int quantity, boolean skipInsert) {
        this.productId = productId;
        this.materialId = materialId;
        this.quantity = quantity;
    }

    private void addToDatabase() throws SQLException {
        String sql = "INSERT INTO product_requirements (product_id, material_id, quantity) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = Main.conn.prepareStatement(sql)) {
            stmt.setInt(1, productId);
            stmt.setInt(2, materialId);
            stmt.setInt(3, quantity);
            stmt.executeUpdate();
        }
    }

    public void updateRequirement(int productId, int materialId, int quantity) throws SQLException {
        String sql = "UPDATE product_requirements SET quantity = ? WHERE product_id = ? AND material_id = ?";
        try (PreparedStatement stmt = Main.conn.prepareStatement(sql)) {
            stmt.setInt(1, quantity);
            stmt.setInt(2, productId);
            stmt.setInt(3, materialId);
            stmt.executeUpdate();
            this.productId = productId;
            this.materialId = materialId;
            this.quantity = quantity;
        }
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getMaterialId() {
        return materialId;
    }

    public void setMaterialId(int materialId) {
        this.materialId = materialId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

}