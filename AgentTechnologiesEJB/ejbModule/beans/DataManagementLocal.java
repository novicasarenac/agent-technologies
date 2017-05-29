package beans;

import javax.ejb.Local;

import exceptions.AliasExistsException;
import model.AgentCenter;

@Local
public interface DataManagementLocal {

	public void register(AgentCenter agentCenter) throws AliasExistsException;
	
}
