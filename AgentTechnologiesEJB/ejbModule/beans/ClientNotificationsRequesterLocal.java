package beans;

import javax.ejb.Local;

import model.AID;

@Local
public interface ClientNotificationsRequesterLocal {

	public void sendNewRunningAgentNotification(AID aid);
	public void sendShutdownNodeNotification();
	public void sendAddNewNodeNotification();
	
}
