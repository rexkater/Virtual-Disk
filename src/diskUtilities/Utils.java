package diskUtilities;

import diskUtilities.VirtualDiskBlock;

/**
 * Class to organize a diversity of utilities to be used on the project.
 * 
 * @author rex.reyes
 *
 */

public class Utils {
	
	private static final int INTSIZE = 4; 
	
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

}
