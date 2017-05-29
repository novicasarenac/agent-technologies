package beans;

import java.util.HashMap;
import java.util.Map;

import javax.ejb.Singleton;

import exceptions.AliasExistsException;
import model.AgentCenter;

@Singleton
public class DataManagement implements DataManagementLocal {

	private Map<String, AgentCenter> agentCenters = new HashMap<>();
	
	@Override
	public void register(AgentCenter agentCenter) throws AliasExistsException {
		if(agentCenters.containsKey(agentCenter.getAlias()))
			throw new AliasExistsException();
		
		agentCenters.put(agentCenter.getAlias(), agentCenter);
	}

	public Map<String, AgentCenter> getAgentCenters() {
		return agentCenters;
	}
	
}
