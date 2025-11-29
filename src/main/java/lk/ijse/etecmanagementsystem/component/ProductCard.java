package lk.ijse.etecmanagementsystem.component;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import lk.ijse.etecmanagementsystem.dto.ProductDTO;

import java.util.Objects;


public class ProductCard extends StackPane {

    public ProductCard(ProductDTO p) {
        this.setPrefSize(215, 349);
        this.setMaxSize(215, 349);
        this.setStyle("-fx-cursor: hand;");

        VBox cardContent = new VBox();
        cardContent.setAlignment(Pos.TOP_CENTER);
        cardContent.setSpacing(2);
        cardContent.setStyle("-fx-padding: 5 5 5 5 ; -fx-background-color: white; -fx-border-color: #e2e8f0;"); // Top, Right, Bottom, Left
        cardContent.setPrefSize(USE_COMPUTED_SIZE, USE_COMPUTED_SIZE);


        Label lblTitle = new Label(p.getName());
        lblTitle.setAlignment(Pos.CENTER);
        lblTitle.setPrefWidth(210);
        lblTitle.setPrefHeight(41);
        lblTitle.setTextAlignment(TextAlignment.CENTER);
        lblTitle.setStyle("-fx-font-weight: bold; -fx-text-fill: black; -fx-font-size: 14px;");
        lblTitle.setWrapText(true);

        ImageView imgProduct = new ImageView();
        imgProduct.setFitWidth(200);
        imgProduct.setFitHeight(200);
        imgProduct.setPreserveRatio(false);

        try {
            String imagePath = "/lk/ijse/etecmanagementsystem/images/" + p.getImagePath();
            if (getClass().getResource(imagePath) != null) {
                imgProduct.setImage(new Image(getClass().getResource(imagePath).toExternalForm()));
            }
        } catch (Exception e) {
            String imagePath = "/lk/ijse/etecmanagementsystem/images/" + "placeholder.png";
            if (getClass().getResource(imagePath) != null) {
                imgProduct.setImage(new Image(Objects.requireNonNull(getClass().getResource(imagePath)).toExternalForm()));
            }
        }


        String descriptionText = p.getName() + " " + p.getDescription();
        Label lblName = new Label(descriptionText);
        lblName.setAlignment(Pos.CENTER);
        lblName.setPrefWidth(210);
        lblName.setPrefHeight(51);
        lblName.setTextAlignment(TextAlignment.CENTER);
        lblName.setStyle("-fx-font-size: 11px; -fx-text-fill: #555555;");
        lblName.setWrapText(true);



        Label lblCode = new Label("STOCK ID : " + p.getId());
        lblCode.setStyle("-fx-font-weight: bold; -fx-font-size: 11px; -fx-text-fill: black;");


        Label lblPrice = new Label("Rs. " + String.format("%,.2f", p.getPrice()));
        lblPrice.setStyle("-fx-text-fill: #dc2626; -fx-font-weight: bold; -fx-font-size: 14px;");


        cardContent.getChildren().addAll(lblTitle, imgProduct, lblName, lblCode, lblPrice);

        StackPane overlayContainer = new StackPane();
        overlayContainer.setAlignment(Pos.TOP_LEFT);
        overlayContainer.setPickOnBounds(false);


        Label lblStock = getLblStock(p);

        overlayContainer.getChildren().add(lblStock);

        this.getChildren().addAll(cardContent, overlayContainer);
    }

    private static Label getLblStock(ProductDTO p) {
        Label lblStock = new Label("IN STOCK : " + p.getQty());
        lblStock.setPrefWidth(127);
        lblStock.setPrefHeight(24);
        lblStock.setAlignment(Pos.CENTER);
        lblStock.setStyle("-fx-background-color: #4ade80; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 12px; -fx-padding: 3 20 3 20;");

        if (p.getQty() < 1) {
            lblStock.setText("OUT OF STOCK");
            lblStock.setStyle("-fx-background-color: #f87171; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 12px; -fx-padding: 3 20 3 20;");
        }
        lblStock.setTranslateX(90);
        lblStock.setTranslateY(220);
        return lblStock;
    }
}