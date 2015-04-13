package ca.jp.secproj.server.auth;

import java.util.Random;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jersey.core.util.Base64;

@Path("/auth/std")
public class StdAuthAPI {

	private static Logger logger = LoggerFactory.getLogger(StdAuthAPI.class);

	private String salt;

	private String hash;

	public StdAuthAPI() {
	}

	@Path("/initauth")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	/**
	 * Initiation d'une requête d'authentification et renvoi du salt.
	 * @param request La requête dans laquelle on trouve une entête d'authentification.
	 * @return Le salt associé à cet usager.
	 */
	public Response initAuth(@Context HttpServletRequest request) {
		String rawAuthHeader = request.getHeader("Authorization");
		if (StringUtils.isNotBlank(rawAuthHeader)) {
			String authHeader = Base64.base64Decode(rawAuthHeader
					.substring("Basic ".length()));
			String[] userAndPw = authHeader.split(":");
			if (userAndPw != null && userAndPw.length >= 1) {
				String user = userAndPw[0];
				logger.info("User {0} requesting authentication. ", user);
				return Response.status(Status.OK).entity(salt).build();
			}
		}
		return Response.status(Status.FORBIDDEN).build();
	}

	@Path("/checkauth")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	/**
	 * Vérification de l'authentification de l'usager.
	 * @param request
	 * @return
	 */
	public Response checkAuth(@Context HttpServletRequest request) {
		String rawAuthHeader = request.getHeader("Authorization");
		if (StringUtils.isNotBlank(rawAuthHeader)) {
			String authHeader = Base64.base64Decode(rawAuthHeader
					.substring("Basic ".length()));
			String[] userAndPw = authHeader.split(":");
			if (userAndPw != null && userAndPw.length >= 1) {
				String user = userAndPw[0];
				String hashedPw = userAndPw[1];
				logger.info("User {0} requesting authentication. ", user);
				if (StringUtils.isNotBlank(hashedPw) && hashedPw.equals(hash)) {
					String token = generateUniqueToken();
					logger.info("Granting access to user {0} with token {1}",
							user, token);
					return Response.status(Status.OK).entity(token).build();
				}
			}
		}
		return Response.status(Status.FORBIDDEN).build();
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

	public String getSalt() {
		return salt;
	}

	public void setSalt(String salt) {
		this.salt = salt;
	}

	public String getHash() {
		return hash;
	}

	public void setHash(String hash) {
		this.hash = hash;
	}

}
