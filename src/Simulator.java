import java.util.HashMap;


public class Simulator {
	HashMap<String, String> registerFile;
	String[] IFID , IDEX , EXMEM , MEMWB ; //pipeline registers as array of strings
	
	public Simulator(){
		// the key for the registerFile is the register number in binary
		registerFile = new HashMap<String, String>();
	}

}
