package lk.ijse.etecmanagementsystem.service;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lk.ijse.etecmanagementsystem.App;

import java.io.IOException;

public class StageManager {

     private  static Scene PrimaryScene;

    public static void setupPrimaryStageScene(String fxmlFileName) throws Exception {
        Stage primaryStage = App.getPrimaryStage();
        PrimaryScene = new Scene(loadFXML(fxmlFileName), 1280, 720);
        primaryStage.setScene(PrimaryScene);
        primaryStage.setResizable(true);
        primaryStage.setMinHeight(656);
        primaryStage.setMinWidth(1016);
        primaryStage.show();
    }
    public static void setRoot(String fxmlFileName) throws IOException {

        PrimaryScene.setRoot(loadFXML(fxmlFileName));
    }
    private static Parent loadFXML(String fxmlFileName) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxmlFileName +".fxml"));
        return fxmlLoader.load();
    }
}
