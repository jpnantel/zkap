package ca.jp.secproj.utils.net;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.stereotype.Component;

import ca.jp.secproj.utils.log.Loggable;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.json.JSONConfiguration;
import com.sun.jersey.client.urlconnection.HttpURLConnectionFactory;
import com.sun.jersey.client.urlconnection.URLConnectionClientHandler;

@Loggable
@Component("jerseyClientFactory")
public class JerseyClientFactory {

    private static Logger logger;

    private Client jerseyClient;

    public Client makeJerseyClient() {
	if (jerseyClient == null) {
	    try {
		ClientConfig cc = new DefaultClientConfig();
		cc.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING, Boolean.TRUE);

		URLConnectionClientHandler ucch = new URLConnectionClientHandler(new HttpURLConnectionFactory() {
		    @Override
		    public HttpURLConnection getHttpURLConnection(URL url) throws IOException {
			return (HttpURLConnection) url.openConnection();
		    }
		});

		jerseyClient = new Client(ucch, cc);
	    } catch (Exception e) {
		logger.log(Level.WARNING, "logEvent construction error: ", e);
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

}
