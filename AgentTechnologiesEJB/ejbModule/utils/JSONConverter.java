package utils;

import java.io.IOException;

import org.codehaus.jackson.map.ObjectMapper;

public class JSONConverter {
	
	public static String convertToJSON(HandshakeMessage message) {
		String jsonMessage = "";
		ObjectMapper mapper = new ObjectMapper();
		try {
			jsonMessage = mapper.writeValueAsString(message);
		} catch(IOException e) {
			e.printStackTrace();
		}
		return jsonMessage;
	}
	
	public static HandshakeMessage convertFromJSON(String message) {
		HandshakeMessage handshakeMessage = null;
		try {
			ObjectMapper mapper = new ObjectMapper();
			handshakeMessage = mapper.readValue(message, HandshakeMessage.class);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return handshakeMessage;
	}
	
	public static String convertAgentCommunicationMessageToJSON(AgentsCommunicationMessage agentsCommunicationMessage) {
		String jsonMessage = "";
		ObjectMapper mapper = new ObjectMapper();
		try {
			jsonMessage = mapper.writeValueAsString(agentsCommunicationMessage);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return jsonMessage;
	}
	
	public static AgentsCommunicationMessage convertAgentCommunicationMessageFromJSON(String message) {
		AgentsCommunicationMessage agentsCommunicationMessage = null;
		try {
			ObjectMapper mapper = new ObjectMapper();
			agentsCommunicationMessage = mapper.readValue(message, AgentsCommunicationMessage.class);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return agentsCommunicationMessage;
	}
	
	public static String convertWSMessageToJSON(WSMessage message) {
		String jsonMessage = "";
		ObjectMapper mapper = new ObjectMapper();
		try {
			jsonMessage = mapper.writeValueAsString(message);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return jsonMessage;
	}
	
	public static WSMessage convertWSMessageFromJSON(String message) {
		WSMessage wsMessage = null;
		try {
			ObjectMapper mapper = new ObjectMapper();
			wsMessage = mapper.readValue(message, WSMessage.class);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return wsMessage;
	}
	
	public static String convertMessageToDeliverToJSON(MessageToDeliver message) {
		String jsonMessage = "";
		ObjectMapper mapper = new ObjectMapper();
		try {
			jsonMessage = mapper.writeValueAsString(message);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return jsonMessage;
	}
	
	public static MessageToDeliver convertMessageToDeliverFromJSON(String message) {
		MessageToDeliver wsMessage = null;
		try {
			ObjectMapper mapper = new ObjectMapper();
			wsMessage = mapper.readValue(message, MessageToDeliver.class);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return wsMessage;
	}
	
}
