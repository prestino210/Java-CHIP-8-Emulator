package loader;

import java.io.FileInputStream;
import chip.CPU;
import chip.Memory;

public class Loader {
	CPU cpu;
	
	public Loader(CPU controller) {
		cpu = controller;
	}
	
	public void LoadROM(String ROM_FILE) { 
		byte[] copiedROMData = null;
		try {
			FileInputStream ROMDataReader = new FileInputStream("./res/ROMs/" + ROM_FILE);
			copiedROMData = ROMDataReader.readAllBytes();
			ROMDataReader.close();
			
		} catch(Exception e) {
			cpu.error("Failed to read ROM: " + ROM_FILE);
		}
		
		if(copiedROMData.length <= Memory.MEMORY_SIZE - cpu.ROM_LOCATION) {
			for(int i = 0; (i < Memory.MEMORY_SIZE) && (i < copiedROMData.length); i++) {
				Memory.write8(i + cpu.ROM_LOCATION, copiedROMData[i]);
			}
		} else {
			cpu.error("ROM size too wide for allocated ROM space");
		}
		
	}
}
