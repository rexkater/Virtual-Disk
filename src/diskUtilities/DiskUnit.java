package diskUtilities;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import diskUtilities.Utils;
import exceptions.ExistingDiskException;
import exceptions.InvalidBlockException;
import exceptions.InvalidBlockNumberException;
import exceptions.InvalidParameterException;
import exceptions.NonExistingDiskException;

/**
 * 
 * Class to create, manipulate and manage disk units. 
 * This is practically the essential part of the project,
 * in which most of the important operations are executed
 * related to the functionality of the disk units.
 * 
 * @author rex.reyes
 *
 */

public class DiskUnit {
	
	private static final int DEFAULT_CAPACITY = 1024;  // Default number of blocks.    
	private static final int DEFAULT_BLOCK_SIZE = 256; // Default number of bytes per block.
	private int capacity; // Number of blocks of current disk instance.
	private int blockSize; // Size of each block of current disk instance.
	public static byte[] bytes;

	private RandomAccessFile disk; // File representing the simulated  disk, where all the disk blocks are stored.

	/** Private constructor to initiate the 
	 * RAF representing the virtual disk.
     * @param name is the name of the disk.
	 */
	
	private DiskUnit(String name) {
	     try {
	         disk = new RandomAccessFile(name, "rw");
	     } catch (IOException e) {
	         System.err.println ("Unable to start the disk");
	         System.exit(1);
	       }
	}
	
	/**
	 * Turns on an existing disk unit whose name is given. If successful, it makes
	 * the particular disk unit available for operations suitable for a disk unit.
	 * @param name is the name of the disk unit to activate.
	 * @return the corresponding DiskUnit object.
	 * @throws NonExistingDiskException whenever no
	 *    ¨disk¨ with the specified name is found.
	 */
	
	public static DiskUnit mount(String name) throws NonExistingDiskException {
		File file = new File(name);
		
		if (!file.exists())
			throw new NonExistingDiskException("No disk has name : " + name);
  
		DiskUnit dUnit = new DiskUnit(name);
       
	   // get the capacity and the block size of the disk from the file
	   // representing the disk
		
	   try {
	       dUnit.disk.seek(0);
	       dUnit.capacity = dUnit.disk.readInt();
	       dUnit.blockSize = dUnit.disk.readInt();
	   } catch (IOException e) {
	       e.printStackTrace();
	     }
	       
	   return dUnit;         
	}
   
	/***
	 * Creates a new disk unit with the given name. The disk is formatted
	 * as having default capacity (number of blocks), each of default
	 * size (number of bytes). Those values are: DEFAULT_CAPACITY and
	 * DEFAULT_BLOCK_SIZE. The created disk is left as in off mode.
	 * @param name is the name of the file that is to represent the disk.
	 * @throws ExistingDiskException whenever the name attempted is
	 * already in use.
	 * @throws InvalidParameterException for parameters of any type but string.
	 */
	public static void createDiskUnit(String name) throws ExistingDiskException, InvalidParameterException {
		createDiskUnit(name, DEFAULT_CAPACITY, DEFAULT_BLOCK_SIZE);
	}
	   
	/**
	 * Creates a new disk unit with the given name. The disk is formatted
	 * as with the specified capacity (number of blocks), each of specified
	 * size (number of bytes).  The created disk is left as in off mode.
	 * @param name the name of the file that is to represent the disk.
	 * @param capacity is the number of blocks in the new disk.
	 * @param blockSize represents the size per block in the new disk.
	 * @throws ExistingDiskException whenever the name attempted is
	 * already in use.
	 * @throws InvalidParameterException whenever the values for capacity
	 *  or blockSize are not valid according to the specifications.
	 */
	
	public static void createDiskUnit(String name, int capacity, int blockSize) throws ExistingDiskException, InvalidParameterException{
	    File file = new File(name);
	    
	    if (file.exists())
	       throw new ExistingDiskException("Disk name is already used: " + name);
	       
	    RandomAccessFile disk = null;
	    if (capacity < 0 || blockSize < 0 || !Utils.powerOf2(capacity) || !Utils.powerOf2(blockSize))
	       throw new InvalidParameterException("Invalid values: " + " capacity = " + capacity + " block size = " + blockSize);
	   
	    // disk parameters are valid... hence create the file to represent the disk unit.
	    
	    try {
	        disk = new RandomAccessFile(name, "rw");
	    } catch (IOException e) {
	        System.err.println ("Unable to start the disk");
	        System.exit(1);
	      }
	
	    reserveDiskSpace(disk, capacity, blockSize);
	    
	       
	    // after creation, just leave it in shutdown mode - just close the corresponding file
	    
	    try {
	        disk.close();
	    } catch (IOException e) {
	        e.printStackTrace();
	      }
	}
	
	/**
	 * Reserves a certain amount of space in the disk for the capacity and blockSize.
	 * This space CAN'T be worked with or used while writing or reading on a certain disk.
	 * It reserves the whole first block, even though only the first 8 bits are needed.
	 * @param disk represents the specific disk in which the space will be reserved.
	 * @param capacity first value to be stored on the reserved block of the disk.
	 * @param blockSize second value to be stored on the reserved block of the disk,
	 * right after the capacity value.
	 */
	
	private static void reserveDiskSpace(RandomAccessFile disk, int capacity, int blockSize) {
	
		try {
			disk.setLength(blockSize * capacity);
		} catch (IOException e) {
			e.printStackTrace();
		  }
	
		// write disk parameters (number of blocks, bytes per block) in
		// block 0 of disk space
		
		try {
		   disk.seek(0);
		   Utils.copyIntToBytesArray(bytes, 0, capacity);
		   Utils.copyIntToBytesArray(bytes, 4, blockSize);
		   Utils.copyIntToBytesArray(bytes, 8, 0); // first free block
		   Utils.copyIntToBytesArray(bytes, 12, 0); // index free block 
		   Utils.copyIntToBytesArray(bytes, 16, 0); // first free iNode 
		   Utils.copyIntToBytesArray(bytes, 20, 0); // number of iNodes
		} catch (IOException e) {
		   e.printStackTrace();
		  }     
	}
	
	/**
	 * Writes the content of block b into the disk block corresponding to blockNum; 
	 * that is, whatever is the actual content of the disk block corresponding to the 
	 * specified block number (blockNum) is changed to (or overwritten by) that of b 
	 * in the current disk instance.
	 * @param blockNum is the number of a specific block of the disk. 
	 * @param b represents the data that will be written on the disk.
	 * @throws InvalidBlockNumberException for a blockNum of zero (block 0 can't be written)
	 * or lower, or a blockNum higher than the capacity of the disk.
	 * @throws InvalidBlockException when the given VirtualDiskBlock parameter is null or if 
	 * block instance does not match the block size of the current disk instance.
	 */
	
	public void write(int blockNum, VirtualDiskBlock b) throws InvalidBlockNumberException, InvalidBlockException {		
		if(blockNum <= 0 || blockNum > capacity-1)
			throw new InvalidBlockNumberException();
		
		if(b==null || b.getCapacity() != blockSize)
			throw new InvalidBlockException();
		
		try { 
			disk.seek(blockNum*blockSize);
			for(int i = 0; i < blockSize; i++){
				disk.write(b.getElement(i));
			}
		} catch (IOException e) {
			e.printStackTrace();
		  }         
	}
	
	/**
	 * Reads a given block from the disk. The content of the specified disk 
	 * block (identified by its number – blockNum) is copied as the new content 
	 * of the current instance of block being referenced by parameter b.
	 * @param blockNum is the number of a specific block of the disk. 
	 * @param b represents the data that will be read from the disk.
	 * @throws InvalidBlockNumberException for a blockNum lower than zero, 
	 * or equal/higher than the capacity of the disk.
	 * @throws InvalidBlockException when the given VirtualDiskBlock parameter is null or if 
	 * block instance does not match the block size of the current disk instance.
	 */
	
	public void read(int blockNum, VirtualDiskBlock b) throws InvalidBlockNumberException, InvalidBlockException{
		if(blockNum < 0 || blockNum >= capacity)
			throw new InvalidBlockNumberException();
		
		if(b==null || b.getCapacity() != blockSize)
			throw new InvalidBlockException();
	
		try { 
			disk.seek(blockNum*blockSize);
			byte[] block = new byte[blockSize];
			disk.read(block);
			
			for(int i = 0; i < blockSize; i++){
				b.setElement(i, block[i]);
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		  }
	}
	
	/**
	 * Method to get the blockSize value.
	 * @return blockSize instance variable.
	 */

	public int getBlockSize() {
		return blockSize;
	}
	
	/**
	 * Method to get the disk's capacity value.
	 * @return capacity instance variable.
	 */

	public int getCapacity() {
		return capacity;
	}
	
	/**
	 * Formats the disk. This operation visits every physical block, 
	 * except block 0, in the disk and fills with zeroes all those blocks.
	 * @throws IOException 
	 */
	
	public void lowLevelFormat() throws IOException{
    	disk.seek(blockSize);
	    for (int i = blockSize; i < disk.length(); i++) {
	    	disk.writeByte(0);
	    }
	}
	
	/** 
	 * Simulates shutting-off the disk. 
	 * Just closes the corresponding RAF. 
	 */
	
	public void shutdown() {
	    try {
	       disk.close();
	    } catch (IOException e) {
	       e.printStackTrace();
	      }
	  }

}
