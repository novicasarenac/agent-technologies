package beans;

import javax.ejb.Stateless;

import org.zeromq.ZMQ;

import model.AgentCenter;
import model.AgentType;
import server_management.SystemPropertiesKeys;
import utils.AgentsCommunicationMessage;
import utils.AgentsCommunicationMessageType;
import utils.JSONConverter;

@Stateless
public class AgentsRequester implements AgentsRequesterLocal {

	@Override
	public boolean sendRunAgentRequest(AgentCenter agentCenter, String name, AgentType agentType) {
		ZMQ.Context context = ZMQ.context(1);
		
		ZMQ.Socket requester = context.socket(ZMQ.REQ);
		String centerPort =  (agentCenter.getAddress().split(":"))[1];
		int port = SystemPropertiesKeys.MASTER_TCP_PORT + Integer.parseInt(centerPort) - SystemPropertiesKeys.MASTER_PORT + 2;
		String url = "tcp://localhost:" + port;
		System.out.println("SENDING TO: " + url);
		requester.connect(url);
		
		AgentsCommunicationMessage message = new AgentsCommunicationMessage(name, agentType, null, AgentsCommunicationMessageType.RUN_AGENT, true);
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

}
