package VirtualDisk;

public class Utils {
	
	public static boolean powerOf2(int n){
		if((n > 0) && ((n & (n - 1)) == 0))
			return true;
		return false;
	}

}
