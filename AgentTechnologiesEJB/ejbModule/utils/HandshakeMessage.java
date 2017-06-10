package utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.AID;
import model.AgentCenter;
import model.AgentType;

public class HandshakeMessage implements Serializable {

	private HandshakeMessageType messsageType;
	private AgentCenter newAgentCenter;
	private List<AgentType> agentTypes;
	private Map<String, AgentCenter> agentCenters;
	private Map<String, AID> runningAgents;
	private boolean status;

	public HandshakeMessage() {
		super();
	}

	public HandshakeMessage(HandshakeMessageType messsageType, AgentCenter newAgentCenter, List<AgentType> agentTypes,
			Map<String, AgentCenter> agentCenters, Map<String, AID> runningAgents, boolean status) {
		super();
		this.messsageType = messsageType;
		this.newAgentCenter = newAgentCenter;
		this.agentTypes = agentTypes;
		this.agentCenters = agentCenters;
		this.runningAgents = runningAgents;
		this.status = status;
	}

	public HandshakeMessageType getMesssageType() {
		return messsageType;
	}

	public void setMesssageType(HandshakeMessageType messsageType) {
		this.messsageType = messsageType;
	}

	public AgentCenter getNewAgentCenter() {
		return newAgentCenter;
	}

	public void setNewAgentCenter(AgentCenter newAgentCenter) {
		this.newAgentCenter = newAgentCenter;
	}

	public List<AgentType> getAgentTypes() {
		return agentTypes;
	}

	public void setAgentTypes(List<AgentType> agentTypes) {
		this.agentTypes = agentTypes;
	}

	public Map<String, AgentCenter> getAgentCenters() {
		return agentCenters;
	}

	public void setAgentCenters(Map<String, AgentCenter> agentCenters) {
		this.agentCenters = agentCenters;
	}

	public Map<String, AID> getRunningAgents() {
		return runningAgents;
	}

	public void setRunningAgents(Map<String, AID> runningAgents) {
		this.runningAgents = runningAgents;
	}

	public boolean isStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}

}
