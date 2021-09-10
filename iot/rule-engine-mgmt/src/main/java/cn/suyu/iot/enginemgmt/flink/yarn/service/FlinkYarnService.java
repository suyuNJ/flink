package cn.suyu.iot.enginemgmt.flink.yarn.service;

import cn.suyu.iot.enginemgmt.common.okhttp.ApiException;
import cn.suyu.iot.enginemgmt.flink.yarn.entity.JobIdName;
import cn.suyu.iot.enginemgmt.flink.yarn.entity.JobParamsInfo;
import cn.suyu.iot.enginemgmt.flink.yarn.enums.ETaskStatus;
import cn.suyu.iot.enginemgmt.yarn.model.ApplicationResponseBody;
import org.apache.commons.math3.util.Pair;

import java.util.List;
import java.util.Optional;

/**
 * @Description FlinkYarnService
 */
public interface FlinkYarnService {

  /**
   * 提交flinkJob
   * @param jobParamsInfo
   * @return
   * @throws Exception
   */
  Optional<Pair<String, String>> submitFlinkJob(JobParamsInfo jobParamsInfo) throws Exception;

  /**
   * 提交flinkJob返回JobId名称
   * @param jobParamsInfo
   * @return
   * @throws Exception
   */
  Optional<Pair<String, JobIdName>> submitFlinkJobReturnJobIdName(JobParamsInfo jobParamsInfo)
      throws Exception;

  /**
   * 取消flinkJob
   * @param jobParamsInfo
   * @param appNameAndJobName
   * @throws Exception
   */
  void cancelFlinkJob(JobParamsInfo jobParamsInfo, Pair<String, String> appNameAndJobName)
      throws Exception;

  /**
   * 根据jobname prefix关闭job
   *
   * @param jobParamsInfo
   * @param appNameAndJobNamePrefix appName appNameAndJobNamePrefix
   * @throws Exception
   */
  void cancelFlinkJobByPrefix(
      JobParamsInfo jobParamsInfo, Pair<String, String> appNameAndJobNamePrefix) throws Exception;

  /**
   * job验证
   * @param jobParamsInfo
   * @throws Exception
   */
  void jobValidate(JobParamsInfo jobParamsInfo) throws Exception;

  /**
   * 获取job状态
   *
   * @param jobParamsInfo
   * @param appIdAndJobId
   * @return
   * @throws Exception
   */
  ETaskStatus getJobStatus(JobParamsInfo jobParamsInfo, Pair<String, String> appIdAndJobId)
          throws Exception;

  /**
   * 获取状态为running的app session列表
   *
   * @return
   * @throws ApiException
   */
  List<ApplicationResponseBody.AppsBean.AppBean> getRunningApplications() throws ApiException;
}
