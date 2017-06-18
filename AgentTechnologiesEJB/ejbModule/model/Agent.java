package model;

import java.io.Serializable;

public abstract class Agent implements AgentRemote {
	
	private AID id;
	
	@Override
	public void init(AID aid) {
		this.id = aid;
	}
	
	public AID getId() {
		return id;
	}

	public void setId(AID id) {
		this.id = id;
	}
}
