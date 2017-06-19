package controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.codehaus.jackson.map.ObjectMapper;

import utils.WSMessage;
import utils.WSMessageType;

@ServerEndpoint("/agentRequest")
public class WSController {

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
				System.out.println("PORUKA: " + wsMessage.toString());
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
}
