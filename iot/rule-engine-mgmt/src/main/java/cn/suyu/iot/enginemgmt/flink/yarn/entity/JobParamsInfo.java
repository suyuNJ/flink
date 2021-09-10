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

package cn.suyu.iot.enginemgmt.flink.yarn.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.net.URL;
import java.util.List;
import java.util.Properties;

/**
 * @Description Flink task execute parameters
 */
@Data
@Builder
@AllArgsConstructor
public class JobParamsInfo {

    private String name;
    private String queue;
    private String runMode;
    private String runJarPath;
    private String flinkConfDir;
    private String flinkJarPath;
    private String yarnConfDir;
    private String entryPointClassName;
    /**
     * 场景联动上传cep jar到hdfs 数据转发不需要传此参数
     **/
    private String[] dependFile;
    /**
     * 场景联动添加cep jar到classpath 数据转发不需要传此参数
     **/
    private String[] classPathFile;
    private String[] execArgs;
    private Properties confProperties;
    private Properties yarnSessionConfProperties;
    private List<URL> classPaths;

}
