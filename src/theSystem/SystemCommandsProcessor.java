package theSystem;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import diskUtilities.DiskUnit;
import diskUtilities.Utils;
import exceptions.ExistingDiskException;
import exceptions.NonExistingDiskException;
import operandHandlers.OperandValidatorUtils;
import listsManagementClasses.ListsManager;
import systemGeneralClasses.Command;
import systemGeneralClasses.CommandActionHandler;
import systemGeneralClasses.CommandProcessor;
import systemGeneralClasses.FixedLengthCommand;
import systemGeneralClasses.SystemCommand;
import systemGeneralClasses.VariableLengthCommand;
import stack.IntStack;

public class SystemCommandsProcessor extends CommandProcessor { 
	
	private ArrayList<String> disksList; 
	private ArrayList<String> resultsList; 
	public static boolean isMounted = false;
	private ListsManager listsManager = new ListsManager(); 
	private boolean stopExecution; 
	public static int mountedDiskIndex = -1;

	/**
	 *  Initializes the list of possible commands for each of the
	 *  states the system can be in. 
	 */
	public SystemCommandsProcessor() {
		
		currentState = new IntStack(); 
		ListsManager.listFilesforFolder(DiskUnit.f);
		currentState.push(GENERALSTATE); 
		createCommandList(1);    
		
		add(GENERALSTATE, SystemCommand.getFLSC("createdisk name int int", new CreateDiskProcessor())); 
		add(GENERALSTATE, SystemCommand.getFLSC("deletedisk name", new DeleteDiskProcessor())); 
		add(GENERALSTATE, SystemCommand.getVLSC("mountdisk name", new mountDiskProcessor())); 
		add(GENERALSTATE, SystemCommand.getVLSC("unmountdisk name", new unmountDiskProcessor()));
		add(GENERALSTATE, SystemCommand.getFLSC("loadfile name name", new loadFileProcessor())); 
		add(GENERALSTATE, SystemCommand.getFLSC("cp name", new cp())); 
		add(GENERALSTATE, SystemCommand.getFLSC("cat name", new cat())); 
		add(GENERALSTATE, SystemCommand.getFLSC("showdisks", new ShowDisksProcessor())); 	
		add(GENERALSTATE, SystemCommand.getFLSC("ls", new ls())); 
		add(GENERALSTATE, SystemCommand.getFLSC("exit", new ShutDownProcessor())); 
		add(GENERALSTATE, SystemCommand.getFLSC("help", new HelpProcessor())); 
				
		stopExecution = false; 
	}
		
	public ArrayList<String> getResultsList() { 
		return disksList; 
	}
	
	// Classes for each command:
	
	/**
	 * 
	 * @author Rex J. Reyes
	 */
	
	private class ShutDownProcessor implements CommandActionHandler { 
		public ArrayList<String> execute(Command c) { 

			disksList = new ArrayList<String>(); 
			disksList.add("SYSTEM IS SHUTTING DOWN!");
			stopExecution = true;
			return disksList; 
		}
	}
	
	/**
	 * 
	 * @author Rex J. Reyes
	 */
	
	private class CreateDiskProcessor implements CommandActionHandler {
		@Override
		public ArrayList<String> execute(Command c) {

			resultsList = new ArrayList<String>(); 
			disksList = new ArrayList<String>(); 
			disksList = listDisks(DiskUnit.f);
			
			FixedLengthCommand fc = (FixedLengthCommand) c;
			
			String name = fc.getOperand(1);
			int cap = Integer.parseInt(fc.getOperand(2));
			int size = Integer.parseInt(fc.getOperand(3));

			if (size < 32 || !Utils.powerOf2(size))
				resultsList.add("Size must be a power of 2 and greater than 32 bytes. Input size was: " + size);
			
			if (!OperandValidatorUtils.isValidName(name))
				resultsList.add("Invalid name formation: " + name); 
			
			else if (nameExists(name, disksList))
				resultsList.add("This disk already exists: " + name);
			
			else {
				try {
					DiskUnit.createDiskUnit(name, cap, size);
					listsManager.createNewList(name);
				} catch (ExistingDiskException e) {
					e.printStackTrace();
				  } catch (exceptions.InvalidParameterException e) {
					e.printStackTrace();
				  }
			}
			
			return resultsList; 
		} 
	}

	/**
	 * 
	 * @return
	 */
	
	public boolean inShutdownMode() {
		return stopExecution;
	}
	
	/**
	 * 
	 * @author Rex J. Reyes
	 */
	
	private class ShowDisksProcessor implements CommandActionHandler { 
		   public ArrayList<String> execute(Command c) {  

			resultsList = new ArrayList<String>(); 
		    disksList = new ArrayList<String>(); 
			disksList = listDisks(DiskUnit.f);
		    int nDisks = listsManager.getNumberOfDisks();
		    
		    if (nDisks == 0)
		    	resultsList.add("There are no disks in the system at this moment."); 
		    else {
		    	resultsList.add("Names of the existing disks are: "); 
		        for (int i=0; i<nDisks; i++)
		        	if (mountedDiskIndex != getIndex(disksList.get(i), disksList))
						resultsList.add("\t" + i + ") " + listsManager.getName(i) + ": Has " + listsManager.getCapacity(i)+ " blocks, with a size of " + listsManager.getBlockSize(i)+" bytes each one. It's not mounted.");
					else if (mountedDiskIndex == getIndex(disksList.get(i), disksList))
						resultsList.add("\t" + i + ") " + listsManager.getName(i) + ": Has " + listsManager.getCapacity(i)+ " blocks, with a size of " + listsManager.getBlockSize(i)+" bytes each one. It is mounted.");       
		        }
		      return resultsList; 
		   } 
	}
	
	private class DeleteDiskProcessor implements CommandActionHandler { 
		public ArrayList<String> execute(Command c) {

			resultsList = new ArrayList<String>(); 
			disksList = new ArrayList<String>();
			disksList = listDisks(DiskUnit.f);

			FixedLengthCommand fc = (FixedLengthCommand) c;
			String name = fc.getOperand(1);

			if (!nameExists(name, disksList))
				resultsList.add("This disk doesnt exist: " + name);
			
			else {
				if (isMounted)
					isMounted = false;
				try {
					DeleteFile(DiskUnit.f, name);
					resultsList.add("This disk was successfully deleted.");
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				  }
			}

			return resultsList;
		}
	}
	
	/**
	 * Mounts the disk.
	 * @author Rex J. Reyes
	 */
	
	private class mountDiskProcessor implements CommandActionHandler {

		@Override
		public ArrayList<String> execute(Command c) {
			
			resultsList = new ArrayList<String>(); 
			disksList = new ArrayList<String>();
			disksList = listDisks(DiskUnit.f);

			VariableLengthCommand vlc = (VariableLengthCommand) c; 
			String name = vlc.getItemsForOperand(1).get(0);

			
			if (!OperandValidatorUtils.isValidName(name))
			resultsList.add("Invalid name formation: " + name); 
			else if (!nameExists(name, disksList))
				resultsList.add("This disk doesnt exist: " + name);
			else if(isMounted)
				resultsList.add("Disk is already mounted :" + disksList.get(mountedDiskIndex));
			else
				try {
					DiskUnit.mount(name);
					resultsList.add("Disk Mounted");
					isMounted =true;
					mountedDiskIndex = getIndex(name, disksList);
				} catch (NonExistingDiskException e) {
					e.printStackTrace();
				}
			
			return resultsList; 
		}
	}
	
	/**
	 * Unmounts the disk.
	 * @author Rex J. Reyes
	 */
	
	private class unmountDiskProcessor implements CommandActionHandler {
		@Override
		public ArrayList<String> execute(Command c) {

			resultsList = new ArrayList<String>(); 
			disksList = new ArrayList<String>();
			disksList = listDisks(DiskUnit.f);
			
			VariableLengthCommand vlc = (VariableLengthCommand) c; 
			String name = vlc.getItemsForOperand(1).get(0);

			DiskUnit dU = new DiskUnit(name);
			if (!isMounted)
				resultsList.add("You have to mount the disk first.");
			else if (mountedDiskIndex != getIndex(name, disksList))
				resultsList.add("Unmount the other disk that is mounted: " + disksList.get(mountedDiskIndex));
			else {
				if (!OperandValidatorUtils.isValidName(name))
					resultsList.add("Invalid name formation: " + name); 
				else if (!nameExists(name, disksList))
					resultsList.add("This disk doesnt exist: " + name);
				else {
					dU.shutdown();
					resultsList.add("Disk Unmounted");
					isMounted =false;
					mountedDiskIndex = -1;
				}	
			}
			
			return resultsList; 
		} 
	}
	
	/**
	 * Loads the file.
	 * Attempts to read a new file into the current directory in the current working disk unit. 
	 * The first operand is the name of the new file; how the system will record it in the current 
	 * directory. If such name already exists in the current directory, and if it corresponds to 
	 * an existing data file, then the current content of such file is erased and replaced by the 
	 * content of the file being read. If the given name is new, then a new file with that name is 
	 * created and its content will be a copy of the actual content of the file being read. 
	 * The second operand is the name of the file to read. Such file must exist in the same 
	 * directory where the program is being executed. If no such file, then the command ends 
	 * with appropriate message. If the disk unit does not have enough space for the new file, 
	 * the command also ends with a message. 
	 * @author Rex J. Reyes
	 */
	
	private class loadFileProcessor implements CommandActionHandler {
		public ArrayList<String> execute(Command c) {

		resultsList = new ArrayList<String>();
		// FixedLengthCommand fc = (FixedLengthCommand) c;

		// String fileName = fc.getOperand(1);
		// String ftr = fc.getOperand(2);

		return resultsList;
		
		}
	}
	
	/**
	 * Copies one internal file to another internal file. 
	 * It works similar to the command loadfile, but this time 
	 * the input file (name given first) is also an internal file that 
	 * must be a data file in the current directory. You should figure out the rest.
	 * @author Rex J. Reyes
	 */
	
	private class cp implements CommandActionHandler {
		public ArrayList<String> execute(Command c) {

		resultsList = new ArrayList<String>();
		FixedLengthCommand fc = (FixedLengthCommand) c;
		String filename = fc.getOperand(1); // Need to work on this.
		
		if (!nameExists(filename, disksList))
			resultsList.add("This directory doesnt exist: " + filename);
		else if(isMounted)
			resultsList.add("Copied");
			
			 else resultsList.add("Mount a disk first.");
		
		return resultsList;
		
		}
	}
	
	/**
	 * List the names and sizes of all the files and 
	 * directories that are part of the current directory.
	 * @author Rex J. Reyes
	 */
	
	private class ls implements CommandActionHandler {
		public ArrayList<String> execute(Command c) {

		resultsList = new ArrayList<String>();

		disksList = new ArrayList<String>();
		disksList = listDisks(DiskUnit.f);
		
		if (isMounted)
			resultsList.add("Names and sizes of files: Disk is empty"); // Need to work on this.
		else resultsList.add("Mount disk first");
		
		return resultsList;
		}
	}
	
	/**
	 * Displays the content of the given internal file.
	 * @author Rex J. Reyes
	 */
	
	private class cat implements CommandActionHandler {
		public ArrayList<String> execute(Command c) {

		resultsList = new ArrayList<String>();
		FixedLengthCommand fc = (FixedLengthCommand) c;
		String filename = fc.getOperand(1);
		File file = null; // Need to work on this.
		
		
		if (!OperandValidatorUtils.isValidName(filename))
			resultsList.add("Invalid name formation: " + filename); 
		else if (!nameExists(filename, disksList))
			resultsList.add("This disk doesnt exist: " + filename);
		
		String path  = file.getAbsolutePath() + file.separator + filename;
		File file2 = new File(path);
		file2.canRead();
		BufferedReader br = null;

		try {
			String sCurrentLine;
			br = new BufferedReader(new FileReader(file2));

			while ((sCurrentLine = br.readLine()) != null) {
				System.out.println(sCurrentLine);
			}

		} catch (IOException e) {
			e.printStackTrace();
		  } finally {
			try {
				if (br != null)br.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			  }
		    }

		return resultsList;
		}
	}
	
	/**
	 * Checks if the name already exists in the list of disks.
	 * @param name of disk.
	 * @param list of disks in which the method searches.
	 * @return true if it exists, false otherwise.
	 */
	
	public boolean nameExists(String name, ArrayList<String> list){
		for (int i = 0; i<list.size();i++){
			if (name.equals(list.get(i)))
				return true;
		}
		return false;
	}
	
	/**
	 * Saves the disk's list to an array list.
	 * @param folder where they are going to be loaded from.
	 * @return the array list with the names of the disks.
	 */
	
	public ArrayList<String> listDisks (File folder){
		ArrayList<String> disksList = new ArrayList<String>();

		for (File fileentry : folder.listFiles() ){
			if (fileentry.isDirectory())
				listDisks(fileentry);
			else disksList.add(fileentry.getName());
		}
		
		return disksList;
	}
	
	/**
	 * 
	 * @param file
	 * @param name
	 * @throws FileNotFoundException
	 */
	
	public void DeleteFile(File file, String name) throws FileNotFoundException {
		String path = file.getAbsolutePath() + File.separator + name;
		//System.out.println(path);
		Path pa = Paths.get(path);
		
		if (!file.exists())
			return;
		
		if(file.isDirectory()){
			
			for (File f : file.listFiles()){
				if (f.getAbsolutePath().equals(path)){
					
					try {
					    Files.delete(pa);
					    // need to modify list's size.
					} catch (NoSuchFileException x) {
					    System.err.format("%s: no such" + " file or directory%n", path);
					  } catch (DirectoryNotEmptyException x) {
					    System.err.format("%s not empty%n", path);
					    } catch (IOException x) {
					    // File permission problems are caught here.
					    System.err.println(x);
					      }
				}
			}
		}
	}
	
	/**
	 * 
	 * @param name
	 * @param list
	 * @return
	 */
	
	public int getIndex(String name, ArrayList<String> list){
		int index = 0;
		for (int i = 0; i<list.size();i++){
			if (name.equals(list.get(i)))
				index = i;
		}
		return index;
	}

}		





