package lk.ijse.etecmanagementsystem.component;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import lk.ijse.etecmanagementsystem.dto.ProductDTO;

public class ProductCard extends VBox {

    public ProductCard(ProductDTO p) {
        // 1. Setup Main Card Layout
        this.setSpacing(5);
        this.setAlignment(Pos.CENTER);
        this.setPadding(new Insets(10));
        this.setPrefSize(180, 220);
        this.setStyle("-fx-background-color: white; -fx-background-radius: 10; " +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 5); " +
                "-fx-cursor: hand;");

        // 2. Image Area
        StackPane imageContainer = new StackPane();
        imageContainer.setPrefSize(100, 100);

        ImageView imageView = new ImageView();
        imageView.setFitWidth(100);
        imageView.setFitHeight(100);
        imageView.setPreserveRatio(true);

        try {
            String imagePath = "/lk/ijse/etecmanagementsystem/images/" + p.getImagePath();
            if (getClass().getResource(imagePath) != null) {
                imageView.setImage(new Image(getClass().getResource(imagePath).toExternalForm()));
            }
        } catch (Exception e) {
            // e.printStackTrace(); // Optional: Log missing images
        }
        imageContainer.getChildren().add(imageView);

        // 3. Name Label
        Label lblName = new Label(p.getName());
        lblName.setWrapText(true);
        lblName.setTextAlignment(TextAlignment.CENTER);
        lblName.setAlignment(Pos.CENTER);
        lblName.setPrefHeight(45); // Fixed height to keep alignment
        lblName.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #1e293b;");

        // 4. Price Label
        Label lblPrice = new Label("LKR " + String.format("%,.2f", p.getPrice()));
        lblPrice.setStyle("-fx-text-fill: #3b82f6; -fx-font-weight: bold; -fx-font-size: 13px;");

        // 5. Stock Label
        Label lblStock = new Label("In Stock: " + p.getQty()); // Assuming ProductDTO has getQty()
        lblStock.setStyle("-fx-text-fill: #10b981; -fx-font-weight: bold; -fx-font-size: 12px;");

        // 6. Add all to VBox
        this.getChildren().addAll(imageContainer, lblName, lblPrice, lblStock);
    }
}