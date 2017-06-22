package agents.map_reduce;

import java.io.File;
import java.io.InputStream;
import java.util.concurrent.ThreadLocalRandom;

import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Stateful;

import agents.AgentManager;
import agents.AgentManagerLocal;
import exceptions.NameExistsException;
import model.ACLMessage;
import model.Agent;
import model.AgentLocal;
import model.AgentType;
import model.Performative;
import sun.misc.IOUtils;

@Stateful
@Local(AgentLocal.class)
public class MasterAgent extends Agent {

	@EJB
	AgentManagerLocal agentManager;
	
	@Override
	public void handleMessage(ACLMessage message) {
		if(message.getPerformative() == Performative.REQUEST) {
			int counter = 0;
			for(String filename : AgentManager.mapReduceFiles) {
				createSlave("slave" + ThreadLocalRandom.current().nextInt(1, 1000));
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
