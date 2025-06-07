package application;

import javafx.scene.control.TextField;

public class MyTextField extends TextField {

    public MyTextField() {
        super();
        setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 3px;" +
            "-fx-border-radius: 6px;" +
            "-fx-border-color: #e5b8ff;" +
            "-fx-border-width: 1px;" +
            "-fx-padding: 8 12;" +
            "-fx-pref-height: 10px;" +
            "-fx-font-size: 13px;" +
            "-fx-text-fill: #4a4a4a;" +
            "-fx-min-height: 30px;"
        );
    }

    public static MyTextField getUneditable() {
        MyTextField field = new MyTextField();
        field.setEditable(false);
        return field;
    }
}
