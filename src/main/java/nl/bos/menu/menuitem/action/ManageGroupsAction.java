package nl.bos.menu.menuitem.action;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import nl.bos.Repository;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import static nl.bos.Constants.ROOT_SCENE_CSS;

public class ManageGroupsAction {
	private static final Logger LOGGER = Logger.getLogger(ManageGroupsAction.class.getName());

	public ManageGroupsAction() {
		Stage groupEditorStage = new Stage();
		Repository repository = Repository.getInstance();
		groupEditorStage.setTitle(String.format("Group Editor - %s", repository.getRepositoryName()));
		groupEditorStage.setResizable(false);
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/nl/bos/views/GroupEditor.fxml"));

		try {
			AnchorPane groupEditor = fxmlLoader.load();
			groupEditorStage.setScene(new Scene(groupEditor));
			groupEditorStage.getScene().getStylesheets()
                    .addAll(ROOT_SCENE_CSS);

		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
		}

		groupEditorStage.showAndWait();
	}
}
