package io.github.thebesteric.framework.agile.logger.commons.utils;

import javax.net.ssl.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

/**
 * SSlUtils
 *
 * @author Eric Joe
 * @version 1.0
 */
public class SSLUtils {

    public static void ignoreSSl() throws Exception {
        trustAllHttpsCertificates();
        HttpsURLConnection.setDefaultHostnameVerifier((hostname, session) -> true);
    }

    private static void trustAllHttpsCertificates() throws Exception {
        SSLContext sc = SSLContext.getInstance("TLS");
        sc.init(null, new TrustManager[]{new TrustAllManager()}, null);
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
    }

    private static class TrustAllManager implements X509TrustManager {
        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }
    }


}
