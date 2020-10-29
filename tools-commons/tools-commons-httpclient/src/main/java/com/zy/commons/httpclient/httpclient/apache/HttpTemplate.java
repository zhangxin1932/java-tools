package com.zy.commons.httpclient.httpclient.apache;

import com.google.common.base.Joiner;
import com.zy.commons.httpclient.serializer.JacksonSerializer;
import com.zy.commons.httpclient.serializer.Serializer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanMap;
import org.apache.http.*;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.EntityBuilder;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.client.utils.HttpClientUtils;
import org.apache.http.entity.AbstractHttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public final class HttpTemplate {
    private static final String URL_FORMAT = "%s?%s";
    private static final Serializer SERIALIZER = new JacksonSerializer();
    private static final Map<Class, JsonResponseHandler> JSON_RESPONSE_HANDLER_MAP = new ConcurrentHashMap<>();
    private static final Map<Class, JsonpResponseHandler> JSONP_RESPONSE_HANDLER_MAP = new ConcurrentHashMap<>();
    @Getter
    private CloseableHttpClient client;

    public HttpTemplate(CloseableHttpClient client) {
        this.client = client;
    }

    public void close() {
        HttpClientUtils.closeQuietly(client);
    }

    public Boolean upload(String uri, File file) throws IOException {
        HttpEntity entity = MultipartEntityBuilder.create().setCharset(StandardCharsets.UTF_8).addBinaryBody("fileUpload", file).build();
        HttpUriRequest request = HttpRequestBuilder.custom(uri, HttpMethod.POST).entity(entity).build();
        return client.execute(request, SuccessResponseHandler.INSTANCE);
    }

    public Boolean upload(String uri, String fileName) throws IOException {
        return upload(uri, new File(fileName));
    }

    public String getString(String uri, Object param, ContentType accept, String userAgent, String proxyHost, int proxyPort) throws IOException {
        String url = createUrl(uri, param);
        HttpUriRequest request = HttpRequestBuilder.custom(uri, HttpMethod.GET).accept(accept).userAgent(userAgent).proxy(proxyHost, proxyPort).build();
        return client.execute(request, StringResponseHandler.INSTANCE);
    }

    public String getString(String uri, Object param, ContentType accept, String userAgent, HttpHost proxy) throws IOException {
        HttpEntity entity = EntityBuilder.create().setParameters(createParameters(param)).setContentEncoding(StandardCharsets.UTF_8.toString()).build();
        HttpUriRequest request = HttpRequestBuilder.custom(uri, HttpMethod.GET, entity).accept(accept).userAgent(userAgent).proxy(proxy).build();
        return client.execute(request, StringResponseHandler.INSTANCE);
    }

    public String getString(String uri, Object param, ContentType accept) throws IOException {
        return getString(uri, param, accept, null, null, 0);
    }

    public String getString(String uri, Object param, String proxyHost, int proxyPort) throws IOException {
        return getString(uri, param, ContentType.WILDCARD, null, proxyHost, proxyPort);
    }

    public String getString(String uri, Object param, HttpHost proxy) throws IOException {
        return getString(uri, param, ContentType.WILDCARD, null, proxy);
    }

    public String getString(String uri, Object param) throws IOException {
        return getString(uri, param, ContentType.WILDCARD);
    }

    public String getString(String uri, ContentType accept) throws IOException {
        return getString(uri, null, accept);
    }

    public String getString(String uri) throws IOException {
        return getString(uri, null);
    }

    public String getStringWithCookie(String uri, String cookie) throws IOException {
        HttpUriRequest request = HttpRequestBuilder.custom(uri, HttpMethod.GET).cookie(cookie).build();
        return client.execute(request, StringResponseHandler.INSTANCE);
    }

    public <T> T postFormData(String uri, Object param, Class<T> returnType) throws IOException {
        HttpEntity entity = EntityBuilder.create().setParameters(createParameters(param)).build();
        HttpUriRequest request = HttpRequestBuilder.custom(uri, HttpMethod.POST, entity).contentType(ContentType.APPLICATION_FORM_URLENCODED).build();
        return client.execute(request, getJsonResponseHandler(returnType));
    }

    public <T> T getJson(String uri, Object param, String userAgent, String proxyHost, int proxyPort, Class<T> returnType) throws IOException {
        HttpEntity entity = EntityBuilder.create().setParameters(createParameters(param)).build();
        HttpUriRequest request = HttpRequestBuilder.custom(uri, HttpMethod.GET, entity).accept(ContentType.APPLICATION_JSON).userAgent(userAgent).proxy(proxyHost, proxyPort).build();
        return client.execute(request, getJsonResponseHandler(returnType));
    }

    public <T> T getJson(String uri, Object param, String proxyHost, int proxyPort, Class<T> returnType) throws IOException {
        return getJson(uri, param, null, proxyHost, proxyPort, returnType);
    }

    public <T> T getJson(String uri, Object param, Class<T> returnType) throws IOException {
        return getJson(uri, param, null, 0, returnType);
    }

    public <T> T getJson(String uri, Class<T> returnType) throws IOException {
        return getJson(uri, null, returnType);
    }

    public <T> T getJsonp(String uri, Object param, String proxyHost, int proxyPort, Class<T> returnType) throws IOException {
        HttpUriRequest request = HttpRequestBuilder.custom(createUrl(uri, param), HttpMethod.GET).proxy(proxyHost, proxyPort).build();
        return client.execute(request, getJsonpResponseHandler(returnType));
    }

    public <T> T getJsonp(String uri, Object param, Class<T> returnType) throws IOException {
        HttpUriRequest request = HttpRequestBuilder.custom(createUrl(uri, param), HttpMethod.GET).build();
        return client.execute(request, getJsonpResponseHandler(returnType));
    }

    public <T> T getJsonp(String uri, Class<T> returnType) throws IOException {
        return getJsonp(uri, null, returnType);
    }

    public String putString(String uri, String body) throws IOException {
        StringEntity entity = new StringEntity(body, StandardCharsets.UTF_8);
        HttpUriRequest request = HttpRequestBuilder.custom(uri, HttpMethod.PUT, entity).contentType(ContentType.TEXT_PLAIN).build();
        return client.execute(request, StringResponseHandler.INSTANCE);
    }

    public String putString(String uri, Object query, String body) throws IOException {
        String url = createUrl(uri, query);
        StringEntity entity = new StringEntity(body, StandardCharsets.UTF_8);
        HttpUriRequest request = HttpRequestBuilder.custom(url, HttpMethod.PUT, entity).contentType(ContentType.TEXT_PLAIN).build();
        return client.execute(request, StringResponseHandler.INSTANCE);
    }

    public String putJson(String uri, Object query, Object body) throws IOException {
        String url = createUrl(uri, query);
        HttpEntity entity = new JacksonEntity(body);
        HttpUriRequest request = HttpRequestBuilder.custom(url, HttpMethod.PUT, entity).contentType(ContentType.APPLICATION_JSON).build();
        return client.execute(request, StringResponseHandler.INSTANCE);
    }

    public <T> T putJson(String uri, Object query, Object body, Class<T> returnType) throws IOException {
        String url = createUrl(uri, query);
        HttpEntity entity = new JacksonEntity(body);
        HttpUriRequest request = HttpRequestBuilder.custom(url, HttpMethod.PUT, entity).contentType(ContentType.APPLICATION_JSON).build();
        return client.execute(request, getJsonResponseHandler(returnType));
    }

    public <T> T putJson(String uri, Object body, Class<T> returnType) throws IOException {
        HttpEntity entity = new JacksonEntity(body);
        HttpUriRequest request = HttpRequestBuilder.custom(uri, HttpMethod.PUT, entity).contentType(ContentType.APPLICATION_JSON).build();
        return client.execute(request, getJsonResponseHandler(returnType));
    }

    public String deleteString(String uri, Object query) throws IOException {
        String url = createUrl(uri, query);
        HttpUriRequest request = HttpRequestBuilder.custom(url, HttpMethod.DELETE).accept(ContentType.TEXT_PLAIN).build();
        return client.execute(request, StringResponseHandler.INSTANCE);
    }

    public String deleteString(String uri) throws IOException {
        HttpUriRequest request = HttpRequestBuilder.custom(uri, HttpMethod.DELETE).accept(ContentType.TEXT_PLAIN).build();
        return client.execute(request, StringResponseHandler.INSTANCE);
    }

    public <T> T deleteJson(String uri, Object query, Class<T> returnType) throws IOException {
        String url = createUrl(uri, query);
        HttpUriRequest request = HttpRequestBuilder.custom(url, HttpMethod.DELETE).accept(ContentType.APPLICATION_JSON).build();
        return client.execute(request, getJsonResponseHandler(returnType));
    }

    public <T> T deleteJson(String uri, Class<T> returnType) throws IOException {
        HttpUriRequest request = HttpRequestBuilder.custom(uri, HttpMethod.DELETE).accept(ContentType.TEXT_PLAIN).build();
        return client.execute(request, getJsonResponseHandler(returnType));
    }

    public <T> T execute(final HttpUriRequest request, final ResponseHandler<? extends T> responseHandler) throws IOException {
        return client.execute(request, responseHandler);
    }

    public <T> T execute(final HttpHost target, final HttpUriRequest request, final ResponseHandler<? extends T> responseHandler) throws IOException {
        return client.execute(target, request, responseHandler);
    }

    public <T> T execute(final HttpHost target, final HttpUriRequest request, final ResponseHandler<? extends T> responseHandler, final HttpContext context) throws IOException {
        return client.execute(target, request, responseHandler, context);
    }

    private static String createUrl(String uri, Object param) {
        if (Objects.nonNull(param)) {
            return String.format(URL_FORMAT, uri, createQueryString(param));
        }
        return uri;
    }

    private static String createQueryString(Object value) {
        Map<?, ?> parameterMap = createParameterMap(value);
        return Joiner.on('&').withKeyValueSeparator('=').useForNull("").join(parameterMap);
    }

    private static Map<?, ?> createParameterMap(Object value) {
        if (Objects.isNull(value)) {
            return Collections.emptyMap();
        }
        if (value instanceof Map) {
            return (Map<?, ?>) value;
        }
        Map<Object, Object> map = new HashMap<>(new BeanMap(value));
        map.remove("class");
        return map;
    }

    private static List<NameValuePair> createParameters(Object param) {
        if (Objects.isNull(param)) {
            return Collections.emptyList();
        }
        Map<?, ?> parameterMap = createParameterMap(param);
        Set<? extends Map.Entry<?, ?>> entries = parameterMap.entrySet();
        List<NameValuePair> parameters = new ArrayList<>(entries.size());
        for (Map.Entry<?, ?> entry : entries) {
            parameters.add(createParameter(entry));
        }
        return parameters;
    }

    private static NameValuePair createParameter(Map.Entry<?, ?> entry) {
        return new BasicNameValuePair(Objects.toString(entry.getKey()), Objects.toString(entry.getValue()));
    }

    @SuppressWarnings("unchecked")
    private static <T> ResponseHandler<T> getJsonpResponseHandler(Class<T> returnType) {
        JsonpResponseHandler handler = JSONP_RESPONSE_HANDLER_MAP.get(returnType);
        if (Objects.isNull(handler)) {
            handler = new JsonpResponseHandler(returnType);
            JSONP_RESPONSE_HANDLER_MAP.put(returnType, handler);
        }
        return handler;
    }

    @SuppressWarnings("unchecked")
    private static <T> ResponseHandler<T> getJsonResponseHandler(Class<T> returnType) {
        JsonResponseHandler handler = JSON_RESPONSE_HANDLER_MAP.get(returnType);
        if (Objects.isNull(handler)) {
            handler = new JsonResponseHandler(returnType);
            JSON_RESPONSE_HANDLER_MAP.put(returnType, handler);
        }
        return handler;
    }

    private enum HttpMethod {
        DELETE, GET, HEAD, OPTIONS, PATCH, POST, PUT, TRACE
    }

    private abstract static class RequestContext {
        private static final ThreadLocal<RequestConfig> REQUEST_CONFIG = new ThreadLocal<>();
        public static RequestConfig getRequestConfig() {
            return REQUEST_CONFIG.get();
        }
        public static void setRequestConfig(RequestConfig config) {
            REQUEST_CONFIG.set(config);
        }
    }

    private static class JacksonEntity extends AbstractHttpEntity {
        private Object objRef;
        private byte[] objSer;

        public JacksonEntity(Object objRef) {
            this.objRef = objRef;
        }

        @Override
        public boolean isRepeatable() {
            return true;
        }

        @Override
        public long getContentLength() {
            return objSer == null ? -1 : objSer.length;
        }

        @Override
        public InputStream getContent() throws IOException, UnsupportedOperationException {
            if (Objects.isNull(objSer)) {
                this.objSer = SERIALIZER.serializer(objRef);
            }
            return new ByteArrayInputStream(objSer);
        }

        @Override
        public void writeTo(OutputStream outputStream) throws IOException {
            if (Objects.isNull(objSer)) {
                this.objSer = SERIALIZER.serializer(objRef);
            }
            outputStream.write(this.objSer);
            outputStream.flush();
        }

        @Override
        public boolean isStreaming() {
            return Objects.isNull(objSer);
        }
    }

    private static class SuccessResponseHandler implements ResponseHandler<Boolean> {
        private static final SuccessResponseHandler INSTANCE = new SuccessResponseHandler();
        @Override
        public Boolean handleResponse(HttpResponse httpResponse) throws IOException {
            EntityUtils.consume(httpResponse.getEntity());
            return httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK;
        }
    }

    private static class StringResponseHandler implements ResponseHandler<String> {
        private static final StringResponseHandler INSTANCE = new StringResponseHandler();
        @Override
        public String handleResponse(HttpResponse httpResponse) throws IOException {
            return EntityUtils.toString(httpResponse.getEntity());
        }
    }

    @AllArgsConstructor
    private static class JsonResponseHandler<T> implements ResponseHandler<T> {
        private final Class<T> returnType;

        @Override
        public T handleResponse(HttpResponse httpResponse) throws IOException {
            return SERIALIZER.deserializer(EntityUtils.toByteArray(httpResponse.getEntity()), returnType);
        }
    }

    private static class JsonpResponseHandler<T> implements ResponseHandler<T> {
        private final Class<T> returnType;
        private static final Pattern JSONP_REGEX = Pattern.compile("\\w+\\((.*)\\);");
        public JsonpResponseHandler(Class<T> returnType) {
            this.returnType = returnType;
        }

        @Override
        public T handleResponse(HttpResponse httpResponse) throws ClientProtocolException, IOException {
            String jsonp = EntityUtils.toString(httpResponse.getEntity());
            Matcher matcher = JSONP_REGEX.matcher(jsonp);
            if (matcher.matches()) {
                String json = matcher.group(1);
                return SERIALIZER.deserializer(json.getBytes(StandardCharsets.UTF_8), returnType);
            }
            return null;
        }
    }

    private static class HttpRequestBuilder {
        private String uri;
        private HttpHost proxy;
        private ContentType contentType;
        private ContentType accept;
        private String encoding;
        private HttpMethod method;
        private String userAgent;
        private HttpEntity entity;
        private String cookie;

        private HttpRequestBuilder(String uri, HttpMethod method, HttpEntity entity) {
            this.uri = uri;
            this.method = method;
            this.entity = entity;
        }

        public static HttpRequestBuilder custom(String uri, HttpMethod method, HttpEntity entity) {
            return new HttpRequestBuilder(uri, method, entity);
        }

        public static HttpRequestBuilder custom(String uri, HttpMethod method) {
            return new HttpRequestBuilder(uri, method, null);
        }

        public HttpRequestBuilder proxy(HttpHost proxy) {
            this.proxy = proxy;
            return this;
        }

        public HttpRequestBuilder proxy(String host, int port, String schema) {
            if (Objects.nonNull(host) && port > 0) {
                this.proxy = new HttpHost(host, port, schema);
            }
            return this;
        }

        public HttpRequestBuilder proxy(String host, int port) {
            if (Objects.nonNull(host) && port > 0) {
                this.proxy = new HttpHost(host, port, null);
            }
            return this;
        }

        public HttpRequestBuilder cookie(String cookie) {
            this.cookie = cookie;
            return this;
        }

        public HttpRequestBuilder contentType(ContentType contentType) {
            this.contentType = contentType;
            return this;
        }

        public HttpRequestBuilder accept(ContentType accept) {
            this.accept = accept;
            return this;
        }

        public HttpRequestBuilder encoding(String encoding) {
            this.encoding = encoding;
            return this;
        }

        public HttpRequestBuilder method(HttpMethod method) {
            this.method = method;
            return this;
        }

        public HttpRequestBuilder userAgent(String userAgent) {
            this.userAgent = userAgent;
            return this;
        }

        public HttpRequestBuilder entity(HttpEntity entity) {
            this.entity = entity;
            return this;
        }

        public HttpUriRequest build() {
            RequestBuilder builder = createHttpUriRequest();
            RequestConfig.Builder requestConfigBuilder = RequestConfig.custom();
            if (Objects.nonNull(proxy)) {
                requestConfigBuilder.setProxy(proxy);
                builder.setConfig(requestConfigBuilder.build());
            }
            if (Objects.nonNull(accept)) {
                builder.setHeader(HttpHeaders.ACCEPT, accept.toString());
            }
            if (Objects.nonNull(contentType)) {
                builder.setHeader(HttpHeaders.CONTENT_TYPE, contentType.toString());
            }
            if (Objects.nonNull(encoding)) {
                encoding = StandardCharsets.UTF_8.toString();
            }
            if (Objects.nonNull(userAgent)) {
                builder.setHeader(HttpHeaders.USER_AGENT, userAgent);
            }
            return builder.build();
        }

        private RequestBuilder createHttpUriRequest() {
            switch (method) {
                case DELETE:
                    return RequestBuilder.delete(uri).setEntity(entity).setCharset(StandardCharsets.UTF_8);
                case GET:
                    return RequestBuilder.get(uri).setEntity(entity).setCharset(StandardCharsets.UTF_8);
                case HEAD:
                    return RequestBuilder.head(uri).setEntity(entity).setCharset(StandardCharsets.UTF_8);
                case OPTIONS:
                    return RequestBuilder.options(uri).setEntity(entity).setCharset(StandardCharsets.UTF_8);
                case PATCH:
                    return RequestBuilder.patch(uri).setEntity(entity).setCharset(StandardCharsets.UTF_8);
                case POST:
                    return RequestBuilder.post(uri).setEntity(entity).setCharset(StandardCharsets.UTF_8);
                case PUT:
                    return RequestBuilder.put(uri).setEntity(entity).setCharset(StandardCharsets.UTF_8);
                case TRACE:
                    return RequestBuilder.trace(uri).setEntity(entity).setCharset(StandardCharsets.UTF_8);
                default:
                    return RequestBuilder.get(uri).setEntity(entity).setCharset(StandardCharsets.UTF_8);
            }
        }

    }
}
