import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;

import javax.imageio.stream.MemoryCacheImageInputStream;


public class Assembler {
    HashMap<Integer, String> memory;
    int pc , endAddress;
    
    public Assembler(File file) throws IOException{
        //key for memory is the address of instruction/data
        memory = new HashMap<Integer, String>();
        FileReader x = new FileReader(file);
        BufferedReader br = new BufferedReader(x);
        
        String line = br.readLine();
        if(line.equalsIgnoreCase(".data")){
            line = br.readLine();
            while(!line.equalsIgnoreCase(".text")){
                int address;
                address = Integer.parseInt(line.split(",",2)[0].substring(2),16);
                
                if(line.split(",",2)[1].charAt(0)=='{'){
                    
                    String commaData="";
                    String[] dataArray;
                    int i = 1;
                    while(line.split(",",2)[1].charAt(i)!='}'){
                        
                        commaData += line.split(",",2)[1].charAt(i);
                        i++;
                    }
                    
                    dataArray = commaData.split(",");
                    for(int j = 0; j<dataArray.length;j++){
                        memory.put(address, dataArray[j]);
                        address +=4;
                        
                    }
                    
                }
                else{
                    memory.put(address, line.split(",")[1]);
                }
                line = br.readLine();
            }
            pc = Integer.parseInt(br.readLine().substring(2),16);
            line = br.readLine();
            String inst;
            String operands;
            int instAddress = pc;
            while(line != null){
                
                inst = line.split(" ")[0];
                operands = line.split(" ")[1];
                
                if(inst.equalsIgnoreCase("add") || inst.equalsIgnoreCase("sub") || inst.equalsIgnoreCase("and") || inst.equalsIgnoreCase("nor") || inst.equalsIgnoreCase("slt") || inst.equalsIgnoreCase("sltu")){
                    
                    inst = "000000";
                    inst += regNum(operands.split(",")[1])+regNum(operands.split(",")[2])+regNum(operands.split(",")[0])+"00000";
                    switch(line.split(" ")[0]){
                        case "add":
                        case "ADD":
                        case "Add":inst+="100000";System.out.println("ana hena");break;
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
                    inst += "00000"+regNum(operands.split(",")[1])+regNum(operands.split(",")[0]);
                    if((Integer.toBinaryString(Integer.parseInt(operands.split(",")[2])) +"" ).length() < 5){
                        String shift = Integer.toBinaryString(Integer.parseInt(operands.split(",")[2]));
                        while(shift.length() < 5){
                            shift = "0"+shift;
                        }
                        inst+=shift;
                    }
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
                    
                    inst = "001000"+regNum(operands.split(",")[1])+regNum(operands.split(",")[0])+toNBinaryString(Integer.parseInt(operands.split(",")[2]),16);
                }
                else if(inst.equalsIgnoreCase("lw") || inst.equalsIgnoreCase("lb") || inst.equalsIgnoreCase("lbu") || inst.equalsIgnoreCase("sw") || inst.equalsIgnoreCase("sb") ){
                    
                    String[] offsetRs = loadStoreOA(operands.split(",")[1]);
                    inst = regNum(offsetRs[1])+regNum(operands.split(",")[0])+toNBinaryString(Integer.parseInt(offsetRs[0]), 16);
                    switch(line.split(" ")[0]){
                        case "lw":
                        case "LW":
                        case "Lw":inst = "100011"+inst;break;
                        case "lb":
                        case "LB":
                        case "Lb":inst = "100000"+inst;break;
                        case "lbu":
                        case "LBU":
                        case "Lbu":inst = "100100"+inst;break;
                        case "sw":
                        case "SW":
                        case "Sw":inst = "101011"+inst;break;
                        case "sb":
                        case "SB":
                        case "Sb":inst = "101000"+inst;break;
                            
                            
                            
                    }
                }
                else if(inst.equalsIgnoreCase("lui")){
                    inst = "001111" + "00000"+ regNum(operands.split(",")[0])+toNBinaryString(Integer.parseInt(operands.split(",")[1]), 16);
                    
                }
                else if(inst.equalsIgnoreCase("beq") || inst.equalsIgnoreCase("bne")){
                    inst = regNum(operands.split(",")[0])+regNum(operands.split(",")[1])+toNBinaryString(Integer.parseInt(operands.split(",")[2]), 16);
                    switch(line.split(" ")[0]){
                        case "beq":
                        case "BEQ":
                        case "Beq":inst = "000100"+inst;break;
                        case "bne":
                        case "BNE":
                        case "Bne":inst = "000101"+inst;break;
                    }
                    
                }
                else if(inst.equalsIgnoreCase("j") || inst.equalsIgnoreCase("jal")){
                    inst = toNBinaryString(Integer.parseInt(operands), 26);
                    switch(line.split(" ")[0]){
                        case "j":
                        case "J": inst = "000010"+inst;break;
                        case "jal":
                        case "JAL":
                        case "Jal":	inst = "000011"+inst;break;
                    }
                }
                else if(inst.equalsIgnoreCase("jr")){
                    inst = "000000"+regNum(operands)+"00000"+"00000"+"00000"+"001000";
                }
                //supporting pseudo instructions
                else if(inst.equalsIgnoreCase("move")){
                    //move instruction gets translated to one add instruction
                    inst ="000000"+regNum(operands.split(",")[1])+regNum("$0")+regNum(operands.split(",")[0])+"00000"+"100000";
                }
                else if(inst.equalsIgnoreCase("blt")){
                    //blt instruction gets translated to two instructions : slt and bne
                    /*blt $rs,$rt,C
                     1) slt $at,$rs,$rt
                     2) bne $at,$0,C
                     */
                    //converting first to slt
                    inst = "000000"+regNum(operands.split(",")[0])+regNum(operands.split(",")[1])+regNum("$at")+"00000"+"101010";
                    memory.put(instAddress, inst);
                    instAddress +=4;
                    //second part --> bne
                    inst = "000101"+regNum("$at")+regNum("$0")+toNBinaryString(Integer.parseInt(operands.split(",")[2]), 16);
                }	
                memory.put(instAddress, inst);
                instAddress +=4;
                line = br.readLine();
            }
            endAddress = instAddress-4;
        }
        br.close();
    }	
    public static String regNum(String reg){
        
        switch(reg){
                
            case "$0":
            case "$zero": reg = "00000";break;
            case "$at": reg = "00001";break;
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
    public static String toNBinaryString(int num , int n){
        String binary;
        if(num<0){
            binary = Integer.toBinaryString(num).substring(16);
            
        }
        else{
            
            binary = Integer.toBinaryString(num);
            while(binary.length()<n){
                binary = "0"+binary;
            }
        }
        return binary;
        
        
    }
    public static String[] loadStoreOA(String string){
        String off="";
        String reg="";
        String tmp;
        int i = 0;
        while(string.charAt(i) != '('){
            
            off +=string.charAt(i);
            i++;
        }
        tmp = string.substring(i+1);
        int j = 0;
        while(tmp.charAt(j)!=')'){
            reg +=tmp.charAt(j);
            j++;
        }
        String[] output = {off,reg};
        return output;
        
        
    }
    public static void main(String[]args) throws IOException{
        String x = "prog1";
        File file = new File(x);
        Assembler a = new Assembler(file);
        
        Simulator s = new Simulator(a.memory,a.pc,a.endAddress);
        s.fetch();
        
    }
    
}



