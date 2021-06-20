package de.thkoeln.ra.team3.risc_thk_simulator.guiTemplates;


import java.util.Set;

import de.thkoeln.ra.team3.risc_thk_simulator.simuCore.Instruction;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;

public class InstructionEntry {

	CheckBox breakpoint;	
	Label ir;
	Label operation;
	
	public InstructionEntry(Instruction instruction, Set<Integer> breakpoints, int addr) {
		String memAddr = Integer.toHexString(addr);
		while(memAddr.length() != 8) {
			memAddr = "0"+memAddr;
		} memAddr = "0x"+memAddr;
		breakpoint = new CheckBox(memAddr);
		
		String irHex = Long.toHexString(instruction.getIr());
		while(irHex.length() != 8) {
			irHex = "0"+irHex;
		}
		ir = new Label("0x"+irHex);
		
		if(breakpoints.contains(addr)) breakpoint.setSelected(true);
		operation = new Label(instruction.toString());
		
		setLayout();
		setBreakpointListener(addr,  breakpoints);
	}
	
	private void setBreakpointListener(int addr, Set<Integer> breakpoints) {
		breakpoint.setOnAction(event -> {
			if(breakpoint.isSelected()) {
				breakpoints.add(addr);
			} else {
				breakpoints.remove(addr);
			}
//			for (Integer integer : breakpoints) {
//				System.out.println(integer);
//			}
		});
	}
	
	private void setLayout() {
		breakpoint.setMinSize(150, 18);
		breakpoint.setTextFill(Color.web("#2bdba9"));
		
		
		ir.setMinSize(150, 18);
		ir.setTextFill(Color.web("#2bdba9"));
		
		operation.setMinSize(200, 18);
		operation.setTextFill(Color.web("#2bdba9"));
	}

	public HBox buildHBox() {
		return new HBox(30, breakpoint, ir, operation);
	}
}
