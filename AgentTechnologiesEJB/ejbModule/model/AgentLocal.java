package model;

import java.io.Serializable;

public interface AgentLocal extends Serializable {

	public void init(AID aid);
	public void handleMessage(ACLMessage message);
	public void setId(AID id);
	public AID getId();
	
}
