/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cn.suyu.iot.enginemgmt.flink.yarn.executor;

import cn.suyu.iot.enginemgmt.flink.yarn.entity.JobParamsInfo;
import cn.suyu.iot.enginemgmt.flink.yarn.enums.ETaskStatus;
import cn.suyu.iot.enginemgmt.flink.yarn.factory.YarnClusterClientFactory;
import cn.suyu.iot.enginemgmt.flink.yarn.utils.HttpClientUtil;
import cn.suyu.iot.enginemgmt.flink.yarn.utils.JobGraphBuildUtil;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.math3.util.Pair;
import org.apache.flink.api.common.JobExecutionResult;
import org.apache.flink.api.common.JobID;
import org.apache.flink.client.deployment.ClusterDescriptor;
import org.apache.flink.client.deployment.ClusterRetrieveException;
import org.apache.flink.client.program.ClusterClient;
import org.apache.flink.client.program.ClusterClientProvider;
import org.apache.flink.client.program.ProgramInvocationException;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.core.execution.DetachedJobExecutionResult;
import org.apache.flink.runtime.jobgraph.JobGraph;
import org.apache.flink.util.ExceptionUtils;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.util.ConverterUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.concurrent.ExecutionException;

import static org.apache.flink.util.Preconditions.checkNotNull;

/** @Description abstract cluster executor */
public abstract class AbstractClusterExecutor {
  private static final Logger LOG = LoggerFactory.getLogger(AbstractClusterExecutor.class);

  public final JobParamsInfo jobParamsInfo;

  public AbstractClusterExecutor(JobParamsInfo jobParamsInfo) {
    this.jobParamsInfo = jobParamsInfo;
  }

  /**
   * job submit
   *
   * @return <yarnApplicatonId, flinkJobId>
   * @throws Exception
   */
  abstract Optional<Pair<String, String>> submit() throws Exception;

  /**
   * job validate
   *
   * @return <jobGraph>
   * @throws Exception
   */
  public JobGraph jobValidateAndGetJobGraph() throws Exception {
    return JobGraphBuildUtil.buildJobGraph(jobParamsInfo);
  }

  public ETaskStatus getJobStatus(String appId, String jobId) throws Exception {
    ClusterClient clusterClient = retrieveClusterClient(appId);
    String webInterfaceURL = clusterClient.getWebInterfaceURL();
    String reqUrl = webInterfaceURL + "/jobs/" + jobId;
    String response = HttpClientUtil.getRequest(reqUrl);

    JSONObject jsonObject = JSONObject.parseObject(response);
    Object stateObj = jsonObject.get("state");
    if (stateObj == null) {
      return ETaskStatus.NOTFOUND;
    }

    String state = (String) stateObj;
    ETaskStatus jobStatus = ETaskStatus.valueOf(StringUtils.upperCase(state));
    LOG.info("getJobStatus() clusterClient close start.");
    clusterClient.close();
    LOG.info("getJobStatus() clusterClient close end.");
    return jobStatus;
  }

  public ClusterClient retrieveClusterClient(String id) throws Exception {
    // rewrite
    return null;
  }

  /**
   * job cancel
   *
   * @param appId yarnApplicatonId
   * @param jobId flinkJobId
   * @throws ClusterRetrieveException
   */
  public void cancel(String appId, String jobId) throws ClusterRetrieveException {
    LOG.info("will cancel flink job ,appId is {},jobId is {}", appId, jobId);
    Configuration flinkConfiguration =
        JobGraphBuildUtil.getFlinkConfiguration(jobParamsInfo.getFlinkConfDir());
    ClusterDescriptor clusterDescriptor =
        YarnClusterClientFactory.INSTANCE.createClusterDescriptor(
            jobParamsInfo.getYarnConfDir(), flinkConfiguration);

    //  get ClusterClient
    ApplicationId applicationId = ConverterUtils.toApplicationId(appId);
    ClusterClientProvider<ApplicationId> retrieve = clusterDescriptor.retrieve(applicationId);
    ClusterClient<ApplicationId> clusterClient = retrieve.getClusterClient();

    JobID runningJobId = new JobID(org.apache.flink.util.StringUtils.hexStringToByte(jobId));
    clusterClient
        .cancel(runningJobId)
        .thenApply(
            a -> {
              LOG.info("cancel after close function. runningJobId: {}", runningJobId);
              clusterClient.close();
              LOG.info("cancel after, a: {}", a);
              return a;
            });

    //    clusterClient.close();
    LOG.info("success cancel job, applicationId:{},jobId:{}", appId, jobId);
  }

  protected static JobExecutionResult submitJob(ClusterClient<?> client, JobGraph jobGraph)
      throws ProgramInvocationException {
    checkNotNull(client);
    checkNotNull(jobGraph);
    try {
      return client.submitJob(jobGraph).thenApply(DetachedJobExecutionResult::new).get();
    } catch (InterruptedException | ExecutionException e) {
      ExceptionUtils.checkInterrupted(e);
      throw new ProgramInvocationException(
          "Could not run job in detached mode.", jobGraph.getJobID(), e);
    }
  }
}
