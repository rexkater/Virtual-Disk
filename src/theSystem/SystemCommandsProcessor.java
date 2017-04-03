package theSystem;

import java.util.ArrayList;
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
	
	private ArrayList<String> resultsList; 
	SystemCommand attemptedSC; 
	boolean stopExecution; 
	private ListsManager listsManager = new ListsManager(); 

	/**
	 *  Initializes the list of possible commands for each of the
	 *  states the system can be in. 
	 */
	public SystemCommandsProcessor() {
		currentState = new IntStack(); 
		currentState.push(GENERALSTATE); 
		createCommandList(1);    
		
		add(GENERALSTATE, SystemCommand.getVLSC("create-disk name", new CreateProcessor())); 
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
		return resultsList; 
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

			resultsList = new ArrayList<String>(); 
			resultsList.add("SYSTEM IS SHUTTING DOWN!!!!");
			stopExecution = true;
			return resultsList; 
		}
	}

	// classes added for the lab exercise about this project. 
	
	private class CreateProcessor implements CommandActionHandler {
		@Override
		public ArrayList<String> execute(Command c) {

			resultsList = new ArrayList<String>(); 

			VariableLengthCommand vlc = (VariableLengthCommand) c; 
			String name = vlc.getItemsForOperand(1).get(0);

			//FixedLengthCommand fc = (FixedLengthCommand) c;
			//String name = fc.getOperand(1); 

			if (!OperandValidatorUtils.isValidName(name))
				resultsList.add("Invalid name formation: " + name); 
			else if (listsManager.nameExists(name)) 
				resultsList.add("Name give is already in use by another list: " + name); 
			else 
				listsManager.createNewList(name);
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
	
	private class ShowListsProcessor implements CommandActionHandler { 
		   public ArrayList<String> execute(Command c) {  

		    // command has no operand - nothing is needed from the
		    // command. if it comes here, it is the showall command....
		    resultsList = new ArrayList<String>(); 

		    int nLists = listsManager.getNumberOfLists();
		    if (nLists == 0)
		        resultsList.add("There are no lists in the system at this moment."); 
		    else {
		        resultsList.add("Names of the existing lists are: "); 
		        for (int i=0; i<nLists; i++)
		          resultsList.add("\t"+listsManager.getName(i));         
		        }
		      return resultsList; 
		   } 
	}
	
	private class AppendProcessor implements CommandActionHandler { 
		   public ArrayList<String> execute(Command c) {  

		      resultsList = new ArrayList<String>(); 

		      FixedLengthCommand fc = (FixedLengthCommand) c;

		      // the following needs to be adapted to named lists and the 
		      // usage of the ListsManagerObject ......

		      String name = fc.getOperand(1); 
		      int listIndex = listsManager.getListIndex(name); 
		      if (listIndex == -1)
		         resultsList.add("No such list: " + name); 
		      else {
		       int value = Integer.parseInt(fc.getOperand(2)); 
		         listsManager.addElement(listIndex, value);
		      }
		      return resultsList; 
		   } 
	}
	
	private class AddProcessor implements CommandActionHandler { 
		   public ArrayList<String> execute(Command c) {  

		      resultsList = new ArrayList<String>(); 

		      FixedLengthCommand fc = (FixedLengthCommand) c;

		      // the following needs to be adapted to named lists and the 
		      // usage of the ListsManagerObject ......

		      String name = fc.getOperand(1); 
		      int listIndex = listsManager.getListIndex(name); 
		      if (listIndex == -1)
		         resultsList.add("No such list: " + name); 
		      else {
		       int index = Integer.parseInt(fc.getOperand(2));
		       if(index > listsManager.getSize(listIndex) || index < 0)
		    	   resultsList.add("Index out of bounds.");
		       else{
		       int value = Integer.parseInt(fc.getOperand(3)); 
		       		listsManager.addElement(listIndex, index, value);}
		      }
		      return resultsList; 
		   } 
	}
	
	// classes added for the lab exercise about this project. 
	private class ShowAllProcessor implements CommandActionHandler { 
	   public ArrayList<String> execute(Command c) {  
	            
	     // command has no operand - nothing is needed from the
	     // command. if it comes here, it is the showall command....
	     resultsList = new ArrayList<String>(); 

	     // Show each element in the list in a different line, followin
	     // the specified format: index   --- value
	     // put some heading too....

	     FixedLengthCommand fc = (FixedLengthCommand) c;
	        
	     String name = fc.getOperand(1); 
	     int listIndex = listsManager.getListIndex(name); 
	     if (listIndex == -1)
	      resultsList.add("No such list: " + name); 
	     else {
	      int lSize = listsManager.getSize(listIndex);
	      if (lSize == 0)
	          resultsList.add("List is currently empty."); 
	      else {
	        resultsList.add("Values in the list are: "); 
	        for (int i=0; i<lSize; i++) 
	            resultsList.add("\tlist[" + i + "] --- " +   
	                    listsManager.getElement(listIndex, i));         
	        }
	     }
	     return resultsList; 
	   } 
	}
	
	private class ShowSize implements CommandActionHandler { 
		   public ArrayList<String> execute(Command c) {  
		            
		     // command has no operand - nothing is needed from the
		     // command. if it comes here, it is the showall command....
		     resultsList = new ArrayList<String>(); 

		     // Show each element in the list in a different line, followin
		     // the specified format: index   --- value
		     // put some heading too....

		     FixedLengthCommand fc = (FixedLengthCommand) c;
		        
		     String name = fc.getOperand(1); 
		     int listIndex = listsManager.getListIndex(name); 
		     if (listIndex == -1)
		      resultsList.add("No such list: " + name); 
		     else {
		      int lSize = listsManager.getSize(listIndex);
		      if (lSize == 0)
		          resultsList.add("List is currently empty."); 
		      else {
		        resultsList.add("The size of the list is: " + lSize);          
		        }
		     }
		     return resultsList; 
		   } 
		}

}		





