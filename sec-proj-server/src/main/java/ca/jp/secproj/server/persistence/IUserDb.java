package ca.jp.secproj.server.persistence;

import java.io.IOException;

public interface IUserDb {

    public String getJPAKESecret(String username) throws IOException;

    public byte[] getSecretKey(String username);

    public void setJPAKESecret(String username, String secretPass) throws IOException;

    public void setSecretKey(String username, byte[] secretKey);
}
