package server_management;

import javax.ejb.Local;

@Local
public interface AppManagementLocal {

	public void handshake(String address, String alias);
	public boolean isMaster();
	public String getPortOffset();
	public boolean isListenerStarted();
	public void setListenerStarted(boolean started);
	
}
