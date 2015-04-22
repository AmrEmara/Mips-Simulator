import java.util.HashMap;
import java.util.Hashtable;

public class Simulator {
	static String rs, rt;
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
    
    public static int binaryToDecimal(String s) {
        int result = 0;
        double temp;
        char c;
        for (int i = 0; i < s.length(); i++) {
            String num = "";
            c = s.charAt(s.length() - i - 1);
            num += c;
            temp = Math.pow(2.0, i) * Integer.parseInt(num);
            result += temp;
        }
        return result;
    }
    
    /*
     * This method takes an integer and converts to a binary string
     */
    public static String decimalToBinary(int c) {
        String res = "";
        int temp, rem;
        while (c > 0) {
            rem = c % 2;
            temp = c / 2;
            res = rem + res;
            c = c / 2;
        }
        return res;
    }
    
    public static int twosCompToDecimal(String c) {
        String result = "";
        // String c = signExtendor(arg);
        String complement = binaryNot(c);
        char carry = '1';
        for (int i = 0; i < c.length(); i++) {
            char op1 = c.charAt(c.length() - i - 1);
            char op2 = complement.charAt(c.length() - i - 1);
            if (op1 == '1' && op2 == '1') {
                if (carry == '1') {
                    result = '1' + result;
                } else {
                    result = '0' + result;
                    carry = '1';
                }
            } else {
                if (op1 == '1' || op2 == '1') {
                    if (carry == '1') {
                        result = '0' + result;
                    } else {
                        result = '1' + result;
                    }
                } else {
                    
                    if (op1 == '0' && op2 == '0') {
                        if (carry == '1') {
                            result = '1' + result;
                        } else {
                            result = '0' + result;
                        }
                    }
                }
            }
            
        }
        System.out.println("The tow's complement is " + result);
        int res = binaryToDecimal(result);
        return -res;
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
        String res = signExtendor(s);
        String out = "";
        if (shiftAmount > 32) {
            System.out.println("invalid shift amount, greater than 32 !");
        }
        String[] shiftString = new String[32];
        for (int i = 0; i < 32; i++) {
            shiftString[i] = res.charAt(i) + "";
        }
        for (int i = shiftAmount; i > 0; i--) {
            String start = shiftString[0];
            for (int j = 0; j < 31; j++) {
                shiftString[j] = shiftString[j + 1];
            }
            shiftString[31] = start;
        }
        for (int i = 0; i < 32; i++) {
            out += shiftString[i];
        }
        
        return out;
    }
    
    public static String binaryShiftRight(String s, int shiftAmount) {
        String res = signExtendor(s);
        String out = "";
        if (shiftAmount > 32) {
            System.out.println("invalid shift amount, greater than 32 !");
        }
        String[] shiftString = new String[32];
        for (int i = 0; i < 32; i++) {
            shiftString[i] = res.charAt(i) + "";
        }
        for (int i = shiftAmount; i > 0; i--) {
            String start = shiftString[31];
            for (int j = 31; j >= 1; j--) {
                shiftString[j] = shiftString[j - 1];
            }
            shiftString[0] = start;
        }
        for (int i = 0; i < 32; i++) {
            out += shiftString[i];
        }
        
        return out;
    }
    
    public static HashMap Execute(HashMap in) {
        
        String controlSignals = "";
        String ALUfunc = "";
        String rFunc = "";
        controlSignals += in.get("RegDst");
        controlSignals += in.get("ALUSrc");
        controlSignals += in.get("MemtoReg");
        controlSignals += in.get("RegWrite");
        controlSignals += in.get("MemRead");
        controlSignals += in.get("MemWrite");
        controlSignals += in.get("Branch");
        controlSignals += in.get("ALUOp");
        
        // what should happen if the control signals indicate a R-type
        // instruction
        if (controlSignals.equals("100100010")) {
            ALUfunc = getFunction((String) in.get("funct"));
            String out = operation(ALUfunc, (String) in.get("firstSource"),
                                   (String) in.get("secondSource"));
        } else {
            if (controlSignals.equals("011110000")) { // then its a load
                // instruction
                String out = ""; // used to store value loaded from memory
                String address = binaryAdd((String) in.get("sourceRegister"),
                                           (String) in.get("address")); // add offset to base
                // address
                out = (String) registerFile.get(address);
                String loader = (String) in.get("destinationRegister");
                in.put(loader, out); // I should put in the register file
                
            } else {
                if (controlSignals.equals("X1X001000")) { // save word
                    // instruction
                    
                    
                } else {
                    if (controlSignals.equals("X0X000101")) { // branch equal
                        // instruction
                        
                    } else {
                        // other I type instructions / which is basically the
                        // addi or not
                    }
                }
            }
            
        }
        
        return null;
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
            int shiftAmount = binaryToDecimal(arg2);
            return binaryShiftLeft(arg1, shiftAmount);
        }
        
        if (ALUControl.equals("srl")) {
            int shiftAmount = binaryToDecimal(arg2);
            return binaryShiftRight(arg1, shiftAmount);
        }
        
        if (ALUControl.equals("nor")) {
            String temp = "";
            temp = binaryOr(arg1, arg2);
            return binaryNot(temp);
        }
        
        return "";
    }
    
    /*
     * Till now this is unnecessary This method simulates the ALU Control Unit,
     * I think karim can use it it controlled by the ALUOp which is obtained in
     * the decode stage. Using the ALUOp it calls the method getFunction(String)
     * to get ALUControl signal then calls ALUOp with correct control signal.
     */
    public static void ALUControl(String instruction5To0, String ALUOp) {
        String ALUControlIn = "";
        if (ALUOp.equals("00")) {
            ALUControlIn = "0010";
        } else {
            if (ALUOp.equals("01")) {
                ALUControlIn = "0110";
            } else {
                if (ALUOp.equals("10")) {
                    ALUControlIn = getFunction(instruction5To0);
                }
            }
        }
        // ALUOp(ALUControlIn);
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

}
