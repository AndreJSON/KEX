package sim;

import map.intersection.*;
import map.track.*;
import math.*;
import tscs.*;

import java.awt.geom.AffineTransform;
import java.awt.event.*;

import javax.swing.JFrame;
import javax.swing.JButton;

public class Simulation implements ActionListener {
	public static final boolean DEBUG = true;
	public static final int X = 0, Y = 1;
	public static final int[] windowSize = { 1000, 800 };
	public static final int HUDSize = windowSize[X] - windowSize[Y];
	public static final int FPS = 60;
	public static final double SCALE = windowSize[Y] / Intersection.intersectionSize;
	public static final AffineTransform SCALER = AffineTransform
			.getScaleInstance(SCALE, SCALE);
	public static final int TICKS_PER_SECOND = 120;
	public static final double SPAWNS_PER_SECOND = 2;
	public static final double SCALE_TICK = 1; // 1 = normal speed, 2 = double
												// speed etc.

	private JFrame window;
	private SimDisplay simDisp;
	private JButton b1;
	private Logic logic;
	private AbstractTSCS tscs;
	private int drawFps;

	/************ Just init stuff in this section *************/

	public Simulation() {
		init();
		run();
	}

	public void init() {
		tscs = new DSCS();
		logic = new Logic(tscs);

		window = new JFrame("SAD Project - Traffic Simulation");
		window.setLayout(null);
		simDisp = new SimDisplay(this);
		simDisp.setBounds(0, 0, windowSize[Y], windowSize[Y]);
		window.add(simDisp);
		b1 = new JButton("Brake");
		b1.setBounds(windowSize[Y] + 20,50, windowSize[X] - windowSize[Y] - 40,50);
		b1.addActionListener(this);
		b1.setActionCommand("Brake");
		window.add(b1);
		window.setSize(windowSize[X], windowSize[Y]);
		window.setLocationRelativeTo(null); // Centers window
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setVisible(true);
	}

	public static void main(String[] args) {
		new Simulation();
	}

	public void run() {
		System.out.print("\nStarting simulation.\n\n");

		int spawnAccum = 0;
		int spawnSeparation = (int) ((double)TICKS_PER_SECOND / SPAWNS_PER_SECOND);

		long nextTime = System.nanoTime();
		long delay = (long) 1e9 / FPS;
		int fps = 0;
		long fpsTime = nextTime;

		long tickTime = System.nanoTime();

		while (true) {

			// Drawing
			if (nextTime <= System.nanoTime()) {
				nextTime += delay;
				simDisp.render();
				fps++;

			}

			// ticking
			long now = System.nanoTime();
			while (now - tickTime >= 1e9 / TICKS_PER_SECOND) {
				logic.tick(SCALE_TICK / TICKS_PER_SECOND);
				tickTime += 1e9 / TICKS_PER_SECOND;
				spawnAccum++;
				// Adding cars
				if (spawnAccum >= spawnSeparation) {
					spawnAccum = 0;
					int source = (int)(Math.random() * 4);
					int dest = (int)(Math.random() * 3 + 1) + source;
					logic.spawnCar("Mazda3", source, dest);
				}
			}

			// FPS
			if (fpsTime <= System.nanoTime() && DEBUG) {
				fpsTime += 1e9;
				System.out.println("FPS: " + fps);
				drawFps = fps;
				fps = 0;
			}
		}
	}

	public int drawFps() {
		return drawFps;
	}

	public void actionPerformed(ActionEvent e) {
		if("Brake".equals(e.getActionCommand())) {
			tscs.setEmergencyBreak(true);
		}
	}
}