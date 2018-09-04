package nl.bos.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import lombok.extern.java.Log;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

@Log
public class BodyPane implements Initializable {
    @FXML
    private VBox vboxBody;

    public void initialize(URL location, ResourceBundle resources) {
        try {
            BorderPane inputPane = FXMLLoader.load(getClass().getResource("/nl/bos/views/InputPane.fxml"));
            vboxBody.getChildren().add(inputPane);
        } catch (IOException e) {
            log.info(e.getMessage());
        }
    }
}