package beans;

import javax.ejb.Local;

import model.ACLMessage;
import model.AID;

@Local
public interface MessageManagerLocal {
	
	public void sendACL(ACLMessage aclMessage);
	public void sendMessageToAgent(AID agent, ACLMessage aclMessage);
		
}
