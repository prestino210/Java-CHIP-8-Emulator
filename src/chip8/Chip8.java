package chip8;

import java.awt.Color;

import chip.CPU;

public class Chip8 {
	// Emulator settings
	public static final String FONT_FILE = "CHIP-8 Fontset";
	
	// ROM
	public static String ROM_FILE;
	
	// Internal clocks
	public static final int CPU_HZ = 900;
	public static final int DISPLAY_HZ = 60;
	public static final int TIMER_HZ = 60;
	
	// Display
	public static String WINDOW_TITLE;
	public static final int DISPLAY_SCALE = 3;
	public static final Color PIXEL_OFF = new Color(0,0,0);
	public static final Color PIXEL_ON = new Color(255,255,255);
	
	public Chip8(String romFile) {
		ROM_FILE = romFile;
		WINDOW_TITLE = ROM_FILE + " | CHIP-8 Emulator";
		new CPU(new EmulatorWindow());
	}

}
