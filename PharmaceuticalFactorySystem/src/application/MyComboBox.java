package application;

import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;

public class MyComboBox<T> extends ComboBox<T> {

    public MyComboBox() {
        super();
        setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 6px;" +
            "-fx-border-radius: 6px;" +
            "-fx-border-color: #e5b8ff;" +
            "-fx-border-width: 2px;" +
            "-fx-padding: 8 12;" +
            "-fx-font-size: 14px;" +
            "-fx-font-family: 'Georgia';" +
            "-fx-text-fill: #4a4a4a;" +
            "-fx-min-height: 35px;" +
            "-fx-effect: dropshadow(gaussian, rgba(155, 89, 182, 0.15), 6, 0, 0, 3);"
        );
        
        setCellFactory(new Callback<ListView<T>, ListCell<T>>() {
            @Override
            public ListCell<T> call(ListView<T> param) {
                return new ListCell<T>() {
                    @Override
                    protected void updateItem(T item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setText(null);
                            setStyle("-fx-background-color: transparent;");
                        } else {
                            setText(item.toString());
                            setStyle(
                                "-fx-background-color: white;" +
                                "-fx-text-fill: #4a4a4a;" +
                                "-fx-font-family: 'Georgia';" +
                                "-fx-font-size: 14px;" +
                                "-fx-padding: 8 12;" +
                                "-fx-border-color: transparent;" +
                                "-fx-border-width: 0 0 1px 0;" +
                                "-fx-border-style: solid;" +
                                "-fx-border-color: #f0e6ff;"
                            );
                        }
                    }
                    
                    @Override
                    public void updateSelected(boolean selected) {
                        super.updateSelected(selected);
                        if (selected && !isEmpty()) {
                            setStyle(
                                "-fx-background-color: linear-gradient(to right, #E0BBE4, #f8f0ff);" +
                                "-fx-text-fill: #3E125F;" +
                                "-fx-font-family: 'Georgia';" +
                                "-fx-font-size: 14px;" +
                                "-fx-font-weight: bold;" +
                                "-fx-padding: 8 12;" +
                                "-fx-border-color: #D291BC;" +
                                "-fx-border-width: 0 0 1px 0;"
                            );
                        }
                    }
                };
            }
        });
        
        setButtonCell(new ListCell<T>() {
            @Override
            protected void updateItem(T item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.toString());
                }
                setStyle(
                    "-fx-background-color: transparent;" +
                    "-fx-text-fill: #4a4a4a;" +
                    "-fx-font-family: 'Georgia';" +
                    "-fx-font-size: 14px;" +
                    "-fx-padding: 0;"
                );
            }
        });
        
        setOnMouseEntered(e -> setStyle(
            "-fx-background-color: #faf8ff;" +
            "-fx-background-radius: 6px;" +
            "-fx-border-radius: 6px;" +
            "-fx-border-color: #D291BC;" +
            "-fx-border-width: 2px;" +
            "-fx-padding: 8 12;" +
            "-fx-font-size: 14px;" +
            "-fx-font-family: 'Georgia';" +
            "-fx-text-fill: #4a4a4a;" +
            "-fx-min-height: 35px;" +
            "-fx-effect: dropshadow(gaussian, rgba(155, 89, 182, 0.25), 8, 0, 0, 4);"
        ));
        
        setOnMouseExited(e -> setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 6px;" +
            "-fx-border-radius: 6px;" +
            "-fx-border-color: #e5b8ff;" +
            "-fx-border-width: 2px;" +
            "-fx-padding: 8 12;" +
            "-fx-font-size: 14px;" +
            "-fx-font-family: 'Georgia';" +
            "-fx-text-fill: #4a4a4a;" +
            "-fx-min-height: 35px;" +
            "-fx-effect: dropshadow(gaussian, rgba(155, 89, 182, 0.15), 6, 0, 0, 3);"
        ));
    }
    
    public MyComboBox(T... items) {
        this();
        getItems().addAll(items);
    }
    
    public MyComboBox(ObservableList<T> items) {
        this();
        setItems(items);
    }
    
    public MyComboBox(java.util.List<T> items) {
        this();
        getItems().addAll(items);
    }
    
    public void stylePopup() {
        setOnShowing(e -> {
        });
    }
}