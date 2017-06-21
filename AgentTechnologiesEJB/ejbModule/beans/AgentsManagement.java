package beans;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.LockType;
import javax.ejb.Lock;
import javax.ejb.Singleton;

import model.AID;
import model.AgentCenter;
import model.AgentType;
import server_management.AppManagementLocal;
import server_management.SystemPropertiesKeys;
import utils.AgentTypesReader;

@Singleton
public class AgentsManagement implements AgentsManagementLocal {

	@EJB
	AppManagementLocal appManagement;
	
	@EJB
	ClientNotificationsRequesterLocal clientNotificationRequester;
	
	private Map<String, List<AgentType>> allTypes = new HashMap<>();
	private List<AgentType> supportedTypes = new ArrayList<>();
	
	private Map<String, AID> runningAgents = new HashMap<>();
	
	@PostConstruct
	public void init() {
		String filename = System.getProperty(SystemPropertiesKeys.FILENAME);
		if(filename == null && System.getProperty(SystemPropertiesKeys.MASTER_NODE) == null)
			filename = "agents1.txt";
		
		AgentTypesReader reader = new AgentTypesReader();
		supportedTypes = reader.readTypes(filename);
		allTypes.put(appManagement.getLocalAlias(), new ArrayList<>());
		for(AgentType type : supportedTypes) {
			System.out.println("Supported type : " + type.getName());
			allTypes.get(appManagement.getLocalAlias()).add(type);
		}
	}
	
	@Override
	public boolean addAgentTypes(AgentCenter agentCenter, List<AgentType> agentTypes) {
		if(!allTypes.containsKey(agentCenter.getAlias())) {
			allTypes.put(agentCenter.getAlias(), new ArrayList<>());
			for(AgentType type : agentTypes) {
				System.out.println(agentCenter.getAlias() + " supports agent type: " + type.getName());
				allTypes.get(agentCenter.getAlias()).add(type);
			}
			return true;
		} else return false;
	}
	
	@Override
	@Lock(LockType.READ)
	public boolean removeAgentTypes(AgentCenter agentCenter) {
		if(allTypes.containsKey(agentCenter.getAlias())) {
			System.out.println("Agent types of " + agentCenter.getAlias() + " removed");
			allTypes.remove(agentCenter.getAlias());
			return true;
		} else return false;
	}

	@Override
	public boolean addRunningAgent(String name, AID id) {
		if(!runningAgents.containsKey(name)) {
			System.out.println("Agent " + name + " is running at " + id.getHost().getAlias());
			runningAgents.put(name, id);
			return true;
		}
		return false;
	}
	
	@Override
	public AID removeRunningAgent(String name) {
		AID retVal = null;
		if(runningAgents.containsKey(name)) {
			retVal = runningAgents.remove(name);
			System.out.println("Agent " + name + " stopped!");
		}
		return retVal;
	}
	
	@Override
	@Lock(LockType.WRITE)
	public void removeRunningAgents(AgentCenter agentCenter) {
		List<String> agentsToRemove = new ArrayList<>();
		for(String key : runningAgents.keySet()) {
			if(agentCenter.getAlias().equals(runningAgents.get(key).getHost().getAlias()))
				agentsToRemove.add(key);
		}
		
		for(String key : agentsToRemove) {
			runningAgents.remove(key);
		}
	}

	@Override
	public List<AgentType> getSupportedTypes() {
		return supportedTypes;
	}

	@Override
	public Map<String, List<AgentType>> getAllTypes() {
		return allTypes;
	}

	@Override
	public Map<String, AID> getRunningAgents() {
		return runningAgents;
	}

}
