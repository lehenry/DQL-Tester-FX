package nl.bos.controllers;

import com.documentum.fc.client.IDfCollection;
import com.documentum.fc.common.DfException;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import lombok.Getter;
import lombok.extern.java.Log;
import nl.bos.Main;
import nl.bos.Repository;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Optional;

@Log
public class InputPane implements EventHandler<WindowEvent> {
    @Getter
    private static Stage loginStage;
    @FXML
    private Label lblStatus;
    @FXML
    private Label lblUsernameOS;
    @FXML
    private Label lblUsernameDC;
    @FXML
    private Label lblDomainOS;
    @FXML
    private Label lblPrivileges;
    @FXML
    private Label lblServerVersion;
    @FXML
    @Getter
    private Button btnFlushCache;
    @FXML
    @Getter
    private Button btnReadQuery;

    private FXMLLoader fxmlLoader;

    @FXML
    private void handleConnect(ActionEvent actionEvent) throws IOException {
        log.info(String.valueOf(actionEvent.getSource()));

        loginStage = new Stage();
        loginStage.initModality(Modality.APPLICATION_MODAL);
        loginStage.setTitle("Documentum Login");
        fxmlLoader = new FXMLLoader(getClass().getResource("/nl/bos/views/LoginPane.fxml"));
        VBox loginPane = fxmlLoader.load();
        loginStage.setScene(new Scene(loginPane));
        loginStage.setOnCloseRequest(this);
        loginStage.showAndWait();
    }

    @FXML
    private void handleExit(ActionEvent actionEvent) throws DfException {
        log.info(String.valueOf(actionEvent.getSource()));

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Quit the application...");
        alert.setHeaderText(null);
        alert.setContentText("Are you sure?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            Repository.disconnect();
            System.exit(0);
        } else {
            alert.close();
        }
    }

    public void handle(WindowEvent event) {
        if (Repository.isConnectionValid()) {
            LoginPane loginPaneController = fxmlLoader.getController();

            lblStatus.setText(String.valueOf(loginPaneController.getChbRepository().getValue()));
            lblUsernameOS.setText("dummyUserNameOS");
            lblUsernameDC.setText(loginPaneController.getTxtUsername().getText());
            lblDomainOS.setText(loginPaneController.getHostName());
            lblPrivileges.setText("dummyPrivileges");
            lblServerVersion.setText("dummyServerVersion");

            btnReadQuery.setDisable(false);
            btnFlushCache.setDisable(false);
        }
    }

    @FXML
    private void handleReadQuery(ActionEvent actionEvent) throws DfException {
        log.info(String.valueOf(actionEvent.getSource()));

        BodyPane bodyPaneController = Main.getBodyPaneLoader().getController();
        String statement = bodyPaneController.getTaStatement().getText();
        JSONObject jsonObject = bodyPaneController.getJsonObject();

        Repository repository = Repository.getRepositoryCon();
        IDfCollection result = repository.query(statement);
        bodyPaneController.updateResultTable(result);
        result.close();
        
        ChoiceBox cmbHistory = bodyPaneController.getCmbHistory();
        ObservableList items = cmbHistory.getItems();
        if (statementNotExists(items, statement)) {
            items.add(0, statement);
            cmbHistory.setValue(statement);
            JSONArray queries = (JSONArray) jsonObject.get("queries");
            queries.add(0, statement);
            try (FileWriter file = new FileWriter("history.json")) {
                file.write(jsonObject.toJSONString());
                file.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
            cmbHistory.setItems(items);
        }
    }

    private boolean statementNotExists(ObservableList items, String statement) {
        for (int i = 0; i < items.size(); i++) {
            String historyStatement = (String) items.get(i);
            if (historyStatement.equalsIgnoreCase(statement))
                return false;
        }
        return true;
    }

    @FXML
    private void handleClearQuery(ActionEvent actionEvent) {
        BodyPane bodyPaneController = Main.getBodyPaneLoader().getController();
        bodyPaneController.getTaStatement().clear();
    }
}