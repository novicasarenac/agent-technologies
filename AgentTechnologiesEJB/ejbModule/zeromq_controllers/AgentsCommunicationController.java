package zeromq_controllers;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.jms.Message;
import javax.jms.MessageListener;

import org.zeromq.ZMQ;

import agents.AgentManagerLocal;
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
			
			AID retVal = agentManager.runAgent(message.getName(), message.getAgentType());
			AgentsCommunicationMessage response;
			if(retVal != null) {
				response = new AgentsCommunicationMessage(null, null, null, AgentsCommunicationMessageType.RUN_AGENT, true);
			} else {
				response = new AgentsCommunicationMessage(null, null, null, AgentsCommunicationMessageType.RUN_AGENT, false);
			}
			String jsonReply = JSONConverter.convertAgentCommunicationMessageToJSON(response);
			
			responder.send(jsonReply, 0);
		}
	}
	
}
