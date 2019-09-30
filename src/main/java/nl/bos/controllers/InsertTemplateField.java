package nl.bos.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import nl.bos.beans.Option;
import nl.bos.utils.Controllers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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

        Option option = new Option();
        option.setName(txtName.getText());
        option.setLocation(position);
        option.setLabel(txtLabel.getText());
        option.setDefaultValue(txtDefaultValue.getText());
        option.setMandatory(cbIsMandatory.isSelected());
        option.setType(String.valueOf(cbType.getValue()));
        option.setValues(createList(txaType.getText()));
        queryWithResult.injectTemplateField(option);

        ((Stage) btnOk.getScene().getWindow()).close();
    }

    private List<String> createList(String values) {
        List<String> result = new ArrayList<>();
        Collections.addAll(result, values.split("\n"));
        return result;
    }


    public void setPosition(int position) {
        this.position = position;
    }
}
