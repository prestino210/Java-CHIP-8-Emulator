package chip;

import java.io.BufferedReader;
import java.io.FileReader;
import chip8.Chip8;
import chip8.EmulatorWindow;
import display.GPU;
import keypad.KeyPad;
import loader.Loader;

public class CPU {
	Loader loader;
	public KeyPad keyPad;
	public EmulatorWindow emulatorWindow;
	public GPU graphics;
	public ChipsetInterpreter interpreter;
	public int FONT_LOCATION = 0x0;
	public int ROM_LOCATION = 0x200;
	public int[] V = new int[0x10];
	public int PC = 0x200;
	public int[] STACK = new int[0x10];
	public int SP = 0;
	public int I = 0x200;
	public int STORE_KEY = 0x0;
	
	public int CYCLES = 0;
	public static CPUState STATE = CPUState.RUNNING;
	int cpuRefRate = Chip8.CPU_HZ;
	int displayRefRate = Chip8.DISPLAY_HZ;
	int timerRefRate = Chip8.TIMER_HZ;
	public long CPU_REF = System.nanoTime();
	public long TIMER_REF = System.nanoTime();
	public Timer timer;
	public String ERROR_MESSAGE = "";
	public String CPU_MESSAGE = "";

	public CPU(EmulatorWindow emulatorWindow) {
		this.emulatorWindow = emulatorWindow;
		initialize();
		load();
		intializeDebugger();
		
		cycle();
	}
	
	private void intializeDebugger() {
		emulatorWindow.state = String.valueOf(STATE);
		emulatorWindow.errorMessage = ERROR_MESSAGE;
		emulatorWindow.cpuMessage = CPU_MESSAGE;
		timer.updateTimersDump();
		updateMemoryDump();
		updateIndexDump();
		updateStackDump();
		updateVRegistersDump();
		emulatorWindow.lastCpuRef = CPU_REF;
		emulatorWindow.lastDisplayRef = graphics.DISPLAY_REF;
		emulatorWindow.lastTimerRef = TIMER_REF;
	}

	private void initialize() {
		new Memory();
		timer = new Timer(this);
		keyPad = new KeyPad(this);
		graphics = new GPU(this);
		interpreter = new ChipsetInterpreter(this);
		loader = new Loader(this);
	}

	private void load() {
		loadFont();
		loader.LoadROM(Chip8.ROM_FILE);
	}

	public void cycle() {
		while (this != null) { 
			switch (STATE) {
			case RUNNING:
				update();
				
				break;
			case WAITING:
				if (keyPad.keyPressed()) {
					setV(STORE_KEY, keyPad.KEY_PRESSES.get(0));
					STATE = CPUState.RUNNING;
				}
				break;
			case STOPPED:
				stop();
				break;
			case ERROR:
				STATE = CPUState.STOPPED;
				break;
			case PAUSED:
				break;
			}
			emulatorWindow.state = String.valueOf(STATE);
			emulatorWindow.update();
			//CYCLES++;
		}
	}

	private void update() {
		long now = System.nanoTime();
		if (now - CPU_REF >= cpuRefRate) {
			CPU_REF = now;
			emulatorWindow.lastCpuRef = now;
			step();
		}
		if (now - TIMER_REF >= timerRefRate) {
			TIMER_REF = now;
			emulatorWindow.lastTimerRef = now;
			timer.updateTimers();
		}
	}

	private void stop() {
		CPU_MESSAGE = "Stopping...";
		emulatorWindow.cpuMessage = CPU_MESSAGE;
		graphics.display.frame.dispose();
		graphics.display = null;
		emulatorWindow.frame.dispose();
		emulatorWindow = null;
		System.exit(0);
	}

	public void step() {
		fetchAndExecute();
	}
	
	public void fetchAndExecute() {
		if (PC < Memory.MEMORY_SIZE) {
			try {
				if (interpreter.executeInstruction(Memory.readInt16(PC))) {
					if (PC + 2 <= Memory.MEMORY_SIZE) {
						changePC(2);
					} else {
						error("Buffer overflow, PC: " + PC + "/4096 bytes");
					}

				}
			} catch (Exception e) {
				error("Error in fetching instruction from addr " + PC);
				e.printStackTrace();
			}

		} else {
			error("Buffer overflow, PC: " + PC + "/4096 bytes");
		}
	}

	public void error(String errorMessage) {
		ERROR_MESSAGE = errorMessage;
		STATE = CPUState.ERROR;
		emulatorWindow.errorMessage = ERROR_MESSAGE;
	}

	public void loadFont() {
		// load font from Chip8.FONT_FILE into 0x000 in memory
		try {
			String line = "";
			int i = FONT_LOCATION;
			int byteCount = 0;
			BufferedReader reader = new BufferedReader(new FileReader("./res/Fonts/" + Chip8.FONT_FILE + ".ch8f"));
			while ((line = reader.readLine()) != null) {
				String[] bytes = line.split(", ");
				
				for (int j = 0; j < bytes.length; j++) {
					byte spriteData = (byte) int8(Integer.decode(bytes[j]));
					Memory.write8(i + j, spriteData);
					emulatorWindow.font[byteCount] = int8(spriteData);
					byteCount++;
				}

				i += bytes.length;
			}

			reader.close();
		} catch (Exception e) {
			error("Unable to load fontset: " + Chip8.FONT_FILE + ".ch8f");
			e.printStackTrace();
		}
	}

	// save cpu state
	public void loadState(String state_file) {
		// load registers and memory from emulator state
		// set clocks to state clocks
	}
	
	public void set_state(CPUState setTo) {
		STATE = setTo;
	}

	// load cpu state
	public void saveState() {

	}

	public void wait_and_store_key(int storeKey_Vregister) {
		STORE_KEY = storeKey_Vregister;
		emulatorWindow.storeKey = STORE_KEY;
		STATE = CPUState.WAITING;
		keyPad.keyPressed_set(false);
		keyPad.KEY_PRESSES.clear();
	}

	// audio system

	// masking
	public static int int4(int integer) {
		return integer & 0xF;
	}

	public static int int8(int integer) {
		return integer & 0xFF;
	}
	
	public static int lsb(int integer) {
		return integer & 1;
	}
	
	public static int int12(int integer) {
		return integer & 0xFFF;
	}

	public static int int16(int integer) {
		return integer & 0xFFFF;
	}

	public int V(int index) {
		return int8(V[index]);
	}

	public void setV(int index, int value) {
		V[index] = int8(value);
		updateVRegistersDump();
	}
	
	public void changeV(int index, int value) {
		V[index] += int8(value);
		updateVRegistersDump();
	}
	
	public void updateVRegistersDump() {
		emulatorWindow.vRegisters = V;
	}
	
	public void updateStackDump() {
		emulatorWindow.stack = STACK;
		emulatorWindow.stackPointer = SP;
	}

	public int I() {
		return int12(I);
	}
	
	
	void updateMemoryDump() {
		int i = 0;
		int start = PC;
		int j = -16;
		while(i < 17) {
			emulatorWindow.memory[i] = Memory.readInt16(start + j);
			i++;
			j += 2;
		}
		emulatorWindow.pc = PC;
	}
	
	void updateIndexDump() {
		int i = 0;
		int start = I;
		int j = -16;
		while(i < 17) {
			emulatorWindow.indexD[i] = Memory.readInt16(start + j);
			i++;
			j += 2;
		}
		emulatorWindow.index = I;
	}
	
	void setPC(int value) {
		PC = value;
		updateMemoryDump();
	}
	
	void changePC(int value) {
		PC += value;
		updateMemoryDump();
	}

	public void setI(int value) {
		I = int12(value);
		updateIndexDump();
	}

	public int get_stack() {
		return STACK[SP];
	}
	
	public boolean jump_to(int addr) {
		setPC(addr);
		return false;
	}
	
	public boolean subroutine(int addr) {
		stack_push(PC + 2);
		setPC(int12(addr));
		updateMemoryDump();
		return false;
	}

	public void stack_push(int addr) {
		STACK[SP] = int12(addr);
		if (SP < STACK.length - 1) {
			SP++;
		}
		updateStackDump();
	}

	public boolean skip_next() {
		changePC(2);
		emulatorWindow.pc = PC;
		return true;
	}
	
	public boolean stack_pull() {
		if (SP > 0) {
			SP--;
		}
		setPC(STACK[SP]);
		updateStackDump();
		return false;
	}
}
