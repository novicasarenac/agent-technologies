package utils;

import java.io.Serializable;
import java.util.List;

import model.ACLMessage;
import model.AID;
import model.AgentType;

public class WSMessage implements Serializable {

	private String name;
	private AgentType newAgentType;
	private AID aid;
	private List<AgentType> agentTypes;
	private ACLMessage aclMessage;
	private WSMessageType messageType;

	public WSMessage() {
		super();
		// TODO Auto-generated constructor stub
	}

	public WSMessage(String name, AgentType newAgentType, AID aid, List<AgentType> agentTypes, ACLMessage aclMessage,
			WSMessageType messageType) {
		super();
		this.name = name;
		this.newAgentType = newAgentType;
		this.aid = aid;
		this.agentTypes = agentTypes;
		this.aclMessage = aclMessage;
		this.messageType = messageType;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public AgentType getNewAgentType() {
		return newAgentType;
	}

	public void setNewAgentType(AgentType newAgentType) {
		this.newAgentType = newAgentType;
	}

	public AID getAid() {
		return aid;
	}

	public void setAid(AID aid) {
		this.aid = aid;
	}

	public List<AgentType> getAgentTypes() {
		return agentTypes;
	}

	public void setAgentTypes(List<AgentType> agentTypes) {
		this.agentTypes = agentTypes;
	}

	public ACLMessage getAclMessage() {
		return aclMessage;
	}

	public void setAclMessage(ACLMessage aclMessage) {
		this.aclMessage = aclMessage;
	}

	public WSMessageType getMessageType() {
		return messageType;
	}

	public void setMessageType(WSMessageType messageType) {
		this.messageType = messageType;
	}

}
