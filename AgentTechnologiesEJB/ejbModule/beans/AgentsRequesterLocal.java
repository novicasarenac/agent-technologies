package beans;

import javax.ejb.Local;

import model.AID;
import model.AgentCenter;
import model.AgentType;

@Local
public interface AgentsRequesterLocal {

	public boolean sendRunAgentRequest(AgentCenter agentCenter, String name, AgentType agentType);
	public void sendStopAgentRequest(AgentCenter agentCenter, String name);
	public boolean sendNewRunningAgent(AgentCenter agentCenter, AID aid);
	public void sendStoppedAgentMessage(AgentCenter agentCenter, String name);
	
}
