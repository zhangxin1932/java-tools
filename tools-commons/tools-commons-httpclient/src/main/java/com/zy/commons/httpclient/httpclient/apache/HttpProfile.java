package com.zy.commons.httpclient.httpclient.apache;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HttpProfile {

    private int maxConnectionPerRoute = 100;

    private int validateAfterInactivity = 1000;

    private int socketTimeoutInMills = 60_000;

    private int connectionTimeoutInMills = 60_000;

    private int retryTimes = 3;

    private int maxTotalConnection = 200;

    private static final HttpProfile httpProfile = new HttpProfile();

    public static HttpProfile getInstance() {
        return httpProfile;
    }

}
