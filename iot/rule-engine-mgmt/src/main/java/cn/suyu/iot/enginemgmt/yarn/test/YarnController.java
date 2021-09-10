package cn.suyu.iot.enginemgmt.yarn.test;

import cn.suyu.iot.enginemgmt.common.okhttp.ApiException;
import cn.suyu.iot.enginemgmt.flink.yarn.entity.JobParamsInfo;
import cn.suyu.iot.enginemgmt.flink.yarn.service.FlinkYarnService;
import cn.suyu.iot.enginemgmt.hadoop.HDFSClient;
import cn.suyu.iot.enginemgmt.yarn.api.YarnApi;
import cn.suyu.iot.enginemgmt.yarn.model.ApplicationResponseBody;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.math3.util.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.Properties;

/**
 * @Description 物模型服务
 */
@RestController
@RequestMapping("/yarnTest")
@Slf4j
public class YarnController {
    @Autowired
    private YarnApi yarnApi;

    @Autowired
    private FlinkYarnService flinkYarnService;

    /**
     * yarn test
     *
     * @return
     */
    @GetMapping(value = "/getAplications", produces = "application/json;charset=UTF-8")
    public ApplicationResponseBody getProperties() throws ApiException {
        ApplicationResponseBody applicationResponseBody = yarnApi.getApplications();
        return applicationResponseBody;
    }

    @GetMapping(value = "/uploadHdfsTest", produces = "application/json;charset=UTF-8")
    public void uploadHdfs() throws IOException {
        String dependFileDir = File.separator + "data" + File.separator + "deployer" + File.separator + "rule-engine-mgmt" + File.separator + "WordCount.jar";
        int index = dependFileDir.lastIndexOf(File.separator);
        String jarFileName = dependFileDir.substring(index + 1);
        HDFSClient.uploadFileFromLocation("D:\\WordCount.jar", jarFileName);
    }

    /**
     * yarn test
     *
     * @return
     */
    @GetMapping(value = "/submitJob", produces = "application/json;charset=UTF-8")
    public void submitJob() throws Exception {

        JobParamsInfo jobParamsInfo = buildJobParamsInfo();
        Optional<Pair<String, String>> appIdAndJobId = flinkYarnService.submitFlinkJob(jobParamsInfo);
        System.out.println("++++++++++++++++++++++" + appIdAndJobId);

//        String runMode = "yarn_session";
//        JobParamsInfo jobParamsInfo = JobParamsInfo.builder()
//                .runMode(runMode)
//                .build();
//        Pair<String, String> job = new Pair<>("cuiot-1", "344_Job");
//        flinkYarnService.cancelFlinkJob(jobParamsInfo, job);
    }

    public static JobParamsInfo buildJobParamsInfo() {

        //        System.setProperty("java.security.krb5.conf",
        // "/Users/maqi/tmp/hadoopconf/cdh514/krb5.conf");
        // 可执行jar包路径
        String runJarPath = "D:\\WordCount.jar";
//        String runJarPath = "hdfs://WordCount.jar";
        // 任务参数
//        String[] execArgs = new String[]{"-jobName", "flink001Submit", "--topic", "suyuTest01", "--bootstrapServers", "172.16.8.107:9092"};
//        String[] execArgs = new String[]{"-yid", "application_1631101260938_0030"};
        String[] execArgs = new String[]{"-yid", "application_1631101260938_0030"};
        //        // 任务名称
        //        String jobName = "Flink session submit";
        //        // flink 文件夹路径
        String flinkConfDir = "D:\\flink_yarn_cfg";
        //        // flink lib包路径
        //        String flinkJarPath = "/tmp/flink/flink-1.10.0/lib";
        //        //  yarn 文件夹路径
        String yarnConfDir = "D:\\flink_yarn_cfg";
        // perjob模式 运行流任务
//        String runMode = "yarn_perjob";
        // session模式 运行流任务
        String runMode = "yarn_session";
        //  作业依赖的外部文件
//        String[] dependFile = new String[]{"/tmp/flink/flink-1.10.0/README.txt"};
        // 任务提交队列
        String queue = "default";
        // yarnsession appid配置
        Properties yarnSessionConfProperties = new Properties();
//        yarnSessionConfProperties.setProperty("yid", "application_1608035000193_0003");

        // 非必要参数，可以通过shade打包指定mainClass, flink自动获取
        // String entryPointClassName = "cn.todd.flink.KafkaReader";
        String entryPointClassName = null;

        // savepoint 及并行度相关
        Properties confProperties = new Properties();
        confProperties.setProperty("parallelism", "1");

        JobParamsInfo jobParamsInfo = JobParamsInfo.builder().execArgs(execArgs)
//                .name(jobName)
                .runJarPath(runJarPath)
//                .dependFile(dependFile)
                .flinkConfDir(flinkConfDir)
                .yarnConfDir(yarnConfDir)
                .confProperties(confProperties)
                .yarnSessionConfProperties(yarnSessionConfProperties)
//                .flinkJarPath(flinkJarPath)
                .queue(queue)
                .runMode(runMode)
                .entryPointClassName(entryPointClassName)
                .build();

        return jobParamsInfo;
    }
}
