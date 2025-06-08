package application;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Customer {

	private int customerId;
	private String name;
	private String email;
	private String phone;
	private String address;

	public Customer(String name, String email, String phone, String address) throws SQLException {
		this.name = name;
		this.email = email;
		this.phone = phone;
		this.address = address;
		addCustomerToDB();
	}

	public Customer(int customerId, String name, String email, String phone, String address) {
		this.customerId = customerId;
		this.name = name;
		this.email = email;
		this.phone = phone;
		this.address = address;
	}

	private void addCustomerToDB() throws SQLException {
		String sql = "INSERT INTO customers (name, email, phone, address) VALUES (?, ?, ?, ?)";
		try (PreparedStatement stmt = Main.conn.prepareStatement(sql)) {
			stmt.setString(1, name);
			stmt.setString(2, email);
			stmt.setString(3, phone);
			stmt.setString(4, address);
			stmt.executeUpdate();
			this.customerId = getLastId();
		}
	}

	public void updateCustomer(String name, String email, String phone, String address) throws SQLException {
		String sql = "UPDATE customers SET name = ?, email = ?, phone = ?, address = ? WHERE customer_id = ?";
		try (PreparedStatement stmt = Main.conn.prepareStatement(sql)) {
			stmt.setString(1, name);
			stmt.setString(2, email);
			stmt.setString(3, phone);
			stmt.setString(4, address);
			stmt.setInt(5, customerId);
			stmt.executeUpdate();

			this.name = name;
			this.email = email;
			this.phone = phone;
			this.address = address;
		}
	}

	private int getLastId() throws SQLException {
		String sql = "SELECT MAX(customer_id) AS last_id FROM customers";
		try (Statement stmt = Main.conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
			if (rs.next()) {
				return rs.getInt("last_id");
			}
		}
		return -1;
	}

	public int getCustomerId() {
		return customerId;
	}

	public void setCustomerId(int customerId) {
		this.customerId = customerId;
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
