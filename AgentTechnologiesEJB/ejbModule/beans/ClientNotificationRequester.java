package beans;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.zeromq.ZMQ;

import model.AID;
import server_management.AppManagementLocal;
import server_management.SystemPropertiesKeys;
import utils.AgentsCommunicationMessage;
import utils.AgentsCommunicationMessageType;
import utils.JSONConverter;
import utils.WSMessage;
import utils.WSMessageType;

@Stateless
public class ClientNotificationRequester implements ClientNotificationsRequesterLocal {

	@EJB
	AppManagementLocal appManagement;
	
	@Override
	public void sendNewRunningAgentNotification(AID aid) {
		ZMQ.Context context = ZMQ.context(1);
		
		ZMQ.Socket requester = context.socket(ZMQ.REQ);
		String centerPort =  (appManagement.getLocal().split(":"))[1];
		int port = SystemPropertiesKeys.MASTER_TCP_PORT + Integer.parseInt(centerPort) - SystemPropertiesKeys.MASTER_PORT + 3;
		String url = "tcp://localhost:" + port;
		System.out.println("SENDING TO: " + url);
		requester.connect(url);

		WSMessage wsMessage = new WSMessage(null, null, aid, null, null, WSMessageType.ADD_RUNNING_AGENT);
		String jsonObject = JSONConverter.convertWSMessageToJSON(wsMessage);
		requester.send(jsonObject, 0);
		
		String reply = requester.recvStr(0);
		
		requester.close();
		context.term();
	}

}
