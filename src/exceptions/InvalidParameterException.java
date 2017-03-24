package exceptions;

public class InvalidParameterException extends Exception {
	
	/**
	 * Exception to be executed if the parameter is not under 
	 * the conditions expected, or of an specific type.
	 */
	
	private static final long serialVersionUID = 1L;

	public InvalidParameterException(String string) {
		super(string);
	}

}
