package cn.suyu.iot.enginemgmt.flink.yarn.service;

import org.apache.hadoop.yarn.client.api.YarnClient;
import org.apache.hadoop.yarn.conf.YarnConfiguration;

/**
 * @Description FlinkYarnService
 */
public interface YarnClientService {
    /**
     * 获取yarn客户端
     * @param yarnConf
     * @return
     */
    YarnClient getYarnClient(YarnConfiguration yarnConf);
}
