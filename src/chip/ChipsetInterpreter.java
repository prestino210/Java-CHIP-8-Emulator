package chip;

import display.GPU;
import keypad.KeyPad;

public class ChipsetInterpreter {
	// nibbles
	CPU cpu;
	GPU gpu;
	Timer timer;
	KeyPad keyPad;
	public String mn = "";
	int opcode;

	// Masked constants
	int n; // 0x000F
	int nn; // 0x00FF
	int nnn; // 0x0FFF
	int x; // 0x0F00
	int y; // 0x00F0
	int c; // 0xF000
	int cc; // 0xFF00

	// Decoding variables
	int vX;
	int vY;
	int sum;
	int diff;
	int lsb;
	int msb;
	int bcd;
	int shifted;
	int randInt;

	boolean iPC = true;

	public ChipsetInterpreter(CPU cpu) {
		this.cpu = cpu;
		this.gpu = cpu.graphics;
		this.timer = cpu.timer;
		this.keyPad = cpu.keyPad;
	}
	
	void setMn(String mnemonic) {
		mn = "0x" + Integer.toHexString(opcode) + " - "+ mnemonic;
	}

	public boolean executeInstruction(int opcode) {
		this.opcode = opcode;
		maskInstruction(opcode);
		iPC = true;
		
		// instruction set
		switch (c) {
		case 0x0:
			if (cc == 0x00) {
				if (nn == 0xE0) {
					gpu.clear_display();
					setMn("CLS");
				} else if (nn == 0xEE) {
					iPC = cpu.stack_pull();
					setMn("RET");
				}
			} else {
				// Jump to RCA 1802 (deprecated)
			}
			break;
		case 0x1:
			iPC = cpu.jump_to(nnn);
			setMn("JP " + nnn);
			break;
		case 0x2:
			iPC = cpu.subroutine(nnn);
			setMn("CALL " + nnn);
			break;
		case 0x3:
			if (cpu.V(x) == nn) {
				iPC = cpu.skip_next();
			}
			setMn("SE V" + x + ", " + nn);
			break;
		case 0x4:
			if (cpu.V(x) != nn) {
				iPC = cpu.skip_next();
			}
			setMn("SNE V" + x + ", " + nn);
			break;
		case 0x5:
			if (cpu.V(x) == cpu.V(y)) {
				iPC = cpu.skip_next();
			}
			setMn("SE V" + x + ", V" + y);
			break;
		case 0x6:
			cpu.setV(x, nn);
			setMn("LD V" + x + ", " + nn);
			break;
		case 0x7:
			cpu.changeV(x, nn);
			setMn("ADD V" + x + ", " + nn);
			break;
		case 0x8:
			switch (n) {
			case 0x0:
				cpu.setV(x, cpu.V(y));
				setMn("LD V" + x + ", V" + y);
				break;
			case 0x1:
				cpu.setV(x, cpu.V(x) | cpu.V(y));
				setMn("OR V" + x + ", V" + y);
				break;
			case 0x2:
				cpu.setV(x, cpu.V(x) & cpu.V(y));
				setMn("AND V" + x + ", V" + y);
				break;
			case 0x3:
				cpu.setV(x, cpu.V(x) ^ cpu.V(y));
				setMn("XOR V" + x + ", V" + y);
				break;
			case 0x4:
				sum = cpu.V[x] + cpu.V(y);
				cpu.setV(0xF, (sum > 0xFF) ? 1 : 0);
				cpu.setV(x, CPU.int8(sum));
				setMn("ADD V" + x + ", V" + y);
				break;
			case 0x5:
				vX = cpu.V(x);
				vY = cpu.V(y);
				cpu.setV(0xF, (vX >= vY) ? 1 : 0);
				cpu.setV(x, CPU.int8(vX - vY));
				setMn("SUB V" + x + ", V" + y);
				break;
			case 0x6: // fix logic
				vX = cpu.V(x);
				shifted = CPU.int8(vX >> 1);
				lsb = CPU.lsb(vX);
				cpu.setV(x, shifted);
				cpu.setV(0xF, lsb);
				setMn("SHR V" + x + "{, V" + y + "}");
				break;
			case 0x7:
				vX = cpu.V(x);
				vY = cpu.V(y);
				cpu.setV(0xF, (vY >= vX) ? 1 : 0);
				cpu.setV(x, CPU.int8(vY - vX));
				setMn("SUBN V" + x + ", V" + y);
				break;
			case 0xE: // fix logic
				vX = cpu.V(x);
				shifted = CPU.int8(vX << 1);
				msb = CPU.lsb(vX >> 7);
				cpu.setV(x, shifted);
				cpu.setV(0xF, msb);
				setMn("SHL V" + x + "{, V" + y + "}");
				break;
			}
			break;
		case 0x9:
			if (cpu.V(x) != cpu.V(y)) {
				iPC = cpu.skip_next();
			}
			setMn("SNE V" + x + ", V" + y);
			break;
		case 0xA:
			cpu.setI(nnn);
			setMn("LD I, " + nnn);
			break;
		case 0xB:
			iPC = cpu.jump_to(nnn + cpu.V(0));
			setMn("JP V0, " + nnn);
			break;
		case 0xC:
			// random
			randInt = (int) (Math.random() * 256);
			cpu.setV(x, randInt & nn);
			setMn("RND V" + x + ", " + nn);
			break;
		case 0xD:
			gpu.draw_sprite(cpu.V(x), cpu.V(y), n, cpu.I());
			setMn("DRW V" + x + ", V" + y + ", " + n);
			break;
		case 0xE:
			if (nn == 0x9E) {
				if (keyPad.KEY_VALUES.get(cpu.V(x))) {
					iPC = cpu.skip_next();
				}
				setMn("SKP V" + x);
			} else if (nn == 0xA1) {
				if (!keyPad.KEY_VALUES.get(CPU.int4(cpu.V(x)))) {
					iPC = cpu.skip_next();
				}
				setMn("SKNP V" + x);
			}
			break;
		case 0xF:
			switch (nn) {
			case 0x07:
				cpu.setV(x, timer.get_delayT());
				setMn("LD V" + x + ", DT");
				break;
			case 0x0A:
				cpu.wait_and_store_key(x);
				setMn("LD V" + x + ", K");
				break;
			case 0x15:
				timer.set_delayT(cpu.V(x));
				setMn("LD DT, V" + x);
				break;
			case 0x18:
				timer.set_soundT(cpu.V(x));
				setMn("LD ST, V" + x);
				break;
			case 0x1E:
				cpu.setI(cpu.I() + cpu.V(x));
				setMn("ADD I, V" + x);
				break;
			case 0x29:
				cpu.setI(cpu.FONT_LOCATION + (cpu.V(x) * 5));
				setMn("LD F, V" + x);
				break;
			case 0x33:
				int value = cpu.V(x);
				Memory.write8(cpu.I(), (byte) (value / 100));
				Memory.write8(cpu.I() + 1, (byte) ((value / 10) % 10));
				Memory.write8(cpu.I() + 2, (byte) (value % 10));
				setMn("LD B, V" + x);
				break;

			case 0x55:
				for (int i = 0; i <= x; i++) {
					Memory.write8(cpu.I() + i, (byte) cpu.V(i));
				}
				setMn("LD [I], V" + x);
				break;
			case 0x65:
				for (int i = 0; i <= x; i++) {
					cpu.setV(i, Memory.readInt8(cpu.I() + i));
				}
				setMn("LD V" + x + ", [I]");
				break;
			}
			break;
		}
		

		cpu.emulatorWindow.mnemonic = mn;
		return iPC;
	}

	public void maskInstruction(int opcode) {
		// take opcode and set nibbles
		n = opcode & 0x000F;
		nn = opcode & 0x00FF;
		nnn = opcode & 0x0FFF;

		c = (opcode & 0xF000) >> 12;
		cc = (opcode & 0xFF00) >> 8;

		x = (opcode & 0x0F00) >> 8;
		y = (opcode & 0x00F0) >> 4;
	}

}
