package com.project.encryptionsoftware;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class EncryptorApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(EncryptorApplication.class.getResource("EncryptorDecryptorScene.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        stage.setTitle("SecureFileCrypt");
        stage.setScene(scene);

        stage.setMinHeight(550);
        stage.setMinWidth(600);

        stage.maximizedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                stage.setMaximized(false);
            }
        });
        stage.show();
    }


}