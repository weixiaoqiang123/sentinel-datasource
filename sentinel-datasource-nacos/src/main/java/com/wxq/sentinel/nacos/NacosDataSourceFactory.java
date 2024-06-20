package com.wxq.sentinel.nacos;

import com.alibaba.csp.sentinel.datasource.Converter;
import com.alibaba.csp.sentinel.datasource.ReadableDataSource;
import com.alibaba.csp.sentinel.datasource.WritableDataSource;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.PropertyKeyConst;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.exception.NacosException;
import com.wxq.sentinel.common.datasource.DataSourceFactory;
import com.wxq.sentinel.common.exception.SentinelException;
import com.wxq.sentinel.common.properties.NacosConfigProperties;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * @author weixiaoqiang
 * @date 2024/5/11
 **/
public class NacosDataSourceFactory extends DataSourceFactory<NacosConfigProperties> {

    private static final Logger logger = LoggerFactory.getLogger(NacosDataSourceFactory.class);

    private static final String NAMESPACES_URI = "/nacos/v1/console/namespaces";

    private final HttpClient httpClient = new HttpClient();

    private final ConfigService configService;

    private final HttpRetryExecutor retryExecutor;

    public NacosDataSourceFactory(String ruleName, Object properties) {
        super(ruleName, (NacosConfigProperties) properties);
        NacosConfigProperties props = (NacosConfigProperties) properties;
        this.retryExecutor = buildHttpRetryExecutor();
        this.configService = buildConfig(props);
        createNamespaceIfNecessary(props);
    }

    @Override
    public <T> WritableDataSource<T> createWritableDataSource(Converter<T, String> converter) {
        return new NacosWritableDataSource<>(ruleName, configService, converter, writeLock, properties);
    }

    @Override
    public <T> ReadableDataSource<String, T> createReadableDataSource(Converter<String, T> parser) {
        return new NacosReadableDataSource<>(ruleName, configService, parser, readLock, properties);
    }

    private ConfigService buildConfig(NacosConfigProperties props) {
        Properties properties = new Properties();
        properties.setProperty(PropertyKeyConst.NAMESPACE, props.getNamespace());
        properties.setProperty(PropertyKeyConst.SERVER_ADDR, props.getServerAddr());
        ConfigService configService = null;
        try {
            configService = NacosFactory.createConfigService(properties);
        } catch (NacosException e) {
            throw new SentinelException("Initialize nacos failed", e);
        }
        return configService;
    }

    private void createNamespaceIfNecessary(NacosConfigProperties properties) {
        List<String> namespaces = getNamespaces();
        String namespace = properties.getNamespace();
        if(!namespaces.contains(namespace)) {
            logger.info("The sentinel namespace was not found, create a new namespace, the name is: {}", namespace);
            createNamespace(namespace);
        }
    }

    private void createNamespace(String namespace) throws SentinelException {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "x-www-form-urlencoded");

        JSONObject requestBody = new JSONObject();
        requestBody.put("customNamespaceId", namespace);
        requestBody.put("namespaceName", namespace);
        Response response = retryExecutor.execute(() -> {
            String createNamespaceUrl = getUrl(NAMESPACES_URI);
            return httpClient.sendPost(createNamespaceUrl, requestBody.toString(), headers);
        });

        int code = response.code();
        String message = response.message();
        if(response.isSuccessful()) {
            try {
                String body = new String(response.body().string());
                if(body.equals(Boolean.TRUE.toString())) {
                    logger.info("create sentinel namespace successful, namespace: " + namespace);
                } else {
                    logger.warn("The namespace has exists");
                }
            } catch (IOException ignore) {
                logger.warn("Failed to acquire response body", ignore);
            }
        } else {
            throw new SentinelException("create sentinel namespace failed, code:  " + code + ", message: " + message);
        }
    }

    private List<String> getNamespaces() {
        Response response = retryExecutor.execute(() -> {
            String url = getUrl(NAMESPACES_URI);
            return httpClient.sendGet(url, null);
        });

        boolean responseSuccess = false;
        String body;
        int code = response.code();
        String errorMessage = response.message();
        List<String> allNamespaces = new ArrayList<>();
        if(response.isSuccessful()) {
            try {
                body = new String(response.body().bytes());
                JSONObject jsonBody = JSON.parseObject(JSON.toJSONString(body), JSONObject.class);
                code = jsonBody.getInteger("code");
                JSONArray namespaces = jsonBody.getJSONArray("data");
                if(code == 200) {
                    responseSuccess = true;
                    for (Object item : namespaces) {
                        JSONObject namespace = (JSONObject) item;
                        String namespaceShowName = namespace.getString("namespaceShowName");
                        allNamespaces.add(namespaceShowName);
                    }
                } else {
                    errorMessage = jsonBody.getString("message");
                }
            } catch (IOException ignore) {
                logger.warn("Failed to acquire response body", ignore);
            }
        }

        if(!responseSuccess) {
            throw new SentinelException("Fetch nacos namespace failed, code: " + code + ", message: " + errorMessage);
        }
        return allNamespaces;
    }

    private String getUrl(String uri) {
        return "http://" + properties.getServerAddr() + uri;
    }

    // private Response sendPost(String url, String data, Headers headers) {
    //     return sendRequest(url, "POST", data, headers);
    // }
    //
    // private Response sendGet(String url) {
    //     return sendRequest(url, "GET", null, null);
    // }
    //
    // private Response sendRequest(String url, String method, String data, Headers headers) {
    //     String contentType = "application/json";
    //     if(headers != null && headers.get("Content-Type") != null) {
    //         contentType = headers.get("Content-Type");
    //     }
    //     Request.Builder requestBuilder = new Request.Builder();
    //     requestBuilder.url(url);
    //     switch (method) {
    //         case "POST":
    //             RequestBody requestBody = RequestBody.create(MediaType.parse(contentType), data);
    //             requestBuilder.post(requestBody);
    //             break;
    //         case "GET":
    //             requestBuilder.get();
    //             break;
    //         default: throw new IllegalArgumentException("Unsupported request type: " + method);
    //     }
    //     requestBuilder.headers(headers);
    //     Call call = httpClient.newCall(requestBuilder.build());
    //     try {
    //         return call.execute();
    //     } catch (IOException e) {
    //         return new Response.Builder()
    //                 .code(500)
    //                 .message(e.getMessage())
    //                 .build();
    //     }
    // }

    public HttpRetryExecutor buildHttpRetryExecutor () {
        HttpRetryExecutor executor = new HttpRetryExecutor();
        executor.setMaxAttempts(3);
        executor.setMultiplier(2.0);
        executor.setInitialInterval(1000);
        executor.setMaxInterval(10000);
        return executor;
    }

    private static class HttpClient {

        private final OkHttpClient httpClient = new OkHttpClient();

        public Response sendGet(String url, Map<String, String> headers) {
            Request request = new Request.Builder()
                    .url(url)
                    .get()
                    .headers(Headers.of(headers))
                    .build();
            return sendRequest(request);
        }

        public Response sendPost(String url, String body, Map<String, String> headers) {
            String contentType = Optional.ofNullable(headers).orElse(new HashMap<>())
                    .getOrDefault("Content-Type", "application/json");
            RequestBody requestBody = RequestBody.create(MediaType.get(contentType), body);
            Request request = new Request.Builder()
                    .url(url)
                    .post(requestBody)
                    .headers(Headers.of(headers))
                    .build();
            return sendRequest(request);
        }

        private Response sendRequest(Request request) {
            Response response = null;
            try {
                response = httpClient.newCall(request).execute();
            } catch (IOException e) {
                response = new Response.Builder()
                        .code(500)
                        .message(e.getMessage())
                        .build();
            }
            return response;
        }
    }

    private static class HttpRetryExecutor {

        private int maxAttempts;

        private double multiplier;

        // unit: ms
        private int initialInterval;

        private int maxInterval;

        public Response execute(Supplier<Response> requestSupplier) {
            int retryCount = 0;
            Response response = null;
            int time = initialInterval;
            while (retryCount <= maxAttempts) {
                try {
                    response = requestSupplier.get();
                } catch (Exception e) {
                    time = (int) (time * multiplier);
                    if(time > maxInterval) {
                        time = maxInterval;
                    }
                    try {
                        TimeUnit.MILLISECONDS.sleep(time);
                    } catch (InterruptedException ignore) {
                    }
                    retryCount++;

                    if(retryCount == maxAttempts) {
                        Response.Builder responseBuilder = new Response.Builder();
                        responseBuilder.code(500);
                        responseBuilder.message(e.getMessage());
                        response = responseBuilder.build();
                    }
                }
            }
            return response;
        }

        public void setMaxAttempts(int maxAttempts) {
            this.maxAttempts = maxAttempts;
        }

        public void setMultiplier(double multiplier) {
            this.multiplier = multiplier;
        }

        public void setInitialInterval(int initialInterval) {
            this.initialInterval = initialInterval;
        }

        public void setMaxInterval(int maxInterval) {
            this.maxInterval = maxInterval;
        }
    }
}
