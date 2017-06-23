package beans;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.jms.Destination;
import javax.jms.JMSContext;

import agents.AgentManagerLocal;
import model.ACLMessage;
import model.AID;
import server_management.AppManagementLocal;

@Stateless
public class MessageManager implements MessageManagerLocal {

	@EJB
	AppManagementLocal appManagement;
	
	@EJB
	AgentsRequesterLocal agentsRequester;
	
	@EJB
	AgentManagerLocal agentManager;
	
	@Inject
	JMSContext context;
	
	@Resource(mappedName = "java:/jms/queue/messageListener")
	private Destination destination;
	
	@Override
	public void sendACL(ACLMessage aclMessage) {
		for(AID receiver : aclMessage.getReceivers()) {
			if(receiver.getHost().getAlias().equals(appManagement.getLocalAlias())) {
				sendMessageToAgent(receiver, aclMessage);
			} else {
				agentsRequester.sendACLMessage(receiver, aclMessage);
			}
		}
	}
	
	@Override
	public void sendMessageToAgent(AID agent, ACLMessage aclMessage) {
		agentManager.deliverMessageToAgent(agent, aclMessage);
	}

}
