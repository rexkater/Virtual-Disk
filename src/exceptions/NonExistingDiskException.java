package exceptions;

public class NonExistingDiskException extends Exception {
	
	/**
	 * Exception that is executed if a disk does not exist.
	 */
	
	private static final long serialVersionUID = 1L;

	public NonExistingDiskException(String string) {
		super(string);
	}

}
