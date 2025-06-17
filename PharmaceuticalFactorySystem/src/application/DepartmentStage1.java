package application;

import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

public class DepartmentStage1 {
    private TilePane cardPane;
    private VBox layout;
    private TextField deptNameTF;
    private Button addBtn;

    public DepartmentStage1() {
        deptNameTF = new MyTextField();
        addBtn = new Button("➕ Add Department");

        Label titleLabel = new Label("Department Management");
        titleLabel.setStyle("""
            -fx-font-size: 32px;
            -fx-font-weight: bold;
            -fx-text-fill: #1e3a8a;
            -fx-padding: 0 0 10 0;
            """);

        Label subtitleLabel = new Label("Manage your organization's departments");
        subtitleLabel.setStyle("""
            -fx-font-size: 16px;
            -fx-text-fill: #64748b;
            -fx-padding: 0 0 20 0;
            """);

        VBox headerBox = new VBox(5, titleLabel);
        headerBox.setAlignment(Pos.CENTER);

        Label deptNameL = new MyLabel("Department Name:");
        deptNameL.setStyle("""
            -fx-font-size: 16px;
            -fx-font-weight: 700;
            -fx-text-fill: #1e3a8a;
            -fx-background-color: #e0f2fe;
            -fx-background-radius: 8;
            -fx-padding: 8 16 8 16;
            -fx-border-color: #7dd3fc;
            -fx-border-radius: 8;
            -fx-border-width: 1;
            """);

        deptNameTF.setStyle("""
            -fx-background-color: #ffffff;
            -fx-border-color: #7dd3fc;
            -fx-border-radius: 8;
            -fx-background-radius: 8;
            -fx-padding: 12 16 12 16;
            -fx-font-size: 14px;
            -fx-pref-width: 250;
            -fx-effect: dropshadow(gaussian, rgba(125,211,252,0.3), 4, 0.0, 0, 1);
            """);
        
        addBtn.setStyle("""
            -fx-background-color: #0ea5e9;
            -fx-text-fill: white;
            -fx-font-weight: bold;
            -fx-font-size: 14px;
            -fx-border-radius: 8;
            -fx-background-radius: 8;
            -fx-padding: 12 20 12 20;
            -fx-effect: dropshadow(gaussian, rgba(14,165,233,0.4), 8, 0.2, 0, 2);
            -fx-cursor: hand;
            """);

        addBtn.setOnAction(e->{
        	String deptNameS = deptNameTF.getText();
			if (deptNameS == null || deptNameS.isEmpty()) {
				Main.notValidAlert("Not Valid Input", "Department name is empty");
				return;
			}
			boolean isExist = false;
			for (int i = 0; i < Main.departments.size(); i++) {
				if (Main.departments.get(i).getDeptName().equalsIgnoreCase(deptNameS)) {
					isExist = true;
					break;
				}
			}
			if (isExist) {
				Main.notValidAlert("Not Valid Input", "there is department with name : " + deptNameS);
				return;
			}
			Department d;
			try {
				d = new Department(deptNameS, -1);
			} catch (SQLException e1) {
				Main.notValidAlert("Error", e1.getMessage());
				return;
			}
			Main.departments.add(d);
			Main.validAlert("Department Added !", "Department added to system successuflly");
			deptNameTF.clear();
			loadCards();
        });
        
        HBox addBox = new HBox(15, deptNameL, deptNameTF, addBtn);
        addBox.setAlignment(Pos.CENTER);
        addBox.setPadding(new Insets(20));
        addBox.setStyle("""
            -fx-background-color: #f0f9ff;
            -fx-border-color: #bae6fd;
            -fx-border-radius: 12;
            -fx-background-radius: 12;
            -fx-effect: dropshadow(gaussian, rgba(186,230,253,0.5), 6, 0.0, 0, 1);
            """);

        cardPane = new TilePane();
        cardPane.setHgap(25);
        cardPane.setVgap(25);
        cardPane.setPadding(new Insets(30));
        cardPane.setPrefColumns(2);
        cardPane.setAlignment(Pos.TOP_CENTER);

        ScrollPane scrollPane = new ScrollPane(cardPane);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("""
            -fx-background-color: transparent;
            -fx-background: transparent;
            """);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        layout = new VBox(18, addBox, scrollPane);
        layout.setAlignment(Pos.TOP_CENTER);
        layout.setPadding(new Insets(20));
        layout.setStyle("""
            -fx-background-color: #f8fafc;
            """);

        loadCards();
    }

    private void loadCards() {
        cardPane.getChildren().clear();
        for (Department dept : Main.departments) {
            cardPane.getChildren().add(createDeptCard(dept));
        }
    }

    private VBox createDeptCard(Department dept) {
        VBox cardHeader = new VBox();
        cardHeader.setPrefHeight(80);
        cardHeader.setStyle("""
            -fx-background-color: #0284c7;
            -fx-background-radius: 20 20 0 0;
            -fx-effect: dropshadow(gaussian, rgba(2,132,199,0.4), 12, 0.3, 0, 4);
            """);

        Label icon = new Label("🏢");
        icon.setStyle("""
            -fx-font-size: 42px; 
            -fx-text-fill: white;
            -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 8, 0.2, 0, 2);
            """);

        Label id = new Label("#" + dept.getDeptId());
        id.setStyle("""
            -fx-font-size: 11px;
            -fx-text-fill: white;
            -fx-font-weight: 800;
            -fx-background-color: rgba(255,255,255,0.25);
            -fx-background-radius: 12;
            -fx-padding: 4 10 4 10;
            -fx-border-color: rgba(255,255,255,0.4);
            -fx-border-radius: 12;
            -fx-border-width: 1;
            -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 4, 0.0, 0, 1);
            """);

        VBox headerContent = new VBox(8, icon, id);
        headerContent.setAlignment(Pos.CENTER);
        headerContent.setPadding(new Insets(15, 0, 10, 0));
        
        cardHeader.getChildren().add(headerContent);

        VBox cardBody = new VBox();
        cardBody.setPadding(new Insets(25, 20, 20, 20));
        cardBody.setSpacing(18);
        cardBody.setStyle("""
            -fx-background-color: #ffffff;
            """);

        Label name = new Label(dept.getDeptName());
        name.setStyle("""
            -fx-font-size: 20px;
            -fx-text-fill: #1e3a8a;
            -fx-font-weight: 800;
            -fx-padding: 12 16 12 16;
            -fx-background-color: #e0f2fe;
            -fx-background-radius: 10;
            -fx-border-color: #7dd3fc;
            -fx-border-radius: 10;
            -fx-border-width: 1;
            """);

        String managerName = Main.employees.stream()
                .filter(e -> e.getEmployeeId() == dept.getManagerId())
                .map(Employee::getName)
                .findFirst()
                .orElse("Unassigned");

        Label managerIcon = new Label("👤");
        managerIcon.setStyle("""
            -fx-font-size: 16px;
            -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 2, 0.0, 0, 1);
            """);
        
        Label manager = new Label("Manager: " + managerName);
        manager.setStyle("""
            -fx-font-size: 14px;
            -fx-text-fill: #475569;
            -fx-font-weight: 600;
            """);

        HBox managerBox = new HBox(10, managerIcon, manager);
        managerBox.setAlignment(Pos.CENTER_LEFT);
        managerBox.setStyle("""
            -fx-background-color: #f1f5f9;
            -fx-background-radius: 12;
            -fx-padding: 12 16 12 16;
            -fx-border-color: #cbd5e1;
            -fx-border-radius: 12;
            -fx-border-width: 1;
            """);

        long empCount = Main.employees.stream().filter(e -> e.getDepartmentId() == dept.getDeptId()).count();
        Label count = new Label("👥 " + empCount + " employees");
        count.setStyle("""
            -fx-font-size: 13px;
            -fx-text-fill: #1e3a8a;
            -fx-font-weight: 700;
            -fx-background-color: #dbeafe;
            -fx-background-radius: 20;
            -fx-padding: 8 16 8 16;
            -fx-border-color: #93c5fd;
            -fx-border-radius: 20;
            -fx-border-width: 1;
            -fx-effect: dropshadow(gaussian, rgba(147,197,253,0.3), 6, 0.0, 0, 2);
            """);

        Region separator = new Region();
        separator.setPrefHeight(1);
        separator.setStyle("""
            -fx-background-color: #bfdbfe;
            """);
        separator.setMaxWidth(180);

        Button updateBtn = new Button("✎ Update");
        updateBtn.setStyle("""
            -fx-background-color: #059669;
            -fx-text-fill: white;
            -fx-font-weight: 700;
            -fx-font-size: 12px;
            -fx-border-radius: 10;
            -fx-background-radius: 10;
            -fx-padding: 10 16 10 16;
            -fx-effect: dropshadow(gaussian, rgba(5,150,105,0.4), 10, 0.3, 0, 4);
            -fx-cursor: hand;
            """);

        Button deleteBtn = new Button("🗑 Delete");
        deleteBtn.setStyle("""
            -fx-background-color: #dc2626;
            -fx-text-fill: white;
            -fx-font-weight: 700;
            -fx-font-size: 12px;
            -fx-border-radius: 10;
            -fx-background-radius: 10;
            -fx-padding: 10 16 10 16;
            -fx-effect: dropshadow(gaussian, rgba(220,38,38,0.4), 10, 0.3, 0, 4);
            -fx-cursor: hand;
            """);

        HBox buttonBox = new HBox(10, updateBtn, deleteBtn);
        buttonBox.setAlignment(Pos.CENTER);

        updateBtn.setOnAction(e -> openEditPopup(dept));
        deleteBtn.setOnAction(e -> deleteDepartment(dept));

        cardBody.getChildren().addAll(name, managerBox, count, separator, buttonBox);

        VBox card = new VBox(0, cardHeader, cardBody);
        card.setAlignment(Pos.TOP_CENTER);
        card.setPrefSize(360, 310);
        card.setMaxSize(360, 310);
        card.setStyle(getDefaultCardStyle());

        ScaleTransition scaleIn = new ScaleTransition(Duration.millis(250), card);
        scaleIn.setToX(1.05);
        scaleIn.setToY(1.05);

        ScaleTransition scaleOut = new ScaleTransition(Duration.millis(250), card);
        scaleOut.setToX(1.0);
        scaleOut.setToY(1.0);

        TranslateTransition moveUp = new TranslateTransition(Duration.millis(250), card);
        moveUp.setToY(-12);

        TranslateTransition moveDown = new TranslateTransition(Duration.millis(250), card);
        moveDown.setToY(0);

        card.setOnMouseEntered(e -> {
            card.setStyle(getHoverCardStyle());
            scaleIn.play();
            moveUp.play();
        });

        card.setOnMouseExited(e -> {
            card.setStyle(getDefaultCardStyle());
            scaleOut.play();
            moveDown.play();
        });

        card.setOnMouseClicked(e -> showEmployeesInDepartment(dept));
        return card;
    }

    private void openEditPopup(Department selected) {
        Stage updateDeptStage = new Stage();
        updateDeptStage.setTitle("Update Department");

        Label title = new MyLabel("Update Department", 1);

        Label deptName = new MyLabel("Department Name:");

        TextField deptName1 = new MyTextField();
        deptName1.setText(selected.getDeptName());

        Label managerId = new MyLabel("Manager ID:");

        TextField managerId1 = new MyTextField();
        if (selected.getManagerId() == -1) {
            managerId1.setText("null");
        } else
            managerId1.setText(selected.getManagerId() + "");

        String getDeptEmpCount = "SELECT COUNT(*) FROM employees WHERE department_id = ?";
        try (PreparedStatement stmt = Main.conn.prepareStatement(getDeptEmpCount)) {
            stmt.setInt(1, selected.getDeptId());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int count = rs.getInt(1);
                if (count == 0)
                    managerId1.setEditable(false);
            }
        } catch (SQLException e3) {
            e3.printStackTrace();
        }

        GridPane formBox = new GridPane();
        formBox.addColumn(0, deptName , managerId);
        formBox.addColumn(1,deptName1, managerId1);
        formBox.setAlignment(Pos.CENTER);
        formBox.setVgap(5);
        formBox.setHgap(5);

        Button update = new MyButton("Update", 2);

        Button clear = new MyButton("Clear", 2);

        HBox buttons1 = new HBox(15, update, clear);
        buttons1.setAlignment(Pos.CENTER);

        VBox all1 = new VBox(25, title, formBox, buttons1);
        all1.setAlignment(Pos.CENTER);
        all1.setPadding(new Insets(30));
        all1.setStyle("""
            -fx-background-color: #ffffff;
            -fx-border-color: #e5e7eb;
            -fx-border-radius: 16;
            -fx-background-radius: 16;
            """);

        Scene updateDeptScene = new Scene(all1, 450, 400);
        updateDeptStage.setScene(updateDeptScene);
        updateDeptStage.show();

        clear.setOnAction(e1 -> {
            deptName1.clear();
            if (managerId1.isEditable())
                managerId1.clear();
        });

        update.setOnAction(e1 -> {
            String deptNameS = deptName1.getText();
            if (deptNameS == null || deptNameS.isEmpty()) {
                Main.notValidAlert("Not Valid Input", "Department number empty");
                return;
            }
            String managerIdS = managerId1.getText();
            if (managerIdS == null || managerIdS.isEmpty()) {
                Main.notValidAlert("Not Valid Input", "Manager id is empty");
                return;
            }

            int managerIdInt = -1;
            if (!managerIdS.equalsIgnoreCase("null")) {
                try {
                    managerIdInt = Integer.parseInt(managerIdS.trim());
                } catch (Exception e2) {
                    Main.notValidAlert("Not Valid Input", "Manager Id must be a number");
                    return;
                }
                Employee isExist = null;
                for (int i = 0; i < Main.employees.size(); i++) {
                    if (Main.employees.get(i).getEmployeeId() == managerIdInt) {
                        isExist = Main.employees.get(i);
                        break;
                    }
                }
                if (isExist == null) {
                    Main.notValidAlert("Not Valid Input", "No employee with this id in system");
                    return;
                }
                if (isExist.getDepartmentId() != selected.getDeptId()) {
                    Main.notValidAlert("Not Valid Input", "Manager cant belongs to another department");
                    return;
                }
            }
            try {
                selected.updateDepartment(deptNameS, managerIdInt);
                loadCards();
            } catch (SQLException e2) {
                Main.notValidAlert("Error", e2.getMessage());
                return;
            }
            Main.validAlert("Department Updated",
                    "Department with id " + selected.getDeptId() + " was updated successfully");
            updateDeptStage.close();
        });
    }

    private void deleteDepartment(Department selected) {
        Alert remove = new Alert(Alert.AlertType.CONFIRMATION);
        remove.setTitle("Remove Department");
        remove.setHeaderText(null);
        remove.setContentText("Are u sure to remove department with id " + selected.getDeptId() + " ?");
        ButtonType res = remove.showAndWait().orElse(ButtonType.CANCEL);
        if (res == ButtonType.OK) {
            Main.departments.remove(selected);
            String removeSql = "DELETE FROM departments Where department_id = ?";
            try (PreparedStatement stmt = Main.conn.prepareStatement(removeSql)) {
                stmt.setInt(1, selected.getDeptId());
                stmt.executeUpdate();
            } catch (SQLException e1) {
                Main.notValidAlert("Not Valid", e1.getMessage());
                return;
            }
            for (int i = 0; i < Main.employees.size(); i++) {
                if (Main.employees.get(i).getDepartmentId() == selected.getDeptId()) {
                    Main.employees.get(i).setDepartmentId(-1);
                }
            }
            Main.validAlert("Department Removed", "Department with id " + selected.getDeptId() + " was removed");
            loadCards();
        }
    }

    private void showEmployeesInDepartment(Department dept) {
        Stage stage = new Stage();
        stage.setTitle("Employees in " + dept.getDeptName());

        List<Employee> list = Main.employees.stream()
                .filter(emp -> emp.getDepartmentId() == dept.getDeptId())
                .collect(Collectors.toList());

        MyTableView<Employee> table = new MyTableView<>();
       
        TableColumn<Employee, Integer> id = table.createStyledColumn("Employee ID", "employeeId", Integer.class);
        TableColumn<Employee, String> name = table.createStyledColumn("Name", "name");
        TableColumn<Employee, String> email = table.createStyledColumn("Email", "email");
        TableColumn<Employee, String> phone = table.createStyledColumn("Phone Number", "phone");
        TableColumn<Employee, String> deptIdCol = new TableColumn<>("Department ID");
        deptIdCol.setCellValueFactory(e -> {
            int dId = e.getValue().getDepartmentId();
            return new SimpleStringProperty(dId == -1 ? "Unassigned" : String.valueOf(dId));
        });
        TableColumn<Employee, String> job = table.createStyledColumn("Job Title", "jobTitle");
        TableColumn<Employee, Double> salary = table.createStyledColumn("Salary", "salary", Double.class);
        TableColumn<Employee, Integer> age = table.createStyledColumn("Age", "age", Integer.class);
        TableColumn<Employee, String> joined = new TableColumn<>("Joined Date");
        joined.setCellValueFactory(e -> {
            Calendar c = e.getValue().getJoinedAt();
            return new SimpleStringProperty(
                c.get(Calendar.DAY_OF_MONTH) + "/" + (c.get(Calendar.MONTH) + 1) + "/" + c.get(Calendar.YEAR)
            );
        });

        table.getColumns().addAll(id, name, email, phone, deptIdCol, job, salary, age, joined);
        table.setItems(FXCollections.observableList(list));
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        
        Label header = new Label(dept.getDeptName() + " Department");
        header.setStyle("""
            -fx-font-size: 28px;
            -fx-font-weight: bold;
            -fx-text-fill: #1e3a8a;
            -fx-padding: 0 0 10 0;
            """);

        Label subHeader = new Label("Total Employees: " + list.size());
        subHeader.setStyle("""
            -fx-font-size: 16px;
            -fx-text-fill: #64748b;
            -fx-padding: 0 0 20 0;
            """);

        VBox headerBox = new VBox(5, header, subHeader);
        headerBox.setAlignment(Pos.CENTER);

        VBox root = new VBox(20, headerBox, table);
        root.setPadding(new Insets(25));
        root.setAlignment(Pos.TOP_CENTER);
        root.setStyle("""
            -fx-background-color: #f8fafc;
            """);

        Scene scene = new Scene(root, 1100, 600);
		scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());

        stage.setScene(scene);
        stage.show();
    }

    private String getDefaultCardStyle() {
        return """
            -fx-background-color: transparent;
            -fx-border-color: #bae6fd;
            -fx-border-width: 1;
            -fx-border-radius: 20;
            -fx-background-radius: 20;
            -fx-effect: dropshadow(gaussian, rgba(186,230,253,0.6), 20, 0.0, 0, 8);
            -fx-cursor: hand;
            """;
    }

    private String getHoverCardStyle() {
        return """
            -fx-background-color: transparent;
            -fx-border-color: #0ea5e9;
            -fx-border-width: 2;
            -fx-border-radius: 20;
            -fx-background-radius: 20;
            -fx-effect: dropshadow(gaussian, rgba(14,165,233,0.4), 35, 0.4, 0, 15);
            -fx-cursor: hand;
            """;
    }

    public VBox getAll() {
        return layout;
    }
}