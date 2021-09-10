package cn.suyu.iot.enginemgmt.yarn.init;

import cn.suyu.iot.enginemgmt.hadoop.HDFSClient;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Iterator;

/**
 * @Description UploadFlinkConf
 */
@Component
@Slf4j
public class UploadFlinkConf implements CommandLineRunner {
    /**
     * kafka conf key
     */
    public static final String BOOTSTRAP_SERVERS_KEY = "bootstrap.servers";

    public static final String MAX_POLL_RECORDS_KEY = "max.poll.records";

    public static final String AUTO_COMMIT_INTERVAL_MS_KEY = "auto.commit.interval.ms";

    public static final String FETCH_MAX_BYTES_KEYf = "fetch.max.bytes";
    StringBuilder flinkProperties;
    @Value("${flinkProperties.json.context}")
    private String flinkPropertiesJsonContext;
    private String lineSeparator = System.getProperty("line.separator");

    @Value("${isLocal:false}")
    private boolean isLocal;


    @PostConstruct
    private void init() {
        flinkProperties = new StringBuilder();
    }

    @Override
    public void run(String... args) throws Exception {
        if (isLocal) {
            return;
        }
        log.info(flinkPropertiesJsonContext);
        JSONObject jsonObject = JSONObject.parseObject(flinkPropertiesJsonContext);
        analysisJson(jsonObject, "");

        String flinkPropertiesContext = flinkProperties.toString();
        log.info("flinkPropertiesContext: {}", flinkPropertiesContext);
        InputStream inputStream = getStringStream(flinkProperties.toString());
        HDFSClient.uploadFileFromInputStream(inputStream, "flink.properties");
        log.info("upload flink properties conf finished!");
    }

    private void appendPropertyLine(String property) {
        flinkProperties.append(property);
        flinkProperties.append(lineSeparator);
    }

    private InputStream getStringStream(String inputString) {
        if (StringUtils.isEmpty(inputString)) {
            return null;
        }
        ByteArrayInputStream inputStringStream = null;
        try {
            inputStringStream = new ByteArrayInputStream(inputString.getBytes());
        } catch (Exception e) {
            log.error("getStringStream fails: ", e);
        }
        return inputStringStream;
    }

    private void analysisJson(Object objJson, String flag) {
        //如果obj为json数组
        if (objJson instanceof JSONArray) {
            JSONArray objArray = (JSONArray) objJson;
            for (int i = 0; i < objArray.size(); i++) {
                analysisJson(objArray.get(i), flag);
            }
        }
        //如果为json对象
        else if (objJson instanceof JSONObject) {
            JSONObject jsonObject = (JSONObject) objJson;
            Iterator it = jsonObject.keySet().iterator();
            while (it.hasNext()) {
                String key = it.next().toString();
                Object object = jsonObject.get(key);
                //如果得到的是数组
                if (object instanceof JSONArray) {
                    JSONArray objArray = (JSONArray) object;
                    String path = "";
                    if (StringUtils.isNotBlank(flag)) {
                        path = flag.trim() + "." + key.trim();
                    } else {
                        path = key;
                    }
                    analysisJson(objArray, path);
                }
                //如果key中是一个json对象
                else if (object instanceof JSONObject) {
                    String path = "";
                    if (StringUtils.isNotBlank(flag)) {
                        path = flag.trim() + "." + key.trim();
                    } else {
                        path = key;
                    }
                    analysisJson((JSONObject) object, path);
                }
                //如果key中是其他
                else {
                    String path = "";
                    if (StringUtils.isNotBlank(flag)) {
                        path = flag.trim() + "." + key.trim();
                    } else {
                        path = key;
                    }
                    String pathPropertyLine = path.trim() + "=" + object.toString() + " ";
                    appendPropertyLine(pathPropertyLine.trim());
                }
            }
        } else {
            //如果key中是其他
            String flagPropertyLine = flag.trim() + "=" + objJson.toString() + " ";
            appendPropertyLine(flagPropertyLine.trim());
        }
    }
}
