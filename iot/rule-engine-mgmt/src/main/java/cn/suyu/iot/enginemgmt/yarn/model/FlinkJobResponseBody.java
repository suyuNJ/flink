package cn.suyu.iot.enginemgmt.yarn.model;

import java.util.List;

/**
 * @Description FlinkJobResponseBody
 */
public class FlinkJobResponseBody {
    private List<JobsBean> jobs;

    public List<JobsBean> getJobs() {
        return jobs;
    }

    public void setJobs(List<JobsBean> jobs) {
        this.jobs = jobs;
    }

    public static class JobsBean {
        /**
         * jid : 329b48becaca56f4f2638b321e34ee1a
         * name : State machine job
         * state : RUNNING
         * start-time : 1608095294953
         * end-time : -1
         * duration : 7847631
         * last-modification : 1608095304597
         * tasks : {"total":2,"created":0,"scheduled":0,"deploying":0,"running":2,"finished":0,"canceling":0,"canceled":0,"failed":0,"reconciling":0}
         */

        private String jid;
        private String name;
        private String state;

        public String getJid() {
            return jid;
        }

        public void setJid(String jid) {
            this.jid = jid;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }

        public static class TasksBean {
            /**
             * total : 2
             * created : 0
             * scheduled : 0
             * deploying : 0
             * running : 2
             * finished : 0
             * canceling : 0
             * canceled : 0
             * failed : 0
             * reconciling : 0
             */

            private int total;
            private int created;
            private int scheduled;
            private int deploying;
            private int running;
            private int finished;
            private int canceling;
            private int canceled;
            private int failed;
            private int reconciling;

            public int getTotal() {
                return total;
            }

            public void setTotal(int total) {
                this.total = total;
            }

            public int getCreated() {
                return created;
            }

            public void setCreated(int created) {
                this.created = created;
            }

            public int getScheduled() {
                return scheduled;
            }

            public void setScheduled(int scheduled) {
                this.scheduled = scheduled;
            }

            public int getDeploying() {
                return deploying;
            }

            public void setDeploying(int deploying) {
                this.deploying = deploying;
            }

            public int getRunning() {
                return running;
            }

            public void setRunning(int running) {
                this.running = running;
            }

            public int getFinished() {
                return finished;
            }

            public void setFinished(int finished) {
                this.finished = finished;
            }

            public int getCanceling() {
                return canceling;
            }

            public void setCanceling(int canceling) {
                this.canceling = canceling;
            }

            public int getCanceled() {
                return canceled;
            }

            public void setCanceled(int canceled) {
                this.canceled = canceled;
            }

            public int getFailed() {
                return failed;
            }

            public void setFailed(int failed) {
                this.failed = failed;
            }

            public int getReconciling() {
                return reconciling;
            }

            public void setReconciling(int reconciling) {
                this.reconciling = reconciling;
            }
        }
    }
}
