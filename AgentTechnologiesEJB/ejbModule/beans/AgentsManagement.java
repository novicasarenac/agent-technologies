package beans;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;

import model.AID;
import model.AgentType;
import server_management.SystemPropertiesKeys;
import utils.AgentTypesReader;

@Singleton
public class AgentsManagement implements AgentsManagementLocal {

	private List<AgentType> allTypes = new ArrayList<>();
	private List<AgentType> supportedTypes = new ArrayList<>();
	
	private Map<String, AID> runningAgents = new HashMap<>();
	
	@PostConstruct
	public void init() {
		String filename = System.getProperty(SystemPropertiesKeys.FILENAME);
		if(filename == null && System.getProperty(SystemPropertiesKeys.MASTER_NODE) == null)
			filename = "agents1.txt";
		
		AgentTypesReader reader = new AgentTypesReader();
		supportedTypes = reader.readTypes(filename);
		for(AgentType type : supportedTypes) {
			System.out.println("Supported type : " + type.getName());
			if(!allTypes.contains(type))
				allTypes.add(type);
		}
	}

	@Override
	public boolean addAgentType(AgentType agentType) {
		if(allTypes.stream().filter(type -> type.getName().equals(agentType.getName())).count() == 0) {
			System.out.println("New agent type: " + agentType.getName());
			allTypes.add(agentType);
			return true;
		} else return false;
	}
	
	@Override
	public void removeAgentType(AgentType agentType) {
		System.out.println("Agent type: " + agentType.getName() + " removed");
		AgentType typeToRemove = getAgentTypeByName(agentType.getName());
		allTypes.remove(typeToRemove);
	}
	
	public AgentType getAgentTypeByName(String name) {
		for(AgentType type : allTypes) {
			if(type.getName().equals(name))
				return type;
		}
		return null;
	}
	
	@Override
	public boolean addRunningAgent(String name, AID id) {
		if(!runningAgents.containsKey(name)) {
			runningAgents.put(name, id);
			return true;
		}
		return false;
	}
	
	@Override
	public boolean removeRunningAgent(String name) {
		if(runningAgents.containsKey(name)) {
			runningAgents.remove(name);
			return true;
		} else return false;
	}

	@Override
	public List<AgentType> getSupportedTypes() {
		return supportedTypes;
	}

	@Override
	public List<AgentType> getAllTypes() {
		return allTypes;
	}

	@Override
	public Map<String, AID> getRunningAgents() {
		return runningAgents;
	}

}
