package com.project.encryptionsoftware;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Window;

import java.io.*;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class EncryptionController extends EncryptorDecryptor implements Initializable {

    @FXML
    private Button encryptButton;

    @FXML
    private Button decryptButton;

    @FXML
    private VBox FolderListBox;

    @FXML
    private void handleDecryption(ActionEvent event) {

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Add New File to decrypt");
        File selectedFile = fileChooser.showOpenDialog(((Node) event.getSource()).getScene().getWindow());

        // Adds a password field to the dialog box
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Decrypt File");
        dialog.setHeaderText("Enter your password");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");

        if (selectedFile == null) {
            return;
        }

        // Checks if the file is encrypted before trying to decrypt it
        if((!selectedFile.getAbsolutePath().endsWith(".enc"))){
            Alert alert = new Alert(Alert.AlertType.ERROR, "File is not encrypted!");
            alert.show();
            return;

        }

        //
        GridPane grid = new GridPane();
        grid.add(passwordField, 0, 0);

        dialog.getDialogPane().setContent(grid);

        Window window = FolderListBox.getScene().getWindow();
        dialog.initOwner(window);


        // Gets the password from the password field
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {

            String password = passwordField.getText();//get the password

            try {

                decryptFile(selectedFile, password);
                selectedFile.delete();
            } catch (Exception e) {

                e.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR, "Decryption error!");
                alert.show();
            }

        }

    }

    ;

    @FXML
    public void encryptButtonAction(ActionEvent event) {

        // Opens a file chooser dialog box to choose the file to encrypt
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Add New File to encrypt");
        File selectedFile = fileChooser.showOpenDialog(((Node) event.getSource()).getScene().getWindow());

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Set Password!");
        dialog.setHeaderText("Set Password for the file");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");

        GridPane grid = new GridPane();
        grid.add(passwordField, 0, 0);

        dialog.getDialogPane().setContent(grid);


        // Checks if the user selected a file or not
        if (selectedFile == null) {
            return;
        }
        else {
            dialog.showAndWait();
        }

        // Gets the password from the password field

        String password = passwordField.getText();

        try {
            encryptFile(selectedFile, password);

            selectedFile.delete();
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Encryption error!");
            alert.show();
        }

    }

    // Initializes the controller class
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        encryptButton.setOnAction(this::encryptButtonAction);
        decryptButton.setOnAction(this::handleDecryption);
    }
}