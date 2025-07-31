package keypad;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.HashMap;

import chip.CPU;

public class KeyPad implements KeyListener {
	// make map of key pad
	CPU cpu;
	int key;
	public ArrayList<Integer> KEY_PRESSES = new ArrayList<Integer>();
	private boolean KEY_PRESSED = false;
	public HashMap<Integer, Boolean> KEY_VALUES = new HashMap<Integer, Boolean>();
	HashMap<Integer, Integer> keyPadMap = new HashMap<Integer, Integer>();

	public KeyPad(CPU cpu) {
		this.cpu = cpu;
		createKeyMap();
	}

	public void createKeyMap() {
		keyPadMap.put(KeyEvent.VK_1, 0x1); // 1
		keyPadMap.put(KeyEvent.VK_2, 0x2); // 2
		keyPadMap.put(KeyEvent.VK_3, 0x3); // 3
		keyPadMap.put(KeyEvent.VK_4, 0xC); // C

		keyPadMap.put(KeyEvent.VK_Q, 0x4); // 4
		keyPadMap.put(KeyEvent.VK_W, 0x5); // 5
		keyPadMap.put(KeyEvent.VK_E, 0x6); // 6
		keyPadMap.put(KeyEvent.VK_R, 0xD); // D

		keyPadMap.put(KeyEvent.VK_A, 0x7); // 7
		keyPadMap.put(KeyEvent.VK_S, 0x8); // 8
		keyPadMap.put(KeyEvent.VK_D, 0x9); // 9
		keyPadMap.put(KeyEvent.VK_F, 0xE); // E

		keyPadMap.put(KeyEvent.VK_Z, 0xA); // A
		keyPadMap.put(KeyEvent.VK_X, 0x0); // 0
		keyPadMap.put(KeyEvent.VK_C, 0xB); // B
		keyPadMap.put(KeyEvent.VK_V, 0xF); // F
		
		KEY_VALUES.put(0x0, false);
		KEY_VALUES.put(0x1, false);
		KEY_VALUES.put(0x2, false);
		KEY_VALUES.put(0x3, false);
		KEY_VALUES.put(0x4, false);
		KEY_VALUES.put(0x5, false);
		KEY_VALUES.put(0x6, false);
		KEY_VALUES.put(0x7, false);
		KEY_VALUES.put(0x8, false);
		KEY_VALUES.put(0x9, false);
		KEY_VALUES.put(0xA, false);
		KEY_VALUES.put(0xB, false);
		KEY_VALUES.put(0xC, false);
		KEY_VALUES.put(0xD, false);
		KEY_VALUES.put(0xE, false);
		KEY_VALUES.put(0xF, false);
	}

	@Override
	public void keyTyped(KeyEvent e) {

	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (keyPadMap.get(e.getKeyCode()) != null) {
			key = keyPadMap.get(e.getKeyCode());
			KEY_VALUES.put(key, true);
			KEY_PRESSED = true;
			if(!KEY_PRESSES.contains(key)) {
				KEY_PRESSES.add(key);
				cpu.emulatorWindow.keysPressed = KEY_PRESSES;
			}
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if (keyPadMap.get(e.getKeyCode()) != null) {
			{
				key = keyPadMap.get(e.getKeyCode());
				KEY_VALUES.put(key, false);
				if (KEY_PRESSES.contains(key)) {
					KEY_PRESSES.remove(KEY_PRESSES.indexOf(key));
					cpu.emulatorWindow.keysPressed = KEY_PRESSES;
				}
				if (KEY_PRESSES.size() == 0) {
					KEY_PRESSED = false;
				}
			}
		}

	}

	public boolean keyPressed() {
		return KEY_PRESSED;
	}

	public void keyPressed_set(boolean status) {
		KEY_PRESSED = status;
	}
}
