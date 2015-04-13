package ca.jp.secproj.server;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/basic")
public class BasicTest {

	@Path("/hello")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String hello() {
		return "hello!";
	}

}
