package ca.jp.secproj.utils.net;

import java.io.IOException;

import javax.ws.rs.core.MediaType;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.ClientResponse.Status;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.WebResource.Builder;

/**
 * 
 * @author Jean-Philippe Nantel
 *
 */
public class ConnectionClientHelper {

    private static Logger logger = LoggerFactory.getLogger(ConnectionClientHelper.class);

    /**
     * Ex�cution d'un appel HTTP POST � l'URL sp�cifi� et s�rialise un objet de
     * classe T pour l'envoyer dans le InputStream de la requ�te et s'attend �
     * recevoir un autre objet T dans le InputStream de la r�ponse. L'objet de
     * la r�ponse sera d�s�rialis� avant d'�tre retourn�. Il faut que l'objet T
     * soit s�rialisable.
     * 
     * @param send
     *            Objet � envoyer
     * @param url
     *            Url du service web
     * @param clazz
     *            classe de l'objet T
     * @return l'objet T de r�ponse du serveur.
     */
    public static <T> T postToServer(Client jerseyClient, T send, Class<T> clazz, String url, String user)
	    throws IOException {

	WebResource wr = jerseyClient.resource(url);
	ClientResponse r = null;
	try {

	    Builder b = wr.entity(send, MediaType.APPLICATION_JSON);
	    if (StringUtils.isNotBlank(user)) {
		b.header("user", user);
	    }
	    r = b.post(ClientResponse.class);
	    int status = r.getStatus();
	    if (status == Status.OK.getStatusCode()) {
		return r.getEntity(clazz);
	    } else {
		throw new IOException("Server error: " + status);
	    }
	} catch (ClassCastException e) {
	    logger.warn("Error doing post (postToServer)", e);
	}
	return null;
    }

    /**
     * Ex�cution d'un appel HTTP POST � l'URL sp�cifi� et s�rialise un objet de
     * classe T pour l'envoyer dans le InputStream de la requ�te et s'attend �
     * recevoir un autre objet T dans le InputStream de la r�ponse. L'objet de
     * la r�ponse sera d�s�rialis� avant d'�tre retourn�. Il faut que l'objet T
     * soit s�rialisable.
     * 
     * @param send
     *            Objet � envoyer
     * @param url
     *            Url du service web
     * @param clazz
     *            classe de l'objet T
     * @return l'objet T de r�ponse du serveur.
     */
    public static <T, U> U postToServer(Client jerseyClient, T send, Class<U> clazz, String url) throws IOException {

	WebResource wr = jerseyClient.resource(url);
	ClientResponse r = null;
	try {

	    Builder b = wr.entity(send, MediaType.APPLICATION_JSON);
	    r = b.post(ClientResponse.class);
	    int status = r.getStatus();
	    if (status == Status.OK.getStatusCode()) {
		return r.getEntity(clazz);
	    } else {
		throw new IOException("Server error: " + r.getEntity(clazz) + " Code: " + status);
	    }
	} catch (ClassCastException e) {
	    logger.warn("Error doing post (postToServer)", e);
	}
	return null;
    }
}
