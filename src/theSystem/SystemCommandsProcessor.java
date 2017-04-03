package theSystem;

import java.io.File;
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
	
	private ArrayList<String> diskList; 
	SystemCommand attemptedSC; 
	boolean stopExecution; 
	boolean bsize = true;
	private ListsManager listsManager = new ListsManager(); 

	/**
	 *  Initializes the list of possible commands for each of the
	 *  states the system can be in. 
	 */
	public SystemCommandsProcessor() {
		currentState = new IntStack(); 
		currentState.push(GENERALSTATE); 
		createCommandList(1);    
		
		add(GENERALSTATE, SystemCommand.getFLSC("create-disk name int int", new CreateProcessor())); 
		add(GENERALSTATE, SystemCommand.getFLSC("showlists", new ShowListsProcessor())); 		
		add(GENERALSTATE, SystemCommand.getFLSC("append name int", new AppendProcessor())); 
		add(GENERALSTATE, SystemCommand.getFLSC("showall name", new ShowAllProcessor())); 
		add(GENERALSTATE, SystemCommand.getFLSC("size name", new ShowSize()));
		add(GENERALSTATE, SystemCommand.getFLSC("add name int int", new AddProcessor())); 
		add(GENERALSTATE, SystemCommand.getFLSC("exit", new ShutDownProcessor())); 
		add(GENERALSTATE, SystemCommand.getFLSC("help", new HelpProcessor())); 
				
		stopExecution = false; 
	}
		
	public ArrayList<String> getResultsList() { 
		return diskList; 
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
	
	private class ShutDownProcessor implements CommandActionHandler { 
		public ArrayList<String> execute(Command c) { 

			diskList = new ArrayList<String>(); 
			diskList.add("SYSTEM IS SHUTTING DOWN!!!!");
			stopExecution = true;
			return diskList; 
		}
	}

	// classes added for the lab exercise about this project. 
	
	private class CreateProcessor implements CommandActionHandler {
		@Override
		public ArrayList<String> execute(Command c) {

			diskList = new ArrayList<String>(); 
			
			FixedLengthCommand fc = (FixedLengthCommand) c;
			
			String name = fc.getOperand(1);
			int cap = Integer.parseInt(fc.getOperand(2));
			int size = Integer.parseInt(fc.getOperand(3));
			
			diskList.add("The capacity of the disk should be + " + cap + " and the size "+ size + ".");
			bsize = true;

			if (size < 32 || !Utils.powerOf2(size)){
				diskList.add("Size must be a power of 2 and greater than 32 bytes. Selected size was: " + size);
				bsize = false;
			}
			
			if (!OperandValidatorUtils.isValidName(name))
				diskList.add("Invalid name formation: " + name); 
			
			else if (nameExists(name, diskList))
				diskList.add("This disk already exist: " + name);
			
			else if(bsize){
				try {
					DiskUnit.createDiskUnit(name, cap, size);
					listsManager.createNewList(name);
				} catch (ExistingDiskException e) {
					e.printStackTrace();
				} catch (exceptions.InvalidParameterException e) {
					e.printStackTrace();
				}
			}

			else diskList.add("disk wasn't created");
			return diskList; 
		} 
		
	}

	/**
	 * 
	 * @return
	 */
	public boolean inShutdownMode() {
		return stopExecution;
	}
	
	private class ShowListsProcessor implements CommandActionHandler { 
		   public ArrayList<String> execute(Command c) {  

		    // command has no operand - nothing is needed from the
		    // command. if it comes here, it is the showall command....
		    diskList = new ArrayList<String>(); 

		    int nLists = listsManager.getNumberOfLists();
		    if (nLists == 0)
		        diskList.add("There are no lists in the system at this moment."); 
		    else {
		        diskList.add("Names of the existing lists are: "); 
		        for (int i=0; i<nLists; i++)
		          diskList.add("\t"+listsManager.getName(i));         
		        }
		      return diskList; 
		   } 
	}
	
	private class AppendProcessor implements CommandActionHandler { 
		   public ArrayList<String> execute(Command c) {  

		      diskList = new ArrayList<String>(); 

		      FixedLengthCommand fc = (FixedLengthCommand) c;

		      // the following needs to be adapted to named lists and the 
		      // usage of the ListsManagerObject ......

		      String name = fc.getOperand(1); 
		      int listIndex = listsManager.getListIndex(name); 
		      if (listIndex == -1)
		         diskList.add("No such list: " + name); 
		      else {
		       int value = Integer.parseInt(fc.getOperand(2)); 
		         listsManager.addElement(listIndex, value);
		      }
		      return diskList; 
		   } 
	}
	
	private class AddProcessor implements CommandActionHandler { 
		   public ArrayList<String> execute(Command c) {  

		      diskList = new ArrayList<String>(); 

		      FixedLengthCommand fc = (FixedLengthCommand) c;

		      // the following needs to be adapted to named lists and the 
		      // usage of the ListsManagerObject ......

		      String name = fc.getOperand(1); 
		      int listIndex = listsManager.getListIndex(name); 
		      if (listIndex == -1)
		         diskList.add("No such list: " + name); 
		      else {
		       int index = Integer.parseInt(fc.getOperand(2));
		       if(index > listsManager.getSize(listIndex) || index < 0)
		    	   diskList.add("Index out of bounds.");
		       else{
		       int value = Integer.parseInt(fc.getOperand(3)); 
		       		listsManager.addElement(listIndex, index, value);}
		      }
		      return diskList; 
		   } 
	}
	
	// classes added for the lab exercise about this project. 
	private class ShowAllProcessor implements CommandActionHandler { 
	   public ArrayList<String> execute(Command c) {  
	            
	     // command has no operand - nothing is needed from the
	     // command. if it comes here, it is the showall command....
	     diskList = new ArrayList<String>(); 

	     // Show each element in the list in a different line, followin
	     // the specified format: index   --- value
	     // put some heading too....

	     FixedLengthCommand fc = (FixedLengthCommand) c;
	        
	     String name = fc.getOperand(1); 
	     int listIndex = listsManager.getListIndex(name); 
	     if (listIndex == -1)
	      diskList.add("No such list: " + name); 
	     else {
	      int lSize = listsManager.getSize(listIndex);
	      if (lSize == 0)
	          diskList.add("List is currently empty."); 
	      else {
	        diskList.add("Values in the list are: "); 
	        for (int i=0; i<lSize; i++) 
	            diskList.add("\tlist[" + i + "] --- " +   
	                    listsManager.getElement(listIndex, i));         
	        }
	     }
	     return diskList; 
	   } 
	}
	
	private class ShowSize implements CommandActionHandler { 
		   public ArrayList<String> execute(Command c) {  
		            
		     // command has no operand - nothing is needed from the
		     // command. if it comes here, it is the showall command....
		     diskList = new ArrayList<String>(); 

		     // Show each element in the list in a different line, followin
		     // the specified format: index   --- value
		     // put some heading too....

		     FixedLengthCommand fc = (FixedLengthCommand) c;
		        
		     String name = fc.getOperand(1); 
		     int listIndex = listsManager.getListIndex(name); 
		     if (listIndex == -1)
		      diskList.add("No such list: " + name); 
		     else {
		      int lSize = listsManager.getSize(listIndex);
		      if (lSize == 0)
		          diskList.add("List is currently empty."); 
		      else {
		        diskList.add("The size of the list is: " + lSize);          
		        }
		     }
		     return diskList; 
		   } 
		}
	
	/**
	 * loads the disks to an arraylist
	 * @param folder where there are going to be loaded from
	 * @return
	 */
	
	public ArrayList<String> listFilesforFolder (File folder){
		ArrayList<String> resultsList2 = new ArrayList<String>();

		for (File fileentry : folder.listFiles() ){
			if (fileentry.isDirectory()){
				listFilesforFolder(fileentry);
			}
			else {
				
				resultsList2.add(fileentry.getName());
				
			}
		}
		return resultsList2;
	}
	
	/**
	 * checks whether the disk exists
	 * @param name name of disk
	 * @param list list to be checked
	 * @return
	 */
	
	public boolean nameExists(String name, ArrayList<String> list){
		for (int i = 0; i<list.size();i++){
			if (name.equals(list.get(i)))
				return true;
		}
		return false;
	}

}		





