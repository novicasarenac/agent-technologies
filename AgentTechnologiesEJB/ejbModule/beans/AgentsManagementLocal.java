package beans;

import java.util.List;
import java.util.Map;

import javax.ejb.Local;

import model.AID;
import model.AgentCenter;
import model.AgentType;

@Local
public interface AgentsManagementLocal {

	public void init();
	public boolean addAgentTypes(AgentCenter agentCenter, List<AgentType> agentTypes);
	public boolean removeAgentTypes(AgentCenter agentCenter);
	public List<AgentType> getSupportedTypes();
	public Map<String, List<AgentType>> getAllTypes();
	public Map<String, AID> getRunningAgents();
	public void removeRunningAgents(AgentCenter agentCenter);
	public boolean addRunningAgent(String name, AID id);
	
}
