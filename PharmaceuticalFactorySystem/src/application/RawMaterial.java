package application;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;

public class RawMaterial {

    private int materialId;
    private String name;
    private int unit;
    private int supplierId;
    private double price;

    public RawMaterial(String name, int unit, int supplierId, double price) throws SQLException {
        this.name = name;
        this.unit = unit;
        this.supplierId = supplierId;
        this.price = price;
        addMaterialToDB();
    }

    public RawMaterial(int materialId, String name, int unit, int supplierId, double price) {
        this.materialId = materialId;
        this.name = name;
        this.unit = unit;
        this.supplierId = supplierId;
        this.price = price;
    }

    private void addMaterialToDB() throws SQLException {
        String sql = "INSERT INTO raw_materials (name, unit, supplier_id, price) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = Main.conn.prepareStatement(sql)) {
            stmt.setString(1, name);
            stmt.setInt(2, unit);
            if (supplierId == -1) {
                stmt.setNull(3, Types.INTEGER);
            } else {
                stmt.setInt(3, supplierId);
            }
            stmt.setDouble(4, price);
            stmt.executeUpdate();
            this.materialId = getLastId();
        }
    }

    public void updateMaterial(String name, int unit, int supplierId, double price) throws SQLException {
        String sql = "UPDATE raw_materials SET name = ?, unit = ?, supplier_id = ?, price = ? WHERE material_id = ?";
        try (PreparedStatement stmt = Main.conn.prepareStatement(sql)) {
            stmt.setString(1, name);
            stmt.setInt(2, unit);
            if (supplierId == -1) {
                stmt.setNull(3, Types.INTEGER);
            } else {
                stmt.setInt(3, supplierId);
            }
            stmt.setDouble(4, price);
            stmt.setInt(5, materialId);
            stmt.executeUpdate();

            this.name = name;
            this.unit = unit;
            this.supplierId = supplierId;
            this.price = price;
        }
    }

    private int getLastId() throws SQLException {
        String sql = "SELECT MAX(material_id) AS last_id FROM raw_materials";
        try (Statement stmt = Main.conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt("last_id");
            }
        }
        return -1;
    }

    public int getMaterialId() {
        return materialId;
    }

    public void setMaterialId(int materialId) {
        this.materialId = materialId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getUnit() {
        return unit;
    }

    public void setUnit(int unit) {
        this.unit = unit;
    }

    public int getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(int supplierId) {
        this.supplierId = supplierId;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
