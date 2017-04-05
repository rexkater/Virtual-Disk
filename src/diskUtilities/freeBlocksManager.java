package diskUtilities;

import exceptions.FullDiskException;

public class freeBlocksManager {

	private int firstFLB;
	private int flIndex;
	public final static int FBROOT_OFFSET = 8;
	public final static int FBINDEX_OFFSET = 12;

	public int getFreeBN() throws FullDiskException { 
	   int bn = 0; 
	   
	   if (firstFLB == 0) 
	      throw new FullDiskException("The disk is full.");
	   
	   // disk has space 
	   
	   if (flIndex != 0) {
		   // bn = first free block[flIndex]
		   flIndex--;
	   }
	   
	   //flIndex==0 //condition
	   else{
		   bn = firstFLB;
		   //FFB == FFB[0]
		   //flIndex = n-1
	   }
			   
	   return bn;
	}
	
	// Whenever a block is freed, it is put in the free block structure. The idea of an algorithm for that is as follows. 
	
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
