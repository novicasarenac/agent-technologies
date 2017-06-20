package controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.ejb.Stateless;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.resource.spi.Activation;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.codehaus.jackson.map.ObjectMapper;

import agents.AgentManagerLocal;
import exceptions.NameExistsException;
import model.AID;
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

	List<Session> sessions = new ArrayList<>();
	
	@OnOpen
	public void onOpen(Session session) {
		if(!sessions.contains(session)) {
			sessions.add(session);
		}
	}
	
	@OnMessage
	public void onMessage(Session session, String message, boolean last) {
		WSMessage wsMessage = null;
		if(session.isOpen()) {
			try {
				ObjectMapper mapper = new ObjectMapper();
				wsMessage = mapper.readValue(message, WSMessage.class);
				try {
					AID aid = agentManager.runAgent(wsMessage.getName(), wsMessage.getNewAgentType());
				} catch (NameExistsException e) {
					e.printStackTrace();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	@OnClose
	public void onClose(Session session) {
		sessions.remove(session);
	}
	
	@OnError
	public void onError(Session session, Throwable t) {
		sessions.remove(session);
	}

	//notification listener
	@Override
	public void onMessage(Message arg0) {
		
	}
}
