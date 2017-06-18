package model;

import java.io.Serializable;

public interface AgentRemote extends Serializable {

	public void init(AID aid);
	public void handleMessage(ACLMessage message);
	
}
