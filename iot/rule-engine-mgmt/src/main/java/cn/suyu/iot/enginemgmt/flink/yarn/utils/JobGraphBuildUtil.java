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

package cn.suyu.iot.enginemgmt.flink.yarn.utils;


import cn.suyu.iot.enginemgmt.flink.yarn.entity.JobParamsInfo;
import cn.suyu.iot.enginemgmt.hadoop.HDFSClient;
import com.esotericsoftware.minlog.Log;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.flink.client.program.PackagedProgram;
import org.apache.flink.client.program.PackagedProgramUtils;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.GlobalConfiguration;
import org.apache.flink.core.fs.Path;
import org.apache.flink.runtime.jobgraph.JobGraph;
import org.apache.flink.runtime.jobgraph.SavepointRestoreSettings;
import org.apache.flink.util.Preconditions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @Description build JobGraph utils
 */
@Slf4j
@Component
public class JobGraphBuildUtil {

    public static final String SAVE_POINT_PATH_KEY = "savePointPath";
    public static final String ALLOW_NON_RESTORED_STATE_KEY = "allowNonRestoredState";
    public static final String PARALLELISM = "parallelism";
    public static final String DEFAULT_FLINK_CONF_DIR = "conf" + File.separator + "flink-yarn";
    //    public static final String DEFAULT_FLINK_CONF_DIR = "D:\\flink_yarn_cfg";
    public static final String PATTERN_JAR_HDFS_PREFIX = File.separator + "webhdfs" + File.separator + "v1" + File.separator;
    public static final String PATTERN_JAR_HDFS_SUFFIX = "?op=OPEN&t=";
    private static String FLINK_HDFS_ADDRESS;

    public static JobGraph buildJobGraph(JobParamsInfo jobParamsInfo) throws Exception {
//        Properties confProperties = jobParamsInfo.getConfProperties();

//        int parallelism = MathUtil.getIntegerVal(confProperties.getProperty(PARALLELISM, "1"));
        int parallelism = 1;
        String flinkConfDir = jobParamsInfo.getFlinkConfDir();
        String[] execArgs = jobParamsInfo.getExecArgs();
        String runJarPath = jobParamsInfo.getRunJarPath();
        String entryPointClassName = jobParamsInfo.getEntryPointClassName();
        String[] dependFileLocalPath = jobParamsInfo.getDependFile();
        String[] classPathFile = jobParamsInfo.getClassPathFile();
        Optional.ofNullable(dependFileLocalPath)
                .ifPresent(dependFiles -> {
                    log.info("processDependFiles before {}", dependFiles);
                    processDependFiles(dependFiles);
                });
        AtomicReference<List<URL>> classPaths = new AtomicReference<>(jobParamsInfo.getClassPaths());
        Optional.ofNullable(classPathFile)
                .ifPresent(classPathFiles -> {
                    log.info("processClassPathFiles before {}", classPathFiles);
                    classPaths.set(processClassPathFiles(classPathFiles));
                });

        Preconditions.checkArgument(FileUtils.getFile(runJarPath).exists(), "runJarPath not exist!");

        File runJarFile = new File(runJarPath);
//        SavepointRestoreSettings savepointRestoreSettings = dealSavepointRestoreSettings(jobParamsInfo.getConfProperties());

        PackagedProgram program;
        PackagedProgram.Builder packagedProgramBuilder = PackagedProgram.newBuilder()
                .setJarFile(runJarFile)
                .setArguments(execArgs)
                .setEntryPointClassName(entryPointClassName);
        List<URL> userClassPaths = classPaths.get();
        if (userClassPaths != null) {
            log.info("set classPaths to PackagedProgram. {}", userClassPaths);
            packagedProgramBuilder
                    .setUserClassPaths(userClassPaths);
        }
        program = packagedProgramBuilder.build();

        Configuration flinkConfig = getFlinkConfiguration(flinkConfDir);
        JobGraph jobGraph = PackagedProgramUtils.createJobGraph(program, flinkConfig, parallelism, false);

        return jobGraph;
    }

    public static void processDependFiles(String[] dependFiles) {
        Arrays.stream(dependFiles).forEach(urlPath -> {
            try {
                HDFSClient.processDependFiles(urlPath);
            } catch (Exception e) {
                Log.error("process dependFiles error.", e);
            }
        });
    }

    public static List<URL> processClassPathFiles(String[] classPathFile) {
        List<URL> urls = new ArrayList<>();
        Arrays.stream(classPathFile).forEach(urlPath -> {
            if (StringUtils.isEmpty(urlPath)) {
                log.warn("urlPath is empty");
                return;
            }
            try {
                long urlTimeStamp = System.currentTimeMillis();
                //cep依赖包hdfs文件路径
                urls.add(new URL(FLINK_HDFS_ADDRESS + PATTERN_JAR_HDFS_PREFIX
                        + urlPath.substring(urlPath.lastIndexOf(File.separator) + 1) + PATTERN_JAR_HDFS_SUFFIX + urlTimeStamp));
                //cep依赖包本地文件路径
                urls.add(new URL("file://" + urlPath));
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        });
        return urls;
    }

    public static Configuration getFlinkConfiguration(String flinkConfDir) {
        return StringUtils.isEmpty(flinkConfDir) ? GlobalConfiguration.loadConfiguration(DEFAULT_FLINK_CONF_DIR) : GlobalConfiguration.loadConfiguration(flinkConfDir);
    }

    private static SavepointRestoreSettings dealSavepointRestoreSettings(Properties confProperties) {
        SavepointRestoreSettings savepointRestoreSettings = SavepointRestoreSettings.none();
        String savePointPath = confProperties.getProperty(SAVE_POINT_PATH_KEY);
        if (StringUtils.isNotBlank(savePointPath)) {
            String allowNonRestoredState = confProperties.getOrDefault(ALLOW_NON_RESTORED_STATE_KEY, "false").toString();
            savepointRestoreSettings = SavepointRestoreSettings.forPath(savePointPath, BooleanUtils.toBoolean(allowNonRestoredState));
        }
        return savepointRestoreSettings;
    }

    public static void fillDependFilesJobGraph(JobGraph jobGraph, String[] dependFiles) {
        Arrays.stream(dependFiles).forEach(path -> jobGraph.addJar(new Path("file://" + path)));
    }

    @Value("${flink.hdfs.rest.url}")
    public void setFlinkHdfsAddress(String flinkHdfsAddress) {
        FLINK_HDFS_ADDRESS = flinkHdfsAddress;
    }

}
