package application;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ProductStage {
	static MyTableView<Product> productTable;
	private TableColumn<Product, Integer> id, quantity;
	private TableColumn<Product, String> name, category, warehouseId;
	private Button add, update, remove;
	private TableColumn<Product, Double> price;
	private VBox all;
	private Button makeRecipe;
	private HBox buttons;
	public ProductStage() {
		productTable = new MyTableView<>();
		id = productTable.createStyledColumn("Product ID", "productId", Integer.class);
		name = productTable.createStyledColumn("Name", "name");
		category = productTable.createStyledColumn("Category", "category");
		quantity = productTable.createStyledColumn("Quantity", "quantity", Integer.class);
		price = productTable.createStyledColumn("Price", "price", Double.class);

		warehouseId = productTable.createStyledColumn("Warehouse ID", "warehouseId");

		warehouseId.setCellValueFactory(cellData -> {
			Product p = cellData.getValue();
			int wid = p.getWarehouseId();
			if (wid == -1)
				return new SimpleStringProperty("null");
			return new SimpleStringProperty(wid + "");
		});

		productTable.getColumns().addAll(id, name, category, quantity, price, warehouseId);
		productTable.setItems(Main.products);
		productTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		productTable.setMinHeight(500);
		productTable.setMaxWidth(1000);

		Label searchL = new MyLabel("Search Name");
		TextField searchTF = new MyTextField();
		HBox searchBox = new HBox(10, searchL, searchTF);
		searchBox.setAlignment(Pos.CENTER);

		searchTF.setOnKeyTyped(e -> {
			String searchS = searchTF.getText();
			if (searchS == null || searchS.isEmpty()) {
				productTable.setItems(Main.products);
				return;
			}
			ObservableList<Product> temp = FXCollections.observableArrayList();
			for (int i = 0; i < Main.products.size(); i++) {
				if (Main.products.get(i).getName().toLowerCase().contains(searchS.toLowerCase())) {
					temp.add(Main.products.get(i));
				}
			}
			if (temp.size() > 0)
				productTable.setItems(temp);
			else
				productTable.setItems(Main.products);
		});
		
		add = new MyButton("➕ Add", 2);
		update = new MyButton("✎ Update", 2);
		remove = new MyButton("➖ Delete", 2);
		Button produce = new MyButton("Produce", 2);
		produce.setOnAction(e -> {
			Product selected = productTable.getSelectionModel().getSelectedItem();
			if (selected == null) {
				Main.notValidAlert("Nothing Selected", "Select product to produce.");
				return;
			}

			int productId = selected.getProductId();

			ObservableList<RecipeRequirement> recipe = FXCollections.observableArrayList();
			String sql = "SELECT material_id, quantity FROM product_requirements WHERE product_id = ?";
			try (PreparedStatement stmt = Main.conn.prepareStatement(sql)) {
				stmt.setInt(1, productId);
				ResultSet rs = stmt.executeQuery();
				while (rs.next()) {
					int mid = rs.getInt("material_id");
					int qty = rs.getInt("quantity");
					RawMaterial m = Main.getMaterialById(mid);
					if (m != null)
						recipe.add(new RecipeRequirement(m, qty));
				}
			} catch (SQLException ex) {
				Main.notValidAlert("Error", ex.getMessage());
				return;
			}

			if (recipe.isEmpty()) {
				Main.notValidAlert("No Recipe", "This product has no recipe defined.");
				return;
			}

			Label title = new MyLabel("Produce Product", 1);
			MyTableView<RecipeRequirement> table = new MyTableView<>();

			TableColumn<RecipeRequirement, String> nameCol = table.createStyledColumn("Material","requiredQuantity");
			nameCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().material.getName()));
			TableColumn<RecipeRequirement, Integer> qtyCol =table.createStyledColumn("Required per unit", "requiredQuantity" , Integer.class);
			
			table.getColumns().addAll(nameCol, qtyCol);
			table.setMaxHeight(500);
			table.setMaxWidth(700);
			table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
			table.setItems(recipe);
			Label qtyL = new MyLabel("Quantity to Produce");
			TextField qtyTF = new MyTextField();
			qtyTF.setMaxWidth(200);
			Button produceBtn = new MyButton("Produce", 2);
			produceBtn.setOnAction(ev -> {
			    String val = qtyTF.getText();
			    int toProduce;
			    try {
			        toProduce = Integer.parseInt(val);
			        if (toProduce <= 0) throw new Exception();
			    } catch (Exception ex) {
			        Main.notValidAlert("Invalid Quantity", "Please enter a valid positive number.");
			        return;
			    }

			    for (RecipeRequirement r : recipe) {
			        int totalNeeded = r.requiredQuantity * toProduce;
			        RawMaterial inv = Main.getMaterialById(r.material.getMaterialId());
			        if (inv.getUnit() < totalNeeded) {
			            Main.notValidAlert("Insufficient Material",
			                    "Not enough " + inv.getName() + ". Needed: " + totalNeeded + ", Available: " + inv.getUnit());
			            return;
			        }
			    }

			    for (RecipeRequirement r : recipe) {
			        int totalNeeded = r.requiredQuantity * toProduce;
			        RawMaterial inv = Main.getMaterialById(r.material.getMaterialId());
			        try {
			            inv.updateMaterial(inv.getName(), inv.getUnit() - totalNeeded, inv.getSupplierId(), inv.getPrice());
			        } catch (SQLException e1) {
			            Main.notValidAlert("Not Valid", e1.getMessage());
			        }
			    }

			    DatePicker expiryPicker = new DatePicker();
			    Button confirmBtn = new MyButton("Confirm", 2);
			    VBox expiryLayout = new VBox(10, new MyLabel("Enter Expiry Date:"), expiryPicker, confirmBtn);
			    expiryLayout.setAlignment(Pos.CENTER);
			    Stage expiryStage = new Stage();
			    expiryStage.setScene(new Scene(expiryLayout, 400, 300));
			    expiryStage.setTitle("Expiry Date");
			    expiryStage.show();

			    confirmBtn.setOnAction(ex -> {
			        if (expiryPicker.getValue() == null) {
			            Main.notValidAlert("Missing Date", "Please select an expiry date.");
			            return;
			        }

			        Date expiryDate = Date.valueOf(expiryPicker.getValue());
			        Date productionDate = new Date(Calendar.getInstance().getTimeInMillis());

			        if (expiryDate.before(productionDate)) {
			            Main.notValidAlert("Invalid Date", "Expiry date cannot be before today.");
			            return;
			        }

			        try {
			            selected.updateProduct(
			                selected.getName(),
			                selected.getCategory(),
			                selected.getQuantity() + toProduce,
			                selected.getWarehouseId(),
			                selected.getPrice()
			            );
			            productTable.refresh();

			            ProductionBatch batch = new ProductionBatch(
			                selected.getProductId(), toProduce, productionDate, expiryDate
			            );
			            Main.batches.add(batch);
			            Main.validAlert("Production Successful", "Produced " + toProduce + " units of " + selected.getName());
			            expiryStage.close();
			            ((Stage)((Button)ev.getSource()).getScene().getWindow()).close();
			        } catch (SQLException ex2) {
			            Main.notValidAlert("Error", ex2.getMessage());
			        }
			    });

			});

			VBox layout = new VBox(10, title, table, qtyL, qtyTF, produceBtn);
			layout.setAlignment(Pos.CENTER);
			Scene scene = new Scene(layout, 800, 750);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			Stage stage = new Stage();
			stage.setScene(scene);
			stage.setTitle("Produce Product");
			stage.show();
		});
	    buttons = new HBox(10, add, update, remove);
		buttons.setAlignment(Pos.CENTER);
		
		add.setOnAction(e -> {
			Label title = new MyLabel("Add Product", 1);
			Label nameL = new MyLabel("Name");
			Label categoryL = new MyLabel("Category");
			Label quantityL = new MyLabel("Quantity");
			Label warehouseL = new MyLabel("Warehouse ID");
			Label priceL = new MyLabel("Price");

			TextField nameTF = new MyTextField();
			TextField categoryTF = new MyTextField();
			TextField quantityTF = new MyTextField();
			TextField warehouseTF = new MyTextField();
			TextField priceTF = new MyTextField();

			GridPane g = new GridPane();
			g.addColumn(0, nameL, categoryL, quantityL, warehouseL,  priceL);
			g.addColumn(1, nameTF, categoryTF,  quantityTF, warehouseTF,  priceTF);
			g.setHgap(5);
			g.setVgap(5);
			g.setAlignment(Pos.CENTER);
			
			Button addBtn = new MyButton("Add", 2);
			Button clear = new MyButton("Clear", 2);
			HBox buttons1 = new HBox(10, addBtn, clear);
			buttons1.setAlignment(Pos.CENTER);

			VBox all = new VBox(10, title, g, buttons1);
			all.setAlignment(Pos.CENTER);
			Scene s = new Scene(all, 500, 500);
			Stage st = new Stage();
			st.setScene(s);
			st.setTitle("Add Product");
			st.show();

			clear.setOnAction(e1 -> {
				nameTF.clear();
				categoryTF.clear();
				quantityTF.clear();
				priceTF.clear();
				warehouseTF.clear();
			});

			addBtn.setOnAction(e1 -> {
				String nameS = nameTF.getText();
				String categoryS = categoryTF.getText();
				String quantityS = quantityTF.getText();
				String warehouseS = warehouseTF.getText();

				if (nameS == null || nameS.isEmpty()) {
					Main.notValidAlert("Not Valid", "Name is empty");
					return;
				}
				if (categoryS == null || categoryS.isEmpty()) {
					Main.notValidAlert("Not Valid", "Category is empty");
					return;
				}
				if (quantityS == null || quantityS.isEmpty()) {
					Main.notValidAlert("Not Valid", "Quantity is empty");
					return;
				}
				if (warehouseS == null || warehouseS.isEmpty()) {
					Main.notValidAlert("Not Valid", "Warehouse is empty");
					return;
				}	
				int quantityInt = -1;
				try {
					quantityInt = Integer.parseInt(quantityS);
					if (quantityInt < 0) throw new NumberFormatException();

				} catch (Exception e2) {
					Main.notValidAlert("Not Valid", "Quantity must be a positive number");
					return;
				}

				int warehouseIdInt = -1;
				if (!warehouseS.equalsIgnoreCase("null") ) {
					try {
						warehouseIdInt = Integer.parseInt(warehouseS);
					} catch (Exception e3) {
						Main.notValidAlert("Not Valid", "Warehouse ID must be a number");
						return;
					}
					boolean exist = false;
					for (int i = 0; i < Main.warehouses.size(); i++) {
						if (Main.warehouses.get(i).getWarehouseId() == warehouseIdInt) {
							exist = true;
							break;
						}
					}
					if (!exist) {
						Main.notValidAlert("Not Valid", "No warehouse with this id");
						return;
					}
				}
				String priceS = priceTF.getText();

				if (priceS == null || priceS.isEmpty()) {
					Main.notValidAlert("Not Valid", "Price is empty");
					return;
				}

				double priceD = -1;
				try {
					priceD = Double.parseDouble(priceS);
					if (priceD < 0) throw new NumberFormatException();
				} catch (Exception e2) {
					Main.notValidAlert("Not Valid", "Price must be a positive number");
					return;
				}

				Product p;
				try {
					p = new Product(nameS, categoryS, quantityInt, warehouseIdInt,priceD);
				} catch (SQLException e2) {
					Main.notValidAlert("Error", e2.getMessage());
					return;
				}
				Main.products.add(p);
				Main.validAlert("Product Added", "Product added successfully");
				st.close();
			});
		});

		update.setOnAction(e -> {
			Product selected = productTable.getSelectionModel().getSelectedItem();
			if (selected == null) {
				Main.notValidAlert("Nothing Selected", "Select product to update");
				return;
			}

			Label title = new MyLabel("Update Product", 1);
			Label nameL = new MyLabel("Name");
			Label categoryL = new MyLabel("Category");
			Label quantityL = new MyLabel("Quantity");
			Label warehouseL = new MyLabel("Warehouse ID");
			Label createdL = new MyLabel("Created At");
			Label priceL = new MyLabel("Price");

			TextField nameTF = new MyTextField();
			nameTF.setText(selected.getName());

			TextField categoryTF = new MyTextField();
			categoryTF.setText(selected.getCategory());


			TextField quantityTF = new MyTextField();
			quantityTF.setText(selected.getQuantity() + "");

			TextField warehouseTF = new MyTextField();
			if (selected.getWarehouseId() == -1)
				warehouseTF.setText("null");
			else
				warehouseTF.setText(selected.getWarehouseId() + "");

			
			TextField priceTF = new MyTextField();
			priceTF.setText(selected.getPrice() + "");

			GridPane g = new GridPane();
			g.addColumn(0, nameL, categoryL, quantityL, warehouseL, priceL);
			g.addColumn(1, nameTF, categoryTF,  quantityTF, warehouseTF,  priceTF);

			g.setHgap(5);
			g.setVgap(5);
			g.setAlignment(Pos.CENTER);

			Button updateBtn = new MyButton("Update", 2);
			Button clear = new MyButton("Clear", 2);
			HBox buttons1 = new HBox(10, updateBtn, clear);
			buttons1.setAlignment(Pos.CENTER);

			VBox all = new VBox(10, title, g, buttons1);
			all.setAlignment(Pos.CENTER);
			Scene s = new Scene(all, 500, 500);
			Stage st = new Stage();
			st.setScene(s);
			st.setTitle("Update Product");
			st.show();

			clear.setOnAction(e1 -> {
				nameTF.clear();
				categoryTF.clear();
				quantityTF.clear();
				warehouseTF.clear();
			});

			updateBtn.setOnAction(e1 -> {
				String nameS = nameTF.getText();
				String categoryS = categoryTF.getText();
				String quantityS = quantityTF.getText();
				String warehouseS = warehouseTF.getText();

				if (nameS == null || nameS.isEmpty()) {
					Main.notValidAlert("Not Valid", "Name is empty");
					return;
				}
				if (categoryS == null || categoryS.isEmpty()) {
					Main.notValidAlert("Not Valid", "Category is empty");
					return;
				}
				if (quantityS == null || quantityS.isEmpty()) {
					Main.notValidAlert("Not Valid", "Quantity is empty");
					return;
				}
				if (warehouseS == null || warehouseS.isEmpty()) {
					Main.notValidAlert("Not Valid", "Warehouse is empty");
					return;
				}	

				int quantityInt = -1;
				try {
					quantityInt = Integer.parseInt(quantityS);
					if (quantityInt < 0) throw new NumberFormatException();
				} catch (Exception e2) {
					Main.notValidAlert("Not Valid", "Quantity must be a positive number");
					return;
				}
				String priceS = priceTF.getText();

				if (priceS == null || priceS.isEmpty()) {
					Main.notValidAlert("Not Valid", "Price is empty");
					return;
				}

				double priceD = -1;
				try {
					priceD = Double.parseDouble(priceS);
					if (priceD < 0) throw new NumberFormatException();
				} catch (Exception e2) {
					Main.notValidAlert("Not Valid", "Price must be a positive number");
					return;
				}

				int warehouseIdInt = -1;
				if (!warehouseS.equalsIgnoreCase("null")) {
					try {
						warehouseIdInt = Integer.parseInt(warehouseS);
					} catch (Exception e3) {
						Main.notValidAlert("Not Valid", "Warehouse ID must be a number");
						return;
					}
					boolean exist = false;
					for (int i = 0; i < Main.warehouses.size(); i++) {
						if (Main.warehouses.get(i).getWarehouseId() == warehouseIdInt) {
							exist = true;
							break;
						}
					}
					if (!exist) {
						Main.notValidAlert("Not Valid", "No warehouse with this id");
						return;
					}
				}

				try {
					selected.updateProduct(nameS, categoryS,  quantityInt, warehouseIdInt,priceD);
					productTable.refresh();
				} catch (SQLException e2) {
					Main.notValidAlert("Error", e2.getMessage());
					return;
				}
				Main.validAlert("Product Updated", "Product updated successfully");
				st.close();
			});
		});
		makeRecipe = new MyButton ("Make Recipe",2);
		makeRecipe.setOnAction(e -> {
		    Product selected = productTable.getSelectionModel().getSelectedItem();
		    if (selected == null) {
		        Main.notValidAlert("Nothing Selected", "Please select a product to define its recipe.");
		        return;
		    }

		    int productId = selected.getProductId();

		    String checkSql = "SELECT 1 FROM product_requirements WHERE product_id = ? LIMIT 1";

		    try (PreparedStatement checkStmt = Main.conn.prepareStatement(checkSql)) {
		        checkStmt.setInt(1, productId);
		        ResultSet rs = checkStmt.executeQuery();

		        if (rs.next()) {
		            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
		            confirm.setTitle("Override Existing Recipe");
		            confirm.setHeaderText("This product already has a recipe.");
		            confirm.setContentText("Do you want to override the existing recipe?");
		            ButtonType result = confirm.showAndWait().orElse(ButtonType.CANCEL);

		            if (result != ButtonType.OK) {
		                return; 
		            }

		            String deleteSql = "DELETE FROM product_requirements WHERE product_id = ?";
		            try (PreparedStatement deleteStmt = Main.conn.prepareStatement(deleteSql)) {
		                deleteStmt.setInt(1, productId);
		                deleteStmt.executeUpdate();
		            }
		        }
		    } catch (SQLException ex) {
		        Main.notValidAlert("Database Error", ex.getMessage());
		        return;
		    }

		    new RawMaterialRequirementSelector(productId);
		});
		
		remove.setOnAction(e -> {
			Product selected = productTable.getSelectionModel().getSelectedItem();
			if (selected == null) {
				Main.notValidAlert("Nothing Selected", "Select product to remove");
				return;
			}
			Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
			alert.setTitle("Remove Product");
			alert.setHeaderText(null);
			alert.setContentText("Are you sure to remove product with id " + selected.getProductId() + " ?");
			ButtonType res = alert.showAndWait().orElse(ButtonType.CANCEL);
			if (res == ButtonType.OK) {
				Main.products.remove(selected);
				String sql = "UPDATE products SET active = FALSE WHERE product_id = ?";
				try (PreparedStatement stmt = Main.conn.prepareStatement(sql)) {
					stmt.setInt(1, selected.getProductId());
					stmt.executeUpdate();
				} catch (SQLException e1) {
					Main.notValidAlert("Error", e1.getMessage());
					return;
				}
				Main.validAlert("Product Removed", "Product removed successfully");
			}
		});
		HBox produceBox = new HBox (10 , produce , makeRecipe);
		produceBox.setAlignment(Pos.CENTER);
		all = new VBox(10,searchBox, buttons, productTable, produceBox);
		all.setAlignment(Pos.CENTER);

	}
	public VBox getAll() {
		return all;
	}
	public VBox getAllForPre() {
		buttons.getChildren().remove(0);
		return all;
	}
	public VBox getAllForProduction() {
		buttons.getChildren().remove(1);
		return all;
	}
	public void addMake() {
		all.getChildren().add(1, this.makeRecipe);
	}
	public void removeBox () {
		all.getChildren().remove(buttons);
		
	}
}
