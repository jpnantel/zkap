package ca.jp.secproj.server.persistence;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.jp.secproj.crypto.zka.ffs.dto.FFSSetupDTO;

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
 * @author Jean-Philippe Nantel
 *
 */
public class UserDbAppEngineImpl implements IUserDb {

    private static Logger logger = LoggerFactory.getLogger(UserDbAppEngineImpl.class);

    private final static String USER_ENTITY_NAME = "User";

    private final static String USER_SECRET_PASS_JPAKE_PPTY = "JPAKESecretPass";

    private final static String USER_SECRET_KEY_JPAKE_PPTY = "JPAKENegotiatedSecretSymKey";

    private final static String USER_FFS_N_PPTY = "FFSN";

    private final static String USER_FFS_NBROUNDS_PPTY = "FFSNbRounds";

    private final static String USER_FFS_PUBLICKEY_PPTY = "FFSPublicKey";

    private String serverId;

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
	Text secretPass = (Text) user.getProperty(USER_SECRET_PASS_JPAKE_PPTY);
	return secretPass.getValue();
    }

    /**
     * Retrieves the secret key (for AES) stored in the datastore for the
     * specified user
     */
    public byte[] getJPAKENegotiatedSecretKey(String username) {
	Entity user;
	try {
	    user = getUser(username);
	} catch (EntityNotFoundException e) {
	    logger.info("Error getting entity in the store. ", e);
	    return null;
	}
	Blob secretKey = (Blob) user.getProperty(USER_SECRET_KEY_JPAKE_PPTY);
	return secretKey.getBytes();
    }

    /**
     * Retrieve a FFS setup previously registered
     * 
     * @param username
     * @return
     * @throws IOException
     */
    public FFSSetupDTO getFFSSetup(String username) throws IOException {
	Entity user;
	try {
	    user = getUser(username);
	} catch (EntityNotFoundException e) {
	    logger.info("Error getting entity in the store. ", e);
	    return null;
	}
	Text n = (Text) user.getProperty(USER_FFS_N_PPTY);
	Long tempNbRounds = (Long) user.getProperty(USER_FFS_NBROUNDS_PPTY);
	int nbRounds = tempNbRounds.intValue();
	Text publicKey = (Text) user.getProperty(USER_FFS_PUBLICKEY_PPTY);

	return new FFSSetupDTO(username, serverId, n.getValue(), nbRounds, publicKey.getValue());
    }

    /**
     * Saves the secret passphrase in the datastore for the specified user
     */
    public void setJPAKESecret(String username, String secretPass) throws IOException {
	saveUser(username, secretPass, null, null, null, null);
    }

    /**
     * Saves the secret key (for AES) in the datastore for the specified user
     */
    public void setJPAKENegotiatedSecretKey(String username, byte[] secretKey) {
	saveUser(username, null, secretKey, null, null, null);
    }

    /**
     * Saves the secret key (for AES) in the datastore for the specified user
     */
    public void setFFSSetup(FFSSetupDTO setup) {
	saveUser(setup.getProverId(), null, null, setup.getN(), setup.getNbRounds(), setup.getPublicKey());
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
     * @param jpakePassword
     * @param jpakeNegoSecretSymKey
     */
    private void saveUser(String username, String jpakePassword, byte[] jpakeNegoSecretSymKey, String FFSN,
	    Integer FFSNbRounds, String FFSPublicKey) {
	Entity user = null;
	try {
	    user = getUser(username);
	} catch (EntityNotFoundException e) {
	    logger.info("Error getting entity in the store. ", e);
	}
	if (user == null) {
	    user = new Entity(USER_ENTITY_NAME, username);
	}
	if (!StringUtils.isBlank(jpakePassword)) {
	    user.setProperty(USER_SECRET_PASS_JPAKE_PPTY, new Text(jpakePassword));
	}
	if (jpakeNegoSecretSymKey != null) {
	    user.setProperty(USER_SECRET_KEY_JPAKE_PPTY, new Blob(jpakeNegoSecretSymKey));
	}
	if (!StringUtils.isBlank(FFSN)) {
	    user.setProperty(USER_FFS_N_PPTY, new Text(FFSN));
	}
	if (FFSNbRounds != null) {
	    user.setProperty(USER_FFS_NBROUNDS_PPTY, FFSNbRounds);
	}
	if (!StringUtils.isBlank(FFSPublicKey)) {
	    user.setProperty(USER_FFS_PUBLICKEY_PPTY, new Text(FFSPublicKey));
	}
	datastore.put(user);
    }

    public String getServerId() {
	return serverId;
    }

    public void setServerId(String serverId) {
	this.serverId = serverId;
    }

}
