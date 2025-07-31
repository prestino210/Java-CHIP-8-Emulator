package display;

import java.awt.Color;
import chip.CPU;
import chip.Memory;
import chip8.Chip8;

public class GPU {
	public Display display;
	CPU cpu;
	int DISPLAY_WIDTH = 64;
	int DISPLAY_HEIGHT = 32;
	public boolean DRAW_FLAG = false;
	public int[][] DISPLAY = new int[DISPLAY_WIDTH][DISPLAY_HEIGHT];
	Color off = Chip8.PIXEL_OFF;
	Color on = Chip8.PIXEL_ON;
	public long DISPLAY_REF = System.nanoTime();

	public GPU(CPU cpu) {
		this.cpu = cpu;
		display = new Display(cpu, this, DISPLAY_WIDTH, DISPLAY_HEIGHT);
	}

	public void draw_sprite(int x, int y, int rows, int location) { 
		cpu.setV(0xF, 0);
		
		for(int i = 0; i < rows; i++) {
			int row = Memory.readInt8(location + i); 
			for(int j = 0; j < 8; j++) {
				int pixel = CPU.lsb(row >> (7-j));
			
				int sX = (x + j) % DISPLAY_WIDTH;
				int sY = (y + i) % DISPLAY_HEIGHT;
				if(pixel == 1) {
					if(DISPLAY[sX][sY] == 1) {
						cpu.setV(0xF, 1);
					}
					
					DISPLAY[sX][sY] ^= 1;
				}
			}
		}
		
		DRAW_FLAG = true;
	}

	public void clear_display() {
		for (int i = 0; i < DISPLAY_WIDTH; i++) {
			for (int j = 0; j < DISPLAY_HEIGHT; j++) {
				DISPLAY[i][j] = 0;
			}
		}
		updateScreenBuffer();
	}

	public void updateScreenBuffer() {
		if(DRAW_FLAG) {
			long now = System.nanoTime();
			DISPLAY_REF = now;
			cpu.emulatorWindow.lastDisplayRef = now;
			// set all pixels of buffer to all pixels
			for (int i = 0; i < DISPLAY_HEIGHT; i++) {
				for (int j = 0; j < DISPLAY_WIDTH; j++) {
					if (DISPLAY[j][i] == 1) {
						display.screenBuffer.setRGB(j, i, on.getRGB());
					} else {
						display.screenBuffer.setRGB(j, i, off.getRGB());
					}
				}
			}
		}
		
	}

}
