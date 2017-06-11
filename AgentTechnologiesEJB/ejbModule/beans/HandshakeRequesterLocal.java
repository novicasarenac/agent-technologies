package beans;

import java.util.List;

import javax.ejb.Local;

import model.AgentCenter;
import model.AgentType;
import utils.HandshakeMessage;

@Local
public interface HandshakeRequesterLocal {

	public boolean sendRegisterRequest(String address, String alias);
	public HandshakeMessage sendGetAgentTypesRequest(AgentCenter agentCenter);
	public HandshakeMessage notifyNode(AgentCenter newAgentCenter, List<AgentType> agentTypes, AgentCenter nodeToNotify);
}
