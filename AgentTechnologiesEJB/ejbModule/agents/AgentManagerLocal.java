package agents;

import java.util.Map;

import javax.ejb.Local;

import exceptions.NameExistsException;
import model.AID;
import model.AgentLocal;
import model.AgentType;

@Local
public interface AgentManagerLocal {

	public AID runAgent(String name, AgentType agentType) throws NameExistsException;
	public void stopAgent(String name);
	public Map<String, AgentLocal> getRunningAgents();
	
}
