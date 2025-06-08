package application;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;

public class Product {
	private int productId;
	private String name;
	private String category;
	private Date expiryDate;
	private int quantity;
	private int warehouseId;
	private Date createdAt;

	public Product(int productId, String name, String category, Date expiryDate, int quantity, int warehouseId, Date createdAt) {
		this.productId = productId;
		this.name = name;
		this.category = category;
		this.expiryDate = expiryDate;
		this.quantity = quantity;
		this.warehouseId = warehouseId;
		this.createdAt = createdAt;
	}

	public Product(String name, String category, Date expiryDate, int quantity, int warehouseId, Date createdAt) throws SQLException {
		this.name = name;
		this.category = category;
		this.expiryDate = expiryDate;
		this.quantity = quantity;
		this.warehouseId = warehouseId;
		this.createdAt = createdAt;
		addProductToDB();
	}

	private void addProductToDB() throws SQLException {
		String sql = "INSERT INTO products (name, category, expiry_date, quantity, warehouse_id, created_at) VALUES (?, ?, ?, ?, ?, ?)";
		try (PreparedStatement stmt = Main.conn.prepareStatement(sql)) {
			stmt.setString(1, name);
			stmt.setString(2, category);
			stmt.setDate(3, expiryDate);
			stmt.setInt(4, quantity);
			if (warehouseId == -1)
				stmt.setNull(5, Types.INTEGER);
			else
				stmt.setInt(5, warehouseId);
			stmt.setDate(6, createdAt);
			stmt.executeUpdate();
			this.productId = getLastId();
		}
	}

	public void updateProduct(String name, String category, Date expiryDate, int quantity, int warehouseId, Date createdAt) throws SQLException {
		String sql = "UPDATE products SET name = ?, category = ?, expiry_date = ?, quantity = ?, warehouse_id = ?, created_at = ? WHERE product_id = ?";
		try (PreparedStatement stmt = Main.conn.prepareStatement(sql)) {
			stmt.setString(1, name);
			stmt.setString(2, category);
			stmt.setDate(3, expiryDate);
			stmt.setInt(4, quantity);
			if (warehouseId == -1)
				stmt.setNull(5, Types.INTEGER);
			else
				stmt.setInt(5, warehouseId);
			stmt.setDate(6, createdAt);
			stmt.setInt(7, productId);
			stmt.executeUpdate();

			this.name = name;
			this.category = category;
			this.expiryDate = expiryDate;
			this.quantity = quantity;
			this.warehouseId = warehouseId;
			this.createdAt = createdAt;
		}
	}

	private int getLastId() throws SQLException {
		String sql = "SELECT MAX(product_id) AS last_id FROM products";
		Statement stmt = Main.conn.createStatement();
		ResultSet rs = stmt.executeQuery(sql);
		if (rs.next()) {
			return rs.getInt("last_id");
		}
		return -1;
	}

	public int getProductId() {
		return productId;
	}

	public void setProductId(int productId) {
		this.productId = productId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public Date getExpiryDate() {
		return expiryDate;
	}

	public void setExpiryDate(Date expiryDate) {
		this.expiryDate = expiryDate;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public int getWarehouseId() {
		return warehouseId;
	}

	public void setWarehouseId(int warehouseId) {
		this.warehouseId = warehouseId;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}
}
