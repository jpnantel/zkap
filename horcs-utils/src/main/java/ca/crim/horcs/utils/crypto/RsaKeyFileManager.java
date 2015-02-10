package ca.crim.horcs.utils.crypto;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import org.apache.commons.codec.binary.Base64;

import ca.crim.horcs.utils.crypto.rsa.RsaKeyInfo;
import ca.crim.horcs.utils.crypto.rsa.RsaKeyInfo.KeyType;
import ca.crim.horcs.utils.crypto.rsa.RsaKeyManager;
import ca.crim.horcs.utils.domain.AppVersion;

/**
 * Gestionnaire de clés cryptographiques. Version temporaire. Reste à
 * implémenter la gestion des
 * 
 * @author nanteljp
 * 
 */
public class RsaKeyFileManager {

    private static final String DEFAULT_CHARSET = "UTF-8";

    // private static final Logger logger =
    // Logger.getLogger(RsaKeyFileManager.class.getName());

    /**
     * Nom d'une clé utilisée pour chiffrer des données provenant du client.
     * Seul le client utilise cette clé.
     */
    public static final String CLIENT_CIPHER_KEY = "cPub";

    /**
     * Nom de la clé utilisée pour déchiffrer les données provenant du client.
     * Seul l'agent utilise cette clé.
     */
    public static final String CLIENT_DECIPHER_KEY = "cPriv";

    /**
     * Nom de la clé utilisée par l'agent pour chiffrer ses données.
     */
    public static final String AGENT_CIPHER_KEY = "aPub";

    /**
     * Nom de la clé utilisée pour déchiffrer les données provenant de l'agent.
     * Seul le client utilise cette clé.
     */
    public static final String AGENT_DECIPHER_KEY = "aPriv";

    public RsaKeyManager keyManager;

    public RsaKeyFileManager(String rsaKeyFile) throws NoSuchAlgorithmException, InvalidKeySpecException,
            UnsupportedEncodingException {
        // TODO faire ça clean

        keyManager = new RsaKeyManager();

        AppVersion versionBidon = new AppVersion("1", "3", "0a");

        keyManager
                .addKey(CLIENT_CIPHER_KEY,
                        Base64.decodeBase64("MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCcN0noNOeJ1NorwMr20hvteRsy7gxrl6srhCfdInG0ewb3OupM/+j0O6Y6rxikLrhDOh8Dso+fWXV3iKUs18eqIJWomz09tM2WiSLI6PiqCOw8w4kz3v8C0PobSBnLsSPaPAKi8I+Mu89ZrqraNFiDpXRw8PsW+dm4EO4RsYgf6wIDAQAB"
                                .getBytes(DEFAULT_CHARSET)), KeyType.PUBLIC, versionBidon);
        keyManager
                .addKey(CLIENT_DECIPHER_KEY,
                        Base64.decodeBase64("MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBAJw3Seg054nU2ivAyvbSG+15GzLuDGuXqyuEJ90icbR7Bvc66kz/6PQ7pjqvGKQuuEM6HwOyj59ZdXeIpSzXx6oglaibPT20zZaJIsjo+KoI7DzDiTPe/wLQ+htIGcuxI9o8AqLwj4y7z1muqto0WIOldHDw+xb52bgQ7hGxiB/rAgMBAAECgYAhhCdCP9+Rje6FRqcFT80YJUy1n6dgijj6bC9VXaeBroevEZ/lh16G0Cl5G8owp6l3Gwg+ddgkuz/dtp6hwWYf/0bM4LDZzJtUlYarg1dlP3NjoRQIYRuoAShRg2oVMez3qBgHfWjuMU4yn3qzoW21SLH1IvRAHnF72Qfsu97L6QJBANST6AGhzuyUK926shIxGzjPpPf4hnBFyI0d13iCE925RxUD0BQ8oCQ7yvptOMGbcSr2+mLFTZdDh6731RVAXxcCQQC8IB4nWUxYc/JdR0Mr3BDT2cF/W/+BHIFF/BPS4Iz8bq3JguZ/UWuYdh53stdkL3yBUCaFl2hxEWeOja6IFGpNAkB2Pxv0JHS9BqLsANji46v3yxFB2l22ACtRzAK6hujUEibnGqrZRuvxm80Jxk2m7Hdj5eRbotLJTIgiHLvLyXXbAkBirs5igbtEov295IXd2gCRwKZ58n5i8dr2JTcY5WXPfRd4n0vd+Y3hWgS9cXj/OtGq26p5ZWUJ+sFBzByDkYH5AkB3ebbwgUAT4siIF2lkn8zTz8CAPh05Z4TN2sfHXmznr4JofG25CRn5vXExUA3aeiwuxuUqJ3TlYCG81cnYIDkB"
                                .getBytes(DEFAULT_CHARSET)), KeyType.PRIVATE, versionBidon);
        keyManager
                .addKey(AGENT_CIPHER_KEY,
                        Base64.decodeBase64("MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCwMqb28PNl64a3TxpnL5ch8wZZcmQItKWVPRRlsE8ja1PXX3XnxzP8xIs+HbO/XZwhclpTCr7qsecZrqKrKMrOctJyiHIwjnWZOmXYG7HaW7cDO36zF7/IkurP2TfqEkbZocuaO5/RS1HHUkEFROQVEDBfXtAlbNLx0l6uuLi2LQIDAQAB"
                                .getBytes(DEFAULT_CHARSET)), KeyType.PUBLIC, versionBidon);

        keyManager
                .addKey(AGENT_DECIPHER_KEY,
                        Base64.decodeBase64("MIICeAIBADANBgkqhkiG9w0BAQEFAASCAmIwggJeAgEAAoGBALAypvbw82XrhrdPGmcvlyHzBllyZAi0pZU9FGWwTyNrU9dfdefHM/zEiz4ds79dnCFyWlMKvuqx5xmuoqsoys5y0nKIcjCOdZk6ZdgbsdpbtwM7frMXv8iS6s/ZN+oSRtmhy5o7n9FLUcdSQQVE5BUQMF9e0CVs0vHSXq64uLYtAgMBAAECgYEAjiiP5XUKLBUXIBbGarJO8akuWkScyRlW703Ugyb48s7JWl8+tb5XB31cCsXUHHXeXm3wykdFOf5HQsVPoWDMHuMnpnZQK6m6Hd9LwttkJjQM1vaFFI1b5BKfUHVATs5HyJZBTA04j/FIQDdRKcTBc0OZcbRyBIQU3Q2lIy4ElFECQQD0Bjxnr1jmycz6cu11eJFmoP7c1i5Wbns7Ucjr1dSUAVQ77q3J/xNqxuagEC5rnmJi78W2S9Y2I505pgoqg+izAkEAuNhJvH2AmGbqKd0ZmMToFlyNlNgk64iQ4PvMZnm11OwTINVNNDId+2IHBgoGP1+50Vjl+IT2lZdef/X4m7mVnwJAdhX2cS5XVQdem+u1oV0A76sE6hTzQ3wfQitnkUheoGisPHxZuSAoY0lxHQ/3hriUX9Ar2vWD6Zr+YIFK9hvzJQJBALHMokijs9FklPNF0LxJEzGHP0ygCAtjJnCy467ifcvcdJ3NbuC5IGvQpA7kD5RgAL3lM/gz9Xh4zJZrRAKOpeMCQQC3uQ8FQbCgo2H/PLfus1Tk2czJlt3hkce2tk3C0nfBtzxqUJoywAU5YxFHeMKKIwotb0RjVxjR7oEAXxmwRG+4"
                                .getBytes(DEFAULT_CHARSET)), KeyType.PRIVATE, versionBidon);
    }

    public RsaKeyInfo getKey(String keyName, AppVersion av) {
        return keyManager.getKey(keyName, av);
    }

}
