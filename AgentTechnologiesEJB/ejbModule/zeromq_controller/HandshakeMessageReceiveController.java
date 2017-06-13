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
public class HandshakeMessageReceiveController implements MessageListener {
	
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
			try {
				listen();
			} catch (Exception e) {
				System.out.println("Connection is already opened!");
			}
		}
	}

	@Asynchronous
	public void listen() throws Exception {
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
				if(newAgentTypes != null) {
					boolean next = notifyAllNodes(message.getNewAgentCenter(), newAgentTypes);
					if(next) {
						if(!sendDataToNewCenter(message.getNewAgentCenter())) {
							rollback(message.getNewAgentCenter());
						}
					} else {
						rollback(message.getNewAgentCenter());
					}
				} else {
					cleanMasterNode(message.getNewAgentCenter());
				}
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
				break;
			}
			
			case GET_AGENT_CLASSES: {
				try {
					retVal.setAgentTypes(agentsManagement.getSupportedTypes());
					retVal.setStatus(true);
				} catch(Exception e) {
					retVal.setStatus(false);
				}
				break;
			}
			
			case NOTIFY_ALL: {
				try {
					agentCentersManagement.register(message.getNewAgentCenter());
					agentsManagement.addAgentTypes(message.getNewAgentCenter(), message.getAgentTypes());
					retVal.setStatus(true);
				} catch (Exception e) {
					retVal.setStatus(false);
				}
				break;
			}
			
			case NOTIFY_NEW_NODE: {
				try {
					for(AgentCenter center : message.getAgentCenters().values()) {
						agentCentersManagement.register(center);
					}
					for(String alias : message.getAllTypes().keySet()) {
						agentsManagement.addAgentTypes(message.getAgentCenters().get(alias), message.getAllTypes().get(alias));
					}
					for(String runningAgentName : message.getRunningAgents().keySet()) {
						agentsManagement.addRunningAgent(runningAgentName, message.getRunningAgents().get(runningAgentName));
					}
					retVal.setStatus(true);
				} catch (Exception e) {
					retVal.setStatus(false);
				}
				break;
			}
			
			case ROLLBACK: {
				agentCentersManagement.removeCenter(message.getNewAgentCenter());
				agentsManagement.removeAgentTypes(message.getNewAgentCenter());

				retVal.setStatus(true);
				break;
			}
			
		}
		
		return retVal;
	}
	
	public List<AgentType> getNewAgentTypes(AgentCenter newAgentCenter) {
		int numberOfTries = 0;
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
			return null;
		} else {
			agentsManagement.addAgentTypes(newAgentCenter, response.getAgentTypes());
		}
		
		return response.getAgentTypes();
	}
	
	public boolean notifyAllNodes(AgentCenter newAgentCenter, List<AgentType> newAgentTypes) {
		HandshakeMessage response;
		int numberOfTries = 0;
		for(AgentCenter agentCenter : agentCentersManagement.getAgentCenters().values()) {
			if(!agentCenter.getAlias().equals(newAgentCenter.getAlias()) && !agentCenter.getAlias().equals(appManagement.getLocalAlias())) {
				response = handshakeRequester.notifyNode(newAgentCenter, newAgentTypes, agentCenter);
				
				if(!response.isStatus()) {
					numberOfTries++;
					response = handshakeRequester.notifyNode(newAgentCenter, newAgentTypes, agentCenter);
					if(!response.isStatus()) {
						numberOfTries++;
					} else numberOfTries = 0;
				}
				if(numberOfTries > 1) {
					return false;
				}
			}
		}
		
		return true;
	}
	
	public boolean sendDataToNewCenter(AgentCenter newCenter) {
		HandshakeMessage response;
		int numberOfTries = 0;
		response = handshakeRequester.sendDataToNewNode(newCenter, agentCentersManagement.getAgentCenters(), agentsManagement.getAllTypes(), agentsManagement.getRunningAgents());
		if(!response.isStatus()) {
			numberOfTries++;
			response = handshakeRequester.sendDataToNewNode(newCenter, agentCentersManagement.getAgentCenters(), agentsManagement.getAllTypes(), agentsManagement.getRunningAgents());
			if(!response.isStatus()) {
				numberOfTries++;
			} else numberOfTries = 0;
		}
		if(numberOfTries > 1) {
			return false;
		}
		
		return true;
	}
	
	public void cleanMasterNode(AgentCenter newAgentCenter) {
		agentCentersManagement.removeCenter(newAgentCenter);
	}
	
	public void rollback(AgentCenter newAgentCenter) {
		agentCentersManagement.removeCenter(newAgentCenter);
		agentsManagement.removeAgentTypes(newAgentCenter);
		
		for(AgentCenter agentCenter : agentCentersManagement.getAgentCenters().values()) {
			if(!agentCenter.getAlias().equals(newAgentCenter.getAlias()) && !agentCenter.getAlias().equals(appManagement.getLocalAlias())) {
				handshakeRequester.cleanNode(agentCenter, newAgentCenter);
			}
		}
	}

}
