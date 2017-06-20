package beans;

import javax.ejb.Local;

import model.AID;

@Local
public interface ClientNotificationsRequesterLocal {

	public void sendNewRunningAgentNotification(AID aid);
	
}
