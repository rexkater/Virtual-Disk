package diskUtilities;

import exceptions.FullDiskException;

public class freeBlocksManager {

	private int firstFLB;
	private int flIndex;

	public int getFreeBN() throws FullDiskException { 
	   int bn = 0; 
	   
	   if (firstFLB == 0) 
	      throw new FullDiskException("The disk is full.");
	   
	   // disk has space 
	   
	   if (flIndex != 0) flIndex--;
	   else bn = firstFLB;
			   
	   return bn;
	}
	
	//Whenever a block is freed, it is put in the free block structure. The idea of an algorithm for that is as follows. 
	
	public void registerFB(int bn) { 
		
	   if (firstFLB == 0)  {
	      firstFLB = bn; 
	      flIndex = 0; 
	   }
	   
	   else if (flIndex == bn-1) {
	      flIndex = 0; 
	      firstFLB = bn; 
	   }
	   
	   else flIndex++;  
	}   
}
