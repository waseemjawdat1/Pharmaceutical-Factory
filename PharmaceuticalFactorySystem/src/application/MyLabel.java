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
            "-fx-text-fill: #2c3e50;" + 
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
            "-fx-text-fill: linear-gradient(to right, #667eea 0%, #764ba2 100%);" + 
            "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.15), 3, 0.5, 1, 1);"
        );
        setAlignment(Pos.CENTER);
    }
}