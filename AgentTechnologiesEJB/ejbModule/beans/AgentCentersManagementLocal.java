package beans;

import java.util.Map;

import javax.ejb.Local;

import exceptions.AliasExistsException;
import model.AgentCenter;

@Local
public interface AgentCentersManagementLocal {

	public void register(AgentCenter agentCenter) throws AliasExistsException;
	public Map<String, AgentCenter> getAgentCenters();
	
}
