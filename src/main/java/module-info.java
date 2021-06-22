module de.thkoeln.ra.team3.risc_thk_simulator {
    requires javafx.controls;
    requires javafx.fxml;
	requires javafx.base;
	requires javafx.graphics;

    opens de.thkoeln.ra.team3.risc_thk_simulator.guiTemplates to javafx.fxml;
    exports de.thkoeln.ra.team3.risc_thk_simulator;
}
