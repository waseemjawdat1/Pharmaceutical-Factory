package application;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class PurchaseOrder {

	private int purchaseOrderId;
	private int supplierId;
	private int employeeId;
	private Date orderDate;
	private double totalAmount;

	public PurchaseOrder(int supplierId, int employeeId, Date orderDate, double totalAmount) throws SQLException {
		this.supplierId = supplierId;
		this.employeeId = employeeId;
		this.orderDate = orderDate;
		this.totalAmount = totalAmount;
		addPurchaseOrderToDB();
	}

	public PurchaseOrder(int purchaseOrderId, int supplierId, int employeeId, Date orderDate, double totalAmount) {
		this.purchaseOrderId = purchaseOrderId;
		this.supplierId = supplierId;
		this.employeeId = employeeId;
		this.orderDate = orderDate;
		this.totalAmount = totalAmount;
	}

	private void addPurchaseOrderToDB() throws SQLException {
		String sql = "INSERT INTO purchase_orders (supplier_id, employee_id, order_date, total_amount) VALUES (?, ?, ?, ?)";
		try (PreparedStatement stmt = Main.conn.prepareStatement(sql)) {
			stmt.setInt(1, supplierId);
			stmt.setInt(2, employeeId);
			stmt.setDate(3, orderDate);
			stmt.setDouble(4, totalAmount);
			stmt.executeUpdate();
			this.purchaseOrderId = getLastId();
		}
	}

	static int getLastId() throws SQLException {
		String sql = "SELECT MAX(purchase_order_id) AS last_id FROM purchase_orders";
		try (Statement stmt = Main.conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
			if (rs.next()) {
				return rs.getInt("last_id");
			}
		}
		return -1;
	}

	public int getPurchaseOrderId() {
		return purchaseOrderId;
	}

	public void setPurchaseOrderId(int purchaseOrderId) {
		this.purchaseOrderId = purchaseOrderId;
	}

	public int getSupplierId() {
		return supplierId;
	}

	public void setSupplierId(int supplierId) {
		this.supplierId = supplierId;
	}

	public int getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(int employeeId) {
		this.employeeId = employeeId;
	}

	public Date getOrderDate() {
		return orderDate;
	}

	public void setOrderDate(Date orderDate) {
		this.orderDate = orderDate;
	}

	public double getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(double totalAmount) {
		this.totalAmount = totalAmount;
	}
}
