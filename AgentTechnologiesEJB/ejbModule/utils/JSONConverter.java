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
	
}
