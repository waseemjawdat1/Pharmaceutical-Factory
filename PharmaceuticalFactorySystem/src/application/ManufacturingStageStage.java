package application;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ManufacturingStageStage {

	static MyTableView<ManufacturingStage> stageTable;
	private TableColumn<ManufacturingStage, Integer> stageId;
	private TableColumn<ManufacturingStage, String> name;
	private Button add, update, remove;

	public ManufacturingStageStage() {
		stageTable = new MyTableView<>();
		stageId = stageTable.createStyledColumn("Stage Id", "stageId", Integer.class);
		name = stageTable.createStyledColumn("Stage Name", "name");
		stageTable.getColumns().addAll(stageId, name);
		stageTable.setItems(Main.manufacturingStages);
		stageTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		stageTable.setMinHeight(500);
		stageTable.setMaxWidth(600);
		TextField searchTF = new MyTextField();
		Label searchL = new MyLabel("Search Name");
		HBox searchBox = new HBox(10, searchL, searchTF);
		searchBox.setAlignment(Pos.CENTER);
		
		searchTF.setOnKeyTyped(e -> {
		    String search = searchTF.getText().trim();
		    if (search.isEmpty()) {
		        stageTable.setItems(Main.manufacturingStages);
		    } else {
		        ObservableList<ManufacturingStage> temp = FXCollections.observableArrayList();
		        for (int i = 0; i < Main.manufacturingStages.size(); i++) {
		            if (Main.manufacturingStages.get(i).getName().toLowerCase().contains(search.toLowerCase())) {
		                temp.add(Main.manufacturingStages.get(i));
		            }
		        }
		        if (temp.size() > 0)
		            stageTable.setItems(temp);
		        else
		            stageTable.setItems(Main.manufacturingStages);
		    }
		});

		
		
		add = new MyButton("➕ Add", 2);
		update = new MyButton("✎ Edit", 2);
		remove = new MyButton("➖ Remove", 2);

		HBox buttons = new HBox(10, add, update, remove);
		buttons.setAlignment(Pos.CENTER);

		VBox all = new VBox(10,searchBox, buttons, stageTable);
		all.setAlignment(Pos.CENTER);
		Scene scene = new Scene(all, 700, 650);
		scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		Stage stage = new Stage();
		stage.setTitle("Manufacturing Stages");
		stage.setScene(scene);
		stage.show();

		add.setOnAction(e -> {
			Label title = new MyLabel("Add Manufacturing Stage", 1);
			Label nameL = new MyLabel("Stage Name : ");
			TextField nameTF = new MyTextField();

			GridPane g = new GridPane();
			g.addColumn(0, nameL);
			g.addColumn(1, nameTF);
			g.setVgap(5);
			g.setHgap(5);
			g.setAlignment(Pos.CENTER);

			Button addBtn = new MyButton("Add", 2);
			Button clear = new MyButton("Clear", 2);
			HBox btns = new HBox(10, addBtn, clear);
			btns.setAlignment(Pos.CENTER);

			VBox v = new VBox(10, title, g, btns);
			v.setAlignment(Pos.CENTER);
			Scene s = new Scene(v, 400, 250);
			Stage st = new Stage();
			st.setScene(s);
			st.setTitle("Add Stage");
			st.show();

			clear.setOnAction(e1 -> nameTF.clear());

			addBtn.setOnAction(e1 -> {
				String nameS = nameTF.getText();
				if (nameS == null || nameS.trim().isEmpty()) {
					Main.notValidAlert("Not Valid Input", "Stage name is empty");
					return;
				}
				for (int i = 0; i < Main.manufacturingStages.size(); i++) {
					if (Main.manufacturingStages.get(i).getName().equalsIgnoreCase(nameS.trim())) {
						Main.notValidAlert("Duplicate", "There is already a stage with name: " + nameS);
						return;
					}
				}
				ManufacturingStage m;
				try {
					m = new ManufacturingStage(nameS.trim());
				} catch (SQLException e2) {
					Main.notValidAlert("Error", e2.getMessage());
					return;
				}
				Main.manufacturingStages.add(m);
				Main.validAlert("Stage Added", "Stage was added successfully");
				st.close();
			});
		});

		update.setOnAction(e -> {
			ManufacturingStage selected = stageTable.getSelectionModel().getSelectedItem();
			if (selected == null) {
				Main.notValidAlert("Nothing selected", "Select row of stage you want to update");
				return;
			}
			Label title = new MyLabel("Update Stage", 1);
			Label nameL = new MyLabel("Stage Name : ");
			TextField nameTF = new MyTextField();
			nameTF.setText(selected.getName());

			GridPane g = new GridPane();
			g.addColumn(0, nameL);
			g.addColumn(1, nameTF);
			g.setVgap(5);
			g.setHgap(5);
			g.setAlignment(Pos.CENTER);

			Button updateBtn = new MyButton("Update", 2);
			Button clear = new MyButton("Clear", 2);
			HBox btns = new HBox(10, updateBtn, clear);
			btns.setAlignment(Pos.CENTER);

			VBox v = new VBox(10, title, g, btns);
			v.setAlignment(Pos.CENTER);
			Scene s = new Scene(v, 400, 250);
			Stage st = new Stage();
			st.setScene(s);
			st.setTitle("Update Stage");
			st.show();

			clear.setOnAction(e1 -> nameTF.clear());

			updateBtn.setOnAction(e1 -> {
				String nameS = nameTF.getText();
				if (nameS == null || nameS.trim().isEmpty()) {
					Main.notValidAlert("Not Valid Input", "Stage name is empty");
					return;
				}
				for (int i = 0; i < Main.manufacturingStages.size(); i++) {
					if (Main.manufacturingStages.get(i).getName().equalsIgnoreCase(nameS.trim()) && Main.manufacturingStages.get(i).getStageId() != selected.getStageId()) {
						Main.notValidAlert("Duplicate", "There is already a stage with name: " + nameS);
						return;
					}
				}
				try {
					selected.updateStage(nameS.trim());
					stageTable.refresh();
					Main.validAlert("Stage Updated", "Stage updated successfully");
					st.close();
				} catch (SQLException e2) {
					Main.notValidAlert("Error", e2.getMessage());
				}
			});
		});

		remove.setOnAction(e -> {
			ManufacturingStage selected = stageTable.getSelectionModel().getSelectedItem();
			if (selected == null) {
				Main.notValidAlert("Nothing selected", "Select row of stage you want to remove");
				return;
			}
			Alert removeAlert = new Alert(AlertType.CONFIRMATION);
			removeAlert.setTitle("Remove Stage");
			removeAlert.setHeaderText(null);
			removeAlert.setContentText("Are you sure to remove stage with id " + selected.getStageId() + " ?");
			ButtonType res = removeAlert.showAndWait().orElse(ButtonType.CANCEL);
			if (res == ButtonType.OK) {
				Main.manufacturingStages.remove(selected);
				String sql = "DELETE FROM manufacturing_stages WHERE stage_id = ?";
				try (PreparedStatement stmt = Main.conn.prepareStatement(sql)) {
					stmt.setInt(1, selected.getStageId());
					stmt.executeUpdate();
					Main.validAlert("Removed", "Stage removed successfully");
				} catch (SQLException e1) {
					Main.notValidAlert("Error", e1.getMessage());
				}
			}
		});
	}
}
