package mdb;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import agents.AgentManagerLocal;
import model.ACLMessage;
import utils.MessageToDeliver;

@MessageDriven(activationConfig = {
		@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
		@ActivationConfigProperty(propertyName = "destination", propertyValue = "java:/jms/queue/messageListener"),
})
public class MessageManagementListener implements MessageListener {

	@EJB
	AgentManagerLocal agentManager;
	
	@Override
	public void onMessage(Message arg0) {
		ObjectMessage objectMessage = (ObjectMessage) arg0;
		MessageToDeliver message = null;
		try {
			message = (MessageToDeliver) objectMessage.getObject();
		} catch (JMSException e) {
			e.printStackTrace();
		}
		agentManager.deliverMessageToAgent(message.getTo(), message.getMessage());
	}
	
}
