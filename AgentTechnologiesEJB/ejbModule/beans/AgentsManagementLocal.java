package beans;

import java.util.List;

import javax.ejb.Local;

import model.AgentType;

@Local
public interface AgentsManagementLocal {

	public void init();
	public boolean addAgentType(AgentType agentType);
	public List<AgentType> getSupportedTypes();
}
