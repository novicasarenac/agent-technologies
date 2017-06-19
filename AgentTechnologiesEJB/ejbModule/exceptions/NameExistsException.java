package exceptions;

public class NameExistsException extends Exception {

	public NameExistsException() {
		super("Agent name already exists");
	}

}
