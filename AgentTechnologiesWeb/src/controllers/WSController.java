package controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.codehaus.jackson.map.ObjectMapper;
import org.zeromq.ZMQ;

import agents.AgentManagerLocal;
import beans.MessageManagerLocal;
import exceptions.NameExistsException;
import server_management.AppManagementLocal;
import server_management.SystemPropertiesKeys;
import utils.JSONConverter;
import utils.WSMessage;

@MessageDriven(activationConfig = {
		@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
		@ActivationConfigProperty(propertyName = "destination", propertyValue = "java:/jms/queue/notificationListener"),
		@ActivationConfigProperty(propertyName = "transactionTimeout", propertyValue = "300000")
})
@ServerEndpoint("/agentRequest")
public class WSController implements MessageListener {
	
	@EJB
	AgentManagerLocal agentManager;
	
	@EJB
	MessageManagerLocal messageManager;
	
	@EJB
	AppManagementLocal appManagement;

	@OnOpen
	public void onOpen(Session session) {
		appManagement.addSession(session);
	}
	
	@OnMessage
	public void onMessage(Session session, String message, boolean last) {
		WSMessage wsMessage = null;
		if(session.isOpen()) {
			try {
				ObjectMapper mapper = new ObjectMapper();
				wsMessage = mapper.readValue(message, WSMessage.class);
				processMessage(wsMessage);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void processMessage(WSMessage message) {
		switch(message.getMessageType()) {
			case RUN_AGENT: {
				try {
					agentManager.runAgent(message.getName(), message.getNewAgentType());
				} catch (NameExistsException e) {
					e.printStackTrace();
				}
				break;
			}
			case STOP_AGENT: {
				agentManager.stopAgent(message.getName());
				break;
			}
			case SEND_ACL_MESSAGE: {
				messageManager.sendACL(message.getAclMessage());
				break;
			}
		}
	}
	
	public void processNotification(WSMessage message) {
		switch (message.getMessageType()) {
			case ADD_RUNNING_AGENT: {
				String jsonObject;
				try {
					ObjectMapper mapper = new ObjectMapper();
					jsonObject = mapper.writeValueAsString(message);
					for(Session s : appManagement.getSessions()) {
						s.getBasicRemote().sendText(jsonObject);
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
				break;
			}
			case REMOVE_STOPPED_AGENT: {
				String jsonObject;
				try {
					ObjectMapper mapper = new ObjectMapper();
					jsonObject = mapper.writeValueAsString(message);
					for(Session s : appManagement.getSessions()) {
						s.getBasicRemote().sendText(jsonObject);
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
				break;
			}
			case REMOVED_NODE: {
				String jsonObject;
				try {
					ObjectMapper mapper = new ObjectMapper();
					jsonObject = mapper.writeValueAsString(message);
					for(Session s : appManagement.getSessions()) {
						s.getBasicRemote().sendText(jsonObject);
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
				break;
			}
			case ADDED_NEW_NODE: {
				String jsonObject;
				try {
					ObjectMapper mapper = new ObjectMapper();
					jsonObject = mapper.writeValueAsString(message);
					for(Session s : appManagement.getSessions()) {
						s.getBasicRemote().sendText(jsonObject);
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
				break;
			}
			case SEND_ACL_MESSAGE: {
				String jsonObject;
				try {
					ObjectMapper mapper = new ObjectMapper();
					jsonObject = mapper.writeValueAsString(message);
					for(Session s : appManagement.getSessions()) {
						s.getBasicRemote().sendText(jsonObject);
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
				break;
			}
		}
	}
	
	@OnClose
	public void onClose(Session session) {
		appManagement.removeSession(session);
	}
	
	@OnError
	public void onError(Session session, Throwable t) {
		appManagement.removeSession(session);
	}

	//notification listener
	@Override
	public void onMessage(Message arg0) {
		if(!appManagement.isClientNotificationListenerStarted()) {
			try {
				listen();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	@Asynchronous
	public void listen() throws Exception {
		appManagement.setClientNotificationListenerStarted(true);
		ZMQ.Context context = ZMQ.context(1);
		
		ZMQ.Socket responder = context.socket(ZMQ.REP);
		int port = SystemPropertiesKeys.MASTER_TCP_PORT + Integer.parseInt(appManagement.getPortOffset()) + 3;
		String url = "tcp://*:" + port;
		System.out.println("URL RECEIVER: " + url);
		responder.bind(url);
		
		while(!Thread.currentThread().isInterrupted()) {
			String request = responder.recvStr(0);
			WSMessage message = JSONConverter.convertWSMessageFromJSON(request);
			
			String reply = "Response";
			
			responder.send(reply, 0);
			processNotification(message);
		}
	}
}
