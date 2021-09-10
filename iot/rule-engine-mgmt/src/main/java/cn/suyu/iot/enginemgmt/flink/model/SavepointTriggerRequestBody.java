package cn.suyu.iot.enginemgmt.flink.model;

import com.google.gson.annotations.SerializedName;

import java.util.Objects;

/**
 * @Description SavepointTriggerRequestBody
 */
public class SavepointTriggerRequestBody {
    @SerializedName("target-directory")
    private String targetDirectory = null;

    @SerializedName("cancel-job")
    private Boolean cancelJob = null;

    public SavepointTriggerRequestBody(String targetDirectory, Boolean cancelJob) {
        this.targetDirectory = targetDirectory;
        this.cancelJob = cancelJob;
    }

    public SavepointTriggerRequestBody targetDirectory(String targetDirectory) {
        this.targetDirectory = targetDirectory;
        return this;
    }

    /**
     * Get targetDirectory
     *
     * @return targetDirectory
     **/
    public String getTargetDirectory() {
        return targetDirectory;
    }

    public void setTargetDirectory(String targetDirectory) {
        this.targetDirectory = targetDirectory;
    }

    public SavepointTriggerRequestBody cancelJob(Boolean cancelJob) {
        this.cancelJob = cancelJob;
        return this;
    }

    /**
     * Get cancelJob
     *
     * @return cancelJob
     **/
    public Boolean isCancelJob() {
        return cancelJob;
    }

    public void setCancelJob(Boolean cancelJob) {
        this.cancelJob = cancelJob;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SavepointTriggerRequestBody savepointTriggerRequestBody = (SavepointTriggerRequestBody) o;
        return Objects.equals(this.targetDirectory, savepointTriggerRequestBody.targetDirectory) &&
                Objects.equals(this.cancelJob, savepointTriggerRequestBody.cancelJob);
    }

    @Override
    public int hashCode() {
        return Objects.hash(targetDirectory, cancelJob);
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class SavepointTriggerRequestBody {\n");

        sb.append("    targetDirectory: ").append(toIndentedString(targetDirectory)).append("\n");
        sb.append("    cancelJob: ").append(toIndentedString(cancelJob)).append("\n");
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
