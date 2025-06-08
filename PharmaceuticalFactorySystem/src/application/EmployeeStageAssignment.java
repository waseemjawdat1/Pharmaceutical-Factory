package application;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class EmployeeStageAssignment {

	private int employeeId;
	private int stageId;

	public EmployeeStageAssignment(int employeeId, int stageId) {
		this.employeeId = employeeId;
		this.stageId = stageId;
	}

	public EmployeeStageAssignment(int employeeId, int stageId, boolean addToDB) throws SQLException {
		this.employeeId = employeeId;
		this.stageId = stageId;
		if (addToDB)
			addToDB();
	}

	private void addToDB() throws SQLException {
		String sql = "INSERT INTO employee_stage_assignments (employee_id, stage_id) VALUES (?, ?)";
		try (PreparedStatement stmt = Main.conn.prepareStatement(sql)) {
			stmt.setInt(1, employeeId);
			stmt.setInt(2, stageId);
			stmt.executeUpdate();
		}
	}

	public int getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(int employeeId) {
		this.employeeId = employeeId;
	}

	public int getStageId() {
		return stageId;
	}

	public void setStageId(int stageId) {
		this.stageId = stageId;
	}
}
