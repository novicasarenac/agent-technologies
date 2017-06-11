package zeromq_controller;

import java.io.IOException;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.jms.Message;
import javax.jms.MessageListener;

import org.codehaus.jackson.map.ObjectMapper;
import org.zeromq.ZMQ;

import beans.AgentCentersManagementLocal;
import exceptions.AliasExistsException;
import model.AgentCenter;
import server_management.AppManagementLocal;
import server_management.SystemPropertiesKeys;
import utils.HandshakeMessage;

@MessageDriven(activationConfig = {
		@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
		@ActivationConfigProperty(propertyName = "destination", propertyValue = "java:/jms/queue/activateListener"),
		@ActivationConfigProperty(propertyName = "transactionTimeout", propertyValue = "300000")
})
public class MessageReceiveController implements MessageListener {
	
	@EJB
	AppManagementLocal appManagement;
	
	@EJB
	AgentCentersManagementLocal agentCentersManagement;
	
	@Override
	public void onMessage(Message arg0) {
		if(!appManagement.isListenerStarted()) {
			listen();
		}
	}

	@Asynchronous
	public void listen() {
		appManagement.setListenerStarted(true);
		ZMQ.Context context = ZMQ.context(1);
		
		ZMQ.Socket responder = context.socket(ZMQ.REP);
		int port = SystemPropertiesKeys.MASTER_TCP_PORT + Integer.parseInt(appManagement.getPortOffset());
		String url = "tcp://*:" + port;
		System.out.println("URL RECEIVER: " + url);
		responder.bind(url);
		
		while(!Thread.currentThread().isInterrupted()) {
			String request = responder.recvStr(0);
			HandshakeMessage message = null;
			try {
				ObjectMapper mapper = new ObjectMapper();
				message = mapper.readValue(request, HandshakeMessage.class);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			HandshakeMessage replyMessage = processMessage(message);
			String jsonReply = "";
			ObjectMapper responseMapper = new ObjectMapper();
			try {
				jsonReply = responseMapper.writeValueAsString(replyMessage);
			} catch (IOException e) {
				e.printStackTrace();
			}
			responder.send(jsonReply, 0);
		}
		
		responder.close();
		context.term();
	}
	
	public HandshakeMessage processMessage(HandshakeMessage message) {
		HandshakeMessage retVal = new HandshakeMessage();
		try {
			agentCentersManagement.register(message.getNewAgentCenter());
			retVal.setStatus(true);
		} catch (AliasExistsException e) {
			retVal.setStatus(false);
		}
		
		return retVal;
	}

}
