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
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.collections.FXCollections;
import javafx.collections.ObservableArray;
import javafx.event.ActionEvent;
import javafx.event.Event;

public class PrimaryController {
	
	@FXML
	VBox codeArea;
	
	@FXML
	VBox regArea;
	
	@FXML
	Pane memoryArea;
	
	@FXML
	TextField viewAddress;
	
	Label addrLabels[];
	Label contLabels[];
	
	int viewerOffset = 0;
	
	
	Set<Integer> breakpoints;
	
	private static final int DEFAULT_STEP_INCREMENT = 1;
	private int lastUpdatedRegister = -1;
	private int lastUpdatedMemoryLocation = -1;
	
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
    		updateMemoryView();
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
		updateMemoryView();
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Completed");
		alert.setHeaderText(null);
		alert.setContentText("100 Steps Completed!");
		alert.showAndWait();
	}
    
	@FXML
	private void offsetKeyPressed(KeyEvent ke){
		if(ke.getCode() == KeyCode.ENTER) {
			String str = viewAddress.getText();
			int offset_new;
			try {
				if(str.charAt(0) == '0' && Character.toLowerCase(str.charAt(1)) == 'x' ) {
					offset_new = Integer.parseInt(str.substring(2), 16);
				}else {
					offset_new = Integer.parseInt(str, 16);
				}
			}catch(NumberFormatException nfe) {
				System.out.println("pls no");
				offset_new = viewerOffset;
			}
			viewNewAddress(offset_new);
		}
	}
	
    @FXML
    private void executeInstruction() { //one step
    	lastUpdatedMemoryLocation = -1;
    	
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
    	updateLastWord();
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
    	/*int pc = register.getPc();
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
    	*/
    	int runCounter = 0;
    	//while(register.getPc() != nextBreakpoint+1) {
    	do {
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
    	}while(!breakpoints.contains(register.getPc()));
    	updateMemoryView();
    	
    }

    @FXML
    private void addressInc32() {
    	viewNewAddress(viewerOffset + 32);
    }
    
    @FXML
    private void addressDec32() {
    	viewNewAddress(viewerOffset - 32);
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
		int addr = (register.readReg(instruction.getR1()) + instruction.getOffset());
		memory.writeMem(addr, register.readReg(instruction.getR2()));
		lastUpdatedMemoryLocation = addr;
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

	public void genMemoryView() {
		addrLabels = new Label[32];
		contLabels = new Label[32];
		
		
		for( int i = 0; i < 32; i++) {
			Label label_new;
			label_new = new Label("offset: " + Integer.toHexString(i));
			label_new.setLayoutX(8.0);
			label_new.setLayoutY(66 + i*28);
			label_new.setPrefHeight(18);
			label_new.setPrefWidth(92);
			label_new.setStyle("-fx-text-fill: #262328;-fx-background-color: #e4a67a;");
			addrLabels[i] = label_new;
			memoryArea.getChildren().add(label_new);

			
			
			
			label_new = new Label("memory at: " + Integer.toHexString(i));
			label_new.setLayoutX(128.0);
			label_new.setLayoutY(66 + i*28);
			label_new.setPrefHeight(18);
			label_new.setPrefWidth(110);
			label_new.setStyle("-fx-text-fill: #e4a67a;-fx-background-color: #262328;");
			contLabels[i] = label_new;
			memoryArea.getChildren().add(label_new);
			
			/*<ListView layoutX="120.0" layoutY="60.0" prefHeight="30.0" prefWidth="135.0" style="-fx-background-color: #262328;" />
            <Label layoutX="128.0" layoutY="66.0" prefHeight="18.0" prefWidth="110.0" style="-fx-text-fill: #e4a67a;" text="0x00000000" />
            <ListView layoutY="90.0" prefHeight="30.0" prefWidth="121.0" style="-fx-background-color: #e4a67a;" />
            */
		}
		
		viewAddress.setText(String.format("0x%08x", viewerOffset));
		updateMemoryView();
		
	}
	private void updateMemoryView() {
		int addr;
		for(int i = 0; i < 32; i++) {
			addr = viewerOffset + i;
			addrLabels[i].setText(String.format("0x%08x", addr));
			contLabels[i].setText(String.format("0x%08x", memory.readMem(addr)));
		}
	}
	private void updateLastWord() {
		int addr = lastUpdatedMemoryLocation;
		if(addr >= viewerOffset && addr < viewerOffset+32)
			contLabels[addr-viewerOffset].setText(String.format("0x%08x", memory.readMem(addr)));
	}
	private void viewNewAddress(int offset_new) {
		if(offset_new < 0) {
			offset_new = 0;
		}
		viewerOffset = offset_new;
		viewAddress.setText(String.format("0x%08x", viewerOffset));
		updateMemoryView();
	}
}
