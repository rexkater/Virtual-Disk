package listsManagementClasses;

import java.io.File;
import java.util.ArrayList;
import listsManagementClasses.DisksManager;

/**
 * This class defines the type of object that manages the different 
 * lists being managed by the system for the lab session 
 * 
 * @author Pedro I Rivera-Vega
 *
 */

public class ListsManager {
	private static ArrayList<DisksManager> lists; 
	
	public ListsManager() { 
		lists = new ArrayList<>(); 
	}

	/**
	 * Find the index of the position where a list with a given name is. 
	 * If no such list it returns -1; otherwise, it returns the index of
	 * the position where it is located in the list of lists....
	 * @param name
	 * @return
	 */
	
	public int getListIndex(String name) { 
		for (int i=0; i<lists.size(); i++) 
			if (lists.get(i).getName().equals(name)) 
				return i; 
		return -1; 
	}

	/**
	 * Creates a new named list with the given name. 
	 * @param lName the name of the new list. 
	 */
	
	public void createNewList(String lName) {
		lists.add(new DisksManager(lName)); 
	}
	
	/**
	 * get the block size 
	 * @param listIndex
	 * @return block size 
	 */
	
	public int getBlockSize(int listIndex){
		String name = lists.get(listIndex).getName();
		return lists.get(listIndex).blockSize(name);
	} 
	
	/**
	 * returns the capacity of the disk 
	 * @param listIndex disk
	 * @return capacity
	 */
	
	public int getCapacity(int listIndex){
		String name = lists.get(listIndex).getName();
		return lists.get(listIndex).Capacity(name);
	}
	
	/**
	 * Method to get the name of a disk.
	 * @param listIndex is the index of an specific disk.
	 * @returns a specific disk name.
	 */
	
	public String getName(int listIndex) {
		return lists.get(listIndex).getName(); 
	}
	
	/**
	 * Gets the number of current existent disks.
	 * @returns number of existent disks.
	 */
	
	public int getNumberOfDisks() { 
		return lists.size(); 
	}
	
	/**
	 * Checks if the specified name exists.
	 * @param name to be compared within the list of disks.
	 * @returns true if existent, false otherwise.
	 */
	
	public boolean nameExists(String name) { 
		int index = getListIndex(name); 
		return index != -1; 
	}
	
	/**
	 * Loads the file of the disks.
	 * @param folder to be read.
	 */
	
	public static void listFilesforFolder (File folder){

		for (File fileentry : folder.listFiles() ){
			if (fileentry.isDirectory()){
				listFilesforFolder(fileentry);
			}
			else {
				
				lists.add(new DisksManager(fileentry.getName()));
				
			}
		}
	}
	
}
