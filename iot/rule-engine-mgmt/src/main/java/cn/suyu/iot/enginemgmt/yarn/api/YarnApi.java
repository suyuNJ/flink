package cn.suyu.iot.enginemgmt.yarn.api;

import cn.suyu.iot.enginemgmt.common.okhttp.ApiException;
import cn.suyu.iot.enginemgmt.common.okhttp.ApiResponse;
import cn.suyu.iot.enginemgmt.common.okhttp.Pair;
import cn.suyu.iot.enginemgmt.yarn.model.ApplicationResponseBody;
import cn.suyu.iot.enginemgmt.yarn.model.FlinkJobResponseBody;
import com.google.gson.reflect.TypeToken;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Type;
import java.util.*;

/**
 * @Description: XXX
 */
@Slf4j
@Component
public class YarnApi {
    public static final String[] localVarAccepts = {"application/json"};
    @Autowired
    private YarnApiClient yarnApiClient;

    public ApplicationResponseBody getApplications() throws ApiException {
        ApiResponse<ApplicationResponseBody> resp = getApplicationsWithHttpInfo();
        return resp.getData();
    }

    /**
     * 不带参数
     *
     * @return
     * @throws ApiException
     */
    public ApiResponse<ApplicationResponseBody> getApplicationsWithHttpInfo() throws ApiException {
        return getApplicationsWithHttpInfo(null);
    }

    public ApiResponse<ApplicationResponseBody> getApplicationsWithHttpInfo(List<Pair> localVarQueryParams) throws ApiException {
        com.squareup.okhttp.Call call = getApplicationsCall(localVarQueryParams);
        Type localVarReturnType = new TypeToken<ApplicationResponseBody>() {
        }.getType();
        return yarnApiClient.execute(call, localVarReturnType);
    }

    /**
     * 带请求param
     *
     * @param localVarQueryParams
     * @return
     * @throws ApiException
     */
    com.squareup.okhttp.Call getApplicationsCall(List<Pair> localVarQueryParams) throws ApiException {
        Object localVarPostBody = null;

        // create path and map variables
        String localVarPath = "/ws/v1/cluster/apps";

        localVarQueryParams = Optional.ofNullable(localVarQueryParams).orElseGet(() -> new ArrayList<>());
        List<Pair> localVarCollectionQueryParams = new ArrayList<>();

        Map<String, String> localVarHeaderParams = new HashMap<>();

        Map<String, Object> localVarFormParams = new HashMap<>();

        String[] localVarAccepts = {
                "application/json"
        };
        String localVarAccept = yarnApiClient.selectHeaderAccept(localVarAccepts);
        if (localVarAccept != null) {
            localVarHeaderParams.put("Accept", localVarAccept);
        }

        String[] localVarContentTypes = {

        };
        String localVarContentType = yarnApiClient.selectHeaderContentType(localVarContentTypes);
        localVarHeaderParams.put("Content-Type", localVarContentType);

        String[] localVarAuthNames = new String[]{};
        return yarnApiClient.buildCall(localVarPath, "GET", localVarQueryParams, localVarCollectionQueryParams, localVarPostBody, localVarHeaderParams, localVarFormParams, localVarAuthNames, null);
    }


    public FlinkJobResponseBody getJobs(String applicationId) throws ApiException {
        ApiResponse<FlinkJobResponseBody> resp = getFlinkJobsWithHttpInfo(applicationId);
        return resp.getData();
    }

    public ApiResponse<FlinkJobResponseBody> getFlinkJobsWithHttpInfo(String applicationId) throws ApiException {
        com.squareup.okhttp.Call call = getFlinkJobsCall(applicationId);
        Type localVarReturnType = new TypeToken<FlinkJobResponseBody>() {
        }.getType();
        return yarnApiClient.execute(call, localVarReturnType);
    }

    com.squareup.okhttp.Call getFlinkJobsCall(String applicationId) throws ApiException {
        Object localVarPostBody = null;

        // create path and map variables
        StringBuilder localVarPath = new StringBuilder();
        localVarPath.append("/proxy/").append(applicationId).append("/jobs/overview");

        List<Pair> localVarQueryParams = new ArrayList<>();
        List<Pair> localVarCollectionQueryParams = new ArrayList<>();

        Map<String, String> localVarHeaderParams = new HashMap<>();

        Map<String, Object> localVarFormParams = new HashMap<>();

        String[] localVarAccepts = {
                "application/json"
        };
        String localVarAccept = yarnApiClient.selectHeaderAccept(localVarAccepts);
        if (localVarAccept != null) localVarHeaderParams.put("Accept", localVarAccept);

        String[] localVarContentTypes = {

        };
        String localVarContentType = yarnApiClient.selectHeaderContentType(localVarContentTypes);
        localVarHeaderParams.put("Content-Type", localVarContentType);

        String[] localVarAuthNames = new String[]{};
        return yarnApiClient.buildCall(localVarPath.toString(), "GET", localVarQueryParams, localVarCollectionQueryParams, localVarPostBody, localVarHeaderParams, localVarFormParams, localVarAuthNames, null);
    }

}
