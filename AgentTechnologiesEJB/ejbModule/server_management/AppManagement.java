package server_management;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
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
import javax.websocket.Session;

import org.zeromq.ZMQ;

import beans.AgentCentersManagementLocal;
import beans.AgentsManagementLocal;
import beans.HandshakeRequesterLocal;
import beans.ShutdownRequesterLocal;
import exceptions.AliasExistsException;
import model.AgentCenter;
import utils.HandshakeMessage;
import utils.HandshakeMessageType;
import utils.JSONConverter;

@Singleton(name = "AppManagement")
@Startup
public class AppManagement implements AppManagementLocal {

	private String master;
	private String local;
	private String localAlias;
	private String portOffset;
	private boolean listenerStarted;
	private boolean heartbeatListenerStarted;
	private boolean agentsCommunicationListenerStarted;
	private boolean clientNotificationListenerStarted;
	private boolean messageListenerStarted;
	List<Session> clientSessions = new ArrayList<>();
	
	@EJB
	AgentCentersManagementLocal agentCentersManagement;
	
	@EJB
	AgentsManagementLocal agentsManagement;
	
	@EJB
	HandshakeRequesterLocal handshakeRequester;
	
	@EJB
	ShutdownRequesterLocal shutdownRequester;
	
	@Inject
	JMSContext context;
	
	@Resource(mappedName = "java:/jms/queue/activateListener")
	private Destination destination;
	
	@Resource(mappedName = "java:/jms/queue/heartbeatListener")
	private Destination destinationHeartbeat;
	
	@Resource(mappedName = "java:/jms/queue/agentsCommunicationListener")
	private Destination destinationAgentsCommunication;
	
	@Resource(mappedName = "java:/jms/queue/notificationListener")
	private Destination notificationListenerDestination;
	
	@Resource(mappedName = "java:/jms/queue/messageListener")
	private Destination destinationMessageListener;
	
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
		sendActivationMessage();
		activateHeartbeatListener();
		activateAgentsCommunicationListener();
		activateClientNotificationListener();
		activateMessageListener();
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
	
	@PreDestroy
	public void shutdown() {
		ZMQ.Context context = ZMQ.context(1);
		
		ZMQ.Socket requester = context.socket(ZMQ.REQ);
		String url = "tcp://localhost:" + SystemPropertiesKeys.MASTER_TCP_PORT;
		System.out.println("SENDING TO: " + url);
		requester.connect(url);
		
		HandshakeMessage message = new HandshakeMessage(HandshakeMessageType.SHUTDOWN, new AgentCenter(localAlias, local), null, null, null, null, true);
		String jsonObject = JSONConverter.convertToJSON(message);
		requester.send(jsonObject, 0);
		
		String reply = requester.recvStr(0);
		HandshakeMessage response = JSONConverter.convertFromJSON(reply);
		
		requester.close();
		context.term();
	}
	
	public void sendActivationMessage() {
		String message = "Activation";
		JMSProducer producer = context.createProducer();
		producer.send(destination, message);
	}
	
	public void activateHeartbeatListener() {
		String message = "Activation";
		JMSProducer producer = context.createProducer();
		producer.send(destinationHeartbeat, message);
	}
	
	public void activateAgentsCommunicationListener() {
		String message = "Activation";
		JMSProducer producer = context.createProducer();
		producer.send(destinationAgentsCommunication, message);
	}
	
	public void activateClientNotificationListener() {
		String message = "Activation";
		JMSProducer producer = context.createProducer();
		producer.send(notificationListenerDestination, message);
	}
	
	public void activateMessageListener() {
		String message = "Activation";
		JMSProducer producer = context.createProducer();
		producer.send(destinationMessageListener, message);
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
	}
	
	@Override
	public String getPortOffset() {
		return portOffset;
	}
	
	@Override
	public String getLocalAlias() {
		return localAlias;
	}
	
	@Override
	public String getLocal() {
		return local;
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

	@Override
	@Lock(LockType.READ)
	public boolean isHeartbeatListenerStarted() {
		return heartbeatListenerStarted;
	}
	
	@Override
	@Lock(LockType.WRITE)
	public void setHeartbeatListenerStarted(boolean started) {
		heartbeatListenerStarted = started;
	}
	
	@Override
	@Lock(LockType.READ)
	public boolean isAgentsCommunicationListenerStarted() {
		return agentsCommunicationListenerStarted;
	}

	@Override
	@Lock(LockType.WRITE)
	public void setAgentsCommunicationListenerStarted(boolean agentsCommunicationListenerStarted) {
		this.agentsCommunicationListenerStarted = agentsCommunicationListenerStarted;
	}
	
	@Override
	public boolean isClientNotificationListenerStarted() {
		return clientNotificationListenerStarted;
	}

	@Override
	public void setClientNotificationListenerStarted(boolean clientNotificationListenerStarted) {
		this.clientNotificationListenerStarted = clientNotificationListenerStarted;
	}

	@Override
	public boolean isMessageListenerStarted() {
		return messageListenerStarted;
	}

	@Override
	public void setMessageListenerStarted(boolean messageListenerStarted) {
		this.messageListenerStarted = messageListenerStarted;
	}

	@Override
	public void addSession(Session session) {
		if(!clientSessions.contains(session)) {
			clientSessions.add(session);
		}
	}
	
	@Override
	public void removeSession(Session session) {
		if(clientSessions.contains(session)) {
			clientSessions.remove(session);
		}
	}
	
	@Override
	public List<Session> getSessions() {
		return clientSessions;
	}
	
}
