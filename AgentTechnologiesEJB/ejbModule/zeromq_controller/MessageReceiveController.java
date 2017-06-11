package zeromq_controller;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.jms.Message;
import javax.jms.MessageListener;

import org.zeromq.ZMQ;

import beans.AgentCentersManagementLocal;
import beans.AgentsManagementLocal;
import beans.HandshakeRequesterLocal;
import exceptions.AliasExistsException;
import model.AgentCenter;
import model.AgentType;
import server_management.AppManagementLocal;
import server_management.SystemPropertiesKeys;
import utils.HandshakeMessage;
import utils.HandshakeMessageType;
import utils.JSONConverter;

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
	
	@EJB
	AgentsManagementLocal agentsManagement;
	
	@EJB
	HandshakeRequesterLocal handshakeRequester;
	
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
			HandshakeMessage message = JSONConverter.convertFromJSON(request);
			
			HandshakeMessage replyMessage = processMessage(message);
			String jsonReply = JSONConverter.convertToJSON(replyMessage);
			responder.send(jsonReply, 0);
			if(replyMessage.isStatus() && message.getMesssageType() == HandshakeMessageType.POST_NODE && appManagement.isMaster()) {
				List<AgentType> newAgentTypes = getNewAgentTypes(message.getNewAgentCenter());
			}
		}
		
		responder.close();
		context.term();
	}
	
	public HandshakeMessage processMessage(HandshakeMessage message) {
		HandshakeMessage retVal = new HandshakeMessage();
		switch(message.getMesssageType()) {
			case POST_NODE: {
				try {
					agentCentersManagement.register(message.getNewAgentCenter());
					retVal.setStatus(true);
				} catch (AliasExistsException e) {
					retVal.setStatus(false);
				}
			}
			
			case GET_AGENT_CLASSES: {
				try {
					retVal.setAgentTypes(agentsManagement.getSupportedTypes());
					retVal.setStatus(true);
				} catch(Exception e) {
					retVal.setStatus(false);
				}
			}
		}
		
		return retVal;
	}
	
	public List<AgentType> getNewAgentTypes(AgentCenter newAgentCenter) {
		int numberOfTries = 0;
		List<AgentType> retVal = new ArrayList<>();
		HandshakeMessage response;
		response = handshakeRequester.sendGetAgentTypesRequest(newAgentCenter);
		
		if(!response.isStatus()) {
			numberOfTries++;
			response = handshakeRequester.sendGetAgentTypesRequest(newAgentCenter);
			if(!response.isStatus()) {
				numberOfTries++;
			} else numberOfTries = 0;
		}
		if(numberOfTries > 1) {
			System.out.println("It needs callback");
		} else {
			for(AgentType type : response.getAgentTypes()) {
				if(agentsManagement.addAgentType(type))
					retVal.add(type);
			}
		}
		
		return retVal;
	}

}
