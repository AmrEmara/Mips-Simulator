import java.util.HashMap;
import java.util.Hashtable;

public class Simulator {
    static String rs, rt;
    static HashMap<String, String> registerFile;
    HashMap<Integer, String> memory;
    int pc, endAddress;
    int cycleNo = 0;
    boolean single_path =false;
    public Simulator(HashMap<Integer, String> inputMemory , int pc, int endAddress) {
        // the key for the registerFile is the register number in binary
        registerFile = new HashMap<String, String>();
        this.memory = inputMemory;
        //        System.out.println(memory.get(pc));
        
        this.pc = pc;
        this.endAddress=endAddress;
        
        registerFile.put("00000", "00000000000000000000000000000000");  // $zero
        registerFile.put("00010", "00000000000000000000000000000000");	// $v0
        registerFile.put("00011", "00000000000000000000000000000000");	// $v1
        registerFile.put("00100", "00000000000000000000000000000000");	// $a0
        registerFile.put("00101", "00000000000000000000000000000000");	// $a1
        registerFile.put("00110", "00000000000000000000000000000000");	// $a2
        registerFile.put("00111", "00000000000000000000000000000000");	// $a3
        registerFile.put("01000", "00000000000000000000000000000000");	// $t0
        registerFile.put("01001", "00000000000000000000000000000000");	// $t1
        registerFile.put("01010", "00000000000000000000000000000000");	// $t2
        registerFile.put("01011", "00000000000000000000000000000000");	// $t3
        registerFile.put("01100", "00000000000000000000000000000000");	// $t4
        registerFile.put("01101", "00000000000000000000000000000000");	// $t5
        registerFile.put("01110", "00000000000000000000000000000000");	// $t6
        registerFile.put("01111", "00000000000000000000000000000000");	// $t7
        registerFile.put("10000", "00000000000000000000000000000000");	// $s0
        registerFile.put("10001", "00000000000000000000000000000000");	// $s1
        registerFile.put("10010", "00000000000000000000000000000000");	// $s2
        registerFile.put("10011", "00000000000000000000000000000000");	// $s3
        registerFile.put("10100", "00000000000000000000000000000000");	// $s4
        registerFile.put("10101", "00000000000000000000000000000000");	// $s5
        registerFile.put("10110", "00000000000000000000000000000000");	// $s6
        registerFile.put("10111", "00000000000000000000000000000000");	// $s7
        registerFile.put("11000", "00000000000000000000000000000000");	// $t8
        registerFile.put("11001", "00000000000000000000000000000000");	// $t9
        registerFile.put("11101", "00000000000000000000000000000000");	// $sp
        registerFile.put("11110", "00000000000000000000000000000000");	// $fp
        registerFile.put("11111", "00000000000000000000000000000000");	// $ra
        
    }
    
    public void statusPrinter(HashMap<String, String> a){
        
    }
    ////////////////////getters for register file and memory///////////////////////
    public HashMap<String, String> getRegisterFile(){
        return this.registerFile;
    }
    public HashMap<Integer, String> getMemory(){
        return this.memory;
    }
    /////////////////////////////fetch/////////////////////////////////////////////
    public void fetch() {
        boolean f=false;
    	if(this.pc>endAddress){
            return;
        }
        int tempPc = this.pc;
        String binary = this.memory.get(pc); // fetch the instruction from
        
        // memory
        String address;// to save the address part of the instruction
        if (binary.startsWith("000100")) { // check if beq
            address = binary.substring(16); // get the address part of the
            // instruction
            tempPc = tempPc+Integer.parseInt(address, 2);// get the decimal value of
            address = binary.substring(16); // get the address part of the instruction
            tempPc = Integer.parseInt(address, 2);// get the decimal value of
            // the address and store it
            // in tempPc
            f=true;
        } else {
            if (binary.startsWith("000101")) { // check if bne
                address = binary.substring(16); // get the address part of the
                // instruction
                tempPc = tempPc+Integer.parseInt(address, 2);// get the decimal value
                // of the address and
                // store it in tempPc
                f=true;
            } else {
                if (binary.startsWith("000010")) { // check if j
                    address = binary.substring(6); // get the address part of
                    // the instruction
                    String x =Integer.toBinaryString(tempPc);
                    address= x.substring(0,3)+address+"00";
                    tempPc = Integer.parseInt(address, 2);// get the decimal
                    // value of the
                    // address and store
                    // it in tempPc
                    f=true;
                } else {
                    if (binary.startsWith("000011")) { // check if jal
                        address = binary.substring(6); // get the address part
                        // of the instruction
                        registerFile.put("11111",
                                         Integer.toBinaryString(tempPc));// save tempPc
                        // value in ra
                        // register
                        String x =Integer.toBinaryString(tempPc);
                        address= x.substring(0,3)+address+"00";
                        tempPc = Integer.parseInt(address, 2);// get the decimal
                        // value of the
                        // address and
                        // store it in
                        // tempPc
                        f=true;
                    } else {
                        if (binary.startsWith("000000")
                            && binary.substring(11).equals(
                                                           "000000000000000001000")) { // check
                            // if
                            // jr
                            tempPc = Integer.parseInt(
                                                      registerFile.get("11111"), 2);// load the
                            // value of
                            // ra
                            // register
                            // in tempPc
                            f=true;
                        } else {
                            tempPc = tempPc + 4;
                        }
                    }
                }
            }
        }
        System.out.println("==========================================================================");
        System.out.println("Cycle Number = " + cycleNo);
        System.out.println("Contents of IF/ID register:");
        System.out.println("PC = " + pc);
        System.out.println("instruction = " + binary);
        System.out.println();
        this.pc = tempPc;
        this.cycleNo++;
        if(!f)
        decoder(binary); // return the instruction
        if (!single_path)
            fetch();
        
    }
    /////////////////////////////decode/////////////////////////////////////////////
    
    public void decoder(String binary) {
        String rs, rt, rd;
        HashMap<String, String> out = new HashMap<String, String>();
        out.put("opCode", binary.substring(0, 6));
        if (binary.startsWith("000000"))
        /*
         * If the instructions starts with "000000" then its an R-Format
         * instruction The control signals are set then the contents of
         * registers rs and rt are placed in the pipline register. The address
         * of rd is put in the register.
         */
        {
            String opcode = binary.substring(0, 6);
            // String rs = binary.substring(6, 11);
            // String rt = binary.substring(11, 16);
            // String rd = binary.substring(16, 21);
            String shmat = binary.substring(21, 26);
            String func = binary.substring(26, 32);
            // set control signals.
            out.put("RegDst", "1");
            out.put("ALUSrc", "0");
            out.put("MemtoReg", "0");
            out.put("RegWrite", "1");
            out.put("MemRead", "0");
            out.put("MemWrite", "0");
            out.put("Branch", "0");
            out.put("ALUOp", "10");
            // read the values in rs and rt and place them in registers
            rs = binary.substring(6, 11);
            rt = binary.substring(11, 16);
            rs = registerFile.get(rs);
            rt = registerFile.get(rt);
            out.put("rs", rs);
            out.put("rsDec",binaryToDecimalUnsigned(rs) +"");
            out.put("rd", binary.substring(16, 21));
            out.put("rdDec",binaryToDecimalUnsigned(binary.substring(16, 21)) +"");
            out.put("rt", rt);
            out.put("rtDec",binaryToDecimalUnsigned(rt) +"");
            out.put("shamt", binary.substring(21, 26));
            out.put("funct", binary.substring(26));
        }
        else if (binary.startsWith("000010") || binary.startsWith("000011"))
            // if J-format ,sends back the address.
        {
            
            out.put("address", binary.substring(6));
        }
        else
            // if I-format sends back the value of reg,destination ,constant/adress.
        {
            // format ="I";
            // rt = binary.substring(11,15);
            // imm= binary.substring(16);
            rs = binary.substring(6, 11);
            rs = registerFile.get(rs);
            out.put("rs", rs);
            out.put("rsDec",binaryToDecimalUnsigned(rs) +"");
            out.put("rt", binary.substring(11, 16));
            out.put("rtDec",binaryToDecimalUnsigned(binary.substring(11, 16)) +"");
            out.put("offset", binary.substring(16));
            out.put("offsetDec", binaryToDecimalUnsigned(binary.substring(16))+"");
            
            if (binary.startsWith("001000")) {
                // if it's a addi ,set control signals.
                out.put("RegDst", "0");
                out.put("ALUSrc", "1");
                out.put("MemtoReg", "0");
                out.put("RegWrite", "1");
                out.put("MemRead", "0");
                out.put("MemWrite", "0");
                out.put("Branch", "0");
                out.put("ALUOp", "00");
            } else if (binary.startsWith("100011") || binary.startsWith("100000") ||
                       binary.startsWith("100100")) {
                // if it's load word,set control signals.
                out.put("RegDst", "0");
                out.put("ALUSrc", "1");
                out.put("MemtoReg", "1");
                out.put("RegWrite", "1");
                out.put("MemRead", "1");
                out.put("MemWrite", "0");
                out.put("Branch", "0");
                out.put("ALUOp", "00");
            } else if(binary.startsWith("001111")){
                // lui
                out.put("RegDst", "0");
                out.put("ALUSrc", "1");
                out.put("MemtoReg", "1");
                out.put("RegWrite", "1");
                out.put("MemRead", "0");
                out.put("MemWrite", "0");
                out.put("Branch", "0");
                out.put("ALUOp", "00");
                
            } else if (binary.startsWith("101011") || binary.startsWith("101000") ) {
                // if it's save word,set control signals.
                out.put("RegDst", "X");
                out.put("ALUSrc", "1");
                out.put("MemtoReg", "X");
                out.put("RegWrite", "0");
                out.put("MemRead", "0");
                out.put("MemWrite", "1");
                out.put("Branch", "0");
                out.put("ALUOp", "00");
            } else if (binary.startsWith("100011")) {
                // if it's Branch on equal,set control signals.
                out.put("RegDst", "X");
                out.put("ALUSrc", "0");
                out.put("MemtoReg", "X");
                out.put("RegWrite", "0");
                out.put("MemRead", "0");
                out.put("MemWrite", "0");
                out.put("Branch", "1");
                out.put("ALUOp", "00");
            }
        }
        System.out.println("Contents of ID/EX  register:");
        System.out.println(out.toString());
        System.out.println();
        Execute(out);
        
    }
    
    /////////////////////////////execute/////////////////////////////////////////////
    // when a load control signal is received the opcode is checked and flags
    // added to tell
    // if this is a lw or lb instruction.
    
    public void Execute(HashMap<String, String> in) {
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
        
        // This portion checks if the control signals provided from the decode
        // register
        // are those of an R-formtat. If it is identified as R-format then
        // we take the function bits passed form the decode stage to determone
        // the type
        // of operation to be performed and calls the correct function to
        // perform this
        // operation. The result is put in the Ex/Mem register with name =
        // "result"
        // This result should be saved in the address provided in the rd of this
        // instruction
        // this is taken from decode stage and passed to the write back stage
        // with
        // the name = "destinationRegister".
        if (controlSignals.equals("100100010")) {
            ALUfunc = getFunction((String) in.get("funct"));
            out = operation(ALUfunc, (String) in.get("rs"),
                            (String) in.get("rt"), (String) in.get("shamt"));
            ExMem.put("destinationRegister", (String) in.get("rd"));
            ExMem.put("destinationRegisterValue", binaryToDecimalUnsigned((String) in.get("rd"))+"");
            
        } else {
            // This means that this a load instruction.
            // for all cases: lw,lb, lbu, lui
            // we will calculate the address of the instruction in memory by
            // summing the offset and contents inside the address passed in the
            // rs register of this instruction.
            // This portion calculates the address that we want to load and puts
            // it in the register with name = "result". The data should be loaded in
            // the register rt provided in the instruction, this is passed to the
            // write back stage
            // with name = "destinationRegister"
            if (controlSignals.equals("011110000")) {
                String op = (String) in.get("opCode");
                out = binaryAdd((String) in.get("rs"),
                                (String) in.get("offset"));
                // adds offset to rs to get address to be loaded
                if (op.equals("100000")) { // instruction
                    ExMem.put("lb", "1");
                    
                } else {
                    if (op.equals("100100")) {
                        ExMem.put("lbu", "1");
                        
                    }
                    ExMem.put("destinationRegister", (String) in.get("rt"));
                    ExMem.put("destinationRegisterValue", binaryToDecimalUnsigned((String) in.get("rt")) + "");
                }
            } else {
                // This is for save instructions, the address is calculated by adding
                // the contents of the rs and offset then placed in register with tag
                // result. The data in register rt should be saved in the address
                // calculated.
                if (controlSignals.equals("X1X001000")) {
                    String op = (String) in.get("opCode");
                    if(op.equals("101000")){
                        ExMem.put("sb", "1");
                    }
                    out = binaryAdd((String) in.get("rs"),
                                    (String) in.get("offset"));
                    ExMem.put("writeData", (String) in.get("rt"));
                    ExMem.put("writeDataValue", binaryToDecimalUnsigned((String) in.get("rt")) + "");
                    
                } else {
                    if(controlSignals.equals("011100000")){
                        //lui instruction
                        ExMem.put("lui", "1");
                        out = (String) in.get("offset");
                    }
                    else{
                        // This part is for addi instructions. The offset is
                        // added to the content of rs and the result should
                        // be put in the address inside the rt register.
                        
                        String immediate = (String)in.get("offset");
                        out = binaryAdd((String)in.get("rs"), immediate);
                        ExMem.put("destinationRegister", (String) in.get("rt"));
                        ExMem.put("destinationRegisterValue", binaryToDecimalUnsigned((String) in.get("rt")) + "");
                    }
                }
            }
            
        }
        ExMem.put("result", out);
        ExMem.put("resultValue", binaryToDecimal(out)+"");
        System.out.println("Contents EX/MEM register: ");
        System.out.println(ExMem.toString());
        System.out.println();
        memor(ExMem);
    }
    ///////////////////////////helpers for execute/////////////////////////////////////
    public static int binaryToDecimal(String s) {
        if (s.charAt(0) == '1') {
            return twosComp(s);
        }
        
        return Integer.parseInt(s, 2);
    }
    
    public static int binaryToDecimalUnsigned(String s){
        int i = 0;
        int result = 0;
        
        while (i<s.length()){
            if(s.charAt(s.length()-1-i) == '1'){
                result += Math.pow(2.0,i );
            }
            i++;
        }
        return result;
    }
    
    /*
     * This method takes an integer and converts to a binary string
     */
    public static String decimalToBinary(int num) {
        if (num < 0) {
            return Integer.toBinaryString(num);
        }
        String res = Integer.toBinaryString(num);
        if (res.length() < 32) {
            for (int i = res.length(); i < 32; i++) {
                res = "0" + res;
            }
        }
        return res;
    }
    
    public static String binaryAdd(String arg1, String arg2) {
        String res = "";
        if(arg1.length() == 16)
            return decimalToBinary(binaryToDecimalUnsigned(arg1) + binaryToDecimal(arg2));
        
        if (arg2.length() == 16)
            return decimalToBinary(binaryToDecimal(arg1) + binaryToDecimalUnsigned(arg2));
        
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
        System.out.println("the value of rs is: " + a );
        System.out.println("the value of rt is: " + b);
        if (a < b)
            return "1";
        return "0";
    }
    
    public static String binarySetLessThanUnsigned(String arg1, String arg2){
        int a = binaryToDecimalUnsigned(arg1);
        int b = binaryToDecimalUnsigned(arg2);
        System.out.println("the value of a is: " + a);
        System.out.println("the value of b is: " + b);
        if(a < b)
            return "1";
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
    
    
    
    public static String operation(String ALUControl, String arg1, String arg2, String shift) {
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
        if (ALUControl.equals("sltu")) {
            return binarySetLessThanUnsigned(arg1, arg2);
        }
        
        if (ALUControl.equals("sll")) {
            int shiftAmount = Integer.parseInt(shift,2);
            return binaryShiftLeft(arg2, shiftAmount);
        }
        
        if (ALUControl.equals("srl")) {
            int shiftAmount = Integer.parseInt(shift,2);
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
            System.out.println("3erf eno add");
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
        if (function.equals("101001")) { // set less than
            return "sltu";
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
    
    
    
    //    public void fetch_single() {
    //        if(this.pc>endAddress){
    //            return;
    //        }
    //        int tempPc = this.pc;
    //        String binary = this.memory.get(pc); // fetch the instruction from
    //
    //        // memory
    //        String address;// to save the address part of the instruction
    //        if (binary.startsWith("000100")) { // check if beq
    //            address = binary.substring(16); // get the address part of the
    //            // instruction
    //            tempPc = Integer.parseInt(address, 2);// get the decimal value of
    //            // the address and store it
    //            // in tempPc
    //        } else {
    //            if (binary.startsWith("000101")) { // check if bne
    //                address = binary.substring(16); // get the address part of the
    //                // instruction
    //                tempPc = Integer.parseInt(address, 2);// get the decimal value
    //                // of the address and
    //                // store it in tempPc
    //            } else {
    //                if (binary.startsWith("000010")) { // check if j
    //                    address = binary.substring(6); // get the address part of
    //                    // the instruction
    //                    tempPc = Integer.parseInt(address, 2);// get the decimal
    //                    // value of the
    //                    // address and store
    //                    // it in tempPc
    //                } else {
    //                    if (binary.startsWith("000011")) { // check if jal
    //                        address = binary.substring(6); // get the address part
    //                        // of the instruction
    //                        registerFile.put("00000000000000000000000000011111",
    //                                         Integer.toBinaryString(tempPc));// save tempPc
    //                        // value in ra
    //                        // register
    //                        tempPc = Integer.parseInt(address, 2);// get the decimal
    //                        // value of the
    //                        // address and
    //                        // store it in
    //                        // tempPc
    //                    } else {
    //                        if (binary.startsWith("000000")
    //                            && binary.substring(11).equals(
    //                                                           "000000000000000001000")) { // check
    //                            // if
    //                            // jr
    //                            tempPc = Integer.parseInt(
    //                                                      registerFile.get("11111"), 2);// load the
    //                            // value of
    //                            // ra
    //                            // register
    //                            // in tempPc
    //                        } else {
    //                            tempPc = tempPc + 4;
    //                        }
    //                    }
    //                }
    //            }
    //        }
    //        this.pc = tempPc;
    //        decoder(binary); // return the instruction
    //    }
    
    
    ///////////////////////////memory/////////////////////////////////////////////
    public void memor(HashMap<String, String> binary) {
        
        System.out.println();
        HashMap<String, String> toWrite = new HashMap<String, String>();
        String data;
        int address;
        if (binary.get("MemRead").equals("1")) { //load instruction
            address = Integer.parseInt(binary.get("result"), 2);
            data = this.memory.get(address);
            if(binary.get("lb").equals("1")){ //lb instruction
                data = "000000000000000000000000"+data.substring(24);
            }
            if(binary.get("lbu").equals("1")){ //lbu instruction
                data = signExtendor(data.substring(24));
            }
            
            toWrite.put("result", binary.get("result"));
            toWrite.put("MemtoReg", binary.get("MemtoReg"));
            toWrite.put("RegWrite", binary.get("RegWrite"));
            toWrite.put("destinationRegister", binary.get("destinationRegister"));
            toWrite.put("data", data);
            toWrite.put("lui",binary.get("lui"));
            // writeBack
        } else if (binary.get("MemWrite").equals("1")) { //store instruction
            
            address = Integer.parseInt(binary.get("result"), 2);
            data = binaryToDecimal(registerFile.get((binary.get("writeData")))) + "";
            toWrite.put("addressForStore",address +"");
            
            if(binary.get("sb").equals("1")){ //sb instruction
                data = "000000000000000000000000"+data.substring(24);
            }
            this.memory.put(address, data);
            //in this case Values for MemtoReg and RegWrite will be X (don't cares)
            toWrite.put("result", binary.get("result"));
            toWrite.put("MemtoReg", binary.get("MemtoReg"));
            toWrite.put("RegWrite", binary.get("RegWrite"));
            toWrite.put("destinationRegister", "00000");
            toWrite.put("data", registerFile.get(data));
            toWrite.put("lui",binary.get("lui"));
            
        } else { //ALU instruction
            data = "00000000000000000000000000000000";
            toWrite.put("result", binary.get("result"));
            toWrite.put("resultValue", binaryToDecimal(binary.get("result")) + "");
            toWrite.put("MemtoReg", binary.get("MemtoReg"));
            toWrite.put("RegWrite", binary.get("RegWrite"));
            toWrite.put("destinationRegister", binary.get("destinationRegister"));
            toWrite.put("destinationRegister #", binaryToDecimalUnsigned(binary.get("destinationRegister")) + "");
            toWrite.put("data", data);
            toWrite.put("lui",binary.get("lui"));
            // writeBack
        }
        System.out.println("Contents of MEM/WB register:");
        System.out.println(toWrite.toString());
        WriteBack(toWrite);
    }
    
    ///////////////////////////write back////////////////////////////////////////////
    public void WriteBack(HashMap<String, String> binary) {
        
        if (binary.get("MemtoReg").equals("0")){
            // this if condition to check whether to add from memory or register, and in this case ALU
            
            if(binary.get("lui").equals("1")){
                // this if handles case of load upper immediate
                
                String tmp = binary.get("result").substring(16)+"0000000000000000";
                this.registerFile.put(binary.get("destinationRegister"),tmp);
                // we substring the last 16 bits from the register and then we add 16 0's, aka 32 bits, then add it to rd
            }
            else{
                // this case we add to the register the value from the ALU
                
                this.registerFile.put(binary.get("destinationRegister"),binary.get("result"));
            }
        }
        else{
            if(!binary.get("MemtoReg").equals("X")){
                // this case we add the value from the Memory to the register
                this.registerFile.put(binary.get("destinationRegister"),decimalToBinary(Integer.parseInt(binary.get("data"))));
            }
        }
        
        System.out.println("==========================================================================");
        if (single_path)
            fetch();
    }
    
    public void isSinglePath(){
        // Main method calls isSinglePath and enters a boolean that decides if its a single path or not.
        
        single_path=true;
       
    }
}
