package beans;

import java.util.concurrent.TimeUnit;

import javax.ejb.Stateless;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.zeromq.ZMQ;

import model.ACLMessage;
import model.AID;
import model.AgentCenter;
import model.AgentType;
import server_management.SystemPropertiesKeys;
import utils.AgentsCommunicationMessage;
import utils.AgentsCommunicationMessageType;
import utils.JSONConverter;
import utils.MessageToDeliver;

@Stateless
public class AgentsRequester implements AgentsRequesterLocal {
	
	@Override
	public boolean sendRunAgentRequest(AgentCenter agentCenter, String name, AgentType agentType) {
		ResteasyClient client = new ResteasyClientBuilder().build();
		Response response = null;
		ResteasyWebTarget target = client.target("http://" + agentCenter.getAddress() + "/AgentsPlayground/rest/agents/running/" + name);
		response = target.request(MediaType.APPLICATION_JSON).put(Entity.entity(agentType, MediaType.APPLICATION_JSON));
		AID aid = response.readEntity(AID.class);
		return true;
	}
	
	@Override
	public void sendStopAgentRequest(AgentCenter agentCenter, String name) {
		ResteasyClient client = new ResteasyClientBuilder().build();
		Response response = null;
		ResteasyWebTarget target = client.target("http://" + agentCenter.getAddress() + "/AgentsPlayground/rest/agents/running/" + name);
		response = target.request().delete();
	}
	
	@Override
	public void sendACLMessage(AID receiver, ACLMessage message) {
		ResteasyClient client = new ResteasyClientBuilder().build();
		Response response = null;
		ResteasyWebTarget target = client.target("http://" + receiver.getHost().getAddress() + "/AgentsPlayground/rest/messages/sendToAgent");
		response = target.request().post(Entity.entity(new MessageToDeliver(receiver, message), MediaType.APPLICATION_JSON));
	}
	
	@Override
	public boolean sendNewRunningAgent(AgentCenter agentCenter, AID aid) {
		ZMQ.Context context = ZMQ.context(1);
		
		ZMQ.Socket requester = context.socket(ZMQ.REQ);
		String centerPort =  (agentCenter.getAddress().split(":"))[1];
		int port = SystemPropertiesKeys.MASTER_TCP_PORT + Integer.parseInt(centerPort) - SystemPropertiesKeys.MASTER_PORT + 2;
		String url = "tcp://localhost:" + port;
		System.out.println("SENDING TO: " + url);
		requester.connect(url);
		
		AgentsCommunicationMessage message = new AgentsCommunicationMessage(null, null, null, aid, AgentsCommunicationMessageType.ADD_RUNNING_AGENT, true);
		String jsonObject = JSONConverter.convertAgentCommunicationMessageToJSON(message);
		requester.send(jsonObject, 0);
		
		String reply = requester.recvStr(0);
		AgentsCommunicationMessage response;
		if(reply != null)
			response = JSONConverter.convertAgentCommunicationMessageFromJSON(reply);
		else response = null;
		
		requester.close();
		context.term();
		return response.isStatus();
	}

	@Override
	public void sendStoppedAgentMessage(AgentCenter agentCenter, String name) {
		ZMQ.Context context = ZMQ.context(1);
		
		ZMQ.Socket requester = context.socket(ZMQ.REQ);
		String centerPort =  (agentCenter.getAddress().split(":"))[1];
		int port = SystemPropertiesKeys.MASTER_TCP_PORT + Integer.parseInt(centerPort) - SystemPropertiesKeys.MASTER_PORT + 2;
		String url = "tcp://localhost:" + port;
		System.out.println("SENDING TO: " + url);
		requester.connect(url);
		
		AgentsCommunicationMessage message = new AgentsCommunicationMessage(name, null, null, null, AgentsCommunicationMessageType.REMOVE_STOPPED_AGENT, true);
		String jsonObject = JSONConverter.convertAgentCommunicationMessageToJSON(message);
		requester.send(jsonObject, 0);
		
		String reply = requester.recvStr(0);
		AgentsCommunicationMessage response;
		if(reply != null)
			response = JSONConverter.convertAgentCommunicationMessageFromJSON(reply);
		else response = null;
		
		requester.close();
		context.term();
	}
	
	@Override
	public void sendNewMessage(AgentCenter agentCenter, AID aid, ACLMessage aclMessage) {
		ZMQ.Context context = ZMQ.context(1);
		
		ZMQ.Socket requester = context.socket(ZMQ.REQ);
		String centerPort =  (agentCenter.getAddress().split(":"))[1];
		int port = SystemPropertiesKeys.MASTER_TCP_PORT + Integer.parseInt(centerPort) - SystemPropertiesKeys.MASTER_PORT + 2;
		String url = "tcp://localhost:" + port;
		System.out.println("SENDING TO: " + url);
		requester.connect(url);
		
		AgentsCommunicationMessage message = new AgentsCommunicationMessage(null, null, aclMessage, aid, AgentsCommunicationMessageType.ACL_MESSAGE, true);
		String jsonObject = JSONConverter.convertAgentCommunicationMessageToJSON(message);
		requester.send(jsonObject, 0);
		
		String reply = requester.recvStr(0);
		AgentsCommunicationMessage response;
		if(reply != null)
			response = JSONConverter.convertAgentCommunicationMessageFromJSON(reply);
		else response = null;
		
		requester.close();
		context.term();
	}
	
}
