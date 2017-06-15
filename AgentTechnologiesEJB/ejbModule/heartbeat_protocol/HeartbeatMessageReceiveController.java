package heartbeat_protocol;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.jms.Message;
import javax.jms.MessageListener;

import org.zeromq.ZMQ;

import server_management.AppManagementLocal;
import server_management.SystemPropertiesKeys;

@MessageDriven(activationConfig = {
		@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
		@ActivationConfigProperty(propertyName = "destination", propertyValue = "java:/jms/queue/heartbeatListener"),
		@ActivationConfigProperty(propertyName = "transactionTimeout", propertyValue = "300000")
})
public class HeartbeatMessageReceiveController implements MessageListener {

	@EJB
	AppManagementLocal appManagement;
	
	@Override
	public void onMessage(Message arg0) {
		if(!appManagement.isHeartbeatListenerStarted()) {
			try {
				listen();
			} catch(Exception e) {
				System.out.println("Connection is already opened!");
			}
		}
	}
	
	@Asynchronous
	public void listen() throws Exception {
		appManagement.setHeartbeatListenerStarted(true);
		ZMQ.Context context = ZMQ.context(1);
		
		ZMQ.Socket responder = context.socket(ZMQ.REP);
		int port = SystemPropertiesKeys.MASTER_TCP_PORT + Integer.parseInt(appManagement.getPortOffset()) + 1;
		String url = "tcp://*:" + port;
		System.out.println("URL RECEIVER: " + url);
		responder.bind(url);
		
		while(!Thread.currentThread().isInterrupted()) {
			String request = responder.recvStr(0);
			String response = "alive";
			responder.send(response, 0);
		}
	}

}
