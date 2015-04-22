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
    public static int binaryToDecimal(String s) {
        if (s.charAt(0) == '1') {
            return twosComp(s);
        }
        
        return Integer.parseInt(s, 2);
    }
    
    /*
     * This method takes an integer and converts to a binary string
     */
    public static String decimalToBinary(int c) {
        return Integer.toBinaryString(c);
    }
    
    public static String binaryAdd(String arg1, String arg2) {
        String res = "";
        if (arg2.length() != arg1.length()) {
            System.out.println("Invalid operands for add operation");
            return null;
        }
        return decimalToBinary(binaryToDecimal(arg1) + binaryToDecimal(arg2));
    }
    
    public static String binarySubtract(String arg1, String arg2) {
        String res = "";
        if (arg2.length() != arg1.length()) {
            System.out.println("Invalid operands for add operation");
            return null;
        }
        return decimalToBinary(binaryToDecimal(arg1) - binaryToDecimal(arg2));
    }
    
    public static String binaryAnd(String a, String b) {
        String result = "";
        if (a.length() != b.length()) {
            System.out.println("Invalid operands for And operation");
            return null;
        }
        for (int i = 0; i < a.length(); i++) {
            char c;
            if (a.charAt(i) == '1' && b.charAt(i) == '1') {
                c = '1';
            } else {
                c = '0';
            }
            result += c;
        }
        return result;
    }
    
    public static String binarySetLessThan(String arg1, String arg2) {
        String result = "";
        int a = binaryToDecimal(arg1);
        int b = binaryToDecimal(arg2);
        if (a > b) {
            return "1";
        }
        return "0";
    }
    
    public static String binaryOr(String a, String b) {
        String result = "";
        if (a.length() != b.length()) {
            System.out.println("Invalid operands for And operation");
            return null;
        }
        for (int i = 0; i < a.length(); i++) {
            char c;
            if (a.charAt(i) == '1' || b.charAt(i) == '1') {
                c = '1';
            } else {
                c = '0';
            }
            result += c;
        }
        return result;
    }
    
    public static String binaryNot(String a) {
        String result = "";
        for (int i = 0; i < a.length(); i++) {
            if (a.charAt(i) == '1') {
                result += "0";
            } else {
                result += "1";
            }
        }
        return result;
    }
    
    public static String signExtendor(String s) {
        if (s.length() == 32) {
            return s;
        }
        char c = s.charAt(0);
        while (s.length() < 32) {
            s = c + s;
        }
        return s;
        
    }
    
    public static String binaryShiftLeft(String s, int shiftAmount) {
        for (int i = 0; i < shiftAmount; i++) {
            s += "0";
            s = s.substring(1, s.length());
        }
        
        return s;
    }
    
    public static String binaryShiftRight(String s, int shiftAmount) {
        for (int i = 0; i < shiftAmount; i++) {
            s = "0" + s;
            s = s.substring(0, s.length() - 1);
        }
        return s;
    }
    
    // when a load control signal is received the opcode is checked and flags
    // added to tell
    // if this is a lw or lb instruction.
    public static HashMap Execute(HashMap in) {
        HashMap<String, String> ExMem = new HashMap<String, String>();
        String out = "";
        ExMem.put("MemtoReg", (String) in.get("MemtoReg"));
        ExMem.put("RegWrite", (String) in.get("RegWrite"));
        ExMem.put("MemRead", (String) in.get("MemRead"));
        ExMem.put("MemWrite", (String) in.get("MemWrite"));
        ExMem.put("RegWrite", (String) in.get("RegWrite"));
        ExMem.put("Branch", (String) in.get("Branch"));
        ExMem.put("lb", "0");
        ExMem.put("lbu", "0");
        ExMem.put("lui", "0");
        ExMem.put("sb", "0");
        
        String controlSignals = "";
        String ALUfunc = "";
        String rFunc = "";
        controlSignals += in.get("RegDst");//
        controlSignals += in.get("ALUSrc");//
        controlSignals += in.get("MemtoReg");
        controlSignals += in.get("RegWrite");
        controlSignals += in.get("MemRead");
        controlSignals += in.get("MemWrite");
        controlSignals += in.get("Branch");
        controlSignals += in.get("ALUOp");//
        
        // what should happen if the control signals indicate a R-type
        // instruction
        if (controlSignals.equals("100100010")) {
            ALUfunc = getFunction((String) in.get("funct"));
            out = operation(ALUfunc, (String) in.get("firstSource"),
                            (String) in.get("secondSource"));
            ExMem.put("result", out);
        } else {
            if (controlSignals.equals("011110000")) {
                String op = (String) in.get("OpCode");
                // adds offset to rs to get address to be loaded
                if (op.equals("100000")) { // instruction
                    ExMem.put("lb", "1");
                    out = binaryAdd((String) in.get("sourceRegister"),
                                    (String) in.get("address"));
                } else {
                    if (op.equals("100100")) {
                        ExMem.put("lbu", "1");
                        out = binaryAdd((String) in.get("sourceRegister"),
                                        (String) in.get("address"));
                    } else {
                        
                        // Correct after changed in the assembler, if you see
                        // this we are screwed
                        ExMem.put("lui", "1");
                        out = (String) in.get("address");
                    }
                }
                
            } else {
                if (controlSignals.equals("X1X001000")) { // save word
                    // instruction
                    out = binaryAdd((String) in.get("sourceRegister"),
                                    (String) in.get("address"));
                    ExMem.put("writeData", (String) in.get("firstSource"));
                    
                } else {
                    // This part is for I-format instructions which is only the
                    // addi instruction
                    String immediate = (String) in.get("address");
                    out = binaryAdd((String) in.get("Source"), immediate);
                    
                }
            }
            
        }
        ExMem.put("result", out);
        return ExMem;
    }
    
    public static String operation(String ALUControl, String arg1, String arg2) {
        String result = "";
        if (ALUControl.equals("0010")) {
            return binaryAdd(arg1, arg2);
        }
        
        if (ALUControl.equals("0110")) {
            return binarySubtract(arg1, arg2);
        }
        
        if (ALUControl.equals("0000")) {
            return binaryAnd(arg1, arg2);
        }
        
        if (ALUControl.equals("0001")) {
            return binaryOr(arg1, arg2);
        }
        
        if (ALUControl.equals("0111")) {
            return binarySetLessThan(arg1, arg2);
        }
        
        if (ALUControl.equals("sll")) {
            int shiftAmount = 0;
            return binaryShiftLeft(arg2, shiftAmount);
        }
        
        if (ALUControl.equals("srl")) {
            int shiftAmount = 0;
            return binaryShiftRight(arg2, shiftAmount);
        }
        
        if (ALUControl.equals("nor")) {
            String temp = "";
            temp = binaryOr(arg1, arg2);
            return binaryNot(temp);
        }
        
        return "";
    }
    
    /*
     * This method is called in the case of an R-type function arriving to the
     * Execution stage. It takes bits 5-0 which are the function bits and
     * computes the ALU Control Input which is passed to the ALU to perform the
     * required operation.
     */
    public static String getFunction(String function) {
        String result = "";
        if (function.equals("100000")) { // add
            return "0010";
        }
        if (function.equals("100010")) { // subtract
            return "0110";
        }
        if (function.equals("100100")) { // and
            return "0000";
        }
        if (function.equals("100101")) { // or
            return "0001";
        }
        if (function.equals("101010")) { // set less than
            return "0111";
        }
        if (function.equals("100101")) { // or
            return "0001";
        }
        // Need to check the correct codes for shifting from the book
        
        if (function.equals("000000")) { // shift logical left
            return "sll";
        }
        
        if (function.equals("000010")) { // shift logical right
            return "srl";
        }
        
        if (function.equals("100111")) { // nor
            return "nor";
        }
        return result;
    }
    
    public static int twosComp(String c) {
        String s = "";
        boolean firstOne = false;
        for (int i = 31; i >= 0; i--) {
            if (firstOne == true) {
                if (c.charAt(i) == '1') {
                    s = '0' + s;
                } else {
                    s = '1' + s;
                }
                
            } else {
                if (c.charAt(i) == '1') {
                    firstOne = true;
                }
                s = c.charAt(i) + s;
            }
        }
        return -Integer.parseInt(s, 2);
    }
    
}

