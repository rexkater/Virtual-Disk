package diskUtilities;

import diskUtilities.VirtualDiskBlock;
import exceptions.InvalidBlockException;
import exceptions.InvalidBlockNumberException;

/**
 * Class to organize a diversity of utilities to be used on the project.
 * 
 * @author rex.reyes
 *
 */

public class Utils {
	
	private static final int INTSIZE = 4; 
	public static final double PERCENTAGE_OF_INODE_BLOCKS = 0.01;
	
	/**
	 * Method to find the power of 2 of an specific number.
	 * @param n is the number from which the power of 2 will be calculated.
	 * @returns n to the power of 2.
	 */
	
	public static boolean powerOf2(int n){
		if((n > 0) && ((n & (n - 1)) == 0))
			return true;
		return false;
	}
    
    /**
     * Copy an integer value into four consecutive bytes in a block.
     * @param vdb The block where the integer value is copied.
     * @param index The index of the first byte in the block that
     * shall be written. The number is stored in four bytes whose 
     * indexes are: index, index+1, index+2, and index+3, starting
     * from the less significant byte in the number up to the most
     * significant. 
     * @param value The integer value to be written in block. 
     */
	
    public static void copyIntToBlock(VirtualDiskBlock vdb, int index, int value) { 
        for (int i = INTSIZE-1; i >= 0; i--) { 
            vdb.setElement(index+i, (byte) (value & 0x000000ff));     
            value = value >> 8; 
        }
    }
    
    /**
     * Extracts an integer value from four consecutive bytes in a block. 
     * @param vdb The block. 
     * @param index The index in block of the less significant byte of the  
     * number to extract. 
     * @return The value extracted from bytes index+3, index+2, index+1, and index. 
     * From most significant to less significant bytes of the number's four bytes. 
     */
    
    public static int getIntFromBlock(VirtualDiskBlock vdb, int index) {  
        int value = 0; 
        int lSB; 
        for (int i=0; i < INTSIZE; i++) { 
            value = value << 8; 
            lSB = 0x000000ff & vdb.getElement(index + i);
            value = value | lSB; 
        }
        return value; 
    }

    /**
     * Copy an integer value into four consecutive bytes in an array of bytes.
     * @param b The array where the integer value is copied.
     * @param index The index of the first byte in the b that
     * shall be written. The number is stored in four bytes whose 
     * indexes are: index, index+1, index+2, and index+3, starting
     * from the less significant byte in the number up to the most
     * significant. 
     * @param value The integer value to be written in the array. 
     */
    
    public static void copyIntToBytesArray(byte[] b, int index, int value) { 
        for (int i = INTSIZE-1; i >= 0; i--) { 
            b[index+i] = (byte) (value & 0x000000ff);     
            value = value >> 8; 
        }
    }
    
    /**
     * Extracts an integer value from four consecutive bytes in a byte[] array. 
     * @param b The array. 
     * @param index The index in block of the less significant byte of the  
     * number to extract. 
     * @return The value extracted from bytes index+3, index+2, index+1, and index. 
     * From most significant to less significant bytes of the number's four bytes. 
     */
    
    public static int getIntFromBytesArray(byte[] b, int index) {  
        int value = 0; 
        int lSB; 
        for (int i=0; i < INTSIZE; i++) { 
            value = value << 8; 
            lSB = 0x000000ff & b[index + i];
            value = value | lSB; 
        }
        return value; 
    }
    
    /**
	 * Copies a Character to a VirtualDiskBlock.
	 * @param vdb the block to copy the Character to
	 * @param index the index to insert the Character at
	 * @param c the Character to insert
	 */
    
	public static void copyCharToBlock(VirtualDiskBlock vdb, int index, char c) { 
		vdb.setElement(index, (byte) c); 
	}	
	
	/**
	 * Returns a Character from the given VirtualDiskBlock.
	 * @param vdb the block that contains the Character
	 * @param index the index at which the Character is located
	 * @return the Character in the given index of the block
	 */
	
	public static char getCharFromBlock(VirtualDiskBlock vdb, int index) { 
		return (char) vdb.getElement(index); 
	}
	
	/**
	 * Copies a Character to a Byte array.
	 * @param b the byte array to copy it to
	 * @param index the index of the array
	 * @param c the Character to insert
	 */
	
	public static void copyCharToBytesArray(byte[] b, int index, char c) { 
		b[index] = (byte) c; 
	}
	
	/**
	 * Returns a Character from a Byte array.
	 * @param b the byte array
	 * @param index the index of the array
	 * @return the Character in the given index of the given array
	 */
	
	public static char getCharFromBytesArray(byte[] b, int index) { 
		return (char) b[index]; 
	}	
	
	/**
	 * Shows a file starting at block 1.
	 * @param d the DiskUnit to read from
	 * @throws InvalidBlockException 
	 * @throws InvalidBlockNumberException 
	 */
	
	public static void showFileInDiskContent(DiskUnit d) throws InvalidBlockNumberException, InvalidBlockException { 
		VirtualDiskBlock vdb = new VirtualDiskBlock(d.getBlockSize()); 
		
		System.out.println("\nContent of the file begining at block 1"); 
		int bn = 1; 
		while (bn != 0) { 
			d.read(bn, vdb); 
			showVirtualDiskBlock(bn, vdb);
			bn = getNextBNFromBlock(vdb);			
		}
		
	}

	
	/**
	 * Shows the contents of the DiskUnit.
	 * @param d the DiskUnit to read from
	 * @throws InvalidBlockException 
	 * @throws InvalidBlockNumberException 
	 */
	
	public static void showDiskContent(DiskUnit d) throws InvalidBlockNumberException, InvalidBlockException { 
		
		System.out.println("Capacity of disk is: " + d.getCapacity()); 
		System.out.println("Size of blocks in the disk is: " + d.getBlockSize()); 
		
		VirtualDiskBlock block = new VirtualDiskBlock(d.getBlockSize()); 
		for (int b = 0; b < d.getCapacity(); b++) { 
			d.read(b, block); 
			showVirtualDiskBlock(b, block); 
		}
		
	}

	/**
	 * Shows the contents (characters) of a block.
	 * @param b the number of the block
	 * @param block the VirtualDiskBlock
	 */
	
	public static void showVirtualDiskBlock(int b, VirtualDiskBlock block) {
	    System.out.print(" Block "+ b + "\t"); 
	    for (int i=0; i<block.getCapacity(); i++) {
	    	char c = (char) block.getElement(i); 
	    	if (Character.isLetterOrDigit(c))
	    		System.out.print(c); 
	    	else
	    		System.out.print('-'); 
	    }
	    System.out.println(); 
	}

	
	/**
	 * Copies the Integer f=given to the last four bytes of the VirtualDiskBlock.
	 * @param vdb the VirtualDiskBlock
	 * @param value the integer to copy
	 */
	
	public static void copyNextBNToBlock(VirtualDiskBlock vdb, int value) { 
		int lastPos = vdb.getCapacity()-1;

		for (int index = 0; index < 4; index++) { 
			vdb.setElement(lastPos - index, (byte) (value & 0x000000ff)); 	
			value = value >> 8; 
		}

	}
	
	/**
	 * Gets the next block number from a given block.
	 * @param vdb the VirtualDiskBlock to read from
	 * @return the next block number
	 */
	
	public static int getNextBNFromBlock(VirtualDiskBlock vdb) { 
		int bsize = vdb.getCapacity(); 
		int value = 0; 
		int lSB; 
		for (int index = 3; index >= 0; index--) { 
			value = value << 8; 
			lSB = 0x000000ff & vdb.getElement(bsize-1-index);
			value = value | lSB; 
		}
		return value; 

	}
	
	/**
	 * Returns the number of INode blocks.
	 * @param dUnit the DiskUnit to check
	 * @return the number of INode blocks from the given DiskUnit
	 */
	
	public static int getNumberOfINodeBlocks(DiskUnit dUnit){
		return (int)(Math.max(1, Math.ceil(((PERCENTAGE_OF_INODE_BLOCKS * dUnit.getCapacity()) * iNodeManager.INODESIZE) / dUnit.getBlockSize())));
	}
	
	/**
	 * Returns the number of INodes from the given DiskUnit.
	 * @param dUnit the DiskUnit to check
	 * @return the number of INodes in the given DiskUnit
	 */
	
	public static int getNumberOfINodes(DiskUnit dUnit){
		return (int)(Math.max(1, (PERCENTAGE_OF_INODE_BLOCKS * dUnit.getCapacity())));
	}
	
	/**
	 * Returns the number of blocks needed for the Free Blocks array.
	 * @param dUnit the DiskUnit to read from
	 * @return the number of blocks for the free blocks array
	 */
	
	public static int getNumberOfBlocksForFBArray(DiskUnit dUnit){
		double numberOfBytesForFBArray = (dUnit.getCapacity()-1-getNumberOfINodeBlocks(dUnit))*4;
		return (int)(Math.max(1, Math.ceil((numberOfBytesForFBArray/(dUnit.getBlockSize())))));
	}

}
