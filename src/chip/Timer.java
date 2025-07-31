package chip;

public class Timer {
	CPU cpu;
	public int DELAY_TIMER = 0;
	public int SOUND_TIMER = 0;

	public Timer(CPU cpu) {
		this.cpu = cpu;
	}

	public void updateTimers() {
		if (SOUND_TIMER > 0) {
			SOUND_TIMER--;
			// play beep
			// write audio system
			// System.out.println("beep");
		}
		if (DELAY_TIMER > 0) {
			DELAY_TIMER--;
		}
		
	}
	
	public void updateTimersDump() {
		cpu.emulatorWindow.delay = DELAY_TIMER;
		cpu.emulatorWindow.sound = SOUND_TIMER;
	}

	public void set_delayT(int value) {
		DELAY_TIMER = value;
	}

	public void set_soundT(int value) {
		SOUND_TIMER = value;
	}

	public int get_delayT() {
		return DELAY_TIMER;
	}

	public int get_soundT() {
		return SOUND_TIMER;
	}
}
