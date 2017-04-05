package listsManagementClasses;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

import exceptions.ExistingDiskException;
import exceptions.InvalidParameterException;
import exceptions.NonExistingDiskException;
import listsManagementClasses.DiskManager;

/**
 * This class defines the type of object that manages the different 
 * lists being managed by the system for the lab session 
 * 
 * @author Pedro I Rivera-Vega, Rex J. Reyes
 *
 */

public class DisksListManager {
	private static ArrayList<DiskManager> disks; 
	private static File nameOfDiskUnits = new File("src" + File.separator + "DiskUnits" + File.separator + "DiskNames.txt");
	
	public DisksListManager() { 
		disks = new ArrayList<>(); 
	}

	/**
	 * Find the index of the position where a list with a given name is. 
	 * If no such list it returns -1; otherwise, it returns the index of
	 * the position where it is located in the list of lists....
	 * @param name
	 * @return
	 */
	
	public int getDiskListIndex(String name) { 
		for (int i=0; i<disks.size(); i++) 
			if (disks.get(i).getName().equals(name)) 
				return i; 
		return -1; 
	}
	
	/**
	 * Creates and adds a new DiskUnit to the list of disks.
	 * @param dName the name of the disk
	 * @throws NonExistingDiskException 
	 * @throws InvalidParameterException 
	 */
	public void createNewDisk(String dName) throws InvalidParameterException, NonExistingDiskException {
		disks.add(new DiskManager(dName)); 
		saveInfoToFile();
	}

	/**
	 * Creates a new named list with the given name. 
	 * @param lName the name of the new list. 
	 * @throws NonExistingDiskException 
	 * @throws InvalidParameterException 
	 */
	
	public void createNewDisk(String dName, int blockSize, int capacity) throws InvalidParameterException, NonExistingDiskException {
		disks.add(new DiskManager(dName, blockSize, capacity)); 
		saveInfoToFile(); 
	}
	
	/**
	 * Removes (deletes) a DiskUnit from the list of disks.
	 * @param index the index of the DiskUnit to remove
	 * @return the NamedDiskUnit that was removed
	 * @throws IndexOutOfBoundsException if the index is not valid
	 */
	public DiskManager removeDisk(int index) 
			throws IndexOutOfBoundsException 
	{
		if(index < 0 || index >= disks.size())
			throw new IndexOutOfBoundsException("Invalid index: " + index);

		DiskManager dtr = disks.remove(index);
		dtr.getDisk().shutdown();

		try {
			dtr.delete();
		} catch (NonExistingDiskException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		saveInfoToFile();
		return dtr;
	}
	
	/**
	 * Formats a disk from the list of disks to the given parameters.
	 * @param index the index of the disk to be formatted
	 * @param numberOfBlocks the number of blocks that the disk will have
	 * @param blockSize he size of each block that the disk will have
	 * @throws NonExistingDiskException if the disk does not exist
	 * @throws InvalidParameterException if the parameters are not powers of 2
	 */
	public void formatDisk(int index, int numberOfBlocks, int blockSize) throws NonExistingDiskException, InvalidParameterException{
				String name = disks.get(index).getName();
				disks.get(index).getDisk().shutdown();
				
				try {
					disks.get(index).formatDisk(numberOfBlocks, blockSize);
				} catch (ExistingDiskException e) {
					e.printStackTrace();
				  }
				
				DiskManager ndu = new DiskManager(name);
				disks.set(index, ndu);
	}
	
	/**
	 * Returns the block size of a disk from the list of disks.
	 * @param index the index of the disk
	 * @return the block size of the disk
	 * @throws IndexOutOfBoundsException if the index is not valid
	 */
	public int getBlockSize(int index) throws IndexOutOfBoundsException
	{
		if(index < 0 || index >= disks.size())
			throw new IndexOutOfBoundsException("Invalid index: " + index);

		return disks.get(index).getBlockSize(); 				
	}
	
	/**
	 * Returns the number of blocks a disk from the list has.
	 * @param index the index of the disk
	 * @return the number of blocks the disk has
	 * @throws IndexOutOfBoundsException if the index is not valid
	 */
	public int getCapacity(int index) throws IndexOutOfBoundsException{
		if(index < 0 || index >= disks.size())
			throw new IndexOutOfBoundsException("Invalid index: " + index);

		return disks.get(index).getNumberOfBlocks();
	}
	
	/**
	 * Returns the name of a disk from the list.
	 * @param index the index of the disk
	 * @return the name of said disk
	 * @throws IndexOutOfBoundsException if the index is not valid
	 */
	public String getName(int index) throws IndexOutOfBoundsException{
		if(index < 0 || index >= disks.size())
			throw new IndexOutOfBoundsException("Invalid index: " + index);
		return disks.get(index).getName(); 
	}
	
	/**
	 * Returns an instance of a NamedDiskUnit representing a particular disk from the list.
	 * @param index the index of the disk
	 * @return the NamedDiskUnit representing said disk
	 * @throws IndexOutOfBoundsException if the index is not valid
	 */
	public DiskManager getNamedDiskUnit(int index) throws IndexOutOfBoundsException{
		if(index < 0 || index >= disks.size())
			throw new IndexOutOfBoundsException("Invalid index: " + index);
		return disks.get(index);
	}

	/**
	 * Returns the number of disks in the list.
	 * @return the number of disks
	 */
	public int getNumberOfDisks() { 
		return disks.size(); 
	}
	
	/**
	 * Checks if the specified name exists.
	 * @param name to be compared within the list of disks.
	 * @returns true if existent, false otherwise.
	 */
	
	public boolean nameExists(String name) { 
		int index = getDiskListIndex(name); 
		return index != -1; 
	}
	
	/**
	 * Loads the file of the disks.
	 * @param folder to be read.
	 * @throws NonExistingDiskException 
	 * @throws InvalidParameterException 
	 */
	
	public static void listFilesforFolder (File folder) throws InvalidParameterException, NonExistingDiskException{

		for (File fileentry : folder.listFiles() ){
			if (fileentry.isDirectory()){
				listFilesforFolder(fileentry);
			}
			else {
				
				disks.add(new DiskManager(fileentry.getName()));
				
			}
		}
	}
	
	/**
	 * Saves the disk names to a file.
	 */
	public void saveInfoToFile(){

		try {
			PrintWriter pw = new PrintWriter(nameOfDiskUnits);
			pw.println(getNumberOfDisks());

			for(int i = 0 ; i < disks.size() ; i++)
				pw.println(disks.get(i).getName());                
			
			pw.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Loads the disk names from a file.
	 * @throws NonExistingDiskException 
	 * @throws InvalidParameterException 
	 */
	public void loadInfoFromFile() throws InvalidParameterException, NonExistingDiskException{
		if(nameOfDiskUnits.exists()){
			Scanner sc;
			try {
				sc = new Scanner(nameOfDiskUnits);
				int numberOfDisks = sc.nextInt();
				sc.nextLine();
				for(int i = 0; i < numberOfDisks ; i++)
					createNewDisk(sc.nextLine());

			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Returns if a disk from the list in mounted or not.
	 * @param index the index of the disk
	 * @return true if the disk is mounted, false otherwise
	 * @throws IndexOutOfBoundsException if the indx is not valid
	 */
	public boolean isMounted(int index) throws IndexOutOfBoundsException{
		if(index < 0 || index >= disks.size())
			throw new IndexOutOfBoundsException("Invalid index: " + index);
		
		return disks.get(index).isMounted();
	}
	
	/**
	 * Shuts down every DiskUnit.
	 */
	public void shutdown(){
		for(DiskManager e : disks){
			e.shutdown();
		}
	}
	
}
