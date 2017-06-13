package beans;

import java.util.List;
import java.util.Map;

import javax.ejb.Local;

import model.AID;
import model.AgentCenter;
import model.AgentType;
import utils.HandshakeMessage;

@Local
public interface HandshakeRequesterLocal {

	public boolean sendRegisterRequest(String address, String alias);
	public HandshakeMessage sendGetAgentTypesRequest(AgentCenter agentCenter);
	public HandshakeMessage notifyNode(AgentCenter newAgentCenter, List<AgentType> agentTypes, AgentCenter nodeToNotify);
	public HandshakeMessage sendDataToNewNode(AgentCenter newNode, Map<String, AgentCenter> agentCenters, Map<String, List<AgentType>> agentTypes, Map<String, AID> runningAgents);
	public boolean cleanNode(AgentCenter nodeToClean, AgentCenter newAgentCenter, Map<String, AID> runningAgents);
}
