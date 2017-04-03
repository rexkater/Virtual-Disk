package theSystem;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import diskUtilities.DiskUnit;
import diskUtilities.Utils;
import exceptions.ExistingDiskException;
import operandHandlers.OperandValidatorUtils;
import listsManagementClasses.ListsManager;
import systemGeneralClasses.Command;
import systemGeneralClasses.CommandActionHandler;
import systemGeneralClasses.CommandProcessor;
import systemGeneralClasses.FixedLengthCommand;
import systemGeneralClasses.SystemCommand;
//import systemGeneralClasses.VariableLengthCommand;
import stack.IntStack;

public class SystemCommandsProcessor extends CommandProcessor { 
	
	private ArrayList<String> disksList; 
	SystemCommand attemptedSC; 
	boolean stopExecution; 
	boolean isMounted = false;
	private ListsManager listsManager = new ListsManager(); 

	/**
	 *  Initializes the list of possible commands for each of the
	 *  states the system can be in. 
	 */
	public SystemCommandsProcessor() {
		currentState = new IntStack(); 
		currentState.push(GENERALSTATE); 
		createCommandList(1);    
		
		add(GENERALSTATE, SystemCommand.getFLSC("createdisk name int int", new CreateDiskProcessor())); 
		add(GENERALSTATE, SystemCommand.getFLSC("showdisks", new ShowDisksProcessor())); 		
//		add(GENERALSTATE, SystemCommand.getFLSC("delete name", new DeleteDiskProcessor())); 
//		add(GENERALSTATE, SystemCommand.getFLSC("showall name", new mountDiskProcessor())); 
//		add(GENERALSTATE, SystemCommand.getFLSC("size name", new unmountDiskProcessor()));
//		add(GENERALSTATE, SystemCommand.getFLSC("add name int int", new loadFileProcessor())); 
		add(GENERALSTATE, SystemCommand.getFLSC("exit", new ShutDownProcessor())); 
		add(GENERALSTATE, SystemCommand.getFLSC("help", new HelpProcessor())); 

				
		stopExecution = false; 
	}
		
	public ArrayList<String> getResultsList() { 
		return disksList; 
	}
	
	/**
	 *  The following are inner classes. Notice that there is one such class
	 *  for each command. The idea is that enclose the implementation of each
	 *  command in a particular unique place. Notice that, for each command, 
	 *  what you need is to implement the internal method "execute(Command c)".
	 *  In each particular case, your implementation assumes that the command
	 *  received as parameter is of the type corresponding to the particular
	 *  inner class. For example, the command received by the "execute(...)" 
	 *  method inside the "LoginProcessor" class must be a "login" command. 
	 */
	
	// Classes for each command:
	
	/**
	 * 
	 * @author Rex J. Reyes
	 *
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
	 *
	 */
	
	private class CreateDiskProcessor implements CommandActionHandler {
		@Override
		public ArrayList<String> execute(Command c) {

			disksList = new ArrayList<String>(); 
			
			FixedLengthCommand fc = (FixedLengthCommand) c;
			
			String name = fc.getOperand(1);
			int cap = Integer.parseInt(fc.getOperand(2));
			int size = Integer.parseInt(fc.getOperand(3));

			if (size < 32 || !Utils.powerOf2(size))
				disksList.add("Size must be a power of 2 and greater than 32 bytes. Input size was: " + size);
			
			if (!OperandValidatorUtils.isValidName(name))
				disksList.add("Invalid name formation: " + name); 
			
			else if (nameExists(name, disksList))
				disksList.add("This disk already exists: " + name);
			
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
			
			return disksList; 
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
	 *
	 */
	
	private class ShowDisksProcessor implements CommandActionHandler { 
		   public ArrayList<String> execute(Command c) {  

		    // command has no operand - nothing is needed from the
		    // command. if it comes here, it is the showall command....
		    disksList = new ArrayList<String>(); 

		    int nLists = listsManager.getNumberOfLists();
		    if (nLists == 0)
		        disksList.add("There are no lists in the system at this moment."); 
		    else {
		        disksList.add("Names of the existing lists are: "); 
		        for (int i=0; i<nLists; i++)
		          disksList.add("\t"+listsManager.getName(i));         
		        }
		      return disksList; 
		   } 
	}
	
	private class DeleteDiskProcessor implements CommandActionHandler { 
		public ArrayList<String> execute(Command c) {

			disksList = new ArrayList<String>();
			
			disksList = listFilesforFolder(DiskUnit.f);

			FixedLengthCommand fc = (FixedLengthCommand) c;

			String name = fc.getOperand(1);

			if (!nameExists(name, disksList))
				disksList.add("This disk doesnt exist: " + name);
			else if (isMounted)
				isMounted = false;
			
			else {
				try {
					DeleteFile(DiskUnit.f, name);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				  }
			}

			return disksList;
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

}		





