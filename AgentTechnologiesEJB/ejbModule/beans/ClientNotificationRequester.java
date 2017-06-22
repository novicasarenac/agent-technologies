package beans;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.zeromq.ZMQ;

import model.ACLMessage;
import model.AID;
import model.AgentType;
import server_management.AppManagementLocal;
import server_management.SystemPropertiesKeys;
import utils.JSONConverter;
import utils.WSMessage;
import utils.WSMessageType;

@Stateless
public class ClientNotificationRequester implements ClientNotificationsRequesterLocal {

	@EJB
	AppManagementLocal appManagement;
	
	@EJB
	AgentsManagementLocal agentsManagement;
	
	@Override
	public void sendNewRunningAgentNotification(AID aid) {
		ZMQ.Context context = ZMQ.context(1);
		
		ZMQ.Socket requester = context.socket(ZMQ.REQ);
		String centerPort =  (appManagement.getLocal().split(":"))[1];
		int port = SystemPropertiesKeys.MASTER_TCP_PORT + Integer.parseInt(centerPort) - SystemPropertiesKeys.MASTER_PORT + 3;
		String url = "tcp://localhost:" + port;
		System.out.println("SENDING TO: " + url);
		requester.connect(url);

		WSMessage wsMessage = new WSMessage(null, null, aid, null, null, null, WSMessageType.ADD_RUNNING_AGENT);
		String jsonObject = JSONConverter.convertWSMessageToJSON(wsMessage);
		requester.send(jsonObject, 0);
		
		String reply = requester.recvStr(0);
		
		requester.close();
		context.term();
	}
	
	@Override
	public void sendStopAgentNotification(AID aid) {
		ZMQ.Context context = ZMQ.context(1);
		
		ZMQ.Socket requester = context.socket(ZMQ.REQ);
		String centerPort =  (appManagement.getLocal().split(":"))[1];
		int port = SystemPropertiesKeys.MASTER_TCP_PORT + Integer.parseInt(centerPort) - SystemPropertiesKeys.MASTER_PORT + 3;
		String url = "tcp://localhost:" + port;
		System.out.println("SENDING TO: " + url);
		requester.connect(url);

		WSMessage wsMessage = new WSMessage(null, null, aid, null, null, null, WSMessageType.REMOVE_STOPPED_AGENT);
		String jsonObject = JSONConverter.convertWSMessageToJSON(wsMessage);
		requester.send(jsonObject, 0);
		
		String reply = requester.recvStr(0);
		
		requester.close();
		context.term();
	}

	@Override
	public void sendShutdownNodeNotification() {
		ZMQ.Context context = ZMQ.context(1);
		
		ZMQ.Socket requester = context.socket(ZMQ.REQ);
		String centerPort =  (appManagement.getLocal().split(":"))[1];
		int port = SystemPropertiesKeys.MASTER_TCP_PORT + Integer.parseInt(centerPort) - SystemPropertiesKeys.MASTER_PORT + 3;
		String url = "tcp://localhost:" + port;
		System.out.println("SENDING TO: " + url);
		requester.connect(url);

		List<AgentType> agentTypes = new ArrayList<>();
		for(String key : agentsManagement.getAllTypes().keySet()) {
			for(AgentType type : agentsManagement.getAllTypes().get(key)) {
				if(agentTypes.stream().filter(addedType -> addedType.getName().equals(type.getName())).count() == 0)
					agentTypes.add(type);
			}
		}
		List<AID> runningAgents = new ArrayList<>();
		for(AID aid : agentsManagement.getRunningAgents().values()) {
			runningAgents.add(aid);
		}
		WSMessage wsMessage = new WSMessage(null, null, null, agentTypes, null, runningAgents, WSMessageType.REMOVED_NODE);
		String jsonObject = JSONConverter.convertWSMessageToJSON(wsMessage);
		requester.send(jsonObject, 0);
		
		String reply = requester.recvStr(0);
		
		requester.close();
		context.term();
	}
	
	@Override
	public void sendAddNewNodeNotification() {
		ZMQ.Context context = ZMQ.context(1);
		
		ZMQ.Socket requester = context.socket(ZMQ.REQ);
		String centerPort =  (appManagement.getLocal().split(":"))[1];
		int port = SystemPropertiesKeys.MASTER_TCP_PORT + Integer.parseInt(centerPort) - SystemPropertiesKeys.MASTER_PORT + 3;
		String url = "tcp://localhost:" + port;
		System.out.println("SENDING TO: " + url);
		requester.connect(url);

		List<AgentType> agentTypes = new ArrayList<>();
		for(String key : agentsManagement.getAllTypes().keySet()) {
			for(AgentType type : agentsManagement.getAllTypes().get(key)) {
				if(agentTypes.stream().filter(addedType -> addedType.getName().equals(type.getName())).count() == 0)
					agentTypes.add(type);
			}
		}
		WSMessage wsMessage = new WSMessage(null, null, null, agentTypes, null, null, WSMessageType.ADDED_NEW_NODE);
		String jsonObject = JSONConverter.convertWSMessageToJSON(wsMessage);
		requester.send(jsonObject, 0);
		
		String reply = requester.recvStr(0);
		
		requester.close();
		context.term();
	}

	@Override
	public void sendMessageNotification(AID aid, ACLMessage message) {
		ZMQ.Context context = ZMQ.context(1);
		
		ZMQ.Socket requester = context.socket(ZMQ.REQ);
		String centerPort =  (appManagement.getLocal().split(":"))[1];
		int port = SystemPropertiesKeys.MASTER_TCP_PORT + Integer.parseInt(centerPort) - SystemPropertiesKeys.MASTER_PORT + 3;
		String url = "tcp://localhost:" + port;
		System.out.println("SENDING TO: " + url);
		requester.connect(url);

		WSMessage wsMessage = new WSMessage(null, null, aid, null, message, null, WSMessageType.SEND_ACL_MESSAGE);
		String jsonObject = JSONConverter.convertWSMessageToJSON(wsMessage);
		requester.send(jsonObject, 0);
		
		String reply = requester.recvStr(0);
		
		requester.close();
		context.term();
	}
	
}
