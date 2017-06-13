package beans;

import javax.ejb.Stateless;

import org.zeromq.ZMQ;

import model.AgentCenter;
import server_management.SystemPropertiesKeys;
import utils.HandshakeMessage;
import utils.HandshakeMessageType;
import utils.JSONConverter;

@Stateless
public class ShutdownRequester implements ShutdownRequesterLocal {

	@Override
	public void shutdown(AgentCenter agentCenter) {
		ZMQ.Context context = ZMQ.context(1);
		
		ZMQ.Socket requester = context.socket(ZMQ.REQ);
		String url = "tcp://localhost:" + SystemPropertiesKeys.MASTER_TCP_PORT;
		System.out.println("SENDING TO: " + url);
		requester.connect(url);
		
		HandshakeMessage message = new HandshakeMessage(HandshakeMessageType.SHUTDOWN, new AgentCenter(agentCenter.getAlias(), agentCenter.getAddress()), null, null, null, null, true);
		String jsonObject = JSONConverter.convertToJSON(message);
		requester.send(jsonObject, 0);
		
		String reply = requester.recvStr(0);
		HandshakeMessage response = JSONConverter.convertFromJSON(reply);
		
		requester.close();
		context.term();
	}
	
}
