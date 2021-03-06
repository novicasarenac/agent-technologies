package utils;

import java.io.Serializable;

import model.ACLMessage;
import model.AID;
import model.AgentType;

public class AgentsCommunicationMessage implements Serializable {

	private String name;
	private AgentType agentType;
	private ACLMessage aclMessage;
	private AID aid;
	private AgentsCommunicationMessageType agentsCommunicationMessageType;
	private boolean status;

	public AgentsCommunicationMessage() {
		super();
		// TODO Auto-generated constructor stub
	}

	public AgentsCommunicationMessage(String name, AgentType agentType, ACLMessage aclMessage, AID aid,
			AgentsCommunicationMessageType agentsCommunicationMessageType, boolean status) {
		super();
		this.name = name;
		this.agentType = agentType;
		this.aclMessage = aclMessage;
		this.aid = aid;
		this.agentsCommunicationMessageType = agentsCommunicationMessageType;
		this.status = status;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public AgentType getAgentType() {
		return agentType;
	}

	public void setAgentType(AgentType agentType) {
		this.agentType = agentType;
	}

	public ACLMessage getAclMessage() {
		return aclMessage;
	}

	public void setAclMessage(ACLMessage aclMessage) {
		this.aclMessage = aclMessage;
	}

	public AID getAid() {
		return aid;
	}

	public void setAid(AID aid) {
		this.aid = aid;
	}

	public AgentsCommunicationMessageType getAgentsCommunicationMessageType() {
		return agentsCommunicationMessageType;
	}

	public void setAgentsCommunicationMessageType(AgentsCommunicationMessageType agentsCommunicationMessageType) {
		this.agentsCommunicationMessageType = agentsCommunicationMessageType;
	}

	public boolean isStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}

}
