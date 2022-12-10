package org.thinkbigthings.zdd.server.test.client;

import javax.net.ssl.X509TrustManager;
import java.security.cert.X509Certificate;

public class InsecureTrustManager implements X509TrustManager {

    public X509Certificate[] getAcceptedIssuers() {
        return null;
    }

    @Override
    public void checkClientTrusted(X509Certificate[] certs, String authType) {
    }

    @Override
    public void checkServerTrusted(X509Certificate[] certs, String authType) {
    }
}
