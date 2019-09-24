package nl.bos.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import nl.bos.utils.Controllers;

import java.util.logging.Logger;

public class InsertTemplateField {
    private static final Logger LOGGER = Logger.getLogger(InsertTemplateField.class.getName());

    @FXML
    private Button btnOk;
    @FXML
    private Button btnExit;
    @FXML
    private TextField txtName;
    @FXML
    private TextField txtLabel;
    @FXML
    private TextField txtDefaultValue;
    @FXML
    private CheckBox cbIsMandatory;
    @FXML
    private ComboBox cbType;
    @FXML
    private TextArea txaType;

    private int position;

    @FXML
    public void initialize() {
        cbType.valueProperty().addListener((observableValue, oldItem, newItem) -> {
            if (newItem != null) {
                if (newItem.equals("None")) {
                    txaType.setVisible(false);
                } else {
                    txaType.setVisible(true);
                }
            }
        });
    }

    @FXML
    private void handleExit(ActionEvent actionEvent) {
        ((Stage) btnExit.getScene().getWindow()).close();
    }

    @FXML
    private void handleOk(ActionEvent actionEvent) {
        QueryWithResult queryWithResult = (QueryWithResult) Controllers.get("QueryWithResult");
        queryWithResult.injectTemplateField(position, txtName.getText(), buildTemplateText());
        ((Stage) btnOk.getScene().getWindow()).close();

    }

    private String buildTemplateText() {
        StringBuilder builder = new StringBuilder();
        builder.append("{Options");
        builder.append(System.lineSeparator());
        builder.append(String.format("\t%s.Label = \"%s\"\n", txtName.getText(), txtLabel.getText()));
        builder.append(String.format("\t%s.Default = \"%s\"\n", txtName.getText(), txtDefaultValue.getText()));
        builder.append(String.format("\t%s.Mandatory = \"%s\"\n", txtName.getText(), cbIsMandatory.isSelected()));
        builder.append(String.format("\t%s.Type = \"%s\"\n", txtName.getText(), cbType.getValue()));
        String values = txaType.getText();
        if (cbType.getValue().equals("Fixed list")) {
            values = parseValues(txaType.getText());
        }
        builder.append(String.format("\t%s.Values = \"%s\"\n", txtName.getText(), values));
        builder.append("}");
        return String.valueOf(builder);
    }

    private String parseValues(String text) {
        String[] split = text.split("\n");
        String result = "";
        for (String item : split) {
            result = result + "'" + item + "', ";
        }
        return result.trim().substring(0, result.length() - 2);
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
