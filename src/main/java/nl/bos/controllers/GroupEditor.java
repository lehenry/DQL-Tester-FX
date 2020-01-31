package nl.bos.controllers;

import static nl.bos.Constants.ROOT_SCENE_CSS;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import nl.bos.beans.GroupObject;
import nl.bos.services.GroupService;
import nl.bos.utils.DialogCallbackInterface;

public class GroupEditor implements DialogCallbackInterface {
	private static final Logger LOGGER = Logger.getLogger(GroupEditor.class.getName());

	@FXML
	private ListView<String> groupList;
	@FXML
	private ListView<String> groupsNames;
	@FXML
	private ListView<String> usersNames;
	@FXML
	private Label groupListCount;
	@FXML
	private TextField groupFilter;
	@FXML
	private TextField r_object_id;
	@FXML
	private TextField r_modify_date;
	@FXML
	private TextField group_name;
	@FXML
	private TextField group_display_name;
	@FXML
	private TextField group_admin;
	@FXML
	private TextField description;

	private GroupService groupService;
	@FXML
	private Button btnUpdate;
	@FXML
	private Button btnExport;

	@FXML
	private void initialize() {
		groupService = new GroupService();

		refreshGroupList();
		groupList.getSelectionModel().selectedItemProperty().addListener((observableValue, oldValue, newValue) -> handleGroupSelection(newValue));

		addChangeListeners();
	}

	private void handleGroupSelection(String selectedGroup) {
		LOGGER.info(String.format("Selected user %s", selectedGroup));
		GroupObject groupObject = groupService.getGroupByName(selectedGroup);
		assert groupObject != null;
		updateUIFields(groupObject);
		btnExport.setDisable(false);
	}

	private void refreshGroupList() {
		List<String> filteredGroups = groupService.getFilteredGrouplist(groupFilter.getText());
		ObservableList<String> observableUserList = FXCollections.observableList(filteredGroups);
		groupList.setItems(observableUserList);
		groupListCount.setText("" + filteredGroups.size());
	}

	public void updateGroupFilter() {
		refreshGroupList();
	}

	private void updateUIFields(GroupObject groupObject) {

		group_name.setText(groupObject.getGroup_name());
		group_display_name.setText(groupObject.getGroup_display_name());
		group_admin.setText(groupObject.getGroup_admin());
		description.setText(groupObject.getDescription());
		usersNames.setItems(FXCollections.observableList(groupObject.getUsers_names()));
		groupsNames.setItems(FXCollections.observableList(groupObject.getGroups_names()));
		r_object_id.setText(groupObject.getR_object_id());
		r_modify_date.setText(groupObject.getR_modify_date());
		btnUpdate.setDisable(true);
	}

	public void closeWindow(ActionEvent actionEvent) {
		LOGGER.info(String.valueOf(actionEvent.getSource()));
		Stage stage = (Stage) groupList.getScene().getWindow();
		stage.close();
	}


	@Override
	public void returnValue(String returnValue, String callbackMessage) {
		switch (callbackMessage) {
			case "groupAdmin":
				group_admin.setText(returnValue);

				break;
//			case "userDelegation":
//				user_delegation.setText(returnValue);
//
//				break;
//			case "userAdministrator":
//				user_administrator.setText(returnValue);
//				break;
		}
	}
	
	public void emptyGroupAdminField() {
		group_admin.clear();
	}

	public void browseGroupAdmin() {
		openSelectGroupDialog("groupAdmin", false);
	}

//	public void browseUserDelegation() {
//		openSelectUserDialog("Select Delegation User", "userDelegation");
//	}
//
//	public void browseUserAdministrator() {
//		openSelectUserDialog("Select User Administrator", "userAdministrator");
//	}

	private void openSelectGroupDialog(String callbackMessage, boolean allowAllGroupsOption) {
		Stage selectGroupStage = new Stage();
		selectGroupStage.setTitle("Select a Group");
		selectGroupStage.setResizable(false);
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/nl/bos/views/dialogs/SelectGroupDialog.fxml"));
		try {
			AnchorPane selectGroupPane = fxmlLoader.load();
			selectGroupStage.setScene(new Scene(selectGroupPane));
			selectGroupStage.getScene().getStylesheets()
					.addAll(ROOT_SCENE_CSS);
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
		}
		SelectGroupDialog controller = fxmlLoader.getController();
		controller.setAllowAllGroupsOption(allowAllGroupsOption);
		controller.setCallbackTarget(this, callbackMessage);
		selectGroupStage.showAndWait();
	}

	public void exportGroup() {
		groupService.exportGroup(group_name.getText());
	}

	public void updateGroup() {
		GroupObject updatedGroupObject = groupService.getGroupByName(group_name.getText());

		updatedGroupObject.setDescription(description.getText());
		updatedGroupObject.setGroup_admin(group_admin.getText());
		updatedGroupObject.setGroup_display_name(group_display_name.getText());
		updatedGroupObject.setGroups_names(groupsNames.getItems());
		updatedGroupObject.setUsers_names(usersNames.getItems());
		groupService.updateGroup(updatedGroupObject);
	}

	private void addChangeListeners() {
		ChangeListener<String> btnUpdateListener = (observableValue, oldValue, newValue) -> btnUpdate.setDisable(false);
		group_display_name.textProperty().addListener(btnUpdateListener);
		group_admin.textProperty().addListener(btnUpdateListener);
		description.textProperty().addListener(btnUpdateListener);
	}
}
