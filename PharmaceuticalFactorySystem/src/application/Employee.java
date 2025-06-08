package application;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;

public class Employee {
	private int employeeId;
	private String name;
	private String email;
	private String phone;
	private int departmentId;
	private String jobTitle;
	private double salary;
	private int yearHired;
	private Calendar joinedAt;

	public Employee(int employeeId, String name, String email, String phone, int departmentId, String jobTitle,
			double salary, int yearHired, Calendar joinedAt) {
		this.employeeId = employeeId;
		this.name = name;
		this.email = email;
		this.phone = phone;
		this.departmentId = departmentId;
		this.jobTitle = jobTitle;
		this.salary = salary;
		this.yearHired = yearHired;
		this.joinedAt = joinedAt;
	}

	public Employee(String name, String email, String phone, int departmentId, String jobTitle, double salary,
			int yearHired, Calendar joinedAt) throws SQLException {
		this.name = name;
		this.email = email;
		this.phone = phone;
		this.departmentId = departmentId;
		this.jobTitle = jobTitle;
		this.salary = salary;
		this.yearHired = yearHired;
		this.joinedAt = joinedAt;
		addEmpToDB();
	}

	public void addEmpToDB() throws SQLException {
		String addEmp = "INSERT INTO employees (name , email , phone , department_id , job_title , salary , year_hired , joined_at) VALUES (? , ? , ? , ? , ? , ? , ? , ?)";
		Date date = new Date(joinedAt.getTimeInMillis());
		try (PreparedStatement stmt = Main.conn.prepareStatement(addEmp)) {
			stmt.setString(1, name);
			stmt.setString(2, email);
			stmt.setString(3, phone);
			stmt.setInt(4, departmentId);
			stmt.setString(5, jobTitle);
			stmt.setDouble(6, salary);
			stmt.setInt(7, yearHired);
			stmt.setDate(8, date);
			stmt.executeUpdate();
			this.employeeId = getLastId();
		}
	}

	public int getLastId() throws SQLException {
		String lastEmpId = "SELECT MAX(employee_id) AS last_id FROM employees";
		Statement stmt = Main.conn.createStatement();
		ResultSet resultSet = stmt.executeQuery(lastEmpId);

		if (resultSet.next())
			return resultSet.getInt("last_id");
		return -1;
	}

	public void updateEmployee(String newName, String newEmail, String newPhone, int newDepartmentId,
			String newJobTitle, double newSalary, int newYearHired, Calendar newJoinedAt) throws SQLException {
		String updateEmp = "Update employees SET name = ?, email = ?, phone = ?, department_id = ?, job_title = ?, salary = ?, year_hired = ?, joined_at = ? WHERE employee_id = ?";
		Date date = new Date(newJoinedAt.getTimeInMillis());
		try (PreparedStatement stmt = Main.conn.prepareStatement(updateEmp)) {
			stmt.setString(1, newName);
			stmt.setString(2, newEmail);
			stmt.setString(3, newPhone);
			stmt.setInt(4, newDepartmentId);
			stmt.setString(5, newJobTitle);
			stmt.setDouble(6, newSalary);
			stmt.setInt(7, newYearHired);
			stmt.setDate(8, date);
			stmt.setInt(9, employeeId);
			stmt.executeUpdate();
			this.name = newName;
			this.email = newEmail;
			this.phone = newPhone;
			this.departmentId = newDepartmentId;
			this.jobTitle = newJobTitle;
			this.salary = newSalary;
			this.yearHired = newYearHired;
			this.joinedAt = newJoinedAt;
		}
	}

	public int getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(int employeeId) {
		this.employeeId = employeeId;
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

	public int getDepartmentId() {
		return departmentId;
	}

	public void setDepartmentId(int departmentId) {
		this.departmentId = departmentId;
	}

	public String getJobTitle() {
		return jobTitle;
	}

	public void setJobTitle(String jobTitle) {
		this.jobTitle = jobTitle;
	}

	public double getSalary() {
		return salary;
	}

	public void setSalary(double salary) {
		this.salary = salary;
	}

	public int getYearHired() {
		return yearHired;
	}

	public void setYearHired(int yearHired) {
		this.yearHired = yearHired;
	}

	public Calendar getJoinedAt() {
		return joinedAt;
	}

	public void setJoinedAt(Calendar joinedAt) {
		this.joinedAt = joinedAt;
	}
}
