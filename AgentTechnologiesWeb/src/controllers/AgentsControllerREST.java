package controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import beans.AgentsManagementLocal;
import model.AID;
import model.AgentType;

@Stateless
@Path("/agents")
public class AgentsControllerREST {

	@EJB
	AgentsManagementLocal agentsManagement;
	
	@GET
	@Path("/classes")
	@Produces(MediaType.APPLICATION_JSON)
	public List<AgentType> getAgentClasses() {
		List<AgentType> retVal = new ArrayList<>();
		for(String key : agentsManagement.getAllTypes().keySet()) {
			List<AgentType> temp = agentsManagement.getAllTypes().get(key).stream().filter(type -> !retVal.contains(type)).collect(Collectors.toList());
			retVal.addAll(temp);
		}
		return retVal;
	}
	
	@GET
	@Path("/running")
	@Produces(MediaType.APPLICATION_JSON)
	public List<AID> getRunningAgents() {
		List<AID> retVal = new ArrayList<>();
		for(AID aid : agentsManagement.getRunningAgents().values()) {
			retVal.add(aid);
		}
		return retVal;
	}
	
	@PUT
	@Path("/running/{name}")
	@Consumes(MediaType.APPLICATION_JSON)
	public void runAgent(@PathParam("name") String name, AgentType agentType) {
		
	}
	
	@DELETE
	@Path("/running/{name}")
	public void deleteAgent(@PathParam("name") String name) {
		
	}
}
