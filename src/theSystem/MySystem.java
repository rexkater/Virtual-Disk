/**
 * 
 */
package theSystem;

import java.io.File;
import java.io.IOException;
import systemGeneralClasses.SystemController;

public class MySystem {

	/**
	 * @param args
	 */
	
	public static void main(String[] args) throws IOException  {
		File f =  new File("src" + File.separator + "DiskUnits" + File.separator);
		
		if (!f.exists()){
			f.mkdirs();
		}
		
		SystemController system = new SystemController(); 
		system.start(); 
		System.out.println("+++++ SYSTEM SHUTDOWN +++++"); 
	}
}
