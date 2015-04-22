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

	public static void decoder(String binary) {
		String rs, rt, rd;
		Hashtable<String, String> toExecute = null;
		Hashtable<String, String> controlSignals=null;
		toExecute.put("op", binary.substring(0, 5));
		if (binary.startsWith("000000"))
		// if R-format, sends back all the data .
		{
			//set control signals.
			controlSignals.put("RegDst","1");
			controlSignals.put("ALUSrc","0");
			controlSignals.put("MemtoReg","0");
			controlSignals.put("RegWrite","1");
			controlSignals.put("MemRead","0");
			controlSignals.put("MemWrite","0");
			controlSignals.put("Branch","0");
			controlSignals.put("ALUOp","10");
			// read the values in rs and rt and place them in registers
			rs = binary.substring(6, 10);
			rt = binary.substring(16, 20);
			rs = registerFile.get(rs);
			rt = registerFile.get(rt);
			toExecute.put("firstSource", rs);
			toExecute.put("destinationRegister", binary.substring(11, 15));
			toExecute.put("secondSource", rt);
			toExecute.put("shamt", binary.substring(21, 25));
			toExecute.put("funct", binary.substring(26));
		} else if (binary.startsWith("000010") || binary.startsWith("000011"))
		// if J-format ,sends back the address.
		{

			toExecute.put("address", binary.substring(6));
		} else
		// if I-format sends back the value of reg,destination ,constant/adress.
		{
			// format ="I";
			// rt = binary.substring(11,15);
			// imm= binary.substring(16);

			rs = binary.substring(6, 10);
			rs = registerFile.get(rs);
			toExecute.put("sourceRegister", rs);
			toExecute.put("destinationRegister", binary.substring(11, 15));
			toExecute.put("address", binary.substring(16));
			if (binary.startsWith("001000")){ 
				// if it's a addi ,set control signals.
				controlSignals.put("RegDst","0");
				controlSignals.put("ALUSrc","1");
				controlSignals.put("MemtoReg","0");
				controlSignals.put("RegWrite","1");
				controlSignals.put("MemRead","0");
				controlSignals.put("MemWrite","0");
				controlSignals.put("Branch","0");
				controlSignals.put("ALUOp","00");
			}
			else if (binary.startsWith("100011")){
				// if it's load word,set control signals.
				controlSignals.put("RegDst","0");
				controlSignals.put("ALUSrc","1");
				controlSignals.put("MemtoReg","1");
				controlSignals.put("RegWrite","1");
				controlSignals.put("MemRead","1");
				controlSignals.put("MemWrite","0");
				controlSignals.put("Branch","0");
				controlSignals.put("ALUOp","00");
			}
			else if (binary.startsWith("100011")){
				// if it's save word,set control signals.
				controlSignals.put("RegDst","X");
				controlSignals.put("ALUSrc","1");
				controlSignals.put("MemtoReg","X");
				controlSignals.put("RegWrite","0");
				controlSignals.put("MemRead","0");
				controlSignals.put("MemWrite","1");
				controlSignals.put("Branch","0");
				controlSignals.put("ALUOp","00");
			}
			else if (binary.startsWith("100011")){
				// if it's Branch on equal,set control signals.
				controlSignals.put("RegDst","X");
				controlSignals.put("ALUSrc","0");
				controlSignals.put("MemtoReg","X");
				controlSignals.put("RegWrite","0");
				controlSignals.put("MemRead","0");
				controlSignals.put("MemWrite","0");
				controlSignals.put("Branch","1");
				controlSignals.put("ALUOp","00");
			}
		}
		Execute(toExecute,controlSignals);

	}
}
