package com.seebie.server.test.client;

import javax.net.ssl.SSLEngine;
import javax.net.ssl.X509ExtendedTrustManager;
import java.net.Socket;
import java.security.cert.X509Certificate;

/**
 * This is a Trust Manager that trusts all certificates. It is used for testing purposes only.
 * Hostname verification is disabled as well.
 */
public class InsecureTrustManager extends X509ExtendedTrustManager {

    public X509Certificate[] getAcceptedIssuers() {
        return null;
    }

    @Override
    public void checkClientTrusted(X509Certificate[] certs, String authType) {     }

    @Override
    public void checkServerTrusted(X509Certificate[] certs, String authType) {    }

    @Override
    public void checkClientTrusted(X509Certificate[] chain, String authType, Socket socket) {    }

    @Override
    public void checkServerTrusted(X509Certificate[] chain, String authType, Socket socket) {    }

    @Override
    public void checkClientTrusted(X509Certificate[] chain, String authType, SSLEngine engine) {    }

    @Override
    public void checkServerTrusted(X509Certificate[] chain, String authType, SSLEngine engine) {    }
}
