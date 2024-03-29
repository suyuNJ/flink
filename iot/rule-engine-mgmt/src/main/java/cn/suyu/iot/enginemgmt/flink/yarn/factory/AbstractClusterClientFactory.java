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

package cn.suyu.iot.enginemgmt.flink.yarn.factory;

import org.apache.flink.client.deployment.ClusterDescriptor;
import org.apache.flink.client.deployment.ClusterSpecification;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.JobManagerOptions;
import org.apache.flink.configuration.TaskManagerOptions;
import org.apache.flink.runtime.clusterframework.TaskExecutorProcessUtils;

import static org.apache.flink.util.Preconditions.checkNotNull;

/**
 * @Description abstract cluster client
 */
public interface AbstractClusterClientFactory {

    default ClusterSpecification getClusterSpecification(Configuration configuration) {
        checkNotNull(configuration);

        final int jobManagerMemoryMb = TaskExecutorProcessUtils
                .processSpecFromConfig(TaskExecutorProcessUtils.getConfigurationMapLegacyTaskManagerHeapSizeToConfigOption(
                        configuration, JobManagerOptions.TOTAL_PROCESS_MEMORY))
                .getTotalProcessMemorySize()
                .getMebiBytes();

        final int taskManagerMemoryMb = TaskExecutorProcessUtils
                .processSpecFromConfig(TaskExecutorProcessUtils.getConfigurationMapLegacyTaskManagerHeapSizeToConfigOption(
                        configuration, TaskManagerOptions.TOTAL_PROCESS_MEMORY))
                .getTotalProcessMemorySize()
                .getMebiBytes();

        int slotsPerTaskManager = configuration.getInteger(TaskManagerOptions.NUM_TASK_SLOTS);

        return new ClusterSpecification.ClusterSpecificationBuilder()
                .setMasterMemoryMB(jobManagerMemoryMb)
                .setTaskManagerMemoryMB(taskManagerMemoryMb)
                .setSlotsPerTaskManager(slotsPerTaskManager)
                .createClusterSpecification();
    }

    /**
     * create ClusterDescriptor
     * @param clusterConfPath  cluster configuration path , E.g. yarn conf dir
     * @param flinkConfig
     * @return
     */
    ClusterDescriptor createClusterDescriptor(String clusterConfPath, Configuration flinkConfig);

}
