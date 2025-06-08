package application;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Warehouse {
	private int warehouseId;
	private String location;
	private int capacity;

	public Warehouse(int warehouseId, String location, int capacity) {
		this.warehouseId = warehouseId;
		this.location = location;
		this.capacity = capacity;
	}

	public Warehouse(String location, int capacity) throws SQLException {
		this.location = location;
		this.capacity = capacity;
		addWarehouseToDB();
	}

	private void addWarehouseToDB() throws SQLException {
		String sql = "INSERT INTO warehouses (location, capacity) VALUES (?, ?)";
		try (PreparedStatement stmt = Main.conn.prepareStatement(sql)) {
			stmt.setString(1, location);
			stmt.setInt(2, capacity);
			stmt.executeUpdate();
			this.warehouseId = getLastId();
		}
	}

	public void updateWarehouse(String location, int capacity) throws SQLException {
		String sql = "UPDATE warehouses SET location = ?, capacity = ? WHERE warehouse_id = ?";
		try (PreparedStatement stmt = Main.conn.prepareStatement(sql)) {
			stmt.setString(1, location);
			stmt.setInt(2, capacity);
			stmt.setInt(3, warehouseId);
			stmt.executeUpdate();
			this.location = location;
			this.capacity = capacity;
		}
	}

	private int getLastId() throws SQLException {
		String sql = "SELECT MAX(warehouse_id) AS last_id FROM warehouses";
		Statement stmt = Main.conn.createStatement();
		ResultSet rs = stmt.executeQuery(sql);
		if (rs.next()) {
			return rs.getInt("last_id");
		}
		return -1;
	}

	public int getWarehouseId() {
		return warehouseId;
	}

	public void setWarehouseId(int warehouseId) {
		this.warehouseId = warehouseId;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public int getCapacity() {
		return capacity;
	}

	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}
}
