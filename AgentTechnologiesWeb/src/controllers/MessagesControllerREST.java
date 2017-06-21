package controllers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.ejb.Stateless;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import model.ACLMessage;
import model.Performative;

@Path("/messages")
@Stateless
public class MessagesControllerREST {

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public void sendACL(ACLMessage aclMessage) {
		
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<Performative> getPerformatives() {
		List<Performative> retVal = Arrays.asList(Performative.values());
		
		return retVal;		
	}
}
