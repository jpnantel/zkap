package ca.jp.secproj.server.persistence;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.appengine.api.datastore.Blob;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Text;

/**
 * Implementation of the User database facade for the google appengine
 * datastore.
 * 
 * @author Jean-Philippe
 *
 */
public class UserDbAppEngineImpl implements IUserDb {

    private static Logger logger = LoggerFactory.getLogger(UserDbAppEngineImpl.class);

    private final static String USER_ENTITY_NAME = "User";

    private final static String USER_SECRET_PASS_PPTY = "secretpass";

    private final static String USER_SECRET_KEY_PPTY = "secretkey";

    private DatastoreService datastore;

    public UserDbAppEngineImpl() {
	this.datastore = DatastoreServiceFactory.getDatastoreService();
    }

    /**
     * Retrieves the secret passphrase stored in the datastore for the specified
     * user
     */
    public String getJPAKESecret(String username) throws IOException {
	Entity user;
	try {
	    user = getUser(username);
	} catch (EntityNotFoundException e) {
	    logger.info("Error getting entity in the store. ", e);
	    return null;
	}
	Text secretPass = (Text) user.getProperty(USER_SECRET_PASS_PPTY);
	return secretPass.getValue();
    }

    /**
     * Retrieves the secret key (for AES) stored in the datastore for the
     * specified user
     */
    public byte[] getSecretKey(String username) {
	Entity user;
	try {
	    user = getUser(username);
	} catch (EntityNotFoundException e) {
	    logger.info("Error getting entity in the store. ", e);
	    return null;
	}
	Blob secretKey = (Blob) user.getProperty(USER_SECRET_KEY_PPTY);
	return secretKey.getBytes();
    }

    /**
     * Saves the secret passphrase in the datastore for the specified user
     */
    public void setJPAKESecret(String username, String secretPass) throws IOException {
	saveUser(username, secretPass, null);
    }

    /**
     * Saves the secret key (for AES) in the datastore for the specified user
     */
    public void setSecretKey(String username, byte[] secretKey) {
	saveUser(username, null, secretKey);
    }

    /**
     * Internal method for retrieving a User entity from the datastore
     * 
     * @param username
     * @return
     * @throws EntityNotFoundException
     */
    private Entity getUser(String username) throws EntityNotFoundException {
	Key useKey = KeyFactory.createKey(USER_ENTITY_NAME, username);
	return datastore.get(useKey);
    }

    /**
     * Internal method used to save user data
     * 
     * @param username
     * @param secretPass
     * @param secretKey
     */
    private void saveUser(String username, String secretPass, byte[] secretKey) {
	Entity user = null;
	try {
	    user = getUser(username);
	} catch (EntityNotFoundException e) {
	    logger.info("Error getting entity in the store. ", e);
	}
	if (user == null) {
	    user = new Entity(USER_ENTITY_NAME, username);
	}
	if (!StringUtils.isBlank(secretPass)) {
	    user.setProperty(USER_SECRET_PASS_PPTY, new Text(secretPass));
	}
	if (secretKey != null) {
	    user.setProperty(USER_SECRET_KEY_PPTY, new Blob(secretKey));
	}
	datastore.put(user);
    }
}
