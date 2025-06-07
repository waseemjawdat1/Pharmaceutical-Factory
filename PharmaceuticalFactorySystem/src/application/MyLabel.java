package application;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class MyLabel extends Label {

    public MyLabel(String text) {
        super(text);
        setFont(Font.font("Times New Roman", FontWeight.BOLD, 16));
        setStyle(
            "-fx-font-size: 18px;" +
            "-fx-text-fill: #9b59b6;" +
            "-fx-font-weight: bold;" +
            "-fx-font-family: 'Georgia';"
        );
    }

    public MyLabel(String text, int a) {
        super(text);
        setFont(Font.font("Times New Roman", FontWeight.BOLD, 16));
        setStyle(
            "-fx-font-size: 32px;" +
            "-fx-font-family: 'Georgia';" +
            "-fx-font-weight: bold;" +
            "-fx-text-base-color: #9b59b6;" +
            "-fx-text-fill: #9b59b6;" +
            "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.25), 5, 0.3, 2, 2);"
        );
        setAlignment(Pos.CENTER);
    }
}
