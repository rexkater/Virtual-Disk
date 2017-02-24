package VirtualDisk;

public class VirtualDiskBlock {
	private static final int DEFCAP = 256; // default number of bytes per block
	private byte[] blockUnit; // store the bytes array virtually for read and write operations
	
	/**
	 * 
	 */
	public VirtualDiskBlock(){ 
		this(DEFCAP);
	}
	
	/**
	 * 
	 * @param blockCapacity
	 */
	public VirtualDiskBlock(int blockCapacity) {
		blockUnit = new byte[blockCapacity];
	}

	/**
	 * 
	 * @return
	 */
	public int getCapacity() {
		return blockUnit.length;
	}

	/**
	 * 
	 * @param i
	 * @return
	 */
	public byte getElement(int i) {
		return blockUnit[i];
	}

	/**
	 * 
	 * @param i
	 * @param b
	 */
	public void setElement(int i, byte b) {
		blockUnit[i] = b;
	}

}
