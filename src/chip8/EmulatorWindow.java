package chip8;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class EmulatorWindow {
	JTextArea debugText;
	public int pc;
	public int index;
	public int delay;
	public int sound;
	public int[] stack = new int[16];
	public int stackPointer;
	public int storeKey;
	public int[] vRegisters;
	public long lastCpuRef;
	public long lastDisplayRef;
	public long lastTimerRef;
	public String mnemonic;
	public String cpuMessage;
	public int[] memory = new int[17];
	public int[] indexD = new int[17];
	public int[] font = new int[80]; // 0xF
	public ArrayList<Integer> keysPressed = new ArrayList<Integer>();
	public String state;
	public String errorMessage;
	public JFrame frame;

	public EmulatorWindow() {
		createWindow();
	}

	public void createWindow() {
		frame = new JFrame("CHIP-8 Emulator Window | Debugger");
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

		frame.setSize(400, 500); // Standard paper size (roughly)
		frame.setLocationRelativeTo(null);
		debugText = new JTextArea();

		debugText.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));
		debugText.setEditable(false);

		JScrollPane scrollPane = new JScrollPane(debugText);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.getVerticalScrollBar().setUnitIncrement(16);

		frame.add(scrollPane);
		frame.setResizable(false);
		frame.setAlwaysOnTop(true);
		frame.setVisible(true);
	}

	public void update() {
		
		//architecture information / specifications
		//opcodes console
		//history of opcodes
		//display screen buffer (raw pixels)
		//change emulator settings
		//save and load emulator state
		//rom dump
		//memory dump
		//internal clocks
		//sprite data
		//pause cpu
		//memory access (what is being written and read)
		//cpu reading or writing state
		
		//change to JText fields so that we only have to update parts for efficiency and speed
		
		StringBuilder sb = new StringBuilder();
		sb.append("EMULATION\n");
		sb.append("------------------------------------------\n");
		sb.append("ALLOCATED MEMORY: 0x1000 / 4096 KB addressable RAM\n");
		sb.append("ROM LOCATION: 0x200 / 512\n");
		sb.append("FONT LOCATION: 0x0 / 0\n");
		sb.append(String.format("PC: 0x%03X / %d\n", pc, pc));
		sb.append(String.format("I:  0x%03X / %d\n", index, index));
		sb.append("STATE: " + state + "\n");
		sb.append("CPU MESSAGE: " + cpuMessage + "\n");
		sb.append("ERROR MESSAGE: " + errorMessage + "\n");
		sb.append("OPCODE: " + mnemonic + "\n"); // You can replace this with actual decode
		sb.append("DELAY TIMER: ").append(delay).append("\n");
		sb.append("SOUND TIMER: ").append(sound).append("\n");
		sb.append(String.format("STORE KEY:  0x%03X\n", storeKey));
		sb.append("------------------------------------------\n");

		sb.append("V REGISTERS\n");
		sb.append("------------------------------------------\n");
		for (int i = 0; i < 16; i++) {
			sb.append(String.format("V%X: 0x%02X  ", i, vRegisters[i]));
			if ((i + 1) % 4 == 0)
				sb.append("\n");
		}
		sb.append("------------------------------------------\n");
		sb.append("STACK\n");
		sb.append("------------------------------------------\n");
		sb.append(String.format("STACK POINTER: 0x%03X / %d\n", stackPointer, stackPointer));
		for (int i = 0; i < 16; i++) {
			if (i == stackPointer) {
				sb.append(String.format(">> 0x%03X / %d <<\n", stack[i],stack[i]));
			} else {
				sb.append(String.format("   0x%03X / %d\n", stack[i], stack[i]));
			}
		}
		sb.append("------------------------------------------\n");
		sb.append("MEMORY DUMP\n");
		sb.append("------------------------------------------\n");
		for (int i = 0; i < 17; i++) {
			if (i == 8) {
				sb.append(String.format(">> 0x%04X <<\n", memory[i]));
			} else {
				sb.append(String.format("   0x%04X\n", memory[i]));
			}
		}
		sb.append("------------------------------------------\n");
		sb.append("INDEX DUMP\n");
		sb.append("------------------------------------------\n");
		for (int i = 0; i < 17; i++) {
			if (i == 8) {
				sb.append(String.format(">> 0x%04X <<\n", indexD[i]));
			} else {
				sb.append(String.format("   0x%04X\n", indexD[i]));
			}
		}
		sb.append("------------------------------------------\n");
		sb.append("FONT DATA\n");
		sb.append("------------------------------------------\n");

		// font data
		int counter = 0;
		int character = 0;
		sb.append("0x0\n");
		for (int i = 0; i < 80; i++) {

			
			String binary = String.format("%8s", Integer.toBinaryString(font[i])).replace(' ', '0');
		    sb.append(String.format("   0x%02X / %s\n", font[i], binary));
		    
			counter++;
			if(counter == 5 & i < 79) {
				counter = 0;
				character++;
				sb.append("\n");
				sb.append("0x" + Integer.toHexString(character) + "\n");
			}
			
		}

		sb.append("------------------------------------------\n");
		sb.append("KEYPAD\n");
		sb.append("------------------------------------------\n");
		int[] keys = { 1, 2, 3, 0xC, 4, 5, 6, 0xD, 7, 8, 9, 0xE, 0xA, 0x0, 0xB, 0xF };
		for (int i = 0; i < keys.length; i++) {
			if (i % 4 == 0 && i != 0)
				sb.append("\n");

			boolean pressed = false;
			for (int k : keysPressed) {
				if (k == keys[i])
					pressed = true;
			}

			if (pressed) {
				sb.append("[").append(Integer.toHexString(keys[i])).append("] ");
			} else {
				sb.append(" ").append(Integer.toHexString(keys[i])).append("  ");
			}
		}

		debugText.setText(sb.toString());
	}

}
