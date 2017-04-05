/**
 * 
 */
package theSystem;

import java.io.File;
import java.io.IOException;

import exceptions.iNodeIndexOutOfBoundsException;
import systemGeneralClasses.SystemController;

public class SecondPhaseMainClass {

	/**
	 * MAIN CLASS TO EXECUTE THE PROGRAM. 
	 * @param args
	 * @throws iNodeIndexOutOfBoundsException 
	 */
	
	public static void main(String[] args) throws IOException, iNodeIndexOutOfBoundsException  {
		File f =  new File("src" + File.separator + "DiskUnits" + File.separator);
		
		if (!f.exists()){
			f.mkdirs();
		}
		
		SystemController system = new SystemController(); 
		system.start(); 
		System.out.println("+++++ SYSTEM SHUTDOWN +++++"); 
	}
}
