package nl.bos.services;

import java.awt.Desktop;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.documentum.fc.client.IDfGroup;
import com.documentum.fc.client.IDfLocalTransaction;
import com.documentum.fc.common.DfException;

import nl.bos.Repository;
import nl.bos.beans.GroupObject;
import nl.bos.utils.Resources;
import nl.bos.utils.UIUtils;

public class GroupService {
	private static final Logger LOGGER = Logger.getLogger(GroupService.class.getName());
	private final Repository repository = Repository.getInstance();

	public void exportGroup(String groupName) {
		try {
			IDfGroup groupObject = repository.getGroupByName(groupName);

			String[] attributesToExport = new String[] { "group_name", "group_display_name", "group_admin",
					"description", "groups_names", "users_names" };

			StringBuilder exportDataBuilder = new StringBuilder();
			exportDataBuilder
					.append(String.format("# *------------ dm_user - %s ------------*%n", groupObject.getGroupName()));
			exportDataBuilder.append("create,c,dm_group").append("\n");

			for (String attributeName : attributesToExport) {
				boolean isRepeating = groupObject.isAttrRepeating(attributeName);
				if (isRepeating) {
					for(int i=0; i<groupObject.getValueCount(attributeName);i++) {
						exportDataBuilder.append("set,c,l,").append(attributeName).append("[").append(i).append("]\n");
						exportDataBuilder.append(groupObject.getRepeatingString(attributeName,i)).append("\n");
					}
				} else {
					exportDataBuilder.append("set,c,l,").append(attributeName).append("\n");
					exportDataBuilder.append(groupObject.getString(attributeName)).append("\n");
				}
			}

			exportDataBuilder.append("save,c,l").append("\n");

			File tempFile = Resources.createTempFile("DcUser_" + groupName, ".api");

			if (tempFile != null) {
				Resources.exportStringToFile(tempFile, exportDataBuilder.toString());
				if (Desktop.isDesktopSupported()) {
					Resources.openFile(tempFile);
				}
			}

		} catch (DfException e) {
			UIUtils.showExpendableExceptionAlert("Export Group failed", "", "Could not export group", e);
			LOGGER.log(Level.SEVERE, "Could not export group", e);
		}
	}

	public GroupObject getGroupByName(String groupName) {
		try {
			IDfGroup dfcGroupObject = repository.getGroupByName(groupName);
			GroupObject groupObject = new GroupObject();
			groupObject.setR_object_id(dfcGroupObject.getObjectId().getId());
			groupObject.setR_modify_date(dfcGroupObject.getModifyDate().asString(""));
			groupObject.setGroup_name(dfcGroupObject.getGroupName());
			groupObject.setGroup_admin(dfcGroupObject.getGroupAdmin());
			groupObject.setDescription(dfcGroupObject.getDescription());
			groupObject.setGroup_display_name(dfcGroupObject.getGroupDisplayName());

			List<String> usersNames = new ArrayList<>();
			for (int i = 0; i < dfcGroupObject.getValueCount("users_names"); i++) {
				usersNames.add(dfcGroupObject.getRepeatingString("users_names", i));
			}
			groupObject.setUsers_names(usersNames);

			List<String> groupsNames = new ArrayList<>();
			for (int i = 0; i < dfcGroupObject.getValueCount("groups_names"); i++) {
				groupsNames.add(dfcGroupObject.getRepeatingString("groups_names", i));
			}
			groupObject.setGroups_names(groupsNames);

			return groupObject;

		} catch (DfException e) {
			UIUtils.showExpendableExceptionAlert("Retrieve User failed", "", "Could not retrieve user", e);
			LOGGER.log(Level.SEVERE, "Could not retrieve user", e);
		}

		return null;
	}

	public void updateGroup(GroupObject groupObject) {
		IDfLocalTransaction localTransaction = null;
		try {
			localTransaction = repository.getSession().beginTransEx();
			IDfGroup dfGroup = repository.getGroupByName(groupObject.getGroup_name());
			dfGroup.setGroupAddress(groupObject.getGroup_admin());
			dfGroup.setDescription(groupObject.getDescription());
			dfGroup.setGroupDisplayName(groupObject.getGroup_display_name());
			dfGroup.truncate("groups_names", 0);
			dfGroup.truncate("users_names", 0);
			for (String groupmember : groupObject.getGroups_names()) {
				dfGroup.addGroup(groupmember);
			}
			for (String usermember : groupObject.getUsers_names()) {
				dfGroup.addUser(usermember);
			}
			dfGroup.save();
			repository.getSession().commitTransEx(localTransaction);
			localTransaction = null;

		} catch (DfException e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
		} finally {
			if (localTransaction != null) {
				try {
					repository.getSession().abortTransEx(localTransaction);
				} catch (DfException ignored) {

				}
			}
		}
	}

	public List<String> getFilteredGrouplist(String text) {
		return repository.getFilteredGroupList(text);
	}

}
