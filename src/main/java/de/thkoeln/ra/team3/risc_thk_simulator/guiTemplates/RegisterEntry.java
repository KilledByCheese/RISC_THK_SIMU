package de.thkoeln.ra.team3.risc_thk_simulator.guiTemplates;

import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

public class RegisterEntry {

	private Label address;
	private Label value;
	
	public RegisterEntry(int addr, int val) {
		String addrStr = ""+addr;
		while(addrStr.length()!=2) addrStr = "0"+addrStr;
		address = new Label("reg"+addrStr);
		String valueStr = Integer.toBinaryString(val);
		while(valueStr.length() != 32) valueStr = "0"+valueStr;
		value = new Label(valueStr);
		
		setLayout();
	}
	
	private void setLayout() {
		address.setMinSize(30, 30);
				
		value.setMinSize(220, 30);
		
	}
	
	public Label getAddress() {
		return address;
	}
	
	public Label getValue() {
		return value;
	}
	
	public HBox buildHBox() {
		return new HBox(20, address, value); 
	}
		
}
