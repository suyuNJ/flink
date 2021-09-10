package cn.suyu.iot.enginemgmt.flink.yarn.service.impl;

import cn.suyu.iot.enginemgmt.common.RuleEngineException;
import cn.suyu.iot.enginemgmt.common.asserts.AbstractRuleEngineAssert;
import cn.suyu.iot.enginemgmt.common.okhttp.ApiException;
import cn.suyu.iot.enginemgmt.common.okhttp.ApiResponse;
import cn.suyu.iot.enginemgmt.code.RuleEngineCode;
import cn.suyu.iot.enginemgmt.flink.yarn.entity.AppIdName;
import cn.suyu.iot.enginemgmt.flink.yarn.entity.JobIdName;
import cn.suyu.iot.enginemgmt.flink.yarn.entity.JobParamsInfo;
import cn.suyu.iot.enginemgmt.flink.yarn.enums.ERunMode;
import cn.suyu.iot.enginemgmt.flink.yarn.enums.ETaskStatus;
import cn.suyu.iot.enginemgmt.flink.yarn.executor.YarnJobClusterExecutor;
import cn.suyu.iot.enginemgmt.flink.yarn.executor.YarnSessionClusterExecutor;
import cn.suyu.iot.enginemgmt.flink.yarn.service.FlinkYarnService;
import cn.suyu.iot.enginemgmt.yarn.api.YarnApi;
import cn.suyu.iot.enginemgmt.yarn.enums.ApplicationStateEnum;
import cn.suyu.iot.enginemgmt.yarn.model.ApplicationResponseBody;
import cn.suyu.iot.enginemgmt.yarn.model.FlinkJobResponseBody;
import org.apache.commons.math3.util.Pair;
import org.apache.flink.client.deployment.ClusterRetrieveException;
import org.apache.flink.util.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static cn.suyu.iot.enginemgmt.flink.yarn.utils.JobGraphBuildUtil.DEFAULT_FLINK_CONF_DIR;


/**
 * @Description flink yarn service implementation
 */
@Service
public class FlinkYarnServiceImpl implements FlinkYarnService {
  private static final Logger LOG = LoggerFactory.getLogger(FlinkYarnServiceImpl.class);

  @Autowired
  private YarnApi yarnApi;

  private static String getRunJarLibPath() {
    StringBuilder libBasePath = new StringBuilder(System.getProperty("user.dir"));
    String runJarLibPath =
            libBasePath.append(File.separator).append("lib").append(File.separator).toString();
    LOG.info("run jar lib path: {}", runJarLibPath);
    return runJarLibPath;
  }

  private static String getRunJarLibFilePath(String jarNamePrefix) throws IOException {
    String runJarLibPath = getRunJarLibPath();
    File file;
    File[] filterFileList = null;
    try {
      file = new File(runJarLibPath);
      // Create a FilenameFilter object
      FileFilter fileFilter = file1 -> file1.getName().startsWith(jarNamePrefix);
      filterFileList = file.listFiles(fileFilter);
      AbstractRuleEngineAssert.isTrue(
              filterFileList.length > 0,
              RuleEngineCode.GET_FLINK_RUN_JAR_FAILS.getCode(),
              RuleEngineCode.GET_FLINK_RUN_JAR_FAILS.getMessage());
    } catch (Exception e) {
      LOG.error("An error occurred when getRunJarLibFilePath.", e);
      throw e;
    }

    return filterFileList[0].getCanonicalPath();
  }

  /**
   * 根据prefix获取jar全路径
   *
   * @param jobParamsInfo
   * @throws IOException
   */
  public static void transformRunJarPath(JobParamsInfo jobParamsInfo) throws IOException {
    String runJarPath = jobParamsInfo.getRunJarPath();
    runJarPath = getRunJarLibFilePath(runJarPath);
    jobParamsInfo.setRunJarPath(runJarPath);
  }

  // todo test STANDALONE
  @Override
  public ETaskStatus getJobStatus(JobParamsInfo jobParamsInfo, Pair<String, String> appIdAndJobId)
          throws Exception {
    String yarnApplicationId = appIdAndJobId.getFirst();
    String jobId = appIdAndJobId.getSecond();

    ERunMode runMode = ERunMode.convertFromString(jobParamsInfo.getRunMode());
    ETaskStatus jobStatus;
    switch (runMode) {
      case YARN_SESSION:
        Pair<String, List<FlinkJobResponseBody.JobsBean>> appIdAndJobsBeans =
                getAppIdAndJobIdsByAppNameAndJobName(appIdAndJobId);
        String yarnApplicationIdByName = appIdAndJobsBeans.getFirst();
        Preconditions.checkNotNull(yarnApplicationId, "application id not null!");
        List<FlinkJobResponseBody.JobsBean> jobsBeans = appIdAndJobsBeans.getSecond();
        LOG.info(
            "getJobStatus, appIdAndJobId: {}, appIdAndJobsBeans", appIdAndJobId, appIdAndJobsBeans);
        AbstractRuleEngineAssert.notEmpty(
                jobsBeans,
                RuleEngineCode.CANNOT_GET_RUNNING_STATUS_JOB.getCode(),
                RuleEngineCode.CANNOT_GET_RUNNING_STATUS_JOB.getMessage());
        String jobIdByName = jobsBeans.get(0).getJid();
        Preconditions.checkNotNull(jobIdByName, "job id not null!");
        jobStatus =
            new YarnSessionClusterExecutor(jobParamsInfo)
                .getJobStatus(yarnApplicationIdByName, jobIdByName);
        break;
      case YARN_PERJOB:
        jobStatus =
            new YarnJobClusterExecutor(jobParamsInfo).getJobStatus(yarnApplicationId, jobId);
        break;
      case STANDALONE:
//        jobStatus = new StandaloneExecutor(jobParamsInfo).getJobStatus(yarnApplicationId, jobId);
      default:
        throw new RuntimeException("Unsupported operating mode, yarnSession,yarnPer");
    }
    return jobStatus;
  }

  @Override
  public Optional<Pair<String, String>> submitFlinkJob(JobParamsInfo jobParamsInfo)
      throws Exception {
    Optional<Pair<String, String>> appIdAndJobId = Optional.empty();
    ERunMode runMode = ERunMode.convertFromString(jobParamsInfo.getRunMode());
    switch (runMode) {
      case YARN_SESSION:
        appIdAndJobId = yarnSessionSubmit(jobParamsInfo);
        break;
      case YARN_PERJOB:
        appIdAndJobId = new YarnJobClusterExecutor(jobParamsInfo).submit();
        break;
      case STANDALONE:
//        new StandaloneExecutor(jobParamsInfo).submit();
        break;
      default:
        throw new RuntimeException(
            "Unsupported operating mode, support YARN_SESSION,YARN_SESSION,STANDALONE");
    }
    return appIdAndJobId;
  }

  @Override
  public Optional<Pair<String, JobIdName>> submitFlinkJobReturnJobIdName(
      JobParamsInfo jobParamsInfo) throws Exception {
    Optional<Pair<String, JobIdName>> appIdAndJobNameId = Optional.empty();
    appIdAndJobNameId = yarnSessionSubmitReturnJobIdName(jobParamsInfo, appIdAndJobNameId);
    return appIdAndJobNameId;
  }

  private Optional<Pair<String, JobIdName>> yarnSessionSubmitReturnJobIdName(
      JobParamsInfo jobParamsInfo, Optional<Pair<String, JobIdName>> appIdAndJobNameId)
      throws Exception {
    Optional<Pair<AppIdName, JobIdName>> appNameIdAndJobNameId =
        getAppNameIdAndJobNameId(jobParamsInfo);
    AppIdName appIdName = appNameIdAndJobNameId.get().getFirst();
    String appName = appIdName.getAppName();
    JobIdName jobIdName = appNameIdAndJobNameId.get().getSecond();
    // 实际返回appName和jobName
    appIdAndJobNameId = Optional.of(new Pair<>(appName, jobIdName));
    return appIdAndJobNameId;
  }

  private Optional<Pair<String, String>> yarnSessionSubmit(JobParamsInfo jobParamsInfo)
          throws Exception {
    Optional<Pair<AppIdName, JobIdName>> appNameIdAndJobNameId =
            getAppNameIdAndJobNameId(jobParamsInfo);
    AppIdName appIdName = appNameIdAndJobNameId.get().getFirst();
    String appName = appIdName.getAppName();
    JobIdName jobIdName = appNameIdAndJobNameId.get().getSecond();
    String jobName = jobIdName.getJobName();
    // 实际返回appName和jobName
    Optional<Pair<String, String>> appIdAndJobId = Optional.of(new Pair<>(appName, jobName));

    return appIdAndJobId;
  }

  private Optional<Pair<AppIdName, JobIdName>> getAppNameIdAndJobNameId(JobParamsInfo jobParamsInfo)
          throws Exception {
    String appId = yarnSessionBaseSubmit(jobParamsInfo);
    Optional<Pair<String, String>> appIdAndJobId =
            new YarnSessionClusterExecutor(jobParamsInfo).submit();
    String jobId = appIdAndJobId.get().getSecond();
    AbstractRuleEngineAssert.notEmpty(
            jobId,
            RuleEngineCode.CANNOT_GET_JOB_ID.getCode(),
            RuleEngineCode.CANNOT_GET_JOB_ID.getMessage());
    String jobName = getJobNameByJobId(jobId, appId);

    AbstractRuleEngineAssert.notEmpty(
            jobName,
            RuleEngineCode.CANNOT_GET_JOB_NAME.getCode(),
            RuleEngineCode.CANNOT_GET_JOB_NAME.getMessage());
    String appName = getAppNameByAppId(appId);
    AbstractRuleEngineAssert.notEmpty(
            appName,
            RuleEngineCode.CANNOT_GET_APPLICATION_NAME.getCode(),
            RuleEngineCode.CANNOT_GET_APPLICATION_NAME.getMessage());
    AppIdName appIdName = AppIdName.builder().appId(appId).appName(appName).build();
    JobIdName jobIdName = JobIdName.builder().jobId(jobId).jobName(jobName).build();

    return Optional.of(new Pair<>(appIdName, jobIdName));
  }

  private String yarnSessionBaseSubmit(JobParamsInfo jobParamsInfo) throws Exception {
    String appId;
    Properties yarnSessionConfProperties = jobParamsInfo.getYarnSessionConfProperties();
    if (yarnSessionConfProperties == null || yarnSessionConfProperties.getProperty("yid") == null) {
      appId = getRunningApplication();
      yarnSessionConfProperties = new Properties();
      yarnSessionConfProperties.setProperty("yid", appId);
      //                    yarnSessionConfProperties.setProperty("yid",
      // "application_1608210828479_0010");
      jobParamsInfo.setYarnSessionConfProperties(yarnSessionConfProperties);
    } else {
      appId = yarnSessionConfProperties.getProperty("yid");
    }

    String[] execArgs = jobParamsInfo.getExecArgs();
    AbstractRuleEngineAssert.isTrue(
            execArgs != null && execArgs.length > 1,
            RuleEngineCode.EXEC_ARGS_CANNOT_EMPTY.getCode(),
            RuleEngineCode.EXEC_ARGS_CANNOT_EMPTY.getMessage());

    // API提交flink session job
//    transformRunJarPath(jobParamsInfo);

    return appId;
  }

  @Override
  public void cancelFlinkJob(JobParamsInfo jobParamsInfo, Pair<String, String> appNameAndJobName)
      throws Exception {
    //        Pair<String, String> appIdAndJobId;
    Pair<String, List<FlinkJobResponseBody.JobsBean>> appIdAndJobsBeans;
    try {
      //            appIdAndJobId = getAppIdAndJobIdByAppNameAndJobName(appNameAndJobName);
      appIdAndJobsBeans = getAppIdAndJobIdsByAppNameAndJobName(appNameAndJobName);
    } catch (RuleEngineException e) {
      LOG.error("getAppIdAndJobIdByAppNameAndJobName error:", e);
      return;
    }
    cancelExecute(jobParamsInfo, appIdAndJobsBeans);
  }

  private void cancelExecute(
      JobParamsInfo jobParamsInfo,
      Pair<String, List<FlinkJobResponseBody.JobsBean>> appIdAndJobsBeans)
      throws ClusterRetrieveException {
    String yarnApplicationId = appIdAndJobsBeans.getFirst();
    List<FlinkJobResponseBody.JobsBean> jobsBeans = appIdAndJobsBeans.getSecond();

    // cancel所有job
    for (FlinkJobResponseBody.JobsBean jobsBean : jobsBeans) {
      Preconditions.checkNotNull(yarnApplicationId, "application id not null!");
      String jobId = jobsBean.getJid();
      Preconditions.checkNotNull(jobId, "job  id not null!");

      ERunMode runMode = ERunMode.convertFromString(jobParamsInfo.getRunMode());
      jobParamsInfo.setFlinkConfDir(DEFAULT_FLINK_CONF_DIR);
      jobParamsInfo.setYarnConfDir(DEFAULT_FLINK_CONF_DIR);
      switch (runMode) {
        case YARN_SESSION:
          new YarnSessionClusterExecutor(jobParamsInfo).cancel(yarnApplicationId, jobId);
          break;
        case YARN_PERJOB:
          new YarnJobClusterExecutor(jobParamsInfo).cancel(yarnApplicationId, jobId);
          break;
        case STANDALONE:
//          new StandaloneExecutor(jobParamsInfo).cancel(yarnApplicationId, jobId);
        default:
          throw new RuntimeException("Unsupported operating mode, yarnSession,yarnPer");
      }
    }
  }

  @Override
  public void cancelFlinkJobByPrefix(
      JobParamsInfo jobParamsInfo, Pair<String, String> appNameAndJobNamePrefix) throws Exception {
    Pair<String, List<FlinkJobResponseBody.JobsBean>> appIdAndJobsBeans;
    try {
      appIdAndJobsBeans = getAppIdAndJobIdsByAppNameAndJobNamePrefix(appNameAndJobNamePrefix);
    } catch (RuleEngineException e) {
      LOG.error("getAppIdAndJobIdByAppNameAndJobNamePrefix error:", e);
      return;
    }
    cancelExecute(jobParamsInfo, appIdAndJobsBeans);
  }

  @Override
  public void jobValidate(JobParamsInfo jobParamsInfo) throws Exception {
    new YarnSessionClusterExecutor(jobParamsInfo).jobValidateAndGetJobGraph();
  }

  private String getRunningApplication() throws ApiException {
    List<ApplicationResponseBody.AppsBean.AppBean> runningAppBeans = getRunningAppBeans();
    LOG.info("running session:", runningAppBeans);
    int runningAppSize = runningAppBeans.size();
    AbstractRuleEngineAssert.isTrue(
            runningAppSize > 0,
            RuleEngineCode.NO_RUNNING_APPLICATIONS.getCode(),
            RuleEngineCode.NO_RUNNING_APPLICATIONS.getMessage());
    Random random = new Random();
    int appIdNum = random.nextInt(runningAppSize);
    return runningAppBeans.get(appIdNum).getId();
  }

  @Override
  public List<ApplicationResponseBody.AppsBean.AppBean> getRunningApplications() throws ApiException {
    List<ApplicationResponseBody.AppsBean.AppBean> runningAppBeans = getRunningAppBeans();
    LOG.info("getRunningApplications, running session:", runningAppBeans);
    return runningAppBeans;
  }

  /**
   * 根据id获取name 提交job场景
   *
   * @param appIdAndJobId
   * @return
   * @throws ApiException
   */
  private Pair<String, String> getAppNameAndJobNameByAppIdAndJobId(
          Pair<String, String> appIdAndJobId) throws ApiException {
    String appId = appIdAndJobId.getFirst();
    String appName = getAppNameByAppId(appId);
    String jobId = appIdAndJobId.getSecond();
    String jobName = getJobNameByJobId(jobId, appId);

    return new Pair<>(appName, jobName);
  }

  /**
   * 根据name获取id 取消job场景
   *
   * @param appNameAndJobName
   * @return
   * @throws ApiException
   */
  private Pair<String, String> getAppIdAndJobIdByAppNameAndJobName(
      Pair<String, String> appNameAndJobName) throws ApiException {
    String appName = appNameAndJobName.getFirst();
    String appId = getAppIdByAppName(appName);
    String jobName = appNameAndJobName.getSecond();
    String jobId = getJobIdByJobName(jobName, appId);

    return new Pair<>(appId, jobId);
  }

  /**
   * 根据name获取id 取消job场景
   *
   * @param appNameAndJobName
   * @return
   * @throws ApiException
   */
  private Pair<String, List<FlinkJobResponseBody.JobsBean>> getAppIdAndJobIdsByAppNameAndJobName(
      Pair<String, String> appNameAndJobName) throws ApiException {
    String appName = appNameAndJobName.getFirst();
    String appId = getAppIdByAppName(appName);
    String jobName = appNameAndJobName.getSecond();
    List<FlinkJobResponseBody.JobsBean> jobsBeans = getJobIdsByJobName(jobName, appId);

    return new Pair<>(appId, jobsBeans);
  }

  private Pair<String, List<FlinkJobResponseBody.JobsBean>>
      getAppIdAndJobIdsByAppNameAndJobNamePrefix(Pair<String, String> appNameAndJobName)
          throws ApiException {
    String appName = appNameAndJobName.getFirst();
    String appId = getAppIdByAppName(appName);
    String jobNamePrefix = appNameAndJobName.getSecond();
    List<FlinkJobResponseBody.JobsBean> jobsBeans = getJobIdsByJobNamePrefix(jobNamePrefix, appId);

    return new Pair<>(appId, jobsBeans);
  }

  private String getAppIdByAppName(String appName) throws ApiException {

    AbstractRuleEngineAssert.notEmpty(
            appName,
            RuleEngineCode.APP_NAME_EMPTY.getCode(),
            RuleEngineCode.APP_NAME_EMPTY.getMessage());
    // 获取Application
    List<ApplicationResponseBody.AppsBean.AppBean> appBeans = getAppBeans();
    // 根据appName筛选
    List<ApplicationResponseBody.AppsBean.AppBean> filteredAppBeans =
        appBeans.stream()
            .filter(appBean -> ApplicationFilterByName(appBean, appName))
            .collect(Collectors.toList());
    // 根据appname获取的appId不唯一则报错
    AbstractRuleEngineAssert.isTrue(
            filteredAppBeans.size() == 1,
            RuleEngineCode.GET_APPLICATION_ID_BY_NAME_NOT_UNIQUE.getCode(),
            RuleEngineCode.GET_APPLICATION_ID_BY_NAME_NOT_UNIQUE.getMessage());
    String appId = filteredAppBeans.get(0).getId();
    return appId;
  }

  private boolean ApplicationFilterByName(
      ApplicationResponseBody.AppsBean.AppBean appBean, String appName) {
    return appName.equals(appBean.getName())
        && ApplicationStateEnum.RUNNING.getCode().equals(appBean.getState());
  }

  private boolean ApplicationFilterById(
      ApplicationResponseBody.AppsBean.AppBean appBean, String appId) {
    return appId.equals(appBean.getId())
        && ApplicationStateEnum.RUNNING.getCode().equals(appBean.getState());
  }

  private String getJobIdByJobName(String jobName, String appId) throws ApiException {
    AbstractRuleEngineAssert.notEmpty(
            jobName,
            RuleEngineCode.CANNOT_GET_JOB_NAME.getCode(),
            RuleEngineCode.CANNOT_GET_JOB_NAME.getMessage());
    // 获取flinkJob
    List<FlinkJobResponseBody.JobsBean> jobsBeans = getJobsBeans(appId);
    List<FlinkJobResponseBody.JobsBean> filteredJobBeans =
        jobsBeans.stream()
            .filter(jobBean -> JobFilterByName(jobBean, jobName))
            .collect(Collectors.toList());
    // 根据appname获取的appId不唯一则报错
    AbstractRuleEngineAssert.isTrue(
            filteredJobBeans.size() == 1,
            RuleEngineCode.GET_APPLICATION_ID_BY_NAME_NOT_UNIQUE.getCode(),
            RuleEngineCode.GET_APPLICATION_ID_BY_NAME_NOT_UNIQUE.getMessage());
    String jobId = filteredJobBeans.get(0).getJid();
    return jobId;
  }

  /**
   * 獲取appId 下jobName對應jobIds
   *
   * @param jobName
   * @param appId
   * @return
   * @throws ApiException
   */
  private List<FlinkJobResponseBody.JobsBean> getJobIdsByJobName(String jobName, String appId)
      throws ApiException {
    AbstractRuleEngineAssert.notEmpty(
            jobName,
            RuleEngineCode.CANNOT_GET_JOB_NAME.getCode(),
            RuleEngineCode.CANNOT_GET_JOB_NAME.getMessage());
    // 获取flinkJob
    List<FlinkJobResponseBody.JobsBean> jobsBeans = getJobsBeans(appId);
    List<FlinkJobResponseBody.JobsBean> filteredJobBeans =
        jobsBeans.stream()
            .filter(jobBean -> JobFilterByName(jobBean, jobName))
            .collect(Collectors.toList());
    // 根据appname获取的appId不唯一则报错
    //        RuleEngineAssert.isTrue(filteredJobBeans.size() == 1,
    // ResultCode.GET_JOB_ERROR_CODE.getCode(),
    //                ResultCode.GET_JOB_ERROR_CODE.getMessage());
    return filteredJobBeans;
  }

  /**
   * 獲取appId 下jobName前缀對應jobIds
   *
   * @param jobNamePrefix
   * @param appId
   * @return
   * @throws ApiException
   */
  private List<FlinkJobResponseBody.JobsBean> getJobIdsByJobNamePrefix(
      String jobNamePrefix, String appId) throws ApiException {
    AbstractRuleEngineAssert.notEmpty(
            jobNamePrefix,
            RuleEngineCode.JOB_NAME_EMPTY.getCode(),
            RuleEngineCode.JOB_NAME_EMPTY.getMessage());
    // 获取flinkJob
    List<FlinkJobResponseBody.JobsBean> jobsBeans = getJobsBeans(appId);
    List<FlinkJobResponseBody.JobsBean> filteredJobBeans =
        jobsBeans.stream()
            .filter(jobBean -> JobFilterByNamePrefix(jobBean, jobNamePrefix))
            .collect(Collectors.toList());
    return filteredJobBeans;
  }

  private boolean JobFilterByName(FlinkJobResponseBody.JobsBean jobBean, String jobName) {
    return jobName.equals(jobBean.getName())
        && ETaskStatus.RUNNING.name().equals(jobBean.getState());
  }

  private boolean JobFilterByNamePrefix(
      FlinkJobResponseBody.JobsBean jobBean, String jobNamePrefix) {
    String jobName = jobBean.getName();
    AbstractRuleEngineAssert.notNull(
            jobName,
            RuleEngineCode.CANNOT_GET_JOB_NAME.getCode(),
            RuleEngineCode.CANNOT_GET_JOB_NAME.getMessage());
    return jobName.startsWith(jobNamePrefix)
        && ETaskStatus.RUNNING.name().equals(jobBean.getState());
  }

  private boolean JobFilterById(FlinkJobResponseBody.JobsBean jobBean, String jobId) {
    return jobId.equals(jobBean.getJid()) && ETaskStatus.RUNNING.name().equals(jobBean.getState());
  }

  public String getAppNameByAppId(String appId) throws ApiException {
    AbstractRuleEngineAssert.notEmpty(
            appId, RuleEngineCode.APP_ID_EMPTY.getCode(), RuleEngineCode.APP_ID_EMPTY.getMessage());
    // 获取Application
    List<ApplicationResponseBody.AppsBean.AppBean> appBeans = getAppBeans();
    // 根据appName筛选
    List<ApplicationResponseBody.AppsBean.AppBean> filteredAppBeans =
        appBeans.stream()
            .filter(appBean -> ApplicationFilterById(appBean, appId))
            .collect(Collectors.toList());
    // 根据appname获取的appId不唯一则报错
    AbstractRuleEngineAssert.isTrue(
            filteredAppBeans.size() == 1,
            RuleEngineCode.GET_APPLICATION_ID_BY_NAME_NOT_UNIQUE.getCode(),
            RuleEngineCode.GET_APPLICATION_ID_BY_NAME_NOT_UNIQUE.getMessage());
    String appName = filteredAppBeans.get(0).getName();
    return appName;
  }

  public String getJobNameByJobId(String jobId, String appId) throws ApiException {
    AbstractRuleEngineAssert.notEmpty(
            appId, RuleEngineCode.APP_ID_EMPTY.getCode(), RuleEngineCode.APP_ID_EMPTY.getMessage());
    AbstractRuleEngineAssert.notEmpty(
            jobId,
            RuleEngineCode.CANNOT_GET_JOB_ID.getCode(),
            RuleEngineCode.CANNOT_GET_JOB_ID.getMessage());
    // 获取flinkJob
    List<FlinkJobResponseBody.JobsBean> jobsBeans = getJobsBeans(appId);
    List<FlinkJobResponseBody.JobsBean> filteredJobBeans =
        jobsBeans.stream()
            .filter(jobBean -> JobFilterById(jobBean, jobId))
            .collect(Collectors.toList());
    // 根据appname获取的appId不唯一则报错
    AbstractRuleEngineAssert.isTrue(
            filteredJobBeans.size() == 1,
            RuleEngineCode.GET_APPLICATION_ID_BY_NAME_NOT_UNIQUE.getCode(),
            RuleEngineCode.GET_APPLICATION_ID_BY_NAME_NOT_UNIQUE.getMessage());
    String jobName = filteredJobBeans.get(0).getName();
    return jobName;
  }

  private List<FlinkJobResponseBody.JobsBean> getJobsBeans(String appId) throws ApiException {
    ApiResponse<FlinkJobResponseBody> flinkJobResponseBodyApiResponse =
        yarnApi.getFlinkJobsWithHttpInfo(appId);
    AbstractRuleEngineAssert.notNull(
            flinkJobResponseBodyApiResponse,
            RuleEngineCode.FLINK_JOB_RESPONSE_EMPTY.getCode(),
            RuleEngineCode.FLINK_JOB_RESPONSE_EMPTY.getMessage());
    // 校验返回状态码
    int statusCode = flinkJobResponseBodyApiResponse.getStatusCode();
    AbstractRuleEngineAssert.isTrue(
            statusCode == 200,
            RuleEngineCode.FLINK_JOB_RESPONSE_STATUS_ERROR.getCode(),
            RuleEngineCode.FLINK_JOB_RESPONSE_STATUS_ERROR.getMessage());
    FlinkJobResponseBody flinkJobResponseBody = flinkJobResponseBodyApiResponse.getData();
    AbstractRuleEngineAssert.notNull(
            flinkJobResponseBody,
            RuleEngineCode.FLINK_JOB_RESPONSE_DATA_EMPTY.getCode(),
            RuleEngineCode.FLINK_JOB_RESPONSE_DATA_EMPTY.getMessage());
    // 校验jobs
    List<FlinkJobResponseBody.JobsBean> jobsBeans = flinkJobResponseBody.getJobs();
    AbstractRuleEngineAssert.notEmpty(
            jobsBeans,
            RuleEngineCode.GET_JOB_ERROR_CODE.getCode(),
            RuleEngineCode.GET_JOB_ERROR_CODE.getMessage());
    return jobsBeans;
  }

  private List<ApplicationResponseBody.AppsBean.AppBean> getRunningAppBeans() throws ApiException {
    List<cn.suyu.iot.enginemgmt.common.okhttp.Pair> localVarQueryParams = new ArrayList<>();
    cn.suyu.iot.enginemgmt.common.okhttp.Pair pair =
        new cn.suyu.iot.enginemgmt.common.okhttp.Pair("states", "running");
    localVarQueryParams.add(pair);
    ApiResponse<ApplicationResponseBody> applicationResponseBodyApiResponse =
        yarnApi.getApplicationsWithHttpInfo(localVarQueryParams);

    List<ApplicationResponseBody.AppsBean.AppBean> appBeans =
        validateAndGetAppBeans(applicationResponseBodyApiResponse);
    return appBeans;
  }

  private List<ApplicationResponseBody.AppsBean.AppBean> getAppBeans() throws ApiException {
    // 获取Application
    ApiResponse<ApplicationResponseBody> applicationResponseBodyApiResponse =
        yarnApi.getApplicationsWithHttpInfo();

    List<ApplicationResponseBody.AppsBean.AppBean> appBeans =
        validateAndGetAppBeans(applicationResponseBodyApiResponse);
    return appBeans;
  }

  private List<ApplicationResponseBody.AppsBean.AppBean> validateAndGetAppBeans(
      ApiResponse<ApplicationResponseBody> applicationResponseBodyApiResponse) {
    AbstractRuleEngineAssert.notNull(
            applicationResponseBodyApiResponse,
            RuleEngineCode.FLINK_APPLICATION_RESPONSE_EMPTY.getCode(),
            RuleEngineCode.FLINK_APPLICATION_RESPONSE_EMPTY.getMessage());
    // 校验返回状态码
    int statusCode = applicationResponseBodyApiResponse.getStatusCode();
    AbstractRuleEngineAssert.isTrue(
            statusCode == 200,
            RuleEngineCode.FLINK_APPLICATION_RESPONSE_STATUS_ERROR.getCode(),
            RuleEngineCode.FLINK_APPLICATION_RESPONSE_STATUS_ERROR.getMessage());

    ApplicationResponseBody applicationResponseBody = applicationResponseBodyApiResponse.getData();
    AbstractRuleEngineAssert.notNull(
            applicationResponseBody,
            RuleEngineCode.FLINK_APPLICATION_RESPONSE_DATA_EMPTY.getCode(),
            RuleEngineCode.FLINK_APPLICATION_RESPONSE_DATA_EMPTY.getMessage());
    // 校验appBeans
    ApplicationResponseBody.AppsBean apps = applicationResponseBody.getApps();
    AbstractRuleEngineAssert.notNull(
            apps,
            RuleEngineCode.GET_APPLICATION_ERROR_CODE.getCode(),
            RuleEngineCode.GET_APPLICATION_ERROR_CODE.getMessage());
    List<ApplicationResponseBody.AppsBean.AppBean> appBeans = apps.getApp();
    AbstractRuleEngineAssert.notEmpty(
            appBeans,
            RuleEngineCode.GET_APPLICATION_ERROR_CODE.getCode(),
            RuleEngineCode.GET_APPLICATION_ERROR_CODE.getMessage());

    return appBeans;
  }
}
