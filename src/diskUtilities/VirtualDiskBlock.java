package diskUtilities;

/**
 * Class to create, and get information from a block unit. 
 * Block units are where a specific disk unit's information is stored.
 * 
 * @author rex.reyes
 *
 */

public class VirtualDiskBlock {
	private static final int DEFCAP = 256; // Default number of bytes per block.
	private static final int DEF_BLOCKSIZE = 32; // Minimum possible size of a block.
	private byte[] blockUnit; // Bytes array for the virtual read and write operations.
	
	/**
	 * Constructor that uses the DEFCAP on the next constructor, which has a capacity parameter.
	 */
	public VirtualDiskBlock(){ 
		this(DEFCAP);
	}
	
	/**
	 * Constructor to initiate the blockUnit array.
	 * @param blockCapacity is the number used for the initiation.
	 */
	public VirtualDiskBlock(int blockCapacity) {
		if(blockCapacity < 8)
			blockUnit = new byte[DEF_BLOCKSIZE];
		
		else blockUnit = new byte[blockCapacity];
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

}
