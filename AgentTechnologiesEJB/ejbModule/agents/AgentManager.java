package agents;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import beans.AgentsManagementLocal;
import exceptions.NameExistsException;
import model.AID;
import model.AgentCenter;
import model.AgentLocal;
import model.AgentType;
import server_management.AppManagementLocal;

@Singleton
public class AgentManager implements AgentManagerLocal {
	
	private Map<String, AgentLocal> localAgents;
	
	@EJB
	AgentsManagementLocal agentsManagement;
	
	@EJB
	AppManagementLocal appManagement;
	
	@PostConstruct
	public void init() {
		localAgents = new HashMap<>();
	}

	@Override
	public AID runAgent(String name, AgentType agentType) throws NameExistsException {
		AgentLocal agent;
		if(agentsManagement.getRunningAgents().keySet().contains(name)) {
			throw new NameExistsException();
		}
		else {
			try {
				Context context = new InitialContext();
				String beanName = "java:module/" + agentType.getName();
				agent = (AgentLocal) context.lookup(beanName);
				agent.setId(new AID(name, new AgentCenter(appManagement.getLocalAlias(), appManagement.getLocal()), agentType));
				localAgents.put(name, agent);
				System.out.println("Agent " + agent.getId().getName() + " started!");
				return agent.getId();
			} catch (NamingException e) {
				e.printStackTrace();
			}
		}
		
		return null;
	}

	@Override
	public void stopAgent(String name) {
		
	}

	@Override
	public Map<String, AgentLocal> getRunningAgents() {
		return localAgents;
	}
	
}
