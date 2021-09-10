package cn.suyu.iot.enginemgmt.common.okhttp;

/**
 * @Description Pair
 */
public class Pair {
    private String name = "";
    private String value = "";

    public Pair(String name, String value) {
        setName(name);
        setValue(value);
    }

    public String getName() {
        return name;
    }

    private void setName(String name) {
        if (!isValidString(name)) {
            return;
        }

        this.name = name;
    }

    public String getValue() {
        return value;
    }

    private void setValue(String value) {
        if (!isValidString(value)) {
            return;
        }

        this.value = value;
    }

    private boolean isValidString(String arg) {
        if (arg == null) {
            return false;
        }
        if (arg.trim().isEmpty()) {
            return false;
        }

        return true;
    }
}
