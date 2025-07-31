package chip;

public class Memory {
	public static byte[] MEM;
	public static int MEMORY_SIZE = 0x10000;
	
	public Memory() {
		MEM = new byte[MEMORY_SIZE];
	}
	
	public static void clear() {
		for(int i = 0; i < MEMORY_SIZE; i++) {
			MEM[i] = 0;
		}
	}
	
	public static int readInt8(int location) {
		return (CPU.int8(MEM[location & 0xFFF]));
	}
	
	public static int readInt16(int location) {
		return (((CPU.int8(MEM[location & 0xFFF])) << 8) | ((CPU.int8(MEM[(location & 0xFFF) + 1]))));
	}
	
	public static void write8(int location, byte data) {
		MEM[location] = (byte) (CPU.int8(data));
	}
	
	public static void write16(int location, byte data) {
		byte int16 = (byte) CPU.int16(data);
		byte h = (byte) ((int16) >> 8);
		byte l = (byte) (int16);
		MEM[location] = (byte) CPU.int8(h);
		MEM[location + 1] = (byte) CPU.int8(l);
	}
}
