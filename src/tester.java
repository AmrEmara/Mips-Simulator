
public class tester {
	static String rs,rd,rt,shft,funct,format, imm ;
	static String addi= "100010", lw= "100001", lb="100000", lbu ="100100", sw= "101011";
	static String sb= "101000", lui ="001111", beq= "000100", bne= "000101",j="000010" ;
	static String jal="000011";
	
	
	public static void main(String [] args){
		System.out.println(decoder("00000011111111111111191111511111"));
	}

	public static  String decoder(String binary) {
		if (binary.startsWith("000000")){
			format ="R";
			rs = binary.substring(6,10);
			rd = binary.substring(11,15);
			rt = binary.substring(16,20);
			shft = binary.substring(21,25);
			funct = binary.substring(26);
			return format +"  "+ rs + "   "+ shft + "   "+ funct ;
		}
		else if (binary.startsWith("001000")){
			format ="I";
			rs = binary.substring(6,10);
			rt = binary.substring(11,15);
			imm= binary.substring(16);
			return format +" " + rs + rt + "   "+ imm;

		}
		else {
			return "J format";
		}
			
	}
}
