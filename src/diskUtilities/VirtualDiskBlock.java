package diskUtilities;

import java.nio.ByteBuffer;

/**
 * Class to create, and get information from a block unit. 
 * Block units are where a specific disk unit's information is stored.
 * 
 * @author rex.reyes
 *
 */

public class VirtualDiskBlock {
	private static final int DEFCAP = 256; // Default number of bytes per block.
	private static final int MIN_BLOCK_SIZE = 32; // Minimum possible size of a block.
	private byte[] blockUnit; // Bytes array for the virtual read and write operations.
	
	/**
	 * No parameter constructor that uses the DEFCAP on the next constructor, which has a capacity parameter.
	 */
	public VirtualDiskBlock(){ 
		this(DEFCAP);
	}
	
	/**
	 * Constructor to initiate the blockUnit array.
	 * @param blockCapacity is the number used for the initiation.
	 */
	public VirtualDiskBlock(int blockCapacity) {
		if(blockCapacity < 32)
			blockUnit = new byte[MIN_BLOCK_SIZE];
		
		else blockUnit = new byte[blockCapacity];
	}
	
	/**
	 * Creates a new VirtualDiskBlock with the capacity given, and the next block given.
	 * @param blockCapacity the capacity of the block
	 * @param nextBlock the number of the next block
	 */
	public VirtualDiskBlock(int blockCapacity , int nextBlock){
		blockUnit = new byte[blockCapacity];
		Utils.copyNextBNToBlock(this, nextBlock);
	}

	/**
	 * Method to get the the blockUnit's capacity value.
	 * @return blockUnit.length which represents the array's capacity.
	 */
	public int getCapacity() {
		return blockUnit.length;
	}

	/**
	 * Gets an array's value at an specific slot.
	 * @param i represents the index from which the element will be picked.
	 * @return blockUnit's element at index i.
	 */
	public byte getElement(int i) {
		return blockUnit[i];
	}

	/**
	 * Sets an indicated element at the specified slot, replacing the old element.
	 * @param i represents the index in which the parameter b will be replacing the old element.
	 * @param b is the element that will replace the old one at the slot/index specified by i.
	 */
	public void setElement(int i, byte b) {
		blockUnit[i] = b;
	}
	
	/**
	 * Returns the contents of this VirtualDiskBlock as an array of bytes.
	 * @return the contents of the disk block
	 */
	public byte[] getAll(){
		return blockUnit;
	}
	
	/**
	 * Returns the block number of the next block.
	 * @return the block number of the next block
	 */
	public int getNextBlockNumber(){
		byte[] nbn = {blockUnit[blockUnit.length-4], blockUnit[blockUnit.length-3], blockUnit[blockUnit.length-2], blockUnit[blockUnit.length-1]};
		return ByteBuffer.wrap(nbn).getInt();
	}

}
