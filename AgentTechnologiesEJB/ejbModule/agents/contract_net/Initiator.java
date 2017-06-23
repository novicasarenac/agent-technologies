package agents.contract_net;

import java.util.concurrent.ThreadLocalRandom;

import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Stateful;

import beans.MessageManagerLocal;
import model.ACLMessage;
import model.Agent;
import model.AgentLocal;
import model.Performative;

@Stateful
@Local(AgentLocal.class)
public class Initiator extends Agent {

	@EJB
	MessageManagerLocal messageManager;
	
	@Override
	public void handleMessage(ACLMessage message) {
		if(message.getPerformative() == Performative.PROPOSE) {
			System.out.println("PROPOSE");
			ACLMessage aclMessage = new ACLMessage();
			if(ThreadLocalRandom.current().nextInt(1, 100) > 50) {
				aclMessage.setPerformative(Performative.ACCEPT_PROPOSAL);
			} else {
				aclMessage.setPerformative(Performative.REJECT_PROPOSAL);
			}
			
			aclMessage.setSender(message.getReceivers().get(0));
			aclMessage.getReceivers().add(message.getSender());
			messageManager.sendACL(aclMessage);
		} else if(message.getPerformative() == Performative.REFUSE) {
			System.out.println("REFUSE");
		} else if(message.getPerformative() == Performative.FAILURE) {
			System.out.println("FAILURE");
		} else if(message.getPerformative() == Performative.INFORM) {
			System.out.println("INFORM");
		}
	}

}
