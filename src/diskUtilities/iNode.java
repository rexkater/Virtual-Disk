package diskUtilities;

public class iNode {
	private byte type; // 0 if data file, 1 if directory (1 byte).
	private int size; // The number of bytes the file has (4 bytes).
	private int firstBlock; // Index of the disk block that corresponds to the first block of the file (4 bytes).
	
	public iNode(byte type, int size, int firstBlock){
		this.type = type;
		this.size = size;
		this.firstBlock = firstBlock;
	}
	
	public byte getType(){
		return type;
	}
	
	public int getSize(){
		return size;
	}
	
	public int getFirstBlock(){
		return firstBlock;
	}
	
	public byte setType(byte t){
		byte temp = type;
		type = t;
		return temp;
	}
	
	public int setFirstBlock(int f){
		int temp = firstBlock;
		firstBlock = f;
		return temp;
	}
	
}
