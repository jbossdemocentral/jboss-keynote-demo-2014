package org.jboss.keynote.endpoints;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.jboss.keynote.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/queryservice")
public class QueryRestfulService {
	private static Logger logger = LoggerFactory.getLogger(QueryRestfulService.class);
	@GET
	@Path("/contact/{clientName}")
	@Produces(MediaType.APPLICATION_JSON)
	public User getContact(@PathParam("clientName") String clientName){
		return null;
	}
}
