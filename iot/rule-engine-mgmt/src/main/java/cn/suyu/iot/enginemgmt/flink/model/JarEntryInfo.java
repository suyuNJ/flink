package cn.suyu.iot.enginemgmt.flink.model;

import com.google.gson.annotations.SerializedName;

import java.util.Objects;

/**
 * @Description JarEntryInfo
 */
public class JarEntryInfo {
    private String name = null;

    @SerializedName("description")
    private String description = null;

    public JarEntryInfo name(String name) {
        this.name = name;
        return this;
    }

    /**
     * Get name
     *
     * @return name
     **/
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public JarEntryInfo description(String description) {
        this.description = description;
        return this;
    }

    /**
     * Get description
     *
     * @return description
     **/
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        JarEntryInfo jarEntryInfo = (JarEntryInfo) o;
        return Objects.equals(this.name, jarEntryInfo.name) &&
                Objects.equals(this.description, jarEntryInfo.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description);
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class JarEntryInfo {\n");

        sb.append("    name: ").append(toIndentedString(name)).append("\n");
        sb.append("    description: ").append(toIndentedString(description)).append("\n");
        sb.append("}");
        return sb.toString();
    }

    /**
     * Convert the given object to string with each line indented by 4 spaces
     * (except the first line).
     */
    private String toIndentedString(Object o) {
        if (o == null) {
            return "null";
        }
        return o.toString().replace("\n", "\n    ");
    }
}
