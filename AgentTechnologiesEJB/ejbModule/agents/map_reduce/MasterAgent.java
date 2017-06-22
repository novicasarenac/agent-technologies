package agents.map_reduce;

import java.io.File;
import java.io.InputStream;

import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Stateful;

import agents.AgentManagerLocal;
import model.ACLMessage;
import model.Agent;
import model.AgentLocal;
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
		}
	}

}
