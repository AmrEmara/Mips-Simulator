import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;


public class Assembler {
	HashMap<String, String> memory;
	String pc ;

	public Assembler(File file) throws IOException{
		//key for memory is the address of instruction/data
		memory = new HashMap<String, String>();
		FileReader x = new FileReader(file);
		BufferedReader br = new BufferedReader(x);
		pc = br.readLine();
		String line = br.readLine();
		String inst;
		String operands;
		String instAddress = pc;
		while(line != null){
			
			inst = line.split(" ")[0];
			if(inst.equalsIgnoreCase("add") || inst.equalsIgnoreCase("sub") || inst.equalsIgnoreCase("and") || inst.equalsIgnoreCase("nor") || inst.equalsIgnoreCase("slt") || inst.equalsIgnoreCase("sltu")){
				
				inst = "000000";
				operands = line.split(" ")[1];
				inst += regNum(operands.split(",")[1])+regNum(operands.split(",")[2])+regNum(operands.split(",")[0])+"00000";
				switch(line.split(" ")[0]){
					case "add":
					case "ADD":
					case "Add":inst+="100000";break;
					case "sub":
					case "SUB":
					case "Sub":inst+="100010";break;	
					case "and":
					case "AND":
					case "And":inst+="100100";break;	
					case "nor":
					case "NOR":
					case "Nor":inst+="100111";break;	
					case "slt":
					case "SLT":
					case "Slt":inst+="101010";break;	
					case "sltu":
					case "SLTU":
					case "Sltu":inst+="101001";break;	
				}
				
				
				
			}
			else if(inst.equalsIgnoreCase("sll") || inst.equalsIgnoreCase("srl")){
				
				inst = "000000";
				operands = line.split(" ")[1];
				inst += "00000"+regNum(operands.split(",")[1])+regNum(operands.split(",")[0])+Integer.toBinaryString(Integer.parseInt(operands.split(",")[2]));
				switch(line.split(" ")[0]){
					case "sll":
					case "SLL":
					case "Sll":inst+="000000";break;
					case "srl":
					case "SRL":
					case "Srl":inst+="000010";break;	
				
				}
			}
			else if(inst.equalsIgnoreCase("addi")){
				
				inst = "";
			}
				
				
		}
		
			
			
	}
	public static String regNum(String reg){
		
		switch(reg){
			
		case "$0":
		case "$zero": reg = "00000";break;
		case "$v0" : reg = "00010";break;
		case "$v1" : reg = "00011";break;
		case "$a0" : reg = "00100";break;
		case "$a1" : reg = "00101";break;
		case "$a2" : reg = "00110";break;
		case "$a3" : reg = "00111";break;
		case "$t0" : reg = "01000";break;
		case "$t1" : reg = "01001";break;
		case "$t2" : reg = "01010";break;
		case "$t3" : reg = "01011";break;
		case "$t4" : reg = "01100";break;
		case "$t5" : reg = "01101";break;
		case "$t6" : reg = "01110";break;
		case "$t7" : reg = "01111";break;
		case "$s0" : reg = "10000";break;
		case "$s1" : reg = "10001";break;
		case "$s2" : reg = "10010";break;
		case "$s3" : reg = "10011";break;
		case "$s4" : reg = "10100";break;
		case "$s5" : reg = "10101";break;
		case "$s6" : reg = "10110";break;
		case "$s7" : reg = "10111";break;
		case "$t8" : reg = "11000";break;
		case "$t9" : reg = "11001";break;
		case "$sp" : reg = "11101";break;
		case "$ra" : reg = "11111";break;
		
		}
		return reg;
		
		
		
	}
		
}
	
	

