package cn.suyu.iot.enginemgmt.flink.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @Description JobIdsWithStatusOverview
 */
public class JobIdsWithStatusOverview {
    @SerializedName("jobs")
    private List<JobIdWithStatus> jobs = null;

    public JobIdsWithStatusOverview jobs(List<JobIdWithStatus> jobs) {
        this.jobs = jobs;
        return this;
    }

    public JobIdsWithStatusOverview addJobsItem(JobIdWithStatus jobsItem) {
        if (this.jobs == null) {
            this.jobs = new ArrayList<JobIdWithStatus>();
        }
        this.jobs.add(jobsItem);
        return this;
    }

    /**
     * Get jobs
     *
     * @return jobs
     **/
    public List<JobIdWithStatus> getJobs() {
        return jobs;
    }

    public void setJobs(List<JobIdWithStatus> jobs) {
        this.jobs = jobs;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        JobIdsWithStatusOverview jobIdsWithStatusOverview = (JobIdsWithStatusOverview) o;
        return Objects.equals(this.jobs, jobIdsWithStatusOverview.jobs);
    }

    @Override
    public int hashCode() {
        return Objects.hash(jobs);
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class JobIdsWithStatusOverview {\n");

        sb.append("    jobs: ").append(toIndentedString(jobs)).append("\n");
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
