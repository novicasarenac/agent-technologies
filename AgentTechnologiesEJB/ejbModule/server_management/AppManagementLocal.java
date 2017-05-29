package server_management;

import javax.ejb.Local;

@Local
public interface AppManagementLocal {

	public void sendRegisterRequest(String address, String alias);
	public boolean isMaster();
	
}
