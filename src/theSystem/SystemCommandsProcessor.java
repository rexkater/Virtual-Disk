package theSystem;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import diskUtilities.DiskUnit;
import diskUtilities.Utils;
import exceptions.ExistingDiskException;
import exceptions.InvalidParameterException;
import exceptions.NonExistingDiskException;
import operandHandlers.OperandValidatorUtils;
import listsManagementClasses.DiskManager;
import listsManagementClasses.DisksListManager;
import systemGeneralClasses.Command;
import systemGeneralClasses.CommandActionHandler;
import systemGeneralClasses.CommandProcessor;
import systemGeneralClasses.FixedLengthCommand;
import systemGeneralClasses.SystemCommand;
import systemGeneralClasses.VariableLengthCommand;
import stack.IntStack;

public class SystemCommandsProcessor extends CommandProcessor { 
	
	private ArrayList<String> resultsList; 
	private DisksListManager disksListManager = new DisksListManager();
	private DiskManager currentDiskManager;
	@SuppressWarnings("unused")
	private DiskUnit currentDiskUnit;
	private boolean stopExecution; 

	/**
	 *  Initializes the list of possible commands for each of the
	 *  states the system can be in. 
	 */
	public SystemCommandsProcessor() {
		
		currentState = new IntStack(); 
		currentState.push(GENERALSTATE); 
		createCommandList(1);    
		
		add(GENERALSTATE, SystemCommand.getFLSC("createdisk name int int", new CreateDiskProcessor())); 
		add(GENERALSTATE, SystemCommand.getFLSC("deletedisk name", new DeleteDiskProcessor())); 
		add(GENERALSTATE, SystemCommand.getVLSC("mount name", new mountDiskProcessor())); 
		add(GENERALSTATE, SystemCommand.getVLSC("unmount", new unmountDiskProcessor()));
		add(GENERALSTATE, SystemCommand.getFLSC("loadfile name name", new loadFileProcessor())); 
		add(GENERALSTATE, SystemCommand.getFLSC("cp name", new cp())); 
		add(GENERALSTATE, SystemCommand.getFLSC("cat name", new cat())); 
		add(GENERALSTATE, SystemCommand.getFLSC("showdisks", new ShowDisksProcessor())); 	
		add(GENERALSTATE, SystemCommand.getFLSC("ls", new ls())); 
		add(GENERALSTATE, SystemCommand.getFLSC("exit", new ShutDownProcessor())); 
		add(GENERALSTATE, SystemCommand.getFLSC("help", new HelpProcessor())); 
				
		stopExecution = false; 
		
		try {
			disksListManager.loadInfoFromFile();
		} catch (InvalidParameterException | NonExistingDiskException e) {
			e.printStackTrace();
		  }
	}
		
	public ArrayList<String> getResultsList() { 
		return resultsList; 
	}
	
	// Classes for each command:
	
	/**
	 * 
	 * @author Rex J. Reyes
	 */
	
	private class ShutDownProcessor implements CommandActionHandler { 
		public ArrayList<String> execute(Command c) { 
			resultsList = new ArrayList<String>();
			resultsList.add("\tSystem is shutting down!");
			disksListManager.saveInfoToFile();
			disksListManager.shutdown();
			stopExecution = true;
			return resultsList;
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
			FixedLengthCommand fc = (FixedLengthCommand) c;
			
			String name = fc.getOperand(1);
			int cap = Integer.parseInt(fc.getOperand(2));
			int bSize = Integer.parseInt(fc.getOperand(3));

			if (bSize < 32 || !Utils.powerOf2(bSize))
				resultsList.add("Size must be a power of 2 and greater than 32 bytes. Input size was: " + bSize);
			
			if (!OperandValidatorUtils.isValidName(name))
				resultsList.add("Invalid name formation: " + name); 
			
			else if (disksListManager.nameExists(name))
				resultsList.add("This disk already exists: " + name);
			
			else {
				try {
					DiskUnit.createDiskUnit(name, cap, bSize);
					disksListManager.createNewDisk(name, bSize, cap);
				} catch (ExistingDiskException e) {
					e.printStackTrace();
				  } catch (exceptions.InvalidParameterException e) {
					e.printStackTrace();
				    } catch (NonExistingDiskException e) {
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
		    int nDisks = disksListManager.getNumberOfDisks();
		    
		    if (nDisks == 0)
		    	resultsList.add("There are no disks in the system at this moment."); 
		    else {
		    	resultsList.add("Names of the existing disks are: "); 
		        for (int i=0; i<nDisks; i++)
		        	if (!disksListManager.isMounted(i))
						resultsList.add("\t" + i + ") " + disksListManager.getName(i) + ": Has " + disksListManager.getCapacity(i)+ " blocks, with a size of " + disksListManager.getBlockSize(i)+" bytes each one. It's not mounted.");
					else resultsList.add("\t" + i + ") " + disksListManager.getName(i) + ": Has " + disksListManager.getCapacity(i)+ " blocks, with a size of " + disksListManager.getBlockSize(i)+" bytes each one. It is mounted.");       
		        }
		      return resultsList; 
		   } 
	}
	
	/**
	 * 
	 * @author rex.reyes
	 *
	 */
	
	private class DeleteDiskProcessor implements CommandActionHandler { 
		public ArrayList<String> execute(Command c) {

			resultsList = new ArrayList<String>(); 
			FixedLengthCommand fc = (FixedLengthCommand) c;
			String name = fc.getOperand(1);
			int diskIndex = disksListManager.getDiskListIndex(name);

			if (diskIndex == -1)
				resultsList.add("This disk doesn't exist: " + name);
			
			else {
				if (currentDiskManager != null)
					resultsList.add("Please unmount the currently mounted disk before deleting it.");
				
				currentDiskManager = null;
				currentDiskUnit = null;
				disksListManager.removeDisk(diskIndex);
				resultsList.add("\tDiskUnit " + name + " was successfully removed.");
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
			
			if(currentDiskManager != null){
				resultsList.add("There's a disk already mounted.");
				return resultsList;
			}

			VariableLengthCommand vlc = (VariableLengthCommand) c; 
			String name = vlc.getItemsForOperand(1).get(0);
			int diskIndex = disksListManager.getDiskListIndex(name);
			
			if (!OperandValidatorUtils.isValidName(name))
			resultsList.add("Invalid name formation: " + name); 
			else if (!disksListManager.nameExists(name))
				resultsList.add("This disk doesnt exist: " + name);
			else {
				currentDiskManager = disksListManager.getNamedDiskUnit(diskIndex);
				currentDiskManager.setMount(true);
				currentDiskUnit = currentDiskManager.getDisk();
				resultsList.add("\tDiskUnit " + name + " was successfully mounted.");
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
			
			if(currentDiskManager == null){
				resultsList.add("\tThere's no disk unit currently mounted.");
				return resultsList;
			}
			
			else {
				currentDiskManager.setMount(false);
				currentDiskUnit = null;
				currentDiskManager = null;
				resultsList.add("\tDiskUnit was successfully unmounted.");
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
		String fileName = fc.getOperand(1); // Need to work on this.
		
		if (!disksListManager.nameExists(fileName))
			resultsList.add("This directory doesnt exist: " + fileName);
		else if(currentDiskManager != null)
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
		
		if (currentDiskManager != null)
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
		String fileName = fc.getOperand(1);
		File file = null; // Need to work on this.
		
		
		if (!OperandValidatorUtils.isValidName(fileName))
			resultsList.add("Invalid name formation: " + fileName); 
		else if (!disksListManager.nameExists(fileName))
			resultsList.add("This disk doesnt exist: " + fileName);
		
		@SuppressWarnings("null")
		String path = file.getAbsolutePath() + File.separator + fileName;
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

}		





