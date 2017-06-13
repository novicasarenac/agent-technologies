package beans;

import javax.ejb.Local;

import model.AgentCenter;

@Local
public interface ShutdownRequesterLocal {

	public void shutdown(AgentCenter agentCenter);
	
}
