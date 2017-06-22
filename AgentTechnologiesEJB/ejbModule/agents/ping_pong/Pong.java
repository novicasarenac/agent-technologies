package agents.ping_pong;

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
public class Pong extends Agent {
	
	private int count;

	@EJB
	MessageManagerLocal messageManager;
	
	@Override
	public void handleMessage(ACLMessage message) {
		if(message.getPerformative() == Performative.REQUEST) {
			count++;
			System.out.println("Message from: " + message.getSender().getName() + "(Ping). Count: " + count);
			ACLMessage response = new ACLMessage();
			if(message.getReplyTo() != null)
				response.getReceivers().add(message.getReplyTo());
			else
				response.getReceivers().add(message.getSender());
			response.setPerformative(Performative.INFORM);
			response.setContent(message.getContent());
			response.setContentObj(message.getContentObj());
			response.setUserArgs(message.getUserArgs());
			response.setLanguage(message.getLanguage());
			response.setEncoding(message.getEncoding());
			response.setOntology(message.getOntology());
			response.setProtocol(message.getProtocol());
			response.setConversationId(message.getConversationId());
			response.setReplyWith(message.getReplyWith());
			response.setInReplyTo(message.getInReplyTo());
			response.setReplyBy(message.getReplyBy());
			messageManager.sendACL(response);
		}
	}

}
