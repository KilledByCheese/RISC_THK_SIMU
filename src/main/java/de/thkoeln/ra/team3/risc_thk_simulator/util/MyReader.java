package de.thkoeln.ra.team3.risc_thk_simulator.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MyReader {

	public static List<String> readFileIntoList(File fileLocation) throws IOException {
		List<String> returnList = new ArrayList<String>();
		BufferedReader br;
		try {
			
			br = new BufferedReader(new FileReader(fileLocation));
			String line = br.readLine();
			while(line != null) {
				returnList.add(line);
				
				line = br.readLine();
			}
			br.close();
		} catch (IOException e) {
			throw e;
		}
				
		return returnList;
	}
}
