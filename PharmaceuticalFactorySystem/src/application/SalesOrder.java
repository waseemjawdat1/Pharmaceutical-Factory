package application;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class SalesOrder {

	private int salesOrderId;
	private int customerId;
	private int employeeId;
	private Date orderDate;
	private double totalAmount;

	public SalesOrder(int customerId, int employeeId, Date orderDate, double totalAmount) throws SQLException {
		this.customerId = customerId;
		this.employeeId = employeeId;
		this.orderDate = orderDate;
		this.totalAmount = totalAmount;
		addSalesOrderToDB();
	}

	public SalesOrder(int salesOrderId, int customerId, int employeeId, Date orderDate, double totalAmount) {
		this.salesOrderId = salesOrderId;
		this.customerId = customerId;
		this.employeeId = employeeId;
		this.orderDate = orderDate;
		this.totalAmount = totalAmount;
	}

	private void addSalesOrderToDB() throws SQLException {
		String sql = "INSERT INTO sales_orders (customer_id, employee_id, order_date, total_amount) VALUES (?, ?, ?, ?)";
		try (PreparedStatement stmt = Main.conn.prepareStatement(sql)) {
			stmt.setInt(1, customerId);
			stmt.setInt(2, employeeId);
			stmt.setDate(3, orderDate);
			stmt.setDouble(4, totalAmount);
			stmt.executeUpdate();
			this.salesOrderId = getLastId();
		}
	}

	public void updateSalesOrder(int customerId, int employeeId, Date orderDate, double totalAmount) throws SQLException {
		String sql = "UPDATE sales_orders SET customer_id = ?, employee_id = ?, order_date = ?, total_amount = ? WHERE sales_order_id = ?";
		try (PreparedStatement stmt = Main.conn.prepareStatement(sql)) {
			stmt.setInt(1, customerId);
			stmt.setInt(2, employeeId);
			stmt.setDate(3, orderDate);
			stmt.setDouble(4, totalAmount);
			stmt.setInt(5, salesOrderId);
			stmt.executeUpdate();

			this.customerId = customerId;
			this.employeeId = employeeId;
			this.orderDate = orderDate;
			this.totalAmount = totalAmount;
		}
	}

	public static int getLastId() throws SQLException {
		String sql = "SELECT MAX(sales_order_id) AS last_id FROM sales_orders";
		try (Statement stmt = Main.conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
			if (rs.next()) {
				return rs.getInt("last_id");
			}
		}
		return -1;
	}

	public int getSalesOrderId() {
		return salesOrderId;
	}

	public void setSalesOrderId(int salesOrderId) {
		this.salesOrderId = salesOrderId;
	}

	public int getCustomerId() {
		return customerId;
	}

	public void setCustomerId(int customerId) {
		this.customerId = customerId;
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
