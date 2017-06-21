package utils;

import java.io.Serializable;

import model.ACLMessage;
import model.AID;

public class MessageToDeliver implements Serializable {

	private AID to;
	private ACLMessage message;

	public MessageToDeliver() {
		super();
		// TODO Auto-generated constructor stub
	}

	public MessageToDeliver(AID to, ACLMessage message) {
		super();
		this.to = to;
		this.message = message;
	}

	public AID getTo() {
		return to;
	}

	public void setTo(AID to) {
		this.to = to;
	}

	public ACLMessage getMessage() {
		return message;
	}

	public void setMessage(ACLMessage message) {
		this.message = message;
	}

}
