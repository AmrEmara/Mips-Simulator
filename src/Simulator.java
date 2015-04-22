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


	public static void decoder(String binary) {
		String rs, rt, rd;
		Hashtable<String, String> out = null;
		out.put("op", binary.substring(0, 5));
		if (binary.startsWith("000000"))
		// if R-format, sends back all the data .
		{
			//set control signals.
			out.put("RegDst","1");
			out.put("ALUSrc","0");
			out.put("MemtoReg","0");
			out.put("RegWrite","1");
			out.put("MemRead","0");
			out.put("MemWrite","0");
			out.put("Branch","0");
			out.put("ALUOp","10");
			// read the values in rs and rt and place them in registers
			rs = binary.substring(6, 10);
			rt = binary.substring(16, 20);
			rs = registerFile.get(rs);
			rt = registerFile.get(rt);
			out.put("firstSource", rs);
			out.put("destinationRegister", binary.substring(11, 15));
			out.put("secondSource", rt);
			out.put("shamt", binary.substring(21, 25));
			out.put("funct", binary.substring(26));
		} else if (binary.startsWith("000010") || binary.startsWith("000011"))
		// if J-format ,sends back the address.
		{

			out.put("address", binary.substring(6));
		} else
		// if I-format sends back the value of reg,destination ,constant/adress.
		{
			// format ="I";
			// rt = binary.substring(11,15);
			// imm= binary.substring(16);

			rs = binary.substring(6, 10);
			rs = registerFile.get(rs);
			out.put("sourceRegister", rs);
			out.put("destinationRegister", binary.substring(11, 15));
			out.put("address", binary.substring(16));
			if (binary.startsWith("001000")){ 
				// if it's a addi ,set control signals.
				out.put("RegDst","0");
				out.put("ALUSrc","1");
				out.put("MemtoReg","0");
				out.put("RegWrite","1");
				out.put("MemRead","0");
				out.put("MemWrite","0");
				out.put("Branch","0");
				out.put("ALUOp","00");
			}
			
			else if (binary.startsWith("100011")){
				// if it's load word,set control signals.
				out.put("RegDst","0");
				out.put("ALUSrc","1");
				out.put("MemtoReg","1");
				out.put("RegWrite","1");
				out.put("MemRead","1");
				out.put("MemWrite","0");
				out.put("Branch","0");
				out.put("ALUOp","00");
			}
			else if (binary.startsWith("100011")){
				// if it's save word,set control signals.
				out.put("RegDst","X");
				out.put("ALUSrc","1");
				out.put("MemtoReg","X");
				out.put("RegWrite","0");
				out.put("MemRead","0");
				out.put("MemWrite","1");
				out.put("Branch","0");
				out.put("ALUOp","00");
			}
			else if (binary.startsWith("100011")){
				// if it's Branch on equal,set control signals.
				out.put("RegDst","X");
				out.put("ALUSrc","0");
				out.put("MemtoReg","X");
				out.put("RegWrite","0");
				out.put("MemRead","0");
				out.put("MemWrite","0");
				out.put("Branch","1");
				out.put("ALUOp","00");
			}
		}
		//pass out to execute;

	}
	
	public static String fetch(HashMap<Integer,String>  memory,int pc) {
		int tempPc = pc;
		String binary = memory.get(pc); // fetch the instruction from
												// memory
		String address;// to save the address part of the instruction
		if (binary.startsWith("0001 00")) { // check if beq
			address = binary.substring(16); // get the address part of the
											// instruction
			tempPc = Integer.parseInt(address, 2);// get the decimal value of
													// the address and store it
													// in tempPc
		} else {
			if (binary.startsWith("0001 01")) { // check if bne
				address = binary.substring(16); // get the address part of the
												// instruction
				tempPc = Integer.parseInt(address, 2);// get the decimal value
														// of the address and
														// store it in tempPc
			} else {
				if (binary.startsWith("0000 10")) { // check if j
					address = binary.substring(6); // get the address part of
													// the instruction
					tempPc = Integer.parseInt(address, 2);// get the decimal
															// value of the
															// address and store
															// it in tempPc
				} else {
					if (binary.startsWith("0000 11")) { // check if jal
						address = binary.substring(6); // get the address part
														// of the instruction
						registerFile.put("00000000000000000000000000011111",
								Integer.toBinaryString(tempPc));// save tempPc
																// value in ra
																// register
						tempPc = Integer.parseInt(address, 2);// get the decimal
																// value of the
																// address and
																// store it in
																// tempPc
					} else {
						if (binary.startsWith("0000 00")
								&& binary.substring(11).equals(
										"0 0000 0000 0000 0000 1000")) { // check
																			// if
																			// jr
							tempPc = Integer.parseInt(
									registerFile.get("11111"), 2);// load the
																	// value of
																	// ra
																	// register
																	// in tempPc
						} else {
							tempPc = tempPc + 4;
						}
					}
				}
			}
		}
		pc = tempPc;
		return binary; // return the instruction
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
