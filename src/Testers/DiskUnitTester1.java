package Testers;
import Exceptions.InvalidBlockException;
import Exceptions.InvalidBlockNumberException;
import Exceptions.NonExistingDiskException;
import VirtualDisk.DiskUnit;
import VirtualDisk.VirtualDiskBlock;

public class DiskUnitTester1 {

	/**
	 * @param args
	 * @throws NonExistingDiskException 
	 * @throws InvalidBlockException 
	 * @throws InvalidBlockNumberException 
	 */
	public static void main(String[] args) throws NonExistingDiskException, InvalidBlockNumberException, InvalidBlockException {
		DiskUnit d = DiskUnit.mount("disk1"); // edit the name of the disk to mount
	    showDiskContent(d); 
		showFileInDiskContent(d);   
		d.shutdown(); 
	}

	private static void showFileInDiskContent(DiskUnit d) throws InvalidBlockNumberException, InvalidBlockException { 
		VirtualDiskBlock vdb = new VirtualDiskBlock(d.getBlockSize()); 
		
		System.out.println("\nContent of the file begining at block 1"); 
		int bn = 1; 
		while (bn != 0) { 
			d.read(bn, vdb); 
			showVirtualDiskBlock(bn, vdb);
			bn = getNextBNFromBlock(vdb);			
		}
	}

	private static void showDiskContent(DiskUnit d) throws InvalidBlockNumberException, InvalidBlockException { 
		System.out.println("Capacity of disk is: " + d.getCapacity()); 
		System.out.println("Size of blocks in the disk is: " + d.getBlockSize()); 
		
		VirtualDiskBlock block = new VirtualDiskBlock(d.getBlockSize()); 
		for (int b = 0; b < d.getCapacity(); b++) { 
			d.read(b, block); 
			showVirtualDiskBlock(b, block); 
		}
	}

	private static void showVirtualDiskBlock(int b, VirtualDiskBlock block) {
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
	
	public static void copyNextBNToBlock(VirtualDiskBlock vdb, int value) { 
		int lastPos = vdb.getCapacity()-1;

		for (int index = 0; index < 4; index++) { 
			vdb.setElement(lastPos - index, (byte) (value & 0x000000ff)); 	
			value = value >> 8; 
		}
	}
	
	private static int getNextBNFromBlock(VirtualDiskBlock vdb) { 
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
}
