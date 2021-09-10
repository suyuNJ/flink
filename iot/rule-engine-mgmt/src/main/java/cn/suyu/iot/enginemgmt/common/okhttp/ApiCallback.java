package cn.suyu.iot.enginemgmt.common.okhttp;

import java.util.List;
import java.util.Map;

/**
 * @Description: ApiCallback
 */
public interface ApiCallback<T> {
    /**
     * 故障
     * @param e
     * @param statusCode
     * @param responseHeaders
     */
    void onFailure(ApiException e, int statusCode, Map<String, List<String>> responseHeaders);

    /**
     * 成功
     * @param result
     * @param statusCode
     * @param responseHeaders
     */
    void onSuccess(T result, int statusCode, Map<String, List<String>> responseHeaders);

    /**
     * 上传进度
     * @param bytesWritten
     * @param contentLength
     * @param done
     */
    void onUploadProgress(long bytesWritten, long contentLength, boolean done);

    /**
     * 下载进度
     * @param bytesRead
     * @param contentLength
     * @param done
     */
    void onDownloadProgress(long bytesRead, long contentLength, boolean done);
}
