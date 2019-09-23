package nl.bos.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.util.logging.Logger;

public class InsertTemplateField {
    private static final Logger LOGGER = Logger.getLogger(InsertTemplateField.class.getName());

    @FXML
    private Button btnExit;

    @FXML
    private void handleExit(ActionEvent actionEvent) {
        LOGGER.info(String.valueOf(actionEvent.getSource()));
        Stage stage = (Stage) btnExit.getScene().getWindow();
        stage.close();
    }
}
