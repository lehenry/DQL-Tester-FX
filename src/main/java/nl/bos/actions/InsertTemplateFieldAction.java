package nl.bos.actions;

import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import nl.bos.controllers.InsertTemplateField;
import nl.bos.utils.Resources;

import java.util.logging.Logger;

import static nl.bos.Constants.ROOT_SCENE_CSS;

public class InsertTemplateFieldAction {
    private static final Logger LOGGER = Logger.getLogger(InsertTemplateFieldAction.class.getName());

    public InsertTemplateFieldAction(int position) {
        LOGGER.info("InsertTemplateFieldAction(" + position + ")");
        Stage insertTemplateFieldStage = new Stage();
        insertTemplateFieldStage.setTitle("Insert template fields");
        Resources resources = new Resources();
        VBox loginPane = (VBox) resources.loadFXML("/nl/bos/views/InsertTemplateField.fxml");
        insertTemplateFieldStage.setScene(new Scene(loginPane));
        insertTemplateFieldStage.getScene().getStylesheets()
                .addAll(ROOT_SCENE_CSS);
        InsertTemplateField controller = resources.getFxmlLoader().getController();
        controller.setPosition(position);
        insertTemplateFieldStage.showAndWait();
    }
}
