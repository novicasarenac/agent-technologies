package mdb;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.jms.Message;
import javax.jms.MessageListener;

import org.zeromq.ZMQ;

import agents.AgentManagerLocal;
import beans.MessageManagerLocal;
import server_management.AppManagementLocal;
import server_management.SystemPropertiesKeys;
import utils.JSONConverter;
import utils.MessageToDeliver;

@MessageDriven(activationConfig = {
		@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
		@ActivationConfigProperty(propertyName = "destination", propertyValue = "java:/jms/queue/messageListener"),
		@ActivationConfigProperty(propertyName = "transactionTimeout", propertyValue = "300000")
})
public class MessageManagementListener implements MessageListener {

	@EJB
	AgentManagerLocal agentManager;
	
	@EJB
	MessageManagerLocal messageManager;
	
	@EJB
	AppManagementLocal appManagement;
	
	@Override
	public void onMessage(Message arg0) {
		if(!appManagement.isMessageListenerStarted()) {
			try {
				listen();
			} catch(Exception e) {
				System.out.println("Connection is already opened!");
			}
		}
	}
	
	@Asynchronous
	public void listen() throws Exception {
		appManagement.setMessageListenerStarted(true);
		ZMQ.Context context = ZMQ.context(1);
		
		ZMQ.Socket responder = context.socket(ZMQ.REP);
		int port = SystemPropertiesKeys.MASTER_TCP_PORT + Integer.parseInt(appManagement.getPortOffset()) + 4;
		String url = "tcp://*:" + port;
		System.out.println("URL RECEIVER: " + url);
		responder.bind(url);
		
		while(!Thread.currentThread().isInterrupted()) {
			String request = responder.recvStr(0);
			MessageToDeliver message = JSONConverter.convertMessageToDeliverFromJSON(request);
			
			String response = "OK";
			
			responder.send(response, 0);
			messageManager.sendMessageToAgent(message.getTo(), message.getMessage());
		}
	}
	
}
