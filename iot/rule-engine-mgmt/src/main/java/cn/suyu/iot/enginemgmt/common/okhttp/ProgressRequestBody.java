package cn.suyu.iot.enginemgmt.common.okhttp;

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.RequestBody;
import okio.*;

import java.io.IOException;

/**
 * @Description ProgressRequestBody
 */
public class ProgressRequestBody extends RequestBody {
    private final RequestBody requestBody;
    private final ProgressRequestListener progressListener;

    public ProgressRequestBody(RequestBody requestBody, ProgressRequestListener progressListener) {
        this.requestBody = requestBody;
        this.progressListener = progressListener;
    }

    @Override
    public MediaType contentType() {
        return requestBody.contentType();
    }

    @Override
    public long contentLength() throws IOException {
        return requestBody.contentLength();
    }

    @Override
    public void writeTo(BufferedSink sink) throws IOException {
        BufferedSink bufferedSink = Okio.buffer(sink(sink));
        requestBody.writeTo(bufferedSink);
        bufferedSink.flush();
    }

    private Sink sink(Sink sink) {
        return new ForwardingSink(sink) {

            long bytesWritten = 0L;
            long contentLength = 0L;

            @Override
            public void write(Buffer source, long byteCount) throws IOException {
                super.write(source, byteCount);
                if (contentLength == 0) {
                    contentLength = contentLength();
                }

                bytesWritten += byteCount;
                progressListener.onRequestProgress(
                        bytesWritten, contentLength, bytesWritten == contentLength);
            }
        };
    }

    public interface ProgressRequestListener {
        /**
         * 请求进度
         * @param bytesWritten
         * @param contentLength
         * @param done
         */
        void onRequestProgress(long bytesWritten, long contentLength, boolean done);
    }
}
