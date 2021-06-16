package de.thkoeln.ra.team3.risc_thk_simulator.guiTemplates;

import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;

public class PC {

	private Label str;
	private Label pc;
	
	public PC(int pc) {
		this.str = new Label("PC:");
		this.pc = new Label(""+pc);
		
		setLayout();
	}
	
	private void setLayout() {
		str.setMinSize(30, 30);
		str.setStyle("-fx-background-color: #000000");
		str.setTextFill(Color.web("#e4a67a"));
		pc.setMinSize(220, 30);
		pc.setStyle("-fx-background-color: #000000");
		pc.setTextFill(Color.web("#e4a67a"));
		
	}
	
	public HBox buildHBox() {
		return new HBox(20, str, pc); 
	}
}
