package beans;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.inject.Inject;
import javax.jms.Destination;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.JMSProducer;
import javax.jms.ObjectMessage;

import model.ACLMessage;
import model.AID;
import server_management.AppManagementLocal;
import utils.MessageToDeliver;

@Singleton
public class MessageManager implements MessageManagerLocal {

	@EJB
	AppManagementLocal appManagement;
	
	@EJB
	AgentsRequesterLocal agentsRequester;
	
	@Inject
	JMSContext context;
	
	@Resource(mappedName = "java:/jms/queue/messageListener")
	private Destination destination;
	
	@Override
	public void sendACL(ACLMessage aclMessage) {
		for(AID receiver : aclMessage.getReceivers()) {
			if(receiver.getHost().getAlias().equals(appManagement.getLocalAlias())) {
				sendMessageToAgent(receiver, aclMessage);
			} else {
				agentsRequester.sendACLMessage(receiver, aclMessage);
			}
		}
	}
	
	@Override
	public void sendMessageToAgent(AID agent, ACLMessage aclMessage) {
		ObjectMessage message = context.createObjectMessage();
		try {
			message.setObject(new MessageToDeliver(agent, aclMessage));
			JMSProducer producer = context.createProducer();
			producer.send(destination, message);
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}

}
