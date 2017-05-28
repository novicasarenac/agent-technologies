package model;

import java.io.Serializable;

public class AgentType implements Serializable {

	private String name;
	private String module;

	public AgentType() {
		super();
		// TODO Auto-generated constructor stub
	}

	public AgentType(String name, String module) {
		super();
		this.name = name;
		this.module = module;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getModule() {
		return module;
	}

	public void setModule(String module) {
		this.module = module;
	}

}
