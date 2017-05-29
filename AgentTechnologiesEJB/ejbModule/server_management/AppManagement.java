package server_management;

import java.net.InetAddress;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;

import beans.DataManagementLocal;
import exceptions.AliasExistsException;
import model.AgentCenter;

@Singleton
@Startup
public class AppManagement implements AppManagementLocal{

	private String master;
	private String local;
	private String localAlias;
	private String portOffset;
	
	@EJB
	DataManagementLocal dataManagement;
	
	@PostConstruct
	public void initialize() {
		master = System.getProperty(SystemPropertiesKeys.MASTER_NODE);
		
		if(master == null)
			System.out.println("This is master node!");
		else
			System.out.println("Master node: " + master);
		
		portOffset = System.getProperty(SystemPropertiesKeys.OFFSET);
		if(portOffset == null) {
			portOffset = "0";
		}
		
		InetAddress address = null;
		try {
			address = InetAddress.getLoopbackAddress();   //for ip address getLocalHost()
			local = address.getHostAddress() + ':' + Integer.toString((SystemPropertiesKeys.MASTER_PORT + Integer.parseInt(portOffset)));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		localAlias = System.getProperty(SystemPropertiesKeys.ALIAS);
		if(localAlias == null)
			localAlias = address.getHostName() + portOffset;
		
		System.out.println("Local address: " + local + "\tLocal alias: " + localAlias);
		
		if(isMaster()) {
			try {
				dataManagement.register(new AgentCenter(localAlias, local));
			} catch (AliasExistsException e) {
				e.printStackTrace();
			}
		} else {
			sendRegisterRequest(local, localAlias);
		}
	}
	
	public boolean isMaster() {
		return master == null;
	}

	@Override
	public void sendRegisterRequest(String address, String alias) {
		
	}
	
}
