package listsManagementClasses;

import theSystem.SystemCommandsProcessor;
import exceptions.NonExistingDiskException;
import diskUtilities.DiskUnit;

/**
 * Disks manager class.
 * @author Rex J. Reyes
 *
 */

public class DisksManager {

	private String name = null; 
	
	/**
	 * Constructor for the disk manager.
	 * @param name of the manager.
	 */
	public DisksManager(String name) { 
		this.name = name; 
	}
	
	/**
	 * Method to get the name of the manager.
	 * @return the name in string form.
	 */
	
	public String getName() {
		return name;
	}
	
	/**
	 * Method to get the capacity of the disk.
	 * @param name of the disk.
	 * @returns capacity of the disk.
	 */
	
	public int Capacity(String name){
		int bsize = 0;
		
		if (!SystemCommandsProcessor.isMounted){
			try {
				bsize = DiskUnit.mount(name).getCapacity(); // DiskUnit
			} catch (NonExistingDiskException e) {
				e.printStackTrace();
			}
			
		DiskUnit.shutdown(name);
		
		} else {
			SystemCommandsProcessor.isMounted = false;
			try {
				 bsize = DiskUnit.mount(name).getCapacity(); // DiskUnit
			} catch (NonExistingDiskException e) {
				e.printStackTrace();
			}
			
			DiskUnit.shutdown(name);
			SystemCommandsProcessor.isMounted=true;
		  }
		
		return bsize;
	}
	
	/**
	 * Method to get the block size of the disk.
	 * @param name of the disk.
	 * @returns the block size of the disk.
	 */
	public int blockSize(String name){
		int bsize = 0;
		
		if (!SystemCommandsProcessor.isMounted){
			try {
				 bsize = DiskUnit.mount(name).getBlockSize(); // DiskUnit 
			} catch (NonExistingDiskException e) {
				e.printStackTrace();
			  }
			
			DiskUnit.shutdown(name);
			
		} else {
			SystemCommandsProcessor.isMounted = false;
			try {
				 bsize = DiskUnit.mount(name).getBlockSize(); // DiskUnit 
			} catch (NonExistingDiskException e) {
				e.printStackTrace();
			  }
			
			DiskUnit.shutdown(name);
			SystemCommandsProcessor.isMounted=true;
		  }
		
		return bsize;
	}
}
