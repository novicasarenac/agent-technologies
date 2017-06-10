package utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import model.AgentType;

public class AgentTypesReader {

	public List<AgentType> readTypes(String filename) {
		List<AgentType> retVal = new ArrayList<>();
		
		InputStream in = getClass().getClassLoader().getResourceAsStream("/" + filename);
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		String line;
		try {
			while((line = reader.readLine()) != null) {
				String[] lineSplit = line.split(",");
				retVal.add(new AgentType(lineSplit[1].trim(), lineSplit[0].trim()));
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return retVal;
	}
}
