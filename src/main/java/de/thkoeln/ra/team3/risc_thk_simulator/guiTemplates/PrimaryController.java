package de.thkoeln.ra.team3.risc_thk_simulator.guiTemplates;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.thkoeln.ra.team3.risc_thk_simulator.simuCore.Instruction;
import de.thkoeln.ra.team3.risc_thk_simulator.simuCore.Memory;
import de.thkoeln.ra.team3.risc_thk_simulator.simuCore.Register;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.event.Event;

public class PrimaryController {
	
	@FXML
	VBox codeArea;
	
	@FXML
	VBox regArea;
	
	Set<Integer> breakpoints;
	
	private static final int DEFAULT_STEP_INCREMENT = 1;
	private int lastUpdatedRegister = -1;
	
	private FileChooser fc;
	private Memory memory;
	private Register register;
	private Instruction instruction;
	
	public PrimaryController() {
		fc = new FileChooser();		
		
		fc.getExtensionFilters().addAll(
			new FileChooser.ExtensionFilter("RSC Files", "*.rsc")
		);
		
		memory = new Memory();
		register = new Register();
		
		breakpoints = new HashSet<Integer>();
	}

    public void updateRegister() {
    	regArea.getChildren().clear();
    	regArea.getChildren().add(new PC(register.getPc()).buildHBox());
    	for (int i = 0; i < Register.REGSIZE; i++) {
			RegisterEntry tmp = new RegisterEntry(i, register.readReg(i));
			
			if((i % 2) == 0) {
				tmp.getAddress().setStyle("-fx-background-color: #5a89ce");
				tmp.getAddress().setTextFill(Color.web("#ffffff"));
				tmp.getValue().setStyle("-fx-background-color: #5a89ce");
				tmp.getValue().setTextFill(Color.web("#ffffff"));
				
			} else {
				tmp.getAddress().setStyle("-fx-background-color: #d1b5f2");
				tmp.getAddress().setTextFill(Color.web("#000000"));
				tmp.getValue().setStyle("-fx-background-color: #d1b5f2");
				tmp.getValue().setTextFill(Color.web("#000000"));
			}	
			HBox entry = tmp.buildHBox();
			regArea.getChildren().add(entry);
			try {
				regArea.getChildren().get(lastUpdatedRegister+1).setStyle("-fx-border-color: red;");
			} catch (Exception e) {
				//e.printStackTrace(); //Do Nothing
			}
		}
	}

	@FXML
    private void chooseFile(Event event) throws IOException {
    	File rscFile = fc.showOpenDialog(((Node) event.getSource()).getScene().getWindow());
    	if(rscFile == null) return;
    	register = new Register();
    	memory = new Memory();
    	lastUpdatedRegister = -1;
    	updateRegister();
    	if(!memory.init(rscFile)) {
    		System.out.println("Error reading file");
    	} else {  
    		buildCodeArea();    		
    	}
    }

	private void buildCodeArea() {
		codeArea.getChildren().clear();
		for(int i = 0; i < memory.getInitMemSize(); i++) {
			//System.out.println(memory.readMem(i));
			
			InstructionEntry entry = new InstructionEntry(new Instruction(memory.readMem(i)), breakpoints, i);
			codeArea.getChildren().add(entry.buildHBox());
		}
	}
	
	@FXML
	private void execute() throws InterruptedException { //TODO find out when a program is ended to run till this point
		
		for (int i = 0; i < 100; i++) {
			executeInstruction();			
		}
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Completed");
		alert.setHeaderText(null);
		alert.setContentText("100 Steps Completed!");
		alert.showAndWait();
	}
    
    @FXML
    private void executeInstruction() { //one step
//    	System.out.println("PC: "+register.getPc());
    	register.step(DEFAULT_STEP_INCREMENT); //increment PC by 1    	
    	if(register.getPc() > memory.getInitMemSize()) {
    		System.out.println("PC exceeds memory");
    		Alert alert = new Alert(AlertType.INFORMATION);
    		alert.setTitle("PC exceeds memory");
    		alert.setHeaderText(null);
    		alert.setContentText("Programm Counter exceeds Memory");
    		alert.showAndWait();
    		return; //dont step if there is no more isntructions
    	}
    	
    	buildCodeArea();
    	codeArea.getChildren().get(register.getPc()-1).setStyle("-fx-border-color: red;");
    	
    	instruction = new Instruction(memory.readMem(register.getPc()));
    	switch (instruction.getOpc()) {
    	case 0b000001:      add();
        	break;
		case 0b000010:      sub();
		    break;
		case 0b000011:      and();
		    break;
		case 0b000100:      or();
		    break;
		case 0b000101:      xor();
		    break;
		case 0b000110:      shl();
		    break;
		case 0b000111:      shr();
		    break;
		case 0b001000:      slt();
		    break;
		case 0b010000:      load();
		    break;
		case 0b010001:      adc();
		    break;
		case 0b010010:      stg();
		    break;
		case 0b011000:      ble();
		    break;
		case 0b011100:      call();
		    break;
		case 0b011101:      ret();
		    break;
		default:            nop();
			break;
		}
    	//System.out.println("PC: " + register.getPc());    	
    	updateRegister();
    }
    
    @FXML
    private void restart(Event event) {
    	System.out.println("restart");
    }
    @FXML
    private void runToNextBreakpoint(Event event) {
    	//System.out.println("runToNextBreakpoint");
    	if(breakpoints.size() <= 0) {
    		Alert alert = new Alert(AlertType.INFORMATION);
    		alert.setTitle("No Breakpoints");
    		alert.setHeaderText(null);
    		alert.setContentText("No Breakpoints Selected!");
    		alert.showAndWait();
    		return;
    	}
    	int pc = register.getPc();
    	List<Integer> sortedList = new ArrayList<>(breakpoints);
    	Collections.sort(sortedList);
    	int nextBreakpoint = -1;
    	int start = 0, end = sortedList.size()-1;
    	while(start <= end) {
    		int mid = (start+end) / 2;
    		if(sortedList.get(mid) <= pc) {
    			start = mid +1;
    		} else {
    			nextBreakpoint = mid;
    			end = mid -1;
    		}
    	}
    	if(nextBreakpoint >= 0) {
    		nextBreakpoint = sortedList.get(nextBreakpoint);
    	} else {
    		nextBreakpoint = sortedList.get(0);
    	}
    	System.out.println("next breakpoint: " + nextBreakpoint);
    	int runCounter = 0;
    	while(register.getPc() != nextBreakpoint+1) {
    		executeInstruction();
    		runCounter++;
    		if(runCounter>100) {
    			Alert alert = new Alert(AlertType.INFORMATION);
        		alert.setTitle("Breakpoint not reached");
        		alert.setHeaderText(null);
        		alert.setContentText("Ran 100 steps.\nBreakpoint not reached!\nPossible Endless Loop");
        		alert.showAndWait();
    			break;
    		}
    	}
    }

	private void nop() {
		//No Operation		
	}

	private void ret() {
		register.setPc((int) register.readReg(31));		
	}

	private void call() {
		lastUpdatedRegister = 31;
		register.writeReg(31, register.getPc());
		register.setPc(register.getPc() + instruction.getOffset());
		
	}

	private void ble() {
		if (register.readReg(instruction.getR1()) <= register.readReg(instruction.getRd())) {
            register.setPc(register.getPc() + instruction.getOffset());            
        } 
	}

	private void stg() {
		memory.writeMem((register.readReg(instruction.getR1()) + instruction.getOffset()), register.readReg(instruction.getR2()));
    }

	private void adc() {
		lastUpdatedRegister = instruction.getRd();
	    register.writeReg(instruction.getRd(), register.readReg(instruction.getR1()) + instruction.getOffset());
	}

	private void load() {
		lastUpdatedRegister = instruction.getRd();
		register.writeReg(instruction.getRd(), (int)memory.readMem((register.readReg(instruction.getR1()) + instruction.getOffset())));
    }

	private void slt() {
		lastUpdatedRegister = instruction.getRd();
	    register.writeReg(instruction.getRd(), (register.readReg(instruction.getR1()) < register.readReg(instruction.getR2()))?1:0);
	}

	private void shr() {
		lastUpdatedRegister = instruction.getRd();
	    register.writeReg(instruction.getRd(), register.readReg(instruction.getR1()) << 1);
	}

	private void shl() {
		lastUpdatedRegister = instruction.getRd();
	    register.writeReg(instruction.getRd(), register.readReg(instruction.getR1()) >> 1);
	}

	private void xor() {
		lastUpdatedRegister = instruction.getRd();
	    register.writeReg(instruction.getRd(), register.readReg(instruction.getR1()) ^ register.readReg(instruction.getR2()));
	}

	private void or() {
		lastUpdatedRegister = instruction.getRd();
		register.writeReg(instruction.getRd(), register.readReg(instruction.getR1()) | register.readReg(instruction.getR2()));	    
	}

	private void and() {
		lastUpdatedRegister = instruction.getRd();
		register.writeReg(instruction.getRd(), register.readReg(instruction.getR1()) & register.readReg(instruction.getR2()));   
	}

	private void sub() {
		lastUpdatedRegister = instruction.getRd();
		register.writeReg(instruction.getRd(), register.readReg(instruction.getR1()) - register.readReg(instruction.getR2()));		
	}

	private void add() {
		lastUpdatedRegister = instruction.getRd();
		register.writeReg(instruction.getRd(), register.readReg(instruction.getR1()) + register.readReg(instruction.getR2()));		
	}
}
