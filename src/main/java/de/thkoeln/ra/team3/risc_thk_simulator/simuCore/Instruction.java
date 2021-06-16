package de.thkoeln.ra.team3.risc_thk_simulator.simuCore;

public class Instruction {

	private long ir;
	private int opc;
	private int r1;
	private int r2;
	private int rd;
	private int offset;

	public Instruction(long instruction) {
		this.ir = instruction;
		opc = (int) instruction & 0x3F;
		instruction = instruction >>> 6;
		r1 = (int) instruction & 0x1F;
		instruction = instruction >>> 5;
		rd = (int) instruction & 0x1F;
		instruction = instruction >>> 5;
		if (opc < 16) {
			r2 = (int) instruction & 0x1F;
			offset = 0;

		} else {
			r2 = rd;
			if ((instruction & 0x8000) != 0) {
				offset = (int) (instruction & 0x7fff) - 32768;
			} else {
				offset = (int) instruction;
			}
		}
	}

	public long getIr() {
		return ir;
	}

	public int getOpc() {
		return opc;
	}

	public int getR1() {
		return r1;
	}

	public int getR2() {
		return r2;
	}

	public int getRd() {
		return rd;
	}

	public int getOffset() {
		return offset;
	}

	private String getOffStr() {
		if (offset < 0) {
			return " " + offset;
		} else {
			return " +" + offset;
		}
	}

	public String toString() {
		String ret;
		switch (opc) {
		case 0b000001:
			ret = "add r" + rd + ", r" + r1 + ", r" + r2;
			break;
		case 0b000010:
			ret = "subtract r" + rd + ", r" + r1 + ", r" + r2;
			break;
		case 0b000011:
			ret = "and r" + rd + ", r" + r1 + ", r" + r2;
			break;
		case 0b000100:
			ret = "or r" + rd + ", r" + r1 + ", r" + r2;
			break;
		case 0b000101:
			ret = "xor r" + rd + ", r" + r1 + ", r" + r2;
			break;
		case 0b000110:
			ret = "shift right r" + r1;
			break;
		case 0b000111:
			ret = "shift left r" + r1;
			break;
		case 0b001000:
			ret = "set if less than r" + rd + ", r" + r1 + ", r" + r2;
			break;
		case 0b010000:
			ret = "load register r" + rd + ", r" + r1 + getOffStr();
			break;
		case 0b010001:
			ret = "add const r" + rd + ", r" + r1 + getOffStr();
			break;
		case 0b010010:
			ret = "store register r" + r2 + ", r" + r1 + getOffStr();
			break;
		case 0b011000:
			ret = "brach less equal r" + rd + ", r" + r1 + "," + getOffStr();
			break;
		case 0b011100:
			ret = "call" + getOffStr();
			break;
		case 0b011101:
			ret = "return";
			break;
		default:
			ret = "illegal OPC";
		}
		return ret;
	}

}
