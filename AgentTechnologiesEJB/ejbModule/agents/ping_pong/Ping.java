package agents.ping_pong;

import javax.ejb.Local;
import javax.ejb.Stateful;

import model.ACLMessage;
import model.Agent;
import model.AgentLocal;
import model.Performative;

@Stateful
@Local(AgentLocal.class)
public class Ping extends Agent {

	@Override
	public void handleMessage(ACLMessage message) {
		if(message.getPerformative() == Performative.INFORM) {
			System.out.println("INFORM message came to " + this.getId().getName());
		}
	}

}
