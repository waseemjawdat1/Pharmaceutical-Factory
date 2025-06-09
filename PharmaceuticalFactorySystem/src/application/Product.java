package application;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;

public class Product {
	private int productId;
	private String name;
	private String category;
	private int quantity;
	private int warehouseId;
	private double price;

	public Product(int productId, String name, String category, int quantity, int warehouseId, double price) {
		this.productId = productId;
		this.name = name;
		this.category = category;
		this.quantity = quantity;
		this.warehouseId = warehouseId;
		this.price = price;
	}

	public Product(String name, String category, int quantity, int warehouseId, double price) throws SQLException {
		this.name = name;
		this.category = category;
		this.quantity = quantity;
		this.warehouseId = warehouseId;
		this.price = price;
		addProductToDB();
	}

	private void addProductToDB() throws SQLException {
		String sql = "INSERT INTO products (name, category, quantity, warehouse_id, price) VALUES (?, ?, ?, ?, ?)";
		try (PreparedStatement stmt = Main.conn.prepareStatement(sql)) {
			stmt.setString(1, name);
			stmt.setString(2, category);
			stmt.setInt(3, quantity);
			if (warehouseId == -1)
				stmt.setNull(4, Types.INTEGER);
			else
				stmt.setInt(4, warehouseId);
			stmt.setDouble(5, price);
			stmt.executeUpdate();
			this.productId = getLastId();
		}
	}

	public void updateProduct(String name, String category, int quantity, int warehouseId, double price) throws SQLException {
		String sql = "UPDATE products SET name = ?, category = ?, quantity = ?, warehouse_id = ?, price = ? WHERE product_id = ?";
		try (PreparedStatement stmt = Main.conn.prepareStatement(sql)) {
			stmt.setString(1, name);
			stmt.setString(2, category);
			stmt.setInt(3, quantity);
			if (warehouseId == -1)
				stmt.setNull(4, Types.INTEGER);
			else
				stmt.setInt(4, warehouseId);
			stmt.setDouble(5, price);
			stmt.setInt(6, productId);
			stmt.executeUpdate();

			this.name = name;
			this.category = category;
			this.quantity = quantity;
			this.warehouseId = warehouseId;
			this.price = price;
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

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}
}
