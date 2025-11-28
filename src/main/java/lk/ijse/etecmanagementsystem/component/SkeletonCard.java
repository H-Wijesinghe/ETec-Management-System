package lk.ijse.etecmanagementsystem.component;

import javafx.animation.FadeTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

public class SkeletonCard extends VBox {

    private final FadeTransition fade;

    public SkeletonCard() {
        this.setSpacing(10);
        this.setAlignment(Pos.CENTER);
        this.setPadding(new Insets(15));
        this.setPrefWidth(180);
        this.setStyle("-fx-background-color: white; -fx-background-radius: 10; " +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.05), 5, 0, 0, 2);");

        // 1. Fake Image
        Rectangle imgPlaceholder = new Rectangle(100, 100, Color.web("#e4e4e4"));
        imgPlaceholder.setArcWidth(10);
        imgPlaceholder.setArcHeight(10);

        // 2. Fake Text Lines
        Rectangle textLine1 = new Rectangle(140, 15, Color.web("#e4e4e4"));
        textLine1.setArcWidth(10);
        textLine1.setArcHeight(10);

        Rectangle textLine2 = new Rectangle(80, 15, Color.web("#e4e4e4"));
        textLine2.setArcWidth(10);
        textLine2.setArcHeight(10);

        this.getChildren().addAll(imgPlaceholder, textLine1, textLine2);

        // 3. Setup Animation
        fade = new FadeTransition(Duration.seconds(0.8), this);
        fade.setFromValue(1.0);
        fade.setToValue(0.5);
        fade.setAutoReverse(true);
        fade.setCycleCount(FadeTransition.INDEFINITE);
        fade.play();

        this.sceneProperty().addListener((observable, oldScene, newScene) -> {
            if (newScene == null) {

                fade.stop();
            } else {

                fade.play();
            }
        });
    }


    public void stop() {
        if (fade != null) {
            fade.stop();
        }
    }
}