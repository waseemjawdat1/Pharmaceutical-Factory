package application;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ManufacturingStage {

	private int stageId;
	private String name;

	public ManufacturingStage(int stageId, String name) {
		this.stageId = stageId;
		this.name = name;
	}

	public ManufacturingStage(String name) throws SQLException {
		this.name = name;
		addStageToDB();
	}

	private void addStageToDB() throws SQLException {
		String sql = "INSERT INTO manufacturing_stages (name) VALUES (?)";
		try (PreparedStatement stmt = Main.conn.prepareStatement(sql)) {
			stmt.setString(1, name);
			stmt.executeUpdate();
			this.stageId = getLastId();
		}
	}

	public void updateStage(String name) throws SQLException {
		String sql = "UPDATE manufacturing_stages SET name = ? WHERE stage_id = ?";
		try (PreparedStatement stmt = Main.conn.prepareStatement(sql)) {
			stmt.setString(1, name);
			stmt.setInt(2, stageId);
			stmt.executeUpdate();
			this.name = name;
		}
	}

	private int getLastId() throws SQLException {
		String sql = "SELECT MAX(stage_id) AS last_id FROM manufacturing_stages";
		Statement stmt = Main.conn.createStatement();
		ResultSet rs = stmt.executeQuery(sql);
		if (rs.next()) {
			return rs.getInt("last_id");
		}
		return -1;
	}

	public int getStageId() {
		return stageId;
	}

	public void setStageId(int stageId) {
		this.stageId = stageId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
