package ca.jp.secproj.server.persistence;

import java.io.IOException;

import ca.jp.secproj.crypto.zka.ffs.dto.FFSSetupDTO;

/**
 * 
 * @author Jean-Philippe Nantel
 *
 */
public interface IUserDb {

    public String getJPAKESecret(String username) throws IOException;

    public void setJPAKESecret(String username, String secretPass) throws IOException;

    public byte[] getJPAKENegotiatedSecretKey(String username);

    public void setJPAKENegotiatedSecretKey(String username, byte[] secretKey);

    public FFSSetupDTO getFFSSetup(String username) throws IOException;

    public void setFFSSetup(FFSSetupDTO setup);

}
