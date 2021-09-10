package cn.suyu.iot.enginemgmt.yarn.model;

import java.util.List;

/**
 * @Description ApplicationResponseBody
 */
public class ApplicationResponseBody {

    /**
     * apps : {"app":[{"id":"application_1607932520010_0009","user":"deployer","name":"Flink session cluster","queue":"root.deployer","state":"FINISHED","finalStatus":"SUCCEEDED","progress":100,"trackingUI":"History","trackingUrl":"http://node1:8099/proxy/application_1607932520010_0009/","diagnostics":"","clusterId":1607932520010,"applicationType":"Apache Flink","applicationTags":"","priority":0,"startedTime":1608003107331,"launchTime":1608003107577,"finishedTime":1608003148118,"elapsedTime":40787,"amContainerLogs":"http://node1:8042/node/containerlogs/container_e36_1607932520010_0009_01_000001/deployer","amHostHttpAddress":"node1:8042","amRPCAddress":"node1:18085","masterNodeId":"node1:51430","allocatedMB":-1,"allocatedVCores":-1,"reservedMB":-1,"reservedVCores":-1,"runningContainers":-1,"memorySeconds":84473,"vcoreSeconds":41,"queueUsagePercentage":0,"clusterUsagePercentage":0,"preemptedResourceMB":0,"preemptedResourceVCores":0,"numNonAMContainerPreempted":0,"numAMContainerPreempted":0,"preemptedMemorySeconds":0,"preemptedVcoreSeconds":0,"preemptedResourceSecondsMap":{},"logAggregationStatus":"DISABLED","unmanagedApplication":false,"amNodeLabelExpression":""}]}
     */

    private AppsBean apps;

    public AppsBean getApps() {
        return apps;
    }

    public void setApps(AppsBean apps) {
        this.apps = apps;
    }

    public static class AppsBean {
        private List<AppBean> app;

        public List<AppBean> getApp() {
            return app;
        }

        public void setApp(List<AppBean> app) {
            this.app = app;
        }

        public static class AppBean {
            /**
             * id : application_1607932520010_0009
             * user : deployer
             * name : Flink session cluster
             * queue : root.deployer
             * state : FINISHED
             * finalStatus : SUCCEEDED
             * progress : 100.0
             * trackingUI : History
             * trackingUrl : http://node1:8099/proxy/application_1607932520010_0009/
             * diagnostics :
             * clusterId : 1607932520010
             * applicationType : Apache Flink
             * applicationTags :
             * priority : 0
             * startedTime : 1608003107331
             * launchTime : 1608003107577
             * finishedTime : 1608003148118
             * elapsedTime : 40787
             * amContainerLogs : http://node1:8042/node/containerlogs/container_e36_1607932520010_0009_01_000001/deployer
             * amHostHttpAddress : node1:8042
             * amRPCAddress : node1:18085
             * masterNodeId : node1:51430
             * allocatedMB : -1
             * allocatedVCores : -1
             * reservedMB : -1
             * reservedVCores : -1
             * runningContainers : -1
             * memorySeconds : 84473
             * vcoreSeconds : 41
             * queueUsagePercentage : 0.0
             * clusterUsagePercentage : 0.0
             * preemptedResourceMB : 0
             * preemptedResourceVCores : 0
             * numNonAMContainerPreempted : 0
             * numAMContainerPreempted : 0
             * preemptedMemorySeconds : 0
             * preemptedVcoreSeconds : 0
             * preemptedResourceSecondsMap : {}
             * logAggregationStatus : DISABLED
             * unmanagedApplication : false
             * amNodeLabelExpression :
             */

            private String id;
            private String user;
            private String name;
            private String state;


            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getUser() {
                return user;
            }

            public void setUser(String user) {
                this.user = user;
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

            @Override
            public String toString() {
                return "AppBean{" +
                        "id='" + id + '\'' +
                        ", user='" + user + '\'' +
                        ", name='" + name + '\'' +
                        ", state='" + state + '\'' +
                        '}';
            }
        }

        @Override
        public String toString() {
            return "AppsBean{" +
                    "app=" + app +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "ApplicationResponseBody{" +
                "apps=" + apps +
                '}';
    }
}
