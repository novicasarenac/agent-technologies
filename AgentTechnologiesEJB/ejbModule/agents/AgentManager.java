package agents;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
import beans.ClientNotificationsRequesterLocal;
import exceptions.NameExistsException;
import model.ACLMessage;
import model.AID;
import model.AgentCenter;
import model.AgentLocal;
import model.AgentType;
import server_management.AppManagementLocal;

@Singleton
public class AgentManager implements AgentManagerLocal {
	
	private Map<String, AgentLocal> localAgents;
	public static List<String> mapReduceFiles;
	
	@EJB
	AgentsManagementLocal agentsManagement;
	
	@EJB
	AgentCentersManagementLocal agentCentersManagement;
	
	@EJB
	AppManagementLocal appManagement;
	
	@EJB
	AgentsRequesterLocal agentsRequester;
	
	@EJB
	ClientNotificationsRequesterLocal clientNotificationRequester;
	
	@PostConstruct
	public void init() {
		localAgents = new HashMap<>();
		mapReduceFiles = new ArrayList<>();
		mapReduceFiles.add("/map_reduce/file1.txt");
		mapReduceFiles.add("/map_reduce/file2.txt");
		mapReduceFiles.add("/map_reduce/file3.txt");
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
				clientNotificationRequester.sendNewRunningAgentNotification(agent.getId());
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
		AID aid = agentsManagement.getRunningAgents().get(name);
		if(aid.getHost().getAlias().equals(appManagement.getLocalAlias())) {
			localAgents.remove(name);
			System.out.println("Agent " + name + " stopped at this node.");
			agentsManagement.removeRunningAgent(name);
			sendStopNotification(name);
			clientNotificationRequester.sendStopAgentNotification(aid);
		} else {
			agentsRequester.sendStopAgentRequest(aid.getHost(), name);
		}
	}
	
	@Override
	public void deliverMessageToAgent(AID agent, ACLMessage message) {
		localAgents.get(agent.getName()).handleMessage(message);
		clientNotificationRequester.sendMessageNotification(agent, message);
		sendNewMessageNotification(agent, message);
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
	
	@Override
	public void sendStopNotification(String name) {
		for(AgentCenter agentCenter : agentCentersManagement.getAgentCenters().values()) {
			if(!agentCenter.getAlias().equals(appManagement.getLocalAlias())) {
				agentsRequester.sendStoppedAgentMessage(agentCenter, name);
			}
		}
	}
	
	@Override
	public void sendNewMessageNotification(AID aid, ACLMessage message) {
		for(AgentCenter agentCenter : agentCentersManagement.getAgentCenters().values()) {
			if(!agentCenter.getAlias().equals(appManagement.getLocalAlias())) {
				agentsRequester.sendNewMessage(agentCenter, aid, message);
			}
		}
	}
	
	@Override
	public Map<String, AID> getAllRunningAgents() {
		return agentsManagement.getRunningAgents();
	}
	
}
