package de.thkoeln.ra.team3.risc_thk_simulator.simuCore;

public class Register {
	public static final int REGSIZE = 32;
	private int[] reg;
	private int pc;
	
	public Register() {
		reg = new int[REGSIZE];
		pc = 0;
		for (int i = 0; i < reg.length; i++) {
			reg[i] = 0;
		}
	}
	
	public void writeReg(int addr, int value) {
		reg[addr] = value;
	}
	
	public int readReg(int addr) {
		return reg[addr];
	}
	
	public void step(int count) {
		pc += count;
	}
	
	public int getPc() {
		return pc;
	}
	
	public void setPc(int pc) {
		this.pc = pc;
	}
	
	@Override
	public String toString() {
		String template = "reg %s \t %s \n";
		String returnValue = "";
		for (int i = 0; i < reg.length; i++) {
			returnValue += String.format(template, ""+i, regToBinary(i));
		}
		return returnValue;
	}

	private String regToBinary(int addr) {
		String value = Integer.toBinaryString(reg[addr]);
		while(value.length() != 32) {
			value = "0"+value;
		}
		return value;
	}
}
