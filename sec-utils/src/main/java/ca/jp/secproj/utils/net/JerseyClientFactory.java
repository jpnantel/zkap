package ca.jp.secproj.utils.net;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.json.JSONConfiguration;
import com.sun.jersey.client.urlconnection.HTTPSProperties;
import com.sun.jersey.client.urlconnection.HttpURLConnectionFactory;
import com.sun.jersey.client.urlconnection.URLConnectionClientHandler;

public class JerseyClientFactory {

    private static Logger logger = LoggerFactory.getLogger(JerseyClientFactory.class);

    /**
     * Suite de jeux de chiffrement considérés comme sécuritaires. Cette liste
     * est construite en ne conservant que les chiffrement symmétriques avec
     * AES256 d'après la commande openSSL ciphers HIGH.
     */
    // "TLS_ECDHE_ECDSA_WITH_AES_256_CBC_SHA384",
    // "TLS_RSA_WITH_AES_256_CBC_SHA256",
    // "TLS_DHE_RSA_WITH_AES_256_CBC_SHA256",
    // "TLS_DHE_DSS_WITH_AES_256_CBC_SHA256",
    // "TLS_ECDHE_ECDSA_WITH_AES_256_CBC_SHA",
    // "TLS_ECDHE_RSA_WITH_AES_256_CBC_SHA", "TLS_RSA_WITH_AES_256_CBC_SHA",
    // "TLS_DHE_RSA_WITH_AES_256_CBC_SHA",
    // "TLS_DHE_DSS_WITH_AES_256_CBC_SHA"
    public static final String[] SECURE_CIPHER_SUITES = { "TLS_ECDHE_RSA_WITH_AES_256_CBC_SHA384" };

    /**
     * Limiter les protocoles possibles à TLSv1.2 car il est le plus sécuritaire
     * et aucun besoin de rétro compatibilité.
     */
    public static final String[] SECURE_PROTOCOLS = { "TLSv1.2" };

    private Client jerseyClient;

    private boolean enforceSecureConnection;

    public Client makeJerseyClient() {
	if (jerseyClient == null) {
	    try {

		ClientConfig cc = new DefaultClientConfig();
		cc.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING, Boolean.TRUE);
		URLConnectionClientHandler ucch = null;
		if (enforceSecureConnection) {
		    ucch = new URLConnectionClientHandler(new HttpURLConnectionFactory() {
			@Override
			public HttpsURLConnection getHttpURLConnection(URL url) throws IOException {
			    return (HttpsURLConnection) url.openConnection();
			}
		    });
		    HTTPSProperties prop = new HTTPSProperties(HttpsURLConnection.getDefaultHostnameVerifier(),
			    getSSLContext());
		    cc.getProperties().put(HTTPSProperties.PROPERTY_HTTPS_PROPERTIES, prop);
		    jerseyClient = new Client(ucch, cc);
		    jerseyClient.getProperties().put(HTTPSProperties.PROPERTY_HTTPS_PROPERTIES, prop);
		} else {
		    ucch = new URLConnectionClientHandler(new HttpURLConnectionFactory() {
			@Override
			public HttpURLConnection getHttpURLConnection(URL url) throws IOException {
			    return (HttpURLConnection) url.openConnection();
			}
		    });
		    jerseyClient = new Client(ucch, cc);
		}

	    } catch (Exception e) {
		logger.warn("LogEvent construction error: ", e);
		return null;
	    }
	}
	return jerseyClient;
    }

    public static Logger getLogger() {
	return logger;
    }

    public static void setLogger(Logger logger) {
	JerseyClientFactory.logger = logger;
    }

    public void setEnforceSecureConnection(boolean enforceSecureConnection) {
	this.enforceSecureConnection = enforceSecureConnection;
    }

    public SSLContext getSSLContext() throws IOException {

	StringBuilder sb = new StringBuilder(255);
	sb.append(SECURE_CIPHER_SUITES[0]);
	for (int i = 1; i < SECURE_CIPHER_SUITES.length; i++) {
	    sb.append(", ").append(SECURE_CIPHER_SUITES[i]);
	}
	System.setProperty("https.cipherSuites", sb.toString());

	try {
	    SSLContext ctx = SSLContext.getInstance(SECURE_PROTOCOLS[0]);
	    ctx.init(null, null, new SecureRandom());
	    return ctx;
	} catch (NoSuchAlgorithmException | KeyManagementException e) {
	    logger.warn("Unable to retrieve protocol: ", e);
	    return null;
	}
    }
}
