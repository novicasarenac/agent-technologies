package zeromq_controllers;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.jms.Message;
import javax.jms.MessageListener;

import org.zeromq.ZMQ;

import agents.AgentManagerLocal;
import beans.AgentsManagementLocal;
import beans.ClientNotificationsRequesterLocal;
import model.AID;
import server_management.AppManagementLocal;
import server_management.SystemPropertiesKeys;
import utils.AgentsCommunicationMessage;
import utils.AgentsCommunicationMessageType;
import utils.JSONConverter;

@MessageDriven(activationConfig = {
		@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
		@ActivationConfigProperty(propertyName = "destination", propertyValue = "java:/jms/queue/agentsCommunicationListener"),
		@ActivationConfigProperty(propertyName = "transactionTimeout", propertyValue = "300000")
})
public class AgentsCommunicationController implements MessageListener {

	@EJB
	AppManagementLocal appManagement;
	
	@EJB
	AgentManagerLocal agentManager;
	
	@EJB
	AgentsManagementLocal agentsManagement;
	
	@EJB
	ClientNotificationsRequesterLocal clientNotificationRequester;
	
	@Override
	public void onMessage(Message arg0) {
		if(!appManagement.isAgentsCommunicationListenerStarted()) {
			try {
				listen();
			} catch(Exception e) {
				System.out.println("Connection is already opened!");
			}
		}
	}
	
	@Asynchronous
	public void listen() throws Exception {
		appManagement.setAgentsCommunicationListenerStarted(true);
		ZMQ.Context context = ZMQ.context(1);
		
		ZMQ.Socket responder = context.socket(ZMQ.REP);
		int port = SystemPropertiesKeys.MASTER_TCP_PORT + Integer.parseInt(appManagement.getPortOffset()) + 2;
		String url = "tcp://*:" + port;
		System.out.println("URL RECEIVER: " + url);
		responder.bind(url);
		
		while(!Thread.currentThread().isInterrupted()) {
			String request = responder.recvStr(0);
			AgentsCommunicationMessage message = JSONConverter.convertAgentCommunicationMessageFromJSON(request);
			
			AgentsCommunicationMessage response = processMessage(message);
			String jsonReply = JSONConverter.convertAgentCommunicationMessageToJSON(response);
			
			responder.send(jsonReply, 0);
			
			if(message.getAgentsCommunicationMessageType() == AgentsCommunicationMessageType.ADD_RUNNING_AGENT)
				clientNotificationRequester.sendNewRunningAgentNotification(message.getAid());
			if(message.getAgentsCommunicationMessageType() == AgentsCommunicationMessageType.REMOVE_STOPPED_AGENT) {
				clientNotificationRequester.sendStopAgentNotification(response.getAid());
			}
		}
	}
	
	public AgentsCommunicationMessage processMessage(AgentsCommunicationMessage message) throws Exception {
		AgentsCommunicationMessage response = null;
		switch(message.getAgentsCommunicationMessageType()) {
			case ADD_RUNNING_AGENT: {
				agentsManagement.addRunningAgent(message.getAid().getName(), message.getAid());
				response = new AgentsCommunicationMessage(null, null, null, null, AgentsCommunicationMessageType.ADD_RUNNING_AGENT, true);
				break;
			}
			case REMOVE_STOPPED_AGENT: {
				AID aid = agentsManagement.getRunningAgents().get(message.getName());
				agentsManagement.removeRunningAgent(message.getName());
				response = new AgentsCommunicationMessage(null, null, null, aid, AgentsCommunicationMessageType.REMOVE_STOPPED_AGENT, true);
				break;
			}
		}
		return response;
	}
	
}
