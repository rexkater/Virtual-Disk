package listsManagementClasses;
import exceptions.ExistingDiskException;
import exceptions.InvalidParameterException;
import exceptions.NonExistingDiskException;
import diskUtilities.DiskUnit;

/**
 * Disks manager class.
 * @author Rex J. Reyes
 *
 */

public class DiskManager {

	private String name; 
	private DiskUnit disk;
	private boolean mounted = false;
	
	/**
	 * Creates a NamedDiskUnit with the given name.
	 * @param name the name of the disk
	 * @throws InvalidParameterException 
	 * @throws NonExistingDiskException 
	 */
	
	public DiskManager(String name) throws InvalidParameterException, NonExistingDiskException {
		
		try{
		DiskUnit.createDiskUnit(name);
		disk = DiskUnit.mount(name);
		} catch(ExistingDiskException e){
			disk = DiskUnit.mount(name);
		  } catch (NonExistingDiskException e) {
			e.printStackTrace();
		    }
		
		this.name = name; 
	}
	
	/**
	 * 
	 * @param name
	 * @param blockSize
	 * @param capacity
	 * @throws InvalidParameterException 
	 * @throws NonExistingDiskException 
	 */
	
	public DiskManager(String name, int blockSize, int capacity) throws InvalidParameterException, NonExistingDiskException {
		
		try{
		DiskUnit.createDiskUnit(name, capacity, blockSize);
		disk = DiskUnit.mount(name);
		} catch(ExistingDiskException e){
			disk = DiskUnit.mount(name);
		  }
		
		this.name = name; 
	}
	
	/**
	 * Method to get the name of the manager.
	 * @return the name in string form.
	 */
	
	public String getName() {
		return name;
	}
	
	public DiskUnit getDisk() {
		return disk;
	}
	
	/**
	 * Returns if the disk is currently mounted.
	 * @return true if the disk is mounted, false otherwise
	 */
	public boolean isMounted(){
		return mounted;
	}
	
	public void setMount(boolean b) {
		mounted = b;
	}
	
	/**
	 * Returns the number of blocks of this disk.
	 * @return the number of blocks of this disk
	 */
	
	public int getNumberOfBlocks(){
		return disk.getCapacity();
	}
	
	/**
	 * Returns the size of each block in this file.
	 * @return the size of each block in this file
	 */
	
	public int getBlockSize(){
		return disk.getBlockSize();
	}
	
	/**
	 * Formats the disk with the given parameters.
	 * @param numberOfBlocks the number of blocks the disk will have
	 * @param blockSize the size of each block the disk will have
	 * @throws NonExistingDiskException if the disk does not exist
	 * @throws InvalidParameterException if the parameters are not powers of 2
	 * @throws ExistingDiskException 
	 */
	
	public void formatDisk(int numberOfBlocks, int blockSize) throws NonExistingDiskException, InvalidParameterException, ExistingDiskException{
		disk.format(name, numberOfBlocks, blockSize);
	}
	
	/**
	 * Shuts down the disk associated with this NamedDiskUnit.
	 */
	
	public void shutdown(){
		disk.shutdown();
	}
	
	/**
	 * Deletes this disk.
	 * @throws NonExistingDiskException 
	 */
	
	public void delete() throws NonExistingDiskException{
		disk.delete(name);
	}
	
//	/**
//	 * Loads a file to the disk.
//	 * @param name the name of the file
//	 * @param size the size of the file
//	 * @param f the file to load
//	 */
//	public void loadFile(String name, int size, File f){
//		disk.createNewFile(this.name, name, size, f);
//	}
//	
	
}
