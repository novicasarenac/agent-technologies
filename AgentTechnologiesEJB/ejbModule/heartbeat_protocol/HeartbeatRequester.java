package heartbeat_protocol;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Schedule;
import javax.ejb.Singleton;

import org.zeromq.ZMQ;

import beans.AgentCentersManagementLocal;
import beans.AgentsManagementLocal;
import beans.ClientNotificationsRequesterLocal;
import beans.ShutdownRequesterLocal;
import model.AgentCenter;
import server_management.AppManagementLocal;
import server_management.SystemPropertiesKeys;

@Singleton
public class HeartbeatRequester implements HeartbeatRequesterLocal {
	
	@EJB
	AgentCentersManagementLocal agentCenterManagement;
	
	@EJB
	ShutdownRequesterLocal shutdownRequester;
	
	@EJB
	AgentsManagementLocal agentsManagement;
	
	@EJB
	AppManagementLocal appManagement;
	
	@EJB
	ClientNotificationsRequesterLocal clientNotificationsRequester;

	@Override
	@Schedule(hour = "*", minute = "*", persistent = false)
	public void heartbeat() {
		List<AgentCenter> centersToRemove = new ArrayList<>();
		
		for(AgentCenter agentCenter : agentCenterManagement.getAgentCenters().values()) {
			if(!agentCenter.getAlias().equals(appManagement.getLocalAlias())) {
				int numberOfTries = 0;
				if(!sendMessage(agentCenter)) {
					numberOfTries++;
					if(!sendMessage(agentCenter))
						numberOfTries++;
					else numberOfTries = 0;
				}
				
				if(numberOfTries > 1)
					centersToRemove.add(agentCenter);
			}
		}

		removeUnactiveCenters(centersToRemove);
	}
	
	public boolean sendMessage(AgentCenter agentCenter) {
		boolean retVal;
		ZMQ.Context context = ZMQ.context(1);
		
		ZMQ.Socket requester = context.socket(ZMQ.REQ);
		requester.setReceiveTimeOut(5000);
		String centerPort =  (agentCenter.getAddress().split(":"))[1];
		int port = SystemPropertiesKeys.MASTER_TCP_PORT + Integer.parseInt(centerPort) - SystemPropertiesKeys.MASTER_PORT + 1;
		
		String url = "tcp://localhost:" + port;
		System.out.println("SENDING TO: " + url);
		requester.connect(url);
		
		String message = "Heartbeat message";
		requester.send(message, 0);
		String reply = requester.recvStr(0);
	
		if(reply == null) {
			retVal = false;
			System.out.println("Host " + agentCenter.getAlias() + " is dead.");
		} else {
			retVal = true;
			System.out.println("Host " + agentCenter.getAlias() + " is alive.");
		}
		
		requester.close();
		if(reply != null)
			context.term();
		
		return retVal;
	}
	
	public void removeUnactiveCenters(List<AgentCenter> listToRemove) {
		for(AgentCenter center : listToRemove) {
			agentCenterManagement.removeCenter(center);
			boolean exists = agentsManagement.removeAgentTypes(center);
			agentsManagement.removeRunningAgents(center);
			clientNotificationsRequester.sendShutdownNodeNotification();
			
			if(exists) {
				for(AgentCenter recipient : agentCenterManagement.getAgentCenters().values()) {
					if(!recipient.getAlias().equals(center.getAlias()) && !recipient.getAlias().equals(appManagement.getLocalAlias())) {
						shutdownRequester.heartbeatShutdownNode(recipient, center);
					}
				}
			}
		}
	}

}
