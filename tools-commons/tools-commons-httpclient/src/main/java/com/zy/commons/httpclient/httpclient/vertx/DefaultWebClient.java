package com.zy.commons.httpclient.httpclient.vertx;

import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.ext.web.client.WebClient;

public final class DefaultWebClient {
    private static final int connectionTimeoutInMills = 60_000;
    private static final int idleTimeoutMills = 60_000;
    private static final int maxTotalConnection = 200;
    private static final WebClient webClient;
    private static final WebClientOptions options;

    static {
        options = new WebClientOptions()
                .setMaxPoolSize(maxTotalConnection)
                .setConnectTimeout(connectionTimeoutInMills)
                .setSsl(false)
                .setIdleTimeout(idleTimeoutMills)
                .setKeepAlive(true);
        webClient = WebClient.create(Vertx.vertx(), options);
    }

    public static WebClient getInstance() {
        return webClient;
    }

}
