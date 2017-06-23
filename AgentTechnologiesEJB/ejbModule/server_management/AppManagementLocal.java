package server_management;

import java.util.List;

import javax.ejb.Local;
import javax.jms.Destination;
import javax.websocket.Session;

@Local
public interface AppManagementLocal {

	public void handshake(String address, String alias);
	public boolean isMaster();
	public String getPortOffset();
	public String getLocalAlias();
	public String getLocal();
	public boolean isListenerStarted();
	public void setHeartbeatListenerStarted(boolean started);
	public boolean isHeartbeatListenerStarted();
	public void setListenerStarted(boolean started);
	public boolean isAgentsCommunicationListenerStarted();
	public void setAgentsCommunicationListenerStarted(boolean agentsCommunicationListenerStarted);
	public boolean isClientNotificationListenerStarted();
	public void setClientNotificationListenerStarted(boolean clientNotificationListenerStarted);
	public void addSession(Session session);
	public void removeSession(Session session);
	public List<Session> getSessions();
	public boolean isMessageListenerStarted();
	public void setMessageListenerStarted(boolean messageListenerStarted);
	
}
