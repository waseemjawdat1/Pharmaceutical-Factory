package application;

import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class MyButton extends Button {

    public MyButton(String text, int i) {
        super(text);

        if (i == 1) {
            setMinWidth(250);
            setMinHeight(120);
            setStyle1();
        } else if (i == 3) {
            setMinWidth(350);
            setMinHeight(350);
            setStyle3();
        } else {
            setMinWidth(100);
            setMinHeight(50);
            setDefaultStyle();
        }
    }

    public MyButton(String text) {
        super(text);
        setMinWidth(300);
        setMinHeight(100);
        setStyleBasic();
    }

    public MyButton(Image i) {
        super(null, new ImageView(i));
        setGraphicSize(50, 50);
        setMinSize(300, 100);
        setStyleImageButton();
    }

    public MyButton(Image i, int a) {
        super(null, new ImageView(i));
        setGraphicSize(50, 50);
        setMinSize(70, 50);
        setStyleImageButton();
    }

    public MyButton(Image i, String s) {
        super(null, new ImageView(i));
        setGraphicSize(30, 30);
        setMinSize(50, 30);
        setStyleImageButton();
    }

    // Helpers

    private void setGraphicSize(int w, int h) {
        ImageView icon = (ImageView) getGraphic();
        icon.setFitWidth(w);
        icon.setFitHeight(h);
    }

    private void setStyle1() {
        setStyle(
            "-fx-font-size: 30px;" +
            "-fx-font-family: 'Georgia';" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: #3E125F;" +
            "-fx-background-color: linear-gradient(to bottom, #A66CFF, #E4C1F9);" +
            "-fx-background-radius: 12px;" +
            "-fx-effect: dropshadow(gaussian, rgba(62, 18, 95, 0.3), 12, 0, 0, 6);" +
            "-fx-cursor: hand;"
        );

        setOnMouseEntered(e -> setStyle(
            "-fx-font-size: 30px;" +
            "-fx-font-family: 'Georgia';" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: #3E125F;" +
            "-fx-background-color: linear-gradient(to bottom, #933FFF, #D9A7EB);" +
            "-fx-background-radius: 12px;" +
            "-fx-effect: dropshadow(gaussian, rgba(62, 18, 95, 0.5), 14, 0, 0, 8);" +
            "-fx-cursor: hand;"
        ));

        setOnMouseExited(e -> setStyle1());
    }

    private void setStyle3() {
        setStyle(
            "-fx-font-size: 24px;" +
            "-fx-font-family: 'Georgia';" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: #3E125F;" +
            "-fx-background-color: linear-gradient(to bottom, #E0BBE4, #D291BC);" +
            "-fx-background-radius: 12px;" +
            "-fx-border-radius: 12px;" +
            "-fx-effect: dropshadow(gaussian, rgba(62, 18, 95, 0.25), 16, 0, 0, 6);" +
            "-fx-cursor: hand;"
        );

        setOnMouseEntered(e -> setStyle(
            "-fx-font-size: 24px;" +
            "-fx-font-family: 'Georgia';" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: #3E125F;" +
            "-fx-background-color: linear-gradient(to bottom, #D291BC, #E0BBE4);" +
            "-fx-background-radius: 12px;" +
            "-fx-border-radius: 12px;" +
            "-fx-effect: dropshadow(gaussian, rgba(62, 18, 95, 0.4), 18, 0, 0, 8);" +
            "-fx-cursor: hand;"
        ));

        setOnMouseExited(e -> setStyle3());
    }

    private void setDefaultStyle() {
        setStyle(
            "-fx-font-size: 20px;" +
            "-fx-font-family: 'Georgia';" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: #3E125F;" +
            "-fx-background-color: linear-gradient(to bottom, #A66CFF, #E4C1F9);" +
            "-fx-background-radius: 12px;" +
            "-fx-effect: dropshadow(gaussian, rgba(62, 18, 95, 0.3), 12, 0, 0, 6);" +
            "-fx-cursor: hand;"
        );

        setOnMouseEntered(e -> setStyle(
            "-fx-font-size: 20px;" +
            "-fx-font-family: 'Georgia';" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: #3E125F;" +
            "-fx-background-color: linear-gradient(to bottom, #933FFF, #D9A7EB);" +
            "-fx-background-radius: 12px;" +
            "-fx-effect: dropshadow(gaussian, rgba(62, 18, 95, 0.5), 14, 0, 0, 8);" +
            "-fx-cursor: hand;"
        ));

        setOnMouseExited(e -> setDefaultStyle());
    }

    private void setStyleBasic() {
        setStyle(
            "-fx-font-size: 20px;" +
            "-fx-font-family: 'Times New Roman';" +
            "-fx-font-weight: bold;" +
            "-fx-text-base-color: #5A00B5;" +
            "-fx-text-fill: #5A00B5;" +
            "-fx-background-color: linear-gradient(to bottom, #B97DFF, #FFB28C);" +
            "-fx-background-radius: 5px;" +
            "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.2), 10, 0, 0, 5);" +
            "-fx-cursor: hand;"
        );

        setOnMouseEntered(e -> setStyle(
            "-fx-font-size: 20px;" +
            "-fx-font-family: 'Times New Roman';" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: #5A00B5;" +
            "-fx-background-color: linear-gradient(to bottom, #9A64FF, #FF9F6A);" +
            "-fx-background-radius: 5px;" +
            "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.3), 12, 0, 0, 6);" +
            "-fx-cursor: hand;"
        ));

        setOnMouseExited(e -> setStyleBasic());
    }

    private void setStyleImageButton() {
        setStyle(
            "-fx-background-color: linear-gradient(to bottom, #B97DFF, #FFB28C);" +
            "-fx-background-radius: 5px;" +
            "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.2), 10, 0, 0, 5);" +
            "-fx-cursor: hand;"
        );

        setOnMouseEntered(e -> setStyle(
            "-fx-background-color: linear-gradient(to bottom, #9A64FF, #FF9F6A);" +
            "-fx-font-size: 14px;" +
            "-fx-background-radius: 5px;" +
            "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.3), 12, 0, 0, 6);" +
            "-fx-cursor: hand;"
        ));

        setOnMouseExited(e -> setStyleImageButton());
    }
}
