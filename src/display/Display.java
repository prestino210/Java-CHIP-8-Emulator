package display;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import javax.swing.*;
import chip.CPU;
import chip.CPUState;
import chip8.Chip8;
import keypad.KeyPad;

@SuppressWarnings("serial")
public class Display extends JPanel implements Runnable {
	public JFrame frame;
	CPU cpu;
	GPU gpu;
	Thread THREAD;
	int FPS = Chip8.DISPLAY_HZ;
	String windowTitle = Chip8.WINDOW_TITLE;
	Color bgColor = new Color(0, 0, 0);
	BufferedImage screenBuffer;
	int displayScale = Chip8.DISPLAY_SCALE + 10;
	int displayWidth;
	int displayHeight;
	
	public Display(CPU cpu, GPU gpu, int width, int height) {
		this.cpu = cpu;
		this.gpu = gpu;
		displayWidth = width * (displayScale);
		displayHeight = height * (displayScale);
		screenBuffer = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		
		startThread();
		createWindow(cpu.keyPad);
	}
	
	public void closeWindow_stopEmulation() { 
		cpu.set_state(CPUState.STOPPED);
	}
	
	public void startThread() {
		THREAD = new Thread(this);
		THREAD.start();
	}
	
	@Override
	public void run() {
		while (THREAD != null) {

			gpu.updateScreenBuffer();

			
			repaint();
		
			try {
				Thread.sleep(1000 / FPS);
			} catch (InterruptedException e) {
				cpu.error("Display thread interrupted");
			}

		}
	}
	
	public void createWindow(KeyPad keyPad) {
		//create frame then attach this panel
		frame = new JFrame(windowTitle);
		frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.addWindowListener(new WindowAdapter() {
		    @Override
		    public void windowClosing(WindowEvent e) {
		        closeWindow_stopEmulation();
		        System.exit(0); 
		    }
		});

		
		this.setFocusable(true);
		this.addKeyListener(keyPad);
		this.setPreferredSize(new Dimension(displayWidth, displayHeight));
		this.setBackground(bgColor);
		
		frame.add(this);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		g2.drawImage(screenBuffer, 0, 0, displayWidth, displayHeight, null);
		g2.dispose();
	}
}
