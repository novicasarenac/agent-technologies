package agents.map_reduce;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Stateful;

import beans.MessageManagerLocal;
import model.ACLMessage;
import model.Agent;
import model.AgentLocal;
import model.Performative;

@Stateful
@Local(AgentLocal.class)
public class SlaveAgent extends Agent {

	@EJB
	MessageManagerLocal messageManager;
	
	@Override
	public void handleMessage(ACLMessage message) {
		if(message.getPerformative() == Performative.REQUEST) {
			InputStream in = getClass().getClassLoader().getResourceAsStream(message.getContent());
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			Map<Character, Integer> occurency = new HashMap<>();
			String line;
			try {
				while((line = reader.readLine()) != null) {
					for(int i = 0; i < line.length(); i++) {
						if(occurency.containsKey(line.charAt(i))) {
							Integer temp = occurency.get(line.charAt(i));
							occurency.put(line.charAt(i), ++temp);
						} else {
							occurency.put(line.charAt(i), 1);
						}
					}
				}
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			ACLMessage aclMessage = new ACLMessage();
			aclMessage.setSender(message.getReceivers().get(0));
			aclMessage.getReceivers().add(message.getSender());
			aclMessage.setPerformative(Performative.INFORM);
			aclMessage.setContentObj(occurency);
			messageManager.sendACL(aclMessage);
		}
	}

}
