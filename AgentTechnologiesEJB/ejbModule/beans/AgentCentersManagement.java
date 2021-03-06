package beans;

import java.util.HashMap;
import java.util.Map;

import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;

import exceptions.AliasExistsException;
import model.AgentCenter;

@Singleton
public class AgentCentersManagement implements AgentCentersManagementLocal {

	private Map<String, AgentCenter> agentCenters = new HashMap<>();
	
	@Override
	public void register(AgentCenter agentCenter) throws AliasExistsException {
		if(agentCenters.containsKey(agentCenter.getAlias()))
			throw new AliasExistsException();
		
		System.out.println("Agent center: " + agentCenter.getAlias() + " registered.");
		agentCenters.put(agentCenter.getAlias(), agentCenter);
	}
	
	@Override
	@Lock(LockType.WRITE)
	public void removeCenter(AgentCenter agentCenter) {
		if(agentCenters.containsKey(agentCenter.getAlias())) {
			System.out.println("Agent center: " + agentCenter.getAlias() + " removed.");
			agentCenters.remove(agentCenter.getAlias());
		}
	}

	@Override
	public Map<String, AgentCenter> getAgentCenters() {
		return agentCenters;
	}

}
