package beans;

import javax.ejb.Local;

import model.ACLMessage;
import model.AID;

@Local
public interface ClientNotificationsRequesterLocal {

	public void sendNewRunningAgentNotification(AID aid);
	public void sendStopAgentNotification(AID aid);
	public void sendShutdownNodeNotification();
	public void sendAddNewNodeNotification();
	public void sendMessageNotification(AID aid, ACLMessage message);
	
}
