package ca.jp.secproj.server.auth;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

import javax.ws.rs.Consumes;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.crypto.CryptoException;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.agreement.jpake.JPAKEParticipant;
import org.bouncycastle.crypto.agreement.jpake.JPAKEPrimeOrderGroup;
import org.bouncycastle.crypto.agreement.jpake.JPAKEPrimeOrderGroups;
import org.bouncycastle.crypto.agreement.jpake.JPAKERound1Payload;
import org.bouncycastle.crypto.agreement.jpake.JPAKERound2Payload;
import org.bouncycastle.crypto.agreement.jpake.JPAKERound3Payload;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Path("/auth/jpake")
@Service
public class JPakeAuthAPI {

    private static Logger logger = LoggerFactory.getLogger(JPakeAuthAPI.class);

    private final static Long JPAKE_PARTICIPANT_TTL = 600000l;

    private JPAKEPrimeOrderGroup group;

    private Digest digest;

    private SecureRandom random;

    @Autowired
    private String password;

    private Map<String, JPAKEParticipant> jPakeParticipants;

    private TreeMap<String, Long> jPakeParticipantsTTL;

    public JPakeAuthAPI() {
        this.group = JPAKEPrimeOrderGroups.NIST_3072;
        this.digest = new SHA256Digest();
        this.random = new SecureRandom();

        this.jPakeParticipants = new HashMap<>();
        this.jPakeParticipantsTTL = new TreeMap<>();
    }

    @Path("/firstround")
    @POST
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    @Consumes(MediaType.APPLICATION_OCTET_STREAM)
    public Response firstRound(InputStream is) {

        String serverUser = generateUniqueToken();

        if (StringUtils.isBlank(serverUser)) {
            logger.warn("User not valid.");
            return Response.status(Status.BAD_REQUEST).build();
        }
        JPAKERound1Payload clientRound1Payload = null;
        ObjectInputStream in = null;
        ObjectOutputStream oos = null;
        ByteArrayOutputStream baos = null;

        try {
            JPAKEParticipant serverParticipant = new JPAKEParticipant(serverUser, password.toCharArray(), group,
                    digest, random);
            JPAKERound1Payload serverRound1Payload = serverParticipant.createRound1PayloadToSend();

            in = new ObjectInputStream(is);
            clientRound1Payload = (JPAKERound1Payload) in.readObject();
            serverParticipant.validateRound1PayloadReceived(clientRound1Payload);

            logger.debug("Bob checks g^{x2}!=1: OK");
            logger.debug("Bob checks KP{x1},: OK");
            logger.debug("Bob checks KP{x2},: OK");

            baos = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(baos);
            oos.writeObject(serverRound1Payload);
            byte[] data = baos.toByteArray();
            InputStream is2 = new ByteArrayInputStream(data);

            jPakeParticipants.put(serverUser, serverParticipant);
            jPakeParticipantsTTL.put(serverUser, System.currentTimeMillis() + JPAKE_PARTICIPANT_TTL);

            return Response.status(Status.OK).entity(is2).header("user", serverUser).build();
        } catch (CryptoException | IOException | ClassNotFoundException e) {
            logger.warn("Error in round 1 POST", e);
            return Response.status(Status.INTERNAL_SERVER_ERROR).build();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    logger.warn("Error in round 1 POST", e);
                }
            }
            if (oos != null) {
                try {
                    oos.close();
                } catch (IOException e) {
                    logger.warn("Error in round 1 POST", e);
                }
            }
            if (baos != null) {
                try {
                    baos.close();
                } catch (IOException e) {
                    logger.warn("Error in round 1 POST", e);
                }
            }
        }
    }

    @Path("/secondround")
    @POST
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    @Consumes(MediaType.APPLICATION_OCTET_STREAM)
    public Response secondRound(InputStream is, @HeaderParam("user")
    String user) {
        if (StringUtils.isBlank(user)) {
            logger.info("User not valid");
            return Response.status(Status.BAD_REQUEST).build();
        }
        JPAKEParticipant serverParticipant = jPakeParticipants.get(user);
        if (serverParticipant == null) {
            logger.info("User not found. ");
            return Response.status(Status.INTERNAL_SERVER_ERROR).build();
        }
        Long participantTTl = jPakeParticipantsTTL.get(user);
        if (System.currentTimeMillis() > participantTTl) {
            logger.info("JPakeParticipant expired! Authentication process aborted. ");
            return Response.status(Status.INTERNAL_SERVER_ERROR).build();
        }

        JPAKERound2Payload clientPayload = null;
        ObjectInputStream in = null;
        ObjectOutputStream oos = null;
        ByteArrayOutputStream baos = null;

        try {

            JPAKERound2Payload serverRound2Payload = serverParticipant.createRound2PayloadToSend();

            in = new ObjectInputStream(is);
            clientPayload = (JPAKERound2Payload) in.readObject();
            serverParticipant.validateRound2PayloadReceived(clientPayload);

            baos = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(baos);
            oos.writeObject(serverRound2Payload);
            byte[] data = baos.toByteArray();
            InputStream is2 = new ByteArrayInputStream(data);

            return Response.status(Status.OK).entity(is2).build();
        } catch (CryptoException | IOException | ClassNotFoundException e) {
            logger.warn("Error in round 1 POST", e);
            return Response.status(Status.INTERNAL_SERVER_ERROR).build();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    logger.warn("Error in round 2 POST", e);
                }
            }
            if (oos != null) {
                try {
                    oos.close();
                } catch (IOException e) {
                    logger.warn("Error in round 2 POST", e);
                }
            }
            if (baos != null) {
                try {
                    baos.close();
                } catch (IOException e) {
                    logger.warn("Error in round 2 POST", e);
                }
            }
        }
    }

    @Path("/thirdround")
    @POST
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    @Consumes(MediaType.APPLICATION_OCTET_STREAM)
    public Response thirdRound(InputStream is, @HeaderParam("user")
    String user) {
        if (StringUtils.isBlank(user)) {
            logger.info("User not valid");
            return Response.status(Status.BAD_REQUEST).build();
        }
        JPAKEParticipant serverParticipant = jPakeParticipants.get(user);
        if (serverParticipant == null) {
            logger.info("User not found. ");
            return Response.status(Status.INTERNAL_SERVER_ERROR).build();
        }
        Long participantTTl = jPakeParticipantsTTL.get(user);
        if (System.currentTimeMillis() > participantTTl) {
            logger.info("JPakeParticipant expired! Authentication process aborted. ");
            return Response.status(Status.INTERNAL_SERVER_ERROR).build();
        }

        JPAKERound3Payload clientPayload = null;
        ObjectInputStream in = null;
        ObjectOutputStream oos = null;
        ByteArrayOutputStream baos = null;

        try {

            BigInteger serverKeyMaterial = serverParticipant.calculateKeyingMaterial();
            BigInteger serverKey = deriveSessionKey(serverKeyMaterial);

            JPAKERound3Payload serverRound3Payload = serverParticipant.createRound3PayloadToSend(serverKeyMaterial);

            logger.info("************ Round 3 **************");
            logger.info("Bob sends to Alice: ");
            logger.info("MacTag=" + serverRound3Payload.getMacTag().toString(16));

            in = new ObjectInputStream(is);
            clientPayload = (JPAKERound3Payload) in.readObject();
            serverParticipant.validateRound3PayloadReceived(clientPayload, serverKeyMaterial);

            baos = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(baos);
            oos.writeObject(serverRound3Payload);
            byte[] data = baos.toByteArray();
            InputStream is3 = new ByteArrayInputStream(data);

            jPakeParticipants.remove(user);
            jPakeParticipantsTTL.remove(user);

            return Response.status(Status.OK).entity(is3).build();
        } catch (CryptoException | IOException | ClassNotFoundException e) {
            logger.warn("Error in round 3 POST", e);
            return Response.status(Status.INTERNAL_SERVER_ERROR).build();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    logger.warn("Error in round 3 POST", e);
                }
            }
            if (oos != null) {
                try {
                    oos.close();
                } catch (IOException e) {
                    logger.warn("Error in round 3 POST", e);
                }
            }
            if (baos != null) {
                try {
                    baos.close();
                } catch (IOException e) {
                    logger.warn("Error in round 3 POST", e);
                }
            }
        }
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    private static BigInteger deriveSessionKey(BigInteger keyingMaterial) {
        /*
         * You should use a secure key derivation function (KDF) to derive the
         * session key.
         * 
         * For the purposes of this example, I'm just going to use a hash of the
         * keying material.
         */
        SHA256Digest digest = new SHA256Digest();

        byte[] keyByteArray = keyingMaterial.toByteArray();

        byte[] output = new byte[digest.getDigestSize()];

        digest.update(keyByteArray, 0, keyByteArray.length);

        digest.doFinal(output, 0);

        return new BigInteger(output);
    }

    /**
     * Génération d'un identifiant uniques composé de 32 caractères
     * alphanumériques ASCII (10 chiffres et 26 lettres). Le nombre total de
     * chaînes est 36^32 donc environ 6,33e10^49. Le risque de collision est
     * donc très faible, même pour une collection d'un grand nombre de ces clés.
     * 
     * @return Un identifiant unique composé de 32 caractèrs ASCII.
     */
    public static String generateUniqueToken() {
        Random generateur = new Random();
        String token = "";

        int numberAsciiStart = 48;
        int numberAsciiEnd = 57;
        int letterAsciiStart = 65;
        int letterAsciiEnd = 90;

        int start = numberAsciiStart;
        int end = letterAsciiEnd;

        int numbers = 0;
        while (numbers < 32) {

            long range = (long) end - (long) start + 1;
            long fraction = (long) (range * generateur.nextDouble());
            int randomNumber = (int) (fraction + start);

            if ((randomNumber >= numberAsciiStart && randomNumber <= numberAsciiEnd)
                    || (randomNumber >= letterAsciiStart && randomNumber <= letterAsciiEnd)) {
                token += (char) (randomNumber);
                numbers++;
            }
        }

        return token;
    }

}
