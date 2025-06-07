package application;

import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableCell;
import javafx.scene.control.cell.PropertyValueFactory;

public class MyTableView<T> extends TableView<T> {

    public MyTableView() {
        super();
        // Fixed: Removed invalid CSS properties that were causing the error
        setStyle(
            "-fx-background-color: #faf8ff;" +
            "-fx-border-color: #e5b8ff;" +
            "-fx-border-width: 2px;" +
            "-fx-border-radius: 8px;" +
            "-fx-background-radius: 8px;" +
            "-fx-effect: dropshadow(gaussian, rgba(155, 89, 182, 0.2), 8, 0, 0, 4);" +
            "-fx-table-cell-border-color: #e5b8ff;"
            // Removed: -fx-selection-bar and -fx-selection-bar-non-focused (these don't exist)
        );
        
        setRowFactory(tv -> {
           TableRow<T> row = new TableRow<>();
            row.setStyle(
                "-fx-background-color: transparent;" +
                "-fx-border-color: transparent;" +
                "-fx-font-family: 'Georgia';" +
                "-fx-font-size: 14px;" +
                "-fx-text-fill: #4a4a4a;"
            );
            
            row.setOnMouseEntered(e -> {
                if (!row.isSelected()) {
                    row.setStyle(
                        "-fx-background-color: #f8f0ff;" +
                        "-fx-border-color: transparent;" +
                        "-fx-font-family: 'Georgia';" +
                        "-fx-font-size: 14px;" +
                        "-fx-text-fill: #4a4a4a;"
                    );
                }
            });
            
            row.setOnMouseExited(e -> {
                if (!row.isSelected()) {
                    row.setStyle(
                        "-fx-background-color: transparent;" +
                        "-fx-border-color: transparent;" +
                        "-fx-font-family: 'Georgia';" +
                        "-fx-font-size: 14px;" +
                        "-fx-text-fill: #4a4a4a;"
                    );
                }
            });
            
            return row;
        });
    }
    
    public <S> TableColumn<T, S> createStyledColumn(String title, String propertyName, Class<S> dataType) {
        TableColumn<T, S> column = new TableColumn<>(title);
        column.setCellValueFactory(new PropertyValueFactory<>(propertyName));
        
        column.setCellFactory(col -> {
            TableCell<T, S> cell = new TableCell<T, S>() {
                @Override
                protected void updateItem(S item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                        setStyle("-fx-background-color: transparent;");
                    } else {
                        setText(item.toString());
                        setStyle(
                            "-fx-background-color: transparent;" +
                            "-fx-text-fill: #4a4a4a;" +
                            "-fx-font-family: 'Georgia';" +
                            "-fx-font-size: 14px;" +
                            "-fx-alignment: CENTER-LEFT;" +
                            "-fx-padding: 8 12;"
                        );
                    }
                }
            };
            return cell;
        });
        
        return column;
    }
    
    public TableColumn<T, String> createStyledColumn(String title, String propertyName) {
        return createStyledColumn(title, propertyName, String.class);
    }
}