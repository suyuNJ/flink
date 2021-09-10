package cn.suyu.iot.enginemgmt.flink.yarn.service.impl;

import cn.suyu.iot.enginemgmt.flink.yarn.service.YarnClientService;
import org.apache.hadoop.yarn.client.api.YarnClient;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.springframework.stereotype.Service;

/**
 * @Description YarnClientServiceImpl
 */
@Service
public class YarnClientServiceImpl implements YarnClientService {

    private static YarnClient yarnClient;

    @Override
    public YarnClient getYarnClient(YarnConfiguration yarnConf) {
        if (yarnClient == null) {
            synchronized (YarnClientServiceImpl.class){
                if(yarnClient == null){
                    yarnClient = createYarnClient(yarnConf);
                }
            }

        }
        YarnClient initYarnClient = YarnClient.createYarnClient();
        initYarnClient.init(yarnConf);
        initYarnClient.start();

        return initYarnClient;
    }

    public YarnClient createYarnClient(YarnConfiguration yarnConf) {
        YarnClient yarnClient = YarnClient.createYarnClient();
        yarnClient.init(yarnConf);
        yarnClient.start();

        return yarnClient;
    }
}
