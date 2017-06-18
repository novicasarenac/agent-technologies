package agents.ping_pong;

import javax.ejb.Remote;
import javax.ejb.Stateful;

import model.ACLMessage;
import model.Agent;
import model.AgentRemote;

@Stateful
@Remote(AgentRemote.class)
public class Ping extends Agent {

	@Override
	public void handleMessage(ACLMessage message) {
		
	}

}
