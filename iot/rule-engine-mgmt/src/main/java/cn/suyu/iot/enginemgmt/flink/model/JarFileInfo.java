package cn.suyu.iot.enginemgmt.flink.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @Description JarFileInfo
 */
public class JarFileInfo {
    private String id = null;

    private String name = null;

    private Long uploaded = null;

    private List<JarEntryInfo> entry = null;

    public JarFileInfo id(String id) {
        this.id = id;
        return this;
    }

    /**
     * Get id
     *
     * @return id
     **/
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public JarFileInfo name(String name) {
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

    public JarFileInfo uploaded(Long uploaded) {
        this.uploaded = uploaded;
        return this;
    }

    /**
     * Get uploaded
     *
     * @return uploaded
     **/
    public Long getUploaded() {
        return uploaded;
    }

    public void setUploaded(Long uploaded) {
        this.uploaded = uploaded;
    }

    public JarFileInfo entry(List<JarEntryInfo> entry) {
        this.entry = entry;
        return this;
    }

    public JarFileInfo addEntryItem(JarEntryInfo entryItem) {
        if (this.entry == null) {
            this.entry = new ArrayList<JarEntryInfo>();
        }
        this.entry.add(entryItem);
        return this;
    }

    /**
     * Get entry
     *
     * @return entry
     **/
    public List<JarEntryInfo> getEntry() {
        return entry;
    }

    public void setEntry(List<JarEntryInfo> entry) {
        this.entry = entry;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        JarFileInfo jarFileInfo = (JarFileInfo) o;
        return Objects.equals(this.id, jarFileInfo.id) &&
                Objects.equals(this.name, jarFileInfo.name) &&
                Objects.equals(this.uploaded, jarFileInfo.uploaded) &&
                Objects.equals(this.entry, jarFileInfo.entry);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, uploaded, entry);
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class JarFileInfo {\n");

        sb.append("    id: ").append(toIndentedString(id)).append("\n");
        sb.append("    name: ").append(toIndentedString(name)).append("\n");
        sb.append("    uploaded: ").append(toIndentedString(uploaded)).append("\n");
        sb.append("    entry: ").append(toIndentedString(entry)).append("\n");
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
