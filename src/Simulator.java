import java.util.HashMap;
import java.util.Hashtable;

public class Simulator {
	static String rs, rt;
	static HashMap<String, String> registerFile;
	String[] IFID, IDEX, EXMEM, MEMWB; // pipeline registers as array of strings
	HashMap<Integer, String> memory;

	public Simulator() {
		// the key for the registerFile is the register number in binary
		registerFile = new HashMap<String, String>();
		memory = new HashMap<Integer, String>();
	}

	public static Hashtable<String, String> decoder(String binary) {
		Hashtable<String, String> toExecute = null;
		toExecute.put("Operation Code", binary.substring(0, 5));
		if (binary.startsWith("000000"))
		// if R-format, sends back all the data .
		{
			// format ="R";
			// shft = binary.substring(21,25);
			// funct = binary.substring(26);

			rs = binary.substring(6, 10);
			rt = binary.substring(16, 20);
			rs = registerFile.get(rs);
			rt = registerFile.get(rt);
			toExecute.put("First Source", rs);
			toExecute.put("Destination Register", binary.substring(11, 15));
			toExecute.put("Second Source", rt);
			toExecute.put("Shift Amount", binary.substring(21, 25));
			toExecute.put("Function", binary.substring(26));
		} else if (binary.startsWith("000010") || binary.startsWith("000011"))
		// if J-format ,sends back the address.
		{
			toExecute.put("Address", binary.substring(6));
		} else
		// if I-format sends back the value of reg,destination ,constant/adress.
		{
			// format ="I";
			// rt = binary.substring(11,15);
			// imm= binary.substring(16);

			rs = binary.substring(6, 10);
			rs = registerFile.get(rs);
			toExecute.put("Source Register", rs);
			toExecute.put("Destination Register", binary.substring(11, 15));
			toExecute.put("Constant-Address", binary.substring(16));
		}
		return toExecute;

	}

	public void memor(HashMap<String,String> binary) {
		HashMap<String,String> toWrite = new HashMap<String,String>();
		String data;
		int address=(int) Integer.parseInt(binary.get("result"),2);
		if(binary.get("memoryRead").equals("1")){
			data=memory.get(address);
			toWrite.put("result", binary.get("result"));
			toWrite.put("memoryToRegister","1");
			toWrite.put("rd", binary.get("rd"));
			toWrite.put("data",data);
			//writeBack
		}else if(binary.get("memoryWrite").equals("1")){
			data=binary.get("data");
			memory.put(address, data);

		}else{
			data="00000000000000000000000000000000";
			toWrite.put("result", binary.get("result"));
			toWrite.put("memoryToRegister","0");
			toWrite.put("data",data);
			toWrite.put("rd", binary.get("rd"));
			//writeBack
		}
	}
}
