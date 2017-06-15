package beans;

import javax.ejb.Local;

import model.AgentCenter;

@Local
public interface ShutdownRequesterLocal {

	public void shutdownToNode(AgentCenter recipient, AgentCenter agentCenter);
	public void heartbeatShutdownNode(AgentCenter recipient, AgentCenter agentCenter);
	
}
