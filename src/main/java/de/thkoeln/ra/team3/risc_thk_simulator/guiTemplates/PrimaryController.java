package de.thkoeln.ra.team3.risc_thk_simulator.guiTemplates;

import java.io.File;
import java.io.IOException;

import de.thkoeln.ra.team3.risc_thk_simulator.simuCore.Instruction;
import de.thkoeln.ra.team3.risc_thk_simulator.simuCore.Memory;
import de.thkoeln.ra.team3.risc_thk_simulator.simuCore.Register;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
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
	
	private static final int pcStep = 1;
	
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
		}
	}

	@FXML
    private void chooseFile(Event event) throws IOException {
    	File rscFile = fc.showOpenDialog(((Node) event.getSource()).getScene().getWindow());
    	if(rscFile == null) return;
    	register = new Register();
    	memory = new Memory();
    	updateRegister();
    	if(!memory.init(rscFile)) {
    		System.out.println("Error reading file");
    	} else {  
    		codeArea.getChildren().clear();
    		for(int i = 0; i < memory.getInitMemSize(); i++) {
    			System.out.println(memory.readMem(i));
    			String addr = Integer.toHexString(i);
    			while(addr.length() != 8) {
    				addr = "0"+addr;
    			} addr = "0x"+addr;
    			InstructionEntry entry = new InstructionEntry(addr, new Instruction(memory.readMem(i)));
    			codeArea.getChildren().add(entry.buildHBox());
    		}
    		
    	}
    }
	
	@FXML
	private void execute() { //TODO find out when a program is ended to run till this point
		
		for (int i = 0; i < 100; i++) {
			executeInstruction();
			System.out.println();
		}
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Completed");
		alert.setHeaderText(null);
		alert.setContentText("100 Steps Completed!");
		alert.showAndWait();
	}
    
    @FXML
    private void executeInstruction() { //one step
    	register.step(pcStep); //increment PC by 1
    	if(register.getPc() > memory.getInitMemSize()) {
    		System.out.println("PC exceeds memory");
    		return; //dont step if there is no more isntructions
    	}
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
    	System.out.println("runToNextBreakpoint");
    }

	private void nop() {
		//No Operation		
	}

	private void ret() {
		register.setPc((int) register.readReg(31));		
	}

	private void call() {
		register.writeReg(31, register.getPc());
		register.setPc(register.getPc() + instruction.getOffset());
		
	}

	private void ble() {
		if (register.readReg(instruction.getR1()) <= register.readReg(instruction.getRd())) {
            register.setPc(register.getPc() + instruction.getOffset());            
        } 
	}

	private void stg() {
		memory.writeMem(register.readReg(instruction.getR1()) + instruction.getOffset(), register.readReg(instruction.getR2()));
    }

	private void adc() {
	    register.writeReg(instruction.getRd(), register.readReg(instruction.getR1()) + instruction.getOffset());
	}

	private void load() {
		register.writeReg(instruction.getRd(), (int)memory.readMem(register.readReg(instruction.getR1()) + instruction.getOffset()));
    }

	private void slt() {
	    register.writeReg(instruction.getRd(), (register.readReg(instruction.getR1()) < register.readReg(instruction.getR2()))?1:0);
	}

	private void shr() {
	    register.writeReg(instruction.getRd(), register.readReg(instruction.getR1()) << 1);
	}

	private void shl() {
	    register.writeReg(instruction.getRd(), register.readReg(instruction.getR1()) >> 1);
	}

	private void xor() {
	    register.writeReg(instruction.getRd(), register.readReg(instruction.getR1()) ^ register.readReg(instruction.getR2()));
	}

	private void or() {
		register.writeReg(instruction.getRd(), register.readReg(instruction.getR1()) | register.readReg(instruction.getR2()));	    
	}

	private void and() {
		register.writeReg(instruction.getRd(), register.readReg(instruction.getR1()) & register.readReg(instruction.getR2()));   
	}

	private void sub() {
		register.writeReg(instruction.getRd(), register.readReg(instruction.getR1()) - register.readReg(instruction.getR2()));		
	}

	private void add() {
		 register.writeReg(instruction.getRd(), register.readReg(instruction.getR1()) + register.readReg(instruction.getR2()));		
	}
}
