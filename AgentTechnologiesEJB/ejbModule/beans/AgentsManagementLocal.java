package beans;

import java.util.List;
import java.util.Map;

import javax.ejb.Local;

import model.AID;
import model.AgentType;

@Local
public interface AgentsManagementLocal {

	public void init();
	public boolean addAgentType(AgentType agentType);
	public List<AgentType> getSupportedTypes();
	public List<AgentType> getAllTypes();
	public Map<String, AID> getRunningAgents();
	public boolean addRunningAgent(String name, AID id);
	
}
