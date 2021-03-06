package beans;

import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;

import org.zeromq.ZMQ;

import model.AID;
import model.AgentCenter;
import model.AgentType;
import server_management.SystemPropertiesKeys;
import utils.HandshakeMessage;
import utils.HandshakeMessageType;
import utils.JSONConverter;

@Stateless
public class HandshakeRequester implements HandshakeRequesterLocal {

	@Override
	public boolean sendRegisterRequest(String address, String alias) {
		ZMQ.Context context = ZMQ.context(1);
		
		ZMQ.Socket requester = context.socket(ZMQ.REQ);
		String url = "tcp://localhost:" + SystemPropertiesKeys.MASTER_TCP_PORT;
		System.out.println("SENDING TO: " + url);
		requester.connect(url);
		
		HandshakeMessage message = new HandshakeMessage(HandshakeMessageType.POST_NODE, new AgentCenter(alias, address), null, null, null, null, true);
		String jsonObject = JSONConverter.convertToJSON(message);
		requester.send(jsonObject, 0);
		
		String reply = requester.recvStr(0);
		HandshakeMessage response = JSONConverter.convertFromJSON(reply);
		
		requester.close();
		context.term();
		return response.isStatus();
	}

	@Override
	public HandshakeMessage sendGetAgentTypesRequest(AgentCenter agentCenter) {
		ZMQ.Context context = ZMQ.context(1);
		
		ZMQ.Socket requester = context.socket(ZMQ.REQ);
		requester.setReceiveTimeOut(5000);
		String centerPort =  (agentCenter.getAddress().split(":"))[1];
		int port = SystemPropertiesKeys.MASTER_TCP_PORT + Integer.parseInt(centerPort) - SystemPropertiesKeys.MASTER_PORT;
		String url = "tcp://localhost:" + port;
		System.out.println("SENDING TO: " + url);
		requester.connect(url);
		
		HandshakeMessage message = new HandshakeMessage(HandshakeMessageType.GET_AGENT_CLASSES, null, null, null, null, null, true);
		String jsonObject = JSONConverter.convertToJSON(message);
		requester.send(jsonObject, 0);
		
		String reply = requester.recvStr(0);
		HandshakeMessage response;
		if(reply != null)
			response = JSONConverter.convertFromJSON(reply);
		else response = null;
		
		requester.close();
		context.term();
		return response;
	}
	
	@Override
	public HandshakeMessage notifyNode(AgentCenter newAgentCenter, List<AgentType> agentTypes, AgentCenter nodeToNotify) {
		ZMQ.Context context = ZMQ.context(1);
		
		ZMQ.Socket requester = context.socket(ZMQ.REQ);
		requester.setReceiveTimeOut(5000);
		String centerPort =  (nodeToNotify.getAddress().split(":"))[1];
		int port = SystemPropertiesKeys.MASTER_TCP_PORT + Integer.parseInt(centerPort) - SystemPropertiesKeys.MASTER_PORT;
		String url = "tcp://localhost:" + port;
		System.out.println("SENDING TO: " + url);
		requester.connect(url);
		
		HandshakeMessage message = new HandshakeMessage(HandshakeMessageType.NOTIFY_ALL, newAgentCenter, agentTypes, null, null, null, true);
		String jsonObject = JSONConverter.convertToJSON(message);
		requester.send(jsonObject, 0);
		
		String reply = requester.recvStr(0);
		HandshakeMessage response;
		if(reply != null)
			response = JSONConverter.convertFromJSON(reply);
		else response = null;
		
		requester.close();
		context.term();
		return response;
	}
	
	@Override
	public HandshakeMessage sendDataToNewNode(AgentCenter newNode, Map<String, AgentCenter> agentCenters, Map<String, List<AgentType>> agentTypes, Map<String, AID> runningAgents) {
		ZMQ.Context context = ZMQ.context(1);
		
		ZMQ.Socket requester = context.socket(ZMQ.REQ);
		requester.setReceiveTimeOut(5000);
		String centerPort =  (newNode.getAddress().split(":"))[1];
		int port = SystemPropertiesKeys.MASTER_TCP_PORT + Integer.parseInt(centerPort) - SystemPropertiesKeys.MASTER_PORT;
		String url = "tcp://localhost:" + port;
		System.out.println("SENDING TO: " + url);
		requester.connect(url);
		
		HandshakeMessage message = new HandshakeMessage(HandshakeMessageType.NOTIFY_NEW_NODE, null, null, agentCenters, runningAgents, agentTypes, true);
		String jsonObject = JSONConverter.convertToJSON(message);
		requester.send(jsonObject, 0);
		
		String reply = requester.recvStr(0);
		HandshakeMessage response;
		if(reply != null)
			response = JSONConverter.convertFromJSON(reply);
		else response = null;
		
		requester.close();
		context.term();
		return response;
	}
	
	@Override
	public boolean cleanNode(AgentCenter nodeToClean, AgentCenter newAgentCenter) {
		ZMQ.Context context = ZMQ.context(1);
		
		ZMQ.Socket requester = context.socket(ZMQ.REQ);
		String centerPort =  (nodeToClean.getAddress().split(":"))[1];
		int port = SystemPropertiesKeys.MASTER_TCP_PORT + Integer.parseInt(centerPort) - SystemPropertiesKeys.MASTER_PORT;
		String url = "tcp://localhost:" + port;
		System.out.println("SENDING TO: " + url);
		requester.connect(url);
		
		HandshakeMessage message = new HandshakeMessage(HandshakeMessageType.ROLLBACK, newAgentCenter, null, null, null, null, true);
		String jsonObject = JSONConverter.convertToJSON(message);
		requester.send(jsonObject, 0);
		
		String reply = requester.recvStr(0);
		HandshakeMessage response = JSONConverter.convertFromJSON(reply);
		
		requester.close();
		context.term();
		return response.isStatus();
	}
	
}
