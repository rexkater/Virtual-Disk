package diskUtilities;

/**
 * Class to organize a diversity of utilities to be used on the project.
 * 
 * @author rex.reyes
 *
 */

public class Utils {
	
	/**
	 * Method to find the power of 2 of an specific number.
	 * @param n is the number from which the power of 2 will be calculated.
	 * @returns n to the power of 2.
	 */
	
	public static boolean powerOf2(int n){
		if((n > 0) && ((n & (n - 1)) == 0))
			return true;
		return false;
	}

}
