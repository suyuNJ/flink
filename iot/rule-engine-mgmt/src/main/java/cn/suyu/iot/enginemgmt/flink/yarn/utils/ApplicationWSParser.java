package cn.suyu.iot.enginemgmt.flink.yarn.utils;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;


/**
 * @Description AM URL解析
 */
public class ApplicationWSParser {

    public static final String AM_ROOT_TAG = "app";
    public static final String AM_STATUE = "state";
    public static final String AM_CONTAINER_LOGS_TAG = "amContainerLogs";
    public static final String AM_USER_TAG = "user";
    public static final String TRACKING_URL = "trackingUrl";

    private static final Pattern ERR_INFO_BYTE_PATTERN = Pattern.compile("(?<name>[^:]+):+\\s+[a-zA-Z\\s]+(\\d+)\\s*bytes");

    public final Map<String, String> amParams;

    public ApplicationWSParser(String jsonStr) {
        JSONObject jsonObject = JSONObject.parseObject(jsonStr);
        JSONObject rootEle = jsonObject.getJSONObject(AM_ROOT_TAG);
        amParams = rootEle.toJavaObject(Map.class);
    }

    public String getParamContent(String key) {
        return amParams.get(key);
    }

    public class LogBaseInfo {
        String name;
        String url;
        String totalBytes;

        public LogBaseInfo() {

        }

        public LogBaseInfo(String name, String url, String totalBytes) {
            this.name = name;
            this.url = url;
            this.totalBytes = totalBytes;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getTotalBytes() {
            return totalBytes;
        }

        public void setTotalBytes(String totalBytes) {
            this.totalBytes = totalBytes;
        }

        @Override
        public String toString() {
            return "LogBaseInfo{" +
                    "name='" + name + '\'' +
                    ", url='" + url + '\'' +
                    ", totalBytes=" + totalBytes +
                    '}';
        }
    }

    public class RollingBaseInfo {
        String typeName;
        List<LogBaseInfo> logs = Lists.newArrayList();
        String otherInfo;

        public RollingBaseInfo() {

        }

        public RollingBaseInfo(String typeName, List<LogBaseInfo> logs) {
            this.typeName = typeName;
            this.logs = logs;
        }

        public void addLogBaseInfo(LogBaseInfo logBaseInfo) {
            logs.add(logBaseInfo);
        }

        public String getTypeName() {
            return typeName;
        }

        public void setTypeName(String typeName) {
            this.typeName = typeName;
        }

        public List<LogBaseInfo> getLogs() {
            return logs;
        }

        public void setLogs(List<LogBaseInfo> logs) {
            this.logs = logs;
        }

        public String getOtherInfo() {
            return otherInfo;
        }

        public void setOtherInfo(String otherInfo) {
            this.otherInfo = otherInfo;
        }
    }
}
