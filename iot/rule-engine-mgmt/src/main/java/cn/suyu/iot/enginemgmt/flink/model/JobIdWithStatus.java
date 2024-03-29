package cn.suyu.iot.enginemgmt.flink.model;

import com.google.gson.TypeAdapter;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.util.Objects;

/**
 * @Description JobIdWithStatus
 */
public class JobIdWithStatus {
    /**
     * id
     */
    @SerializedName("id")
    private String id = null;

    /**
     * status
     */
    @SerializedName("status")
    private StatusEnum status = null;

    public JobIdWithStatus id(String id) {
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

    public JobIdWithStatus status(StatusEnum status) {
        this.status = status;
        return this;
    }

    /**
     * Get status
     *
     * @return status
     **/
    public StatusEnum getStatus() {
        return status;
    }

    public void setStatus(StatusEnum status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        JobIdWithStatus jobIdWithStatus = (JobIdWithStatus) o;
        return Objects.equals(this.id, jobIdWithStatus.id) &&
                Objects.equals(this.status, jobIdWithStatus.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, status);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class JobIdWithStatus {\n");

        sb.append("    id: ").append(toIndentedString(id)).append("\n");
        sb.append("    status: ").append(toIndentedString(status)).append("\n");
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

    /**
     * Gets or Sets status
     */
    @JsonAdapter(StatusEnum.Adapter.class)
    public enum StatusEnum {

        /**
         * created
         */
        CREATED("CREATED"),

        /**
         * running
         */
        RUNNING("RUNNING"),

        /**
         * failing
         */
        FAILING("FAILING"),

        /**
         * failed
         */
        FAILED("FAILED"),

        /**
         * cancelling
         */
        CANCELLING("CANCELLING"),

        /*+
         *canceled
         */
        CANCELED("CANCELED"),

        /**
         * finished
         */
        FINISHED("FINISHED"),

        /**
         * restarting
         */
        RESTARTING("RESTARTING"),

        /**
         * suspending
         */
        SUSPENDING("SUSPENDING"),

        /**
         * suspended
         */
        SUSPENDED("SUSPENDED"),

        /**
         * recconcling
         */
        RECONCILING("RECONCILING");

        private String value;

        StatusEnum(String value) {
            this.value = value;
        }

        public static StatusEnum fromValue(String text) {
            for (StatusEnum b : StatusEnum.values()) {
                if (String.valueOf(b.value).equals(text)) {
                    return b;
                }
            }
            return null;
        }

        public String getValue() {
            return value;
        }

        @Override
        public String toString() {
            return String.valueOf(value);
        }

        public static class Adapter extends TypeAdapter<StatusEnum> {
            @Override
            public void write(final JsonWriter jsonWriter, final StatusEnum enumeration) throws IOException {
                jsonWriter.value(enumeration.getValue());
            }

            @Override
            public StatusEnum read(final JsonReader jsonReader) throws IOException {
                String value = jsonReader.nextString();
                return StatusEnum.fromValue(String.valueOf(value));
            }
        }
    }
}
