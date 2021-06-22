package de.thkoeln.ra.team3.risc_thk_simulator.simuCore;

import java.io.File;
import java.io.IOException;
import java.util.List;

import de.thkoeln.ra.team3.risc_thk_simulator.util.MyReader;

public class Memory {

	private int[] memContent;
	private static final int MEMSIZE = 65536;
	private int initMemSize;
	
	public Memory() {
		resetMemory();
	}
	
	private void resetMemory() {
		memContent = new int[MEMSIZE];
		initMemSize = 0;
	}
	
	public int readMem(int addr) {
		return memContent[addr];
	}
	
	public void writeMem(int addr, int value) {
		memContent[addr] = value;
	}
	
	public boolean init(File file) {		
		try {
			resetMemory();
			List<String> instructions = MyReader.readFileIntoList(file);
			for (int i = 0; i < instructions.size(); i++) {
				//java's weirdness with parsing full sized ints
				long parse_tmp = Long.parseLong(instructions.get(i),2);
				
				memContent[i] = (int)(0xFFFFFFFF & parse_tmp);
				/*if((memContent[i]&(~0xFFFFFFFF)) != 0) {
					System.err.println("OH NOES!!");
					System.err.println(instructions.get(i));
					System.err.println(parse_tmp);
					System.err.println(memContent[i]);
				}*/
			}
			initMemSize = instructions.size();
		} catch (IOException e) {
//			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public int getInitMemSize() {
		return initMemSize;
	}
}
