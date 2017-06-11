package server_management;

import java.io.IOException;
import java.net.InetAddress;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import javax.jms.Destination;
import javax.jms.JMSContext;
import javax.jms.JMSProducer;

import org.codehaus.jackson.map.ObjectMapper;
import org.zeromq.ZMQ;

import beans.AgentCentersManagementLocal;
import beans.AgentsManagementLocal;
import beans.HandshakeRequesterLocal;
import exceptions.AliasExistsException;
import model.AgentCenter;
import utils.HandshakeMessage;
import utils.HandshakeMessageType;
import utils.JSONConverter;

@Singleton(name = "AppManagement")
@Startup
public class AppManagement implements AppManagementLocal{

	private String master;
	private String local;
	private String localAlias;
	private String portOffset;
	private boolean listenerStarted;
	
	@EJB
	AgentCentersManagementLocal agentCentersManagement;
	
	@EJB
	AgentsManagementLocal agentsManagement;
	
	@EJB
	HandshakeRequesterLocal handshakeRequester;
	
	@Inject
	JMSContext context;
	
	@Resource(mappedName = "java:/jms/queue/activateListener")
	private Destination destination;
	
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
		agentsManagement.testiraj();
		sendActivationMessage();
		if(isMaster()) {
			try {
				agentCentersManagement.register(new AgentCenter(localAlias, local));
			} catch (AliasExistsException e) {
				e.printStackTrace();
			}
		} else {
			handshake(local, localAlias);
		}
	}
	
	public void sendActivationMessage() {
		String message = "Activation";
		JMSProducer producer = context.createProducer();
		producer.send(destination, message);
	}
	
	@Override
	public void handshake(String address, String alias) {
		int numberOfTries = 0;
		if(!handshakeRequester.sendRegisterRequest(address, alias)) {
			numberOfTries++;
			if(!handshakeRequester.sendRegisterRequest(address, alias)) {
				numberOfTries++;
			} else numberOfTries = 0;
		}
		if(numberOfTries > 1) {
			//rollback
			System.out.println("It needs callback");
		} else System.out.println("It doesnt need callback");
	}
	
	public String getPortOffset() {
		return portOffset;
	}

	@Lock(LockType.READ)
	public boolean isListenerStarted() {
		return listenerStarted;
	}

	@Lock(LockType.WRITE)
	public void setListenerStarted(boolean listenerStarted) {
		this.listenerStarted = listenerStarted;
	}
	
	@Override
	public boolean isMaster() {
		return master == null;
	}
	
}
