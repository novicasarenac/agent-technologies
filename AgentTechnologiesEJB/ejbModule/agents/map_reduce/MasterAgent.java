package agents.map_reduce;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Stateful;

import agents.AgentManager;
import agents.AgentManagerLocal;
import beans.MessageManagerLocal;
import exceptions.NameExistsException;
import model.ACLMessage;
import model.Agent;
import model.AgentLocal;
import model.AgentType;
import model.Performative;

@Stateful
@Local(AgentLocal.class)
public class MasterAgent extends Agent {

	@EJB
	AgentManagerLocal agentManager;
	@EJB
	MessageManagerLocal messageManager;

	private String localName;
	private List<String> slaves = new ArrayList<>();
	
	@Override
	public void handleMessage(ACLMessage message) {
		if(message.getPerformative() == Performative.REQUEST) {
			if(localName == null) {
				localName = message.getReceivers().get(0).getName();
			}
			if(slaves.isEmpty()) {
				for(String filename : AgentManager.mapReduceFiles) {
					String slaveName = "slave" + ThreadLocalRandom.current().nextInt(1, 1000);
					createSlave(slaveName);
					slaves.add(slaveName);
				}
			}
			int i = 0;
			for(String agentName : slaves) {
				ACLMessage aclMessage = new ACLMessage();
				aclMessage.setSender(agentManager.getAllRunningAgents().get(localName));
				aclMessage.getReceivers().add(agentManager.getAllRunningAgents().get(agentName));
				aclMessage.setPerformative(Performative.REQUEST);
				aclMessage.setContent(AgentManager.mapReduceFiles.get(i));
				i++;
				messageManager.sendACL(aclMessage);
			}
		} else if(message.getPerformative() == Performative.INFORM) {
			System.out.println("STIGOO ODGOVOR");
		}
	}
	
	public void createSlave(String name) {
		try {
			agentManager.runAgent(name, new AgentType(SlaveAgent.class.getSimpleName(), "agents"));
		} catch (NameExistsException e) {
			e.printStackTrace();
		}
	}

}
