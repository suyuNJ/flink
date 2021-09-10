package cn.suyu.iot.enginemgmt.yarn.api;

import cn.suyu.iot.enginemgmt.common.json.Json;
import cn.suyu.iot.enginemgmt.common.okhttp.AbstractApiClient;
import com.squareup.okhttp.OkHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * @Description YarnApiClient
 */
@Service
public class YarnApiClient extends AbstractApiClient {

    /**
     * Constructor for ApiClient
     */
    public YarnApiClient(@Value("${flink.yarn.rest.url}") String yarnBasePath) {
        httpClient = new OkHttpClient();

        json = new Json();

        basePath = yarnBasePath;

        setUserAgent("Swagger-Codegen/1.0.0/java");
    }
}
