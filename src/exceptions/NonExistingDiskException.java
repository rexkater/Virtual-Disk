package exceptions;

public class NonExistingDiskException extends Exception {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public NonExistingDiskException(String string) {
		super(string);
	}

}
