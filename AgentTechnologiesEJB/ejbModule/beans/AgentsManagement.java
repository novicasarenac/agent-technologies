package beans;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;

import model.AID;
import model.AgentType;
import server_management.SystemPropertiesKeys;
import utils.AgentTypesReader;

@Singleton
public class AgentsManagement implements AgentsManagementLocal {

	private List<AgentType> allTypes = new ArrayList<>();
	private List<AgentType> supportedTypes = new ArrayList<>();
	
	private Map<String, AID> runningAgents = new HashMap<>();
	
	@PostConstruct
	public void init() {
		String filename = System.getProperty(SystemPropertiesKeys.FILENAME);
		if(filename == null && System.getProperty(SystemPropertiesKeys.MASTER_NODE) == null)
			filename = "agents1.txt";
		
		AgentTypesReader reader = new AgentTypesReader();
		supportedTypes = reader.readTypes(filename);
		for(AgentType type : supportedTypes) {
			System.out.println(type);
			if(!allTypes.contains(type))
				allTypes.add(type);
		}
	}

	@Override
	public void testiraj() {
		// TODO Auto-generated method stub
		
	}
}
