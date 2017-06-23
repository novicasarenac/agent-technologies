package agents.map_reduce;

import java.util.ArrayList;
import java.util.HashMap;
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
	private List<String> delivered = new ArrayList<>();
	private Map<Character, Integer> result = new HashMap<>();
	
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
			Map<Character, Integer> slaveResult = (Map<Character, Integer>) message.getContentObj();
			for(Character key : slaveResult.keySet()) {
				if(result.containsKey(key)) {
					Integer temp = result.get(key) + slaveResult.get(key);
					result.put(key, temp);
				} else {
					result.put(key, slaveResult.get(key));
				}
			}
			delivered.add(message.getSender().getName());
			
			if(slaves.size() == delivered.size()) {
				System.out.println(result);
				delivered.clear();
				result.clear();
			}
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
