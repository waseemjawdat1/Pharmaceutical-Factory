package application;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Supplier {
    private int supplierId;
    private String name;
    private String email;
    private String phone;
    private String address;

    public Supplier(String name, String email, String phone, String address) throws SQLException {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.address = address;
        addSupplierToDB();
    }

    public Supplier(int supplierId, String name, String email, String phone, String address) {
        this.supplierId = supplierId;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.address = address;
    }

    private void addSupplierToDB() throws SQLException {
        String addSupplier = "INSERT INTO suppliers (name, email, phone, address) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = Main.conn.prepareStatement(addSupplier)) {
            stmt.setString(1, name);
            stmt.setString(2, email);
            stmt.setString(3, phone);
            stmt.setString(4, address);
            stmt.executeUpdate();
            this.supplierId = getLastId();
        }
    }

    public void updateSupplier(String newName, String newEmail, String newPhone, String newAddress) throws SQLException {
        String updateSql = "UPDATE suppliers SET name = ?, email = ?, phone = ?, address = ? WHERE supplier_id = ?";
        try (PreparedStatement stmt = Main.conn.prepareStatement(updateSql)) {
            stmt.setString(1, newName);
            stmt.setString(2, newEmail);
            stmt.setString(3, newPhone);
            stmt.setString(4, newAddress);
            stmt.setInt(5, supplierId);
            stmt.executeUpdate();
            this.name = newName;
            this.email = newEmail;
            this.phone = newPhone;
            this.address = newAddress;
        }
    }

    private int getLastId() throws SQLException {
        String sql = "SELECT MAX(supplier_id) AS last_id FROM suppliers";
        Statement stmt = Main.conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql);
        if (rs.next()) {
            return rs.getInt("last_id");
        }
        return -1;
    }

    public int getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(int supplierId) {
        this.supplierId = supplierId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
