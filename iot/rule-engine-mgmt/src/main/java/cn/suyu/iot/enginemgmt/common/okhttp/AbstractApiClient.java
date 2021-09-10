package cn.suyu.iot.enginemgmt.common.okhttp;

import cn.suyu.iot.enginemgmt.common.json.Json;
import com.squareup.okhttp.*;
import com.squareup.okhttp.internal.http.HttpMethod;
import okio.BufferedSink;
import okio.Okio;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author suyu
 */
public class AbstractApiClient {
    private static final String APPLICATION_JSON = "application/json";
    private static final String CONTENT_TYPE = "Content-Type";
    private static final String DOWNLOAD_PREFIX = "download-";
    private static final String UTF_8 = "utf8";
    private static final String SEPARATION_REPLACEMENT = "%20";
    private static final String BYTE_ARRAY = "byte[]";
    private static final String CONTENT_DISPOSITION = "Content-Disposition";
    private static final String POINT = ".";
    private static final String HYPHEN = "-";
    private static final String QUESTION = "?";
    private static final String AND = "&";
    private static final String EQUALS = "=";
    private static final String APPLICATION_OCTET_STREAM = "application/octet-stream";
    private static final String COMMA = ",";
    private static final String USER_AGENT = "User-Agent";
    private static final String DOUBLE_STARS = "*/*";
    private static final int INT_204 = 204;
    private static final int INT_3 = 3;
    private static final int BEGIN_INDEX = 0;
    private static final String MULTIPART_FORM_DATA = "multipart/form-data";
    private static final String APPLICATION_X_WWW_FORM_URLENCODED = "application/x-www-form-urlencoded";
    private static final String DELETE_METHOD = "DELETE";
    private static final String EMPTY_CONTENT = "";
    public static final String FILENAME_REGEX = "filename=['\"]?([^'\"\\s]+)['\"]?";
    protected OkHttpClient httpClient;
    protected Json json;
    protected String basePath;
    private Map<String, String> defaultHeaderMap = new HashMap<String, String>();
    private String tempFolderPath = null;

    /**
     * Get HTTP client
     *
     * @return An instance of OkHttpClient
     */
    public OkHttpClient getHttpClient() {
        return httpClient;
    }

    /**
     * Set the User-Agent header's value (by adding to the default header map).
     *
     * @param userAgent HTTP request's user agent
     * @return ApiClient
     */
    protected AbstractApiClient setUserAgent(String userAgent) {
        addDefaultHeader(USER_AGENT, userAgent);
        return this;
    }

    /**
     * Add a default header.
     *
     * @param key   The header's key
     * @param value The header's value
     * @return ApiClient
     */
    private AbstractApiClient addDefaultHeader(String key, String value) {
        defaultHeaderMap.put(key, value);
        return this;
    }

    /**
     * Format the given parameter object into string.
     *
     * @param param Parameter
     * @return String representation of the parameter
     */
    private String parameterToString(Object param) {
        if (param == null) {
            return EMPTY_CONTENT;
        } else if (param instanceof Date
                || param instanceof OffsetDateTime
                || param instanceof LocalDate) {
            // Serialize to json string and remove the " enclosing characters
            String jsonStr = json.serialize(param);
            return jsonStr.substring(1, jsonStr.length() - 1);
        } else if (param instanceof Collection) {
            StringBuilder b = new StringBuilder();
            for (Object o : (Collection) param) {
                if (b.length() > 0) {
                    b.append(COMMA);
                }
                b.append(o);
            }
            return b.toString();
        } else {
            return String.valueOf(param);
        }
    }

    /**
     * Formats the specified query parameter to a list containing a single {@code Pair} object.
     *
     * <p>Note that {@code value} must not be a collection.
     *
     * @param name  The name of the parameter.
     * @param value The value of the parameter.
     * @return A list containing a single {@code Pair} object.
     */
    public List<Pair> parameterToPair(String name, Object value) {
        List<Pair> params = new ArrayList<Pair>();

        // preconditions
        if (name == null || name.isEmpty() || value == null || value instanceof Collection) {
            return params;
        }

        params.add(new Pair(name, parameterToString(value)));
        return params;
    }

    /**
     * Sanitize filename by removing path. e.g. ../../sun.gif becomes sun.gif
     *
     * @param filename The filename to be sanitized
     * @return The sanitized filename
     */
    private String sanitizeFilename(String filename) {
        return filename.replaceAll(".*[/\\\\]", EMPTY_CONTENT);
    }

    private boolean isJsonMime(String mime) {
        String jsonMime = "(?i)^(application/json|[^;/ \t]+/[^;/ \t]+[+]json)[ \t]*(;.*)?$";
        return mime != null && (mime.matches(jsonMime) || mime.equals("*/*"));
    }

    public String selectHeaderAccept(String[] accepts) {
        if (accepts.length == 0) {
            return null;
        }
        for (String accept : accepts) {
            if (isJsonMime(accept)) {
                return accept;
            }
        }
        return StringUtil.join(accepts, COMMA);
    }

    public String selectHeaderContentType(String[] contentTypes) {
        if (contentTypes.length == 0 || DOUBLE_STARS.equals(contentTypes[0])) {
            return APPLICATION_JSON;
        }
        for (String contentType : contentTypes) {
            if (isJsonMime(contentType)) {
                return contentType;
            }
        }
        return contentTypes[0];
    }

    public String escapeString(String str) {
        try {
            return URLEncoder.encode(str, UTF_8).replaceAll("\\+", SEPARATION_REPLACEMENT);
        } catch (UnsupportedEncodingException e) {
            return str;
        }
    }

    @SuppressWarnings("unchecked")
    private <T> T deserialize(Response response, Type returnType) throws ApiException {
        if (response == null || returnType == null) {
            return null;
        }

        if (BYTE_ARRAY.equals(returnType.toString())) {
            // Handle binary response (byte array).
            try {
                return (T) response.body().bytes();
            } catch (IOException e) {
                throw new ApiException(e);
            }
        } else if (returnType.equals(File.class)) {
            // Handle file downloading.
            return (T) downloadFileFromResponse(response);
        }

        String respBody;
        try {
            if (response.body() != null) {
                respBody = response.body().string();
            } else {
                respBody = null;
            }
        } catch (IOException e) {
            throw new ApiException(e);
        }

        if (respBody == null || EMPTY_CONTENT.equals(respBody)) {
            return null;
        }

        String contentType = response.headers().get(CONTENT_TYPE);
        if (contentType == null) {
            // ensuring a default content type
            contentType = APPLICATION_JSON;
        }
        if (isJsonMime(contentType)) {
            return json.deserialize(respBody, returnType);
        } else if (returnType.equals(String.class)) {
            // Expecting string, return the raw response body.
            return (T) respBody;
        } else {
            throw new ApiException(
                    "Content type \"" + contentType + "\" is not supported for type: " + returnType,
                    response.code(),
                    response.headers().toMultimap(),
                    respBody);
        }
    }

    private RequestBody serialize(Object obj, String contentType) throws ApiException {
        if (obj instanceof byte[]) {
            // Binary (byte array) body parameter support.
            return RequestBody.create(MediaType.parse(contentType), (byte[]) obj);
        } else if (obj instanceof File) {
            // File body parameter support.
            return RequestBody.create(MediaType.parse(contentType), (File) obj);
        } else if (isJsonMime(contentType)) {
            String content;
            if (obj != null) {
                content = json.serialize(obj);
            } else {
                content = null;
            }
            return RequestBody.create(MediaType.parse(contentType), content);
        } else {
            throw new ApiException("Content type \"" + contentType + "\" is not supported");
        }
    }

    private File downloadFileFromResponse(Response response) throws ApiException {
        try {
            File file = prepareDownloadFile(response);
            BufferedSink sink = Okio.buffer(Okio.sink(file));
            sink.writeAll(response.body().source());
            sink.close();
            return file;
        } catch (IOException e) {
            throw new ApiException(e);
        }
    }

    private File prepareDownloadFile(Response response) throws IOException {
        String filename = null;
        String contentDisposition = response.header(CONTENT_DISPOSITION);
        if (contentDisposition != null && !EMPTY_CONTENT.equals(contentDisposition)) {
            // Get filename from the Content-Disposition header.
            Pattern pattern = Pattern.compile(FILENAME_REGEX);
            Matcher matcher = pattern.matcher(contentDisposition);
            if (matcher.find()) {
                filename = sanitizeFilename(matcher.group(1));
            }
        }

        String prefix = null;
        String suffix = null;
        if (filename == null) {
            prefix = DOWNLOAD_PREFIX;
            suffix = EMPTY_CONTENT;
        } else {
            int pos = filename.lastIndexOf(POINT);
            if (pos == -1) {
                prefix = filename + HYPHEN;
            } else {
                prefix = filename.substring(BEGIN_INDEX, pos) + HYPHEN;
                suffix = filename.substring(pos);
            }
            // File.createTempFile requires the prefix to be at least three characters long
            if (prefix.length() < INT_3) {
                prefix = DOWNLOAD_PREFIX;
            }
        }

        if (tempFolderPath == null) {
            return File.createTempFile(prefix, suffix);
        } else {
            return File.createTempFile(prefix, suffix, new File(tempFolderPath));
        }
    }

    public <T> ApiResponse<T> execute(Call call) throws ApiException {
        return execute(call, null);
    }

    public <T> ApiResponse<T> execute(Call call, Type returnType) throws ApiException {
        try {
            Response response = call.execute();
            T data = handleResponse(response, returnType);
            return new ApiResponse<T>(response.code(), response.headers().toMultimap(), data);
        } catch (IOException e) {
            throw new ApiException(e);
        }
    }

    public <T> void executeAsync(
            Call call, cn.suyu.iot.enginemgmt.common.okhttp.ApiCallback<T> callback) {
        executeAsync(call, null, callback);
    }

    public <T> void executeAsync(Call call, Type returnType, ApiCallback<T> callback) {
        call.enqueue(
                new Callback() {
                    @Override
                    public void onFailure(Request request, IOException e) {
                        callback.onFailure(new ApiException(e), 0, null);
                    }

                    @Override
                    public void onResponse(Response response) throws IOException {
                        T result;
                        try {
                            result = (T) handleResponse(response, returnType);
                        } catch (ApiException e) {
                            callback.onFailure(e, response.code(), response.headers().toMultimap());
                            return;
                        }
                        callback.onSuccess(result, response.code(), response.headers().toMultimap());
                    }
                });
    }

    private <T> T handleResponse(Response response, Type returnType) throws ApiException {
        if (response.isSuccessful()) {
            if (returnType == null || response.code() == INT_204) {
                // returning null if the returnType is not defined,
                // or the status code is 204 (No Content)
                if (response.body() != null) {
                    try {
                        response.body().close();
                    } catch (IOException e) {
                        throw new ApiException(
                                response.message(), e, response.code(), response.headers().toMultimap());
                    }
                }
                return null;
            } else {
                return deserialize(response, returnType);
            }
        } else {
            String respBody = null;
            if (response.body() != null) {
                try {
                    respBody = response.body().string();
                } catch (IOException e) {
                    throw new ApiException(
                            response.message(), e, response.code(), response.headers().toMultimap());
                }
            }
            throw new ApiException(
                    response.message(), response.code(), response.headers().toMultimap(), respBody);
        }
    }

    public Call buildCall(
            String path,
            String method,
            List<Pair> queryParams,
            List<Pair> collectionQueryParams,
            Object body,
            Map<String, String> headerParams,
            Map<String, Object> formParams,
            String[] authNames,
            ProgressRequestBody.ProgressRequestListener progressRequestListener)
            throws ApiException {
        Request request =
                buildRequest(
                        path,
                        method,
                        queryParams,
                        collectionQueryParams,
                        body,
                        headerParams,
                        formParams,
                        authNames,
                        progressRequestListener);

        return httpClient.newCall(request);
    }

    private Request buildRequest(
            String path,
            String method,
            List<Pair> queryParams,
            List<Pair> collectionQueryParams,
            Object body,
            Map<String, String> headerParams,
            Map<String, Object> formParams,
            String[] authNames,
            ProgressRequestBody.ProgressRequestListener progressRequestListener)
            throws ApiException {

        String url = buildUrl(path, queryParams, collectionQueryParams);
        Request.Builder reqBuilder = new Request.Builder().url(url);
        processHeaderParams(headerParams, reqBuilder);

        String contentType = headerParams.get(CONTENT_TYPE);
        // ensuring a default content type
        if (contentType == null) {
            contentType = APPLICATION_JSON;
        }

        RequestBody reqBody;
        if (!HttpMethod.permitsRequestBody(method)) {
            reqBody = null;
        } else if (APPLICATION_X_WWW_FORM_URLENCODED.equals(contentType)) {
            reqBody = buildRequestBodyFormEncoding(formParams);
        } else if (MULTIPART_FORM_DATA.equals(contentType)) {
            reqBody = buildRequestBodyMultipart(formParams);
        } else if (body == null) {
            if (DELETE_METHOD.equals(method)) {
                // allow calling DELETE without sending a request body
                reqBody = null;
            } else {
                // use an empty request body (for POST, PUT and PATCH)
                reqBody = RequestBody.create(MediaType.parse(contentType), EMPTY_CONTENT);
            }
        } else {
            reqBody = serialize(body, contentType);
        }

        Request request = null;

        if (progressRequestListener != null && reqBody != null) {
            ProgressRequestBody progressRequestBody =
                    new ProgressRequestBody(reqBody, progressRequestListener);
            request = reqBuilder.method(method, progressRequestBody).build();
        } else {
            request = reqBuilder.method(method, reqBody).build();
        }

        return request;
    }

    private String buildUrl(String path, List<Pair> queryParams, List<Pair> collectionQueryParams) {
        StringBuilder url = new StringBuilder();
        url.append(basePath).append(path);

        if (queryParams != null && !queryParams.isEmpty()) {
            // support (constant) query string in `path`, e.g. "/posts?draft=1"
            String prefix = path.contains(QUESTION) ? AND : QUESTION;
            for (Pair param : queryParams) {
                if (param.getValue() != null) {
                    if (prefix != null) {
                        url.append(prefix);
                        prefix = null;
                    } else {
                        url.append(AND);
                    }
                    String value = parameterToString(param.getValue());
                    url.append(escapeString(param.getName())).append(EQUALS).append(escapeString(value));
                }
            }
        }

        if (collectionQueryParams != null && !collectionQueryParams.isEmpty()) {
            String prefix = url.toString().contains(QUESTION) ? AND : QUESTION;
            for (Pair param : collectionQueryParams) {
                if (param.getValue() != null) {
                    if (prefix != null) {
                        url.append(prefix);
                        prefix = null;
                    } else {
                        url.append(AND);
                    }
                    String value = parameterToString(param.getValue());
                    // collection query parameter value already escaped as part of parameterToPairs
                    url.append(escapeString(param.getName())).append(EQUALS).append(value);
                }
            }
        }

        return url.toString();
    }

    private void processHeaderParams(Map<String, String> headerParams, Request.Builder reqBuilder) {
        for (Map.Entry<String, String> param : headerParams.entrySet()) {
            reqBuilder.header(param.getKey(), parameterToString(param.getValue()));
        }
        for (Map.Entry<String, String> header : defaultHeaderMap.entrySet()) {
            if (!headerParams.containsKey(header.getKey())) {
                reqBuilder.header(header.getKey(), parameterToString(header.getValue()));
            }
        }
    }

    private RequestBody buildRequestBodyFormEncoding(Map<String, Object> formParams) {
        FormEncodingBuilder formBuilder = new FormEncodingBuilder();
        for (Map.Entry<String, Object> param : formParams.entrySet()) {
            formBuilder.add(param.getKey(), parameterToString(param.getValue()));
        }
        return formBuilder.build();
    }

    private RequestBody buildRequestBodyMultipart(Map<String, Object> formParams) {
        MultipartBuilder mpBuilder = new MultipartBuilder().type(MultipartBuilder.FORM);
        for (Map.Entry<String, Object> param : formParams.entrySet()) {
            if (param.getValue() instanceof File) {
                File file = (File) param.getValue();
                Headers partHeaders =
                        Headers.of(
                                CONTENT_DISPOSITION,
                                "form-data; name=\"" + param.getKey() + "\"; filename=\"" + file.getName() + "\"");
                MediaType mediaType = MediaType.parse(guessContentTypeFromFile(file));
                mpBuilder.addPart(partHeaders, RequestBody.create(mediaType, file));
            } else {
                Headers partHeaders =
                        Headers.of(CONTENT_DISPOSITION, "form-data; name=\"" + param.getKey() + "\"");
                mpBuilder.addPart(
                        partHeaders, RequestBody.create(null, parameterToString(param.getValue())));
            }
        }
        return mpBuilder.build();
    }

    private String guessContentTypeFromFile(File file) {
        String contentType = URLConnection.guessContentTypeFromName(file.getName());
        if (contentType == null) {
            return APPLICATION_OCTET_STREAM;
        } else {
            return contentType;
        }
    }
}
