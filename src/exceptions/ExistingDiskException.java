package exceptions;

public class ExistingDiskException extends Exception {
	
	/**
	 * Exception to be executed if a disk unit already exists.
	 */
	
	private static final long serialVersionUID = 1L;

	public ExistingDiskException(String string) {
		super(string);
	}

}
