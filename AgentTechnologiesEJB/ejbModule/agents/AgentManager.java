package agents;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import beans.AgentCentersManagementLocal;
import beans.AgentsManagementLocal;
import beans.AgentsRequesterLocal;
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
	AgentCentersManagementLocal agentCentersManagement;
	
	@EJB
	AppManagementLocal appManagement;
	
	@EJB
	AgentsRequesterLocal agentsRequester;
	
	@PostConstruct
	public void init() {
		localAgents = new HashMap<>();
	}

	@Override
	public AID runAgent(String name, AgentType agentType) throws NameExistsException {
		if(agentsManagement.getRunningAgents().keySet().contains(name)) {
			throw new NameExistsException();
		}
		if(agentsManagement.getSupportedTypes().stream().anyMatch(type -> type.getName().equals(agentType.getName()))) {
			AgentLocal agent;
			try {
				Context context = new InitialContext();
				String beanName = "java:module/" + agentType.getName();
				agent = (AgentLocal) context.lookup(beanName);
				agent.setId(new AID(name, new AgentCenter(appManagement.getLocalAlias(), appManagement.getLocal()), agentType));
				localAgents.put(name, agent);
				System.out.println("Agent " + agent.getId().getName() + " started!");
				agentsManagement.addRunningAgent(name, agent.getId());
				sendRunningAgentNotification(agent.getId());
				return agent.getId();
			} catch (NamingException e) {
				e.printStackTrace();
			}
			return null;
		} else {
			for(String key : agentsManagement.getAllTypes().keySet()) {
				if(agentsManagement.getAllTypes().get(key).stream().anyMatch(type -> type.getName().equals(agentType.getName()))) {
					AgentCenter agentCenter = agentCentersManagement.getAgentCenters().get(key);
					boolean started = agentsRequester.sendRunAgentRequest(agentCenter, name, agentType);
					if(started) {
						AID aid = new AID(name, agentCenter, agentType);
						agentsManagement.addRunningAgent(name, aid);
						return aid;
					} else {
						return null;
					}
				}
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
	
	@Override
	public void sendRunningAgentNotification(AID aid) {
		for(AgentCenter agentCenter : agentCentersManagement.getAgentCenters().values()) {
			if(!agentCenter.getAlias().equals(appManagement.getLocalAlias())) {
				agentsRequester.sendNewRunningAgent(agentCenter, aid);
			}
		}
	}
	
}
