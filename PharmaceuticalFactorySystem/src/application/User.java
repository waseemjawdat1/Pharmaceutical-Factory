package application;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class User {
private int userId;
private String userName;
private String password;
private String role;
private int employeeId;

public User (int userId , String userName , String password , String role , int employeeId ) {
	this.userId= userId;
	this.userName = userName;
	this.password = password;
	this.role = role;
	this.employeeId = employeeId;
}

public User (String userName , String password , String role , int employeeId) throws SQLException{
	this.userName = userName;
	this.password = password;
	this.role = role;
	this.employeeId = employeeId;
	addUserToDB();
}
public void addUserToDB() throws SQLException {
    String addUserSQL = "INSERT INTO users (username, password, role, employee_id) VALUES (?, ?, ?, ?)";
    PreparedStatement stmt = Main.conn.prepareStatement(addUserSQL);
    stmt.setString(1, userName);
    stmt.setString(2, password);
    stmt.setString(3, role);
    stmt.setInt(4, employeeId);
    stmt.executeUpdate();
    this.userId = getLastInsertedUserId();

   
}
public int getLastInsertedUserId() throws SQLException {
    String sql = "SELECT MAX(user_id) AS last_id FROM users";
    Statement stmt = Main.conn.createStatement();
    ResultSet rs = stmt.executeQuery(sql);

    if (rs.next()) {
        return rs.getInt("last_id");
    }
    return -1;
}

public int getUserId() {
	return userId;
}

public void setUserId(int userId) {
	this.userId = userId;
}

public String getUserName() {
	return userName;
}

public void setUserName(String userName) throws SQLException {
	this.userName = userName;
	String update = "Update users SET username = ? WHERE user_id = ?";
	  try (PreparedStatement stmt = Main.conn.prepareStatement(update)) {
	        stmt.setString(1, userName);
	        stmt.setInt(2, userId);
	        stmt.executeUpdate();
	  }
}

public String getPassword() {
	return password;
}

public void setPassword(String password) throws SQLException {
	this.password = password;
	String update = "Update users SET password = ? WHERE user_id = ?";
	  try (PreparedStatement stmt = Main.conn.prepareStatement(update)) {
	        stmt.setString(1, password);
	        stmt.setInt(2, userId);
	        stmt.executeUpdate();
	  }
}

public String getRole() {
	return role;
}

public void setRole(String role) throws SQLException {
	this.role = role;
	String update = "Update users SET role = ? WHERE user_id = ?";
	  try (PreparedStatement stmt = Main.conn.prepareStatement(update)) {
	        stmt.setString(1, role);
	        stmt.setInt(2, userId);
	        stmt.executeUpdate();
	  }
}

public int getEmployeeId() {
	return employeeId;
}

public void setEmployeeId(int employeeId) throws SQLException {
	this.employeeId = employeeId;
	String update = "Update users SET employee_id = ? WHERE user_id = ?";
	  try (PreparedStatement stmt = Main.conn.prepareStatement(update)) {
	        stmt.setInt(1, employeeId);
	        stmt.setInt(2, userId);
	        stmt.executeUpdate();
	  }
}

}
