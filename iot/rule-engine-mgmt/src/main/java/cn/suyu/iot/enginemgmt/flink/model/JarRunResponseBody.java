package cn.suyu.iot.enginemgmt.flink.model;

import java.util.Objects;

/**
 * @Description JarRunResponseBody
 */
public class JarRunResponseBody {
    private String jobid = null;

    public JarRunResponseBody jobid(String jobid) {
        this.jobid = jobid;
        return this;
    }

    /**
     * Get jobid
     *
     * @return jobid
     */
    public String getJobid() {
        return jobid;
    }

    public void setJobid(String jobid) {
        this.jobid = jobid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        JarRunResponseBody jarRunResponseBody = (JarRunResponseBody) o;
        return Objects.equals(this.jobid, jarRunResponseBody.jobid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(jobid);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class JarRunResponseBody {\n");

        sb.append("    jobid: ").append(toIndentedString(jobid)).append("\n");
        sb.append("}");
        return sb.toString();
    }

    /**
     * Convert the given object to string with each line indented by 4 spaces (except the first line).
     */
    private String toIndentedString(Object o) {
        if (o == null) {
            return "null";
        }
        return o.toString().replace("\n", "\n    ");
    }
}
