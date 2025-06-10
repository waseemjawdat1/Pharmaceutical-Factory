package application;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;

import com.mysql.cj.xdevapi.PreparableStatement;

public class Department {
	private int deptId;
	private String deptName;
	private int managerId;

	public Department(int deptId, String deptName, int managerId) {
		this.deptId = deptId;
		this.deptName = deptName;
		this.managerId = managerId;
	}

	public Department(String deptName, int managerId) throws SQLException {
		this.deptName = deptName;
		this.managerId = managerId;
		addDeptToDB();
	}

	public void addDeptToDB() throws SQLException {
		String addDept = "INSERT INTO departments (name , manager_id) VALUES (? , ?)";
		try (PreparedStatement stmt = Main.conn.prepareStatement(addDept)) {
			stmt.setString(1, deptName);
			if (managerId == -1)
				stmt.setNull(2, Types.INTEGER);
			else
				stmt.setInt(2, managerId);
			stmt.executeUpdate();
			this.deptId = getLastId();
		}
	}

	public void updateDepartment(String newName, int newManager) throws SQLException {
		String update = "Update departments SET name = ?, manager_id = ? WHERE department_id = ?";
		try (PreparedStatement stmt = Main.conn.prepareStatement(update)) {
			stmt.setString(1, newName);
			if (newManager == -1)
				stmt.setNull(2, Types.INTEGER);
			else
				stmt.setInt(2, newManager);
			stmt.setInt(3, deptId);
			stmt.executeUpdate();
			this.deptName = newName;
			this.managerId = newManager;
		}

	}

	public int getLastId() throws SQLException {
		String lastDeptId = "SELECT MAX(department_id) AS last_id FROM departments";
		Statement stmt = Main.conn.createStatement();
		ResultSet rs = stmt.executeQuery(lastDeptId);

		if (rs.next()) {
			return rs.getInt("last_id");
		}
		return -1;
	}

	public int getDeptId() {
		return deptId;
	}

	public void setDeptId(int deptId) {
		this.deptId = deptId;
	}

	public String getDeptName() {
		return deptName;
	}

	public void setDeptName(String deptName) {
		this.deptName = deptName;
	}

	public int getManagerId() {
		return managerId;
	}

	public void setManagerId(int managerId) {
		this.managerId = managerId;
	}

}
