package agents.map_reduce;

import javax.ejb.Local;
import javax.ejb.Stateful;

import model.ACLMessage;
import model.Agent;
import model.AgentLocal;

@Stateful
@Local(AgentLocal.class)
public class SlaveAgent extends Agent {

	@Override
	public void handleMessage(ACLMessage message) {
		
	}

}
