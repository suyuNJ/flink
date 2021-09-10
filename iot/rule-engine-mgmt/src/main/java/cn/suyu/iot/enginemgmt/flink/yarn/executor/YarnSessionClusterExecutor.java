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
import cn.suyu.iot.enginemgmt.flink.yarn.factory.YarnClusterClientFactory;
import cn.suyu.iot.enginemgmt.flink.yarn.utils.JobGraphBuildUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.math3.util.Pair;
import org.apache.flink.api.common.JobExecutionResult;
import org.apache.flink.client.ClientUtils;
import org.apache.flink.client.deployment.ClusterDescriptor;
import org.apache.flink.client.deployment.ClusterRetrieveException;
import org.apache.flink.client.program.ClusterClient;
import org.apache.flink.client.program.ClusterClientProvider;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.runtime.jobgraph.JobGraph;
import org.apache.flink.util.Preconditions;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.util.ConverterUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Optional;

/** @Description YarnSessionClusterExecutor */
@Slf4j
public class YarnSessionClusterExecutor extends AbstractClusterExecutor {
  private static final Logger LOG = LoggerFactory.getLogger(YarnSessionClusterExecutor.class);

  public static final String DEFAULT_FLINK_CONF_DIR = "conf" + File.separator + "flink-yarn";

  public YarnSessionClusterExecutor(JobParamsInfo jobParamsInfo) {
    super(jobParamsInfo);
  }

  @Override
  public Optional<Pair<String, String>> submit() throws Exception {
    JobGraph jobGraph = jobValidateAndGetJobGraph();
    Object yid = jobParamsInfo.getYarnSessionConfProperties().get("yid");
    ClusterClient clusterClient = retrieveClusterClient(yid.toString());

    JobExecutionResult jobExecutionResult = ClientUtils.submitJob(clusterClient, jobGraph);
    LOG.info("jobID:{}", jobExecutionResult.getJobID().toString());

    LOG.info("submit() clusterClient close start.");
    clusterClient.close();
    LOG.info("submit() clusterClient close end.");
    return Optional.of(new Pair<>(yid.toString(), jobExecutionResult.getJobID().toString()));
  }

  @Override
  public ClusterClient retrieveClusterClient(String yid) throws ClusterRetrieveException {
    log.info("retrieve cluster client.");
    Preconditions.checkNotNull(yid, "yarn session mode applicationId required!");
    ClusterClient<ApplicationId> clusterClient = getClusterClient(yid);

    return clusterClient;
  }

  private ClusterClient<ApplicationId> getClusterClient(String yid)
      throws ClusterRetrieveException {
    ApplicationId applicationId = ConverterUtils.toApplicationId(yid);
    Configuration flinkConfiguration =
        JobGraphBuildUtil.getFlinkConfiguration(jobParamsInfo.getFlinkConfDir());
    String yarnConfDir = jobParamsInfo.getYarnConfDir();
    if (StringUtils.isEmpty(yarnConfDir)) {
      yarnConfDir = DEFAULT_FLINK_CONF_DIR;
    }
    ClusterDescriptor clusterDescriptor =
        YarnClusterClientFactory.INSTANCE.createClusterDescriptor(yarnConfDir, flinkConfiguration);

    ClusterClientProvider<ApplicationId> retrieve = clusterDescriptor.retrieve(applicationId);
    ClusterClient<ApplicationId> clusterClient = retrieve.getClusterClient();

    return clusterClient;
  }
}
