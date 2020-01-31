package nl.bos.beans;

import java.util.List;

public class GroupObject {
	
	public String getR_object_id() {
		return r_object_id;
	}
	public void setR_object_id(String r_object_id) {
		this.r_object_id = r_object_id;
	}
	public String getR_modify_date() {
		return r_modify_date;
	}
	public void setR_modify_date(String r_modify_date) {
		this.r_modify_date = r_modify_date;
	}
	public String getGroup_name() {
		return group_name;
	}
	public void setGroup_name(String group_name) {
		this.group_name = group_name;
	}
	public String getGroup_address() {
		return group_address;
	}
	public void setGroup_address(String group_address) {
		this.group_address = group_address;
	}
	public List<String> getUsers_names() {
		return users_names;
	}
	public void setUsers_names(List<String> users_names) {
		this.users_names = users_names;
	}
	public List<String> getGroups_names() {
		return groups_names;
	}
	public void setGroups_names(List<String> groups_names) {
		this.groups_names = groups_names;
	}
	public String getOwner_name() {
		return owner_name;
	}
	public void setOwner_name(String owner_name) {
		this.owner_name = owner_name;
	}
	public boolean isIs_private() {
		return is_private;
	}
	public void setIs_private(boolean is_private) {
		this.is_private = is_private;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public boolean isGlobally_managed() {
		return globally_managed;
	}
	public void setGlobally_managed(boolean globally_managed) {
		this.globally_managed = globally_managed;
	}
	public String getAlias_set_id() {
		return alias_set_id;
	}
	public void setAlias_set_id(String alias_set_id) {
		this.alias_set_id = alias_set_id;
	}
	public String getGroup_source() {
		return group_source;
	}
	public void setGroup_source(String group_source) {
		this.group_source = group_source;
	}
	public String getGroup_class() {
		return group_class;
	}
	public void setGroup_class(String group_class) {
		this.group_class = group_class;
	}
	public String getGroup_admin() {
		return group_admin;
	}
	public void setGroup_admin(String group_admin) {
		this.group_admin = group_admin;
	}
	public boolean isIs_dynamic() {
		return is_dynamic;
	}
	public void setIs_dynamic(boolean is_dynamic) {
		this.is_dynamic = is_dynamic;
	}
	public boolean isIs_dynamic_default() {
		return is_dynamic_default;
	}
	public void setIs_dynamic_default(boolean is_dynamic_default) {
		this.is_dynamic_default = is_dynamic_default;
	}
	public String getGroup_global_unique_id() {
		return group_global_unique_id;
	}
	public void setGroup_global_unique_id(String group_global_unique_id) {
		this.group_global_unique_id = group_global_unique_id;
	}
	public String getGroup_native_room_id() {
		return group_native_room_id;
	}
	public void setGroup_native_room_id(String group_native_room_id) {
		this.group_native_room_id = group_native_room_id;
	}
	public String getGroup_directory_id() {
		return group_directory_id;
	}
	public void setGroup_directory_id(String group_directory_id) {
		this.group_directory_id = group_directory_id;
	}
	public String getGroup_display_name() {
		return group_display_name;
	}
	public void setGroup_display_name(String group_display_name) {
		this.group_display_name = group_display_name;
	}
	public boolean isIs_protected() {
		return is_protected;
	}
	public void setIs_protected(boolean is_protected) {
		this.is_protected = is_protected;
	}
	public boolean isIs_module_only() {
		return is_module_only;
	}
	public void setIs_module_only(boolean is_module_only) {
		this.is_module_only = is_module_only;
	}
	private String r_object_id;
	private String r_modify_date;
	
	private String group_name;
	private String group_address;
	private List<String> users_names;
	private List<String> groups_names;
	private String owner_name;
	private boolean is_private;
	private String description;
	private boolean globally_managed;
	private String alias_set_id;
	private String group_source;
	private String group_class;
	private String group_admin;
	private boolean is_dynamic;
	private boolean is_dynamic_default;
	private String group_global_unique_id;
	private String group_native_room_id;
	private String group_directory_id;
	private String group_display_name;
	private boolean is_protected;
	private boolean is_module_only;
	  
}
