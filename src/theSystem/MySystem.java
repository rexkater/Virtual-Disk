/**
 * 
 */
package theSystem;

import java.io.IOException;
import systemGeneralClasses.SystemController;

public class MySystem {

	/**
	 * @param args
	 */
	
	public static void main(String[] args) throws IOException  {
		SystemController system = new SystemController(); 
		system.start(); 
		System.out.println("+++++ SYSTEM SHUTDOWN +++++"); 
	}
}
