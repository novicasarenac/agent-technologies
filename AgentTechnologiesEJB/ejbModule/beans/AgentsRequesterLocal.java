package beans;

import javax.ejb.Local;

import model.AgentCenter;
import model.AgentType;

@Local
public interface AgentsRequesterLocal {

	public boolean sendRunAgentRequest(AgentCenter agentCenter, String name, AgentType agentType);
	
}
