package nl.bos.beans;

import java.util.List;

public class Option {
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
        builder.append(String.format("\t%s.Label = \"%s\"\n", this.name, this.label));
        builder.append(String.format("\t%s.Default = \"%s\"\n", this.name, this.defaultValue));
        builder.append(String.format("\t%s.Mandatory = \"%s\"\n", this.name, this.isMandatory));
        builder.append(String.format("\t%s.Type = \"%s\"\n", this.name, this.type));
        builder.append(String.format("\t%s.Values = \"%s\"\n", this.name, String.join(", ", values)));
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
