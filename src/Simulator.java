import java.util.HashMap;
import java.util.Hashtable;

public class Simulator {
	static String rs, rt,pc;
	static HashMap<String, String> registerFile;
	String[] IFID, IDEX, EXMEM, MEMWB; // pipeline registers as array of strings

	public Simulator() {
		// the key for the registerFile is the register number in binary
		registerFile = new HashMap<String, String>();
	}

	public static Hashtable<String,String>  decoder(String binary) {
		Hashtable<String,String> toExecute = null;
		toExecute.put("Operation Code", binary.substring(0, 5));
		if (binary.startsWith("000000"))
			//if R-format, sends back all the data .
		{
			// format ="R";
			//shft = binary.substring(21,25);
			// funct = binary.substring(26);
			
			rs = binary.substring(6,10);
			rt = binary.substring(11,15);
			rs= registerFile.get(rs); 
			rt= registerFile.get(rt);	
			toExecute.put("First Source", rs);
			toExecute.put("Destination Register", binary.substring(16,20));
			toExecute.put("Second Source", rt);
			toExecute.put("Shift Amount", binary.substring(21,25));
			toExecute.put("Function", binary.substring(26));	
		}
		else if (binary.startsWith("000010") || binary.startsWith("000011"))
		// if J-format ,sends back the address.
		{
			toExecute.put("Address",binary.substring(6));
		}
		else 
		// if I-format sends back the value of reg,destination ,constant/adress.
		{
			//format ="I";
			//rt = binary.substring(11,15);
			//imm= binary.substring(16);
			
			
			rs = binary.substring(6,10);
			rs= registerFile.get(rs); 
			toExecute.put("Source Register", rs);
			toExecute.put("Destination Register", binary.substring(11,15));
			toExecute.put("Constant-Address", binary.substring(16));
		}
		return toExecute;
			
	}
	
	public static String fetch(String programCounter){
		int tempPc=Integer.parseInt(programCounter, 2);//convert programcounter from binary string to decimal
		String binary = registerFile.get(""); //fetch the instruction from memory
		String address;//to save the address part of the instruction
		if(binary.startsWith("0001 00")){ // check if beq
			address=binary.substring(16); //get the address part of the instruction
			tempPc=Integer.parseInt(address, 2);//get the decimal value of the address and store it in tempPc
		}
		else{
			if(binary.startsWith("0001 01")){ // check if bne
				address=binary.substring(16); //get the address part of the instruction
				tempPc=Integer.parseInt(address, 2);//get the decimal value of the address and store it in tempPc
			}
			else{
				if(binary.startsWith("0000 10")){ // check if j
					address=binary.substring(6); //get the address part of the instruction
					tempPc=Integer.parseInt(address, 2);//get the decimal value of the address and store it in tempPc
				}
				else{
					if(binary.startsWith("0000 11")){ // check if jal
						address=binary.substring(6); //get the address part of the instruction
						registerFile.put("11111", Integer.toBinaryString(tempPc));//save tempPc value in ra register
						tempPc=Integer.parseInt(address, 2);//get the decimal value of the address and store it in tempPc
					}
					else{
						if(binary.startsWith("0000 00") && binary.substring(11).equals("0 0000 0000 0000 0000 1000")){ // check if jr
							tempPc=Integer.parseInt(registerFile.get("11111"), 2);//load the value of ra register in tempPc
						}
						else{
							tempPc=tempPc+4;
						}
					}
				}
			}
		}
		pc =Integer.toBinaryString(tempPc);
		return binary; //return the instruction
	}
}
