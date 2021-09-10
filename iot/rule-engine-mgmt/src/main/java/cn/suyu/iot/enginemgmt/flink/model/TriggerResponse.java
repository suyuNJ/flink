package cn.suyu.iot.enginemgmt.flink.model;

import com.google.gson.annotations.SerializedName;

import java.util.Objects;

/**
 * @Description TriggerResponse
 */
public class TriggerResponse {
    @SerializedName("request-id")
    private String requestId = null;

    public TriggerResponse requestId(String requestId) {
        this.requestId = requestId;
        return this;
    }

    /**
     * Get requestId
     *
     * @return requestId
     **/
    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        TriggerResponse triggerResponse = (TriggerResponse) o;
        return Objects.equals(this.requestId, triggerResponse.requestId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(requestId);
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class TriggerResponse {\n");

        sb.append("    requestId: ").append(toIndentedString(requestId)).append("\n");
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
