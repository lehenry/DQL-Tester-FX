package nl.bos.beans;

import java.util.List;

public class Option {
    public static final String DEFAULT = "Default";
    public static final String LABEL = "Label";
    public static final String MANDATORY = "Mandatory";
    public static final String TYPE = "Type";
    public static final String VALUES = "Values";
    public static final String LOCATION = "Location";
    private String name;
    private String label;
    private String defaultValue;
    private boolean isMandatory;
    private String type;
    private List<String> values;
    private int location;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public boolean isMandatory() {
        return isMandatory;
    }

    public void setMandatory(boolean mandatory) {
        isMandatory = mandatory;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<String> getValues() {
        return values;
    }

    public void setValues(List<String> values) {
        this.values = values;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(String.format("%s:%s=%s\n", this.name, LOCATION, this.location));
        builder.append(String.format("%s:%s=%s\n", this.name, LABEL, this.label));
        builder.append(String.format("%s:%s=%s\n", this.name, DEFAULT, this.defaultValue));
        builder.append(String.format("%s:%s=%s\n", this.name, MANDATORY, this.isMandatory));
        builder.append(String.format("%s:%s=%s\n", this.name, TYPE, this.type));
        builder.append(String.format("%s:%s=%s\n", this.name, VALUES, String.join(",", values)));
        return String.valueOf(builder);
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public int getLocation() {
        return location;
    }

    public void setLocation(int location) {
        this.location = location;
    }
}
