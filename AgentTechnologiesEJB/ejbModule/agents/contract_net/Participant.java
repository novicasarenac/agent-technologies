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
public class Participant extends Agent {

	@EJB
	MessageManagerLocal messageManager;
	
	@Override
	public void handleMessage(ACLMessage message) {
		if(message.getPerformative() == Performative.CALL_FOR_PROPOSAL) {
			System.out.println("CALL FOR PROPOSAL");
			ACLMessage aclMessage = new ACLMessage();
			aclMessage.setSender(message.getReceivers().get(0));
			aclMessage.getReceivers().add(message.getSender());
			if(ThreadLocalRandom.current().nextInt(1, 100) > 50) {
				aclMessage.setPerformative(Performative.PROPOSE);
			} else {
				aclMessage.setPerformative(Performative.REFUSE);
			}
			
			messageManager.sendACL(aclMessage);
		} else if(message.getPerformative() == Performative.ACCEPT_PROPOSAL) {
			ACLMessage aclMessage = new ACLMessage();
			aclMessage.setSender(message.getReceivers().get(0));
			aclMessage.getReceivers().add(message.getSender());
			if(ThreadLocalRandom.current().nextInt(1, 100) > 80) {
				aclMessage.setPerformative(Performative.INFORM);
			} else {
				aclMessage.setPerformative(Performative.FAILURE);
			}
			
			messageManager.sendACL(aclMessage);
		} else if(message.getPerformative() == Performative.REJECT_PROPOSAL) {
			System.out.println("REJECT PROPOSAL");
		}
	}

}
