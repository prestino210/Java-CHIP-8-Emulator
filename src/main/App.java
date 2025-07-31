package main;

import java.awt.Choice;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFrame;

import chip8.Chip8;

public class App {
	public static void main(String[] args) {
		JFrame frame = new JFrame("CHIP-8 Emulator ROM Select");
		frame.setSize(new Dimension(500, 500));
		frame.setLocationRelativeTo(null);
		frame.setFocusable(true);
		frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setAlwaysOnTop(true);
		File ROMDirectory = new File("./res/ROMs");
		File[] ROMs = ROMDirectory.listFiles();
		Choice ROMSelect = new Choice();
		JButton start = new JButton("Start");

		for (int i = 0; i < ROMs.length; i++) {
			ROMSelect.add(ROMs[i].getName());
		}

		ROMSelect.select(0);

		frame.add(ROMSelect);
		frame.add(start);
		frame.setVisible(true);

		start.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				frame.setVisible(false);
				frame.dispose();
				new Chip8(ROMSelect.getSelectedItem());

			}
		});
	}
}
