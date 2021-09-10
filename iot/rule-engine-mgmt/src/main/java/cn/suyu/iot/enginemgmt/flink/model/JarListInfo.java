package cn.suyu.iot.enginemgmt.flink.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @Description JarListInfo
 */
public class JarListInfo {
    private String address = null;

    private List<JarFileInfo> files = null;

    public JarListInfo address(String address) {
        this.address = address;
        return this;
    }

    /**
     * Get address
     *
     * @return address
     **/
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public JarListInfo files(List<JarFileInfo> files) {
        this.files = files;
        return this;
    }

    public JarListInfo addFilesItem(JarFileInfo filesItem) {
        if (this.files == null) {
            this.files = new ArrayList<JarFileInfo>();
        }
        this.files.add(filesItem);
        return this;
    }

    /**
     * Get files
     *
     * @return files
     **/
    public List<JarFileInfo> getFiles() {
        return files;
    }

    public void setFiles(List<JarFileInfo> files) {
        this.files = files;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        JarListInfo jarListInfo = (JarListInfo) o;
        return Objects.equals(this.address, jarListInfo.address) &&
                Objects.equals(this.files, jarListInfo.files);
    }

    @Override
    public int hashCode() {
        return Objects.hash(address, files);
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class JarListInfo {\n");

        sb.append("    address: ").append(toIndentedString(address)).append("\n");
        sb.append("    files: ").append(toIndentedString(files)).append("\n");
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
