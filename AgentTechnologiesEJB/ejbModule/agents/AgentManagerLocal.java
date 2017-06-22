package agents;

import java.util.Map;

import javax.ejb.Local;

import exceptions.NameExistsException;
import model.ACLMessage;
import model.AID;
import model.AgentLocal;
import model.AgentType;

@Local
public interface AgentManagerLocal {

	public AID runAgent(String name, AgentType agentType) throws NameExistsException;
	public void stopAgent(String name);
	public void deliverMessageToAgent(AID agent, ACLMessage message);
	public Map<String, AgentLocal> getRunningAgents();
	public void sendRunningAgentNotification(AID aid);
	public void sendStopNotification(String name);
	public void sendNewMessageNotification(AID aid, ACLMessage message);
	public Map<String, AID> getAllRunningAgents();
	
}
