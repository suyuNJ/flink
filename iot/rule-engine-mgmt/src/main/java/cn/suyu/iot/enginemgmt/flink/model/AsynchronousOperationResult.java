package cn.suyu.iot.enginemgmt.flink.model;

import com.google.gson.annotations.SerializedName;
import org.apache.flink.runtime.rest.messages.queue.QueueStatus;

import java.util.Objects;

/**
 * @Description AsynchronousOperationResult
 */
public class AsynchronousOperationResult {
    @SerializedName("status")
    private QueueStatus status = null;

    @SerializedName("operation")
    private Object operation = null;

    public AsynchronousOperationResult status(QueueStatus status) {
        this.status = status;
        return this;
    }

    /**
     * Get status
     *
     * @return status
     **/
    public QueueStatus getStatus() {
        return status;
    }

    public void setStatus(QueueStatus status) {
        this.status = status;
    }

    public AsynchronousOperationResult operation(Object operation) {
        this.operation = operation;
        return this;
    }

    /**
     * Get operation
     *
     * @return operation
     **/
    public Object getOperation() {
        return operation;
    }

    public void setOperation(Object operation) {
        this.operation = operation;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AsynchronousOperationResult asynchronousOperationResult = (AsynchronousOperationResult) o;
        return Objects.equals(this.status, asynchronousOperationResult.status) &&
                Objects.equals(this.operation, asynchronousOperationResult.operation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(status, operation);
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class AsynchronousOperationResult {\n");

        sb.append("    status: ").append(toIndentedString(status)).append("\n");
        sb.append("    operation: ").append(toIndentedString(operation)).append("\n");
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
