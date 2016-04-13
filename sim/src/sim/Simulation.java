package sim;

import map.intersection.*;
import tscs.*;

import java.awt.geom.AffineTransform;
import java.awt.event.*;

import javax.swing.JFrame;
import javax.swing.JButton;

public class Simulation implements ActionListener {

	// public static final fields
	public static final boolean SHOW_GRID = true;
	public static final boolean SHOW_TRACKS = false;
	public static final boolean DEBUG = false;
	public static final int X = 0, Y = 1;
	public static final int[] windowSize = { 1000, 800 };
	public static final int HUDSize = windowSize[X] - windowSize[Y];
	public static final int SimulationSize = windowSize[Y];
	public static final int FPS = 60;
	public static final double SCALE = windowSize[Y]
			/ Intersection.getSize();
	public static final AffineTransform SCALER = AffineTransform
			.getScaleInstance(SCALE, SCALE);
	// 1 = normal speed, 2 = double speed etc.
	public static final double SCALE_TICK = 0.8;
	public static final int TICKS_PER_SECOND = (int) (120 * SCALE_TICK);

	// private fields
	private double elapsedTime = 0;
	private JFrame window;
	private SimDisplay simDisp;
	private JButton b1, b2;
	private Logic logic;
	private AbstractTSCS tscs;
	private int lastFps;
	private boolean currentlySpawning = true;

	// main
	public static void main(String[] args) {
		Simulation simulation = new Simulation();
		simulation.run();
	}

	// Constructor
	public Simulation() {
		init();
	}

	// Initializer
	private void init() {
		tscs = new SAD();
		logic = new Logic(this, tscs);
		simDisp = new SimDisplay(this);
		simDisp.setBounds(0, 0, windowSize[Y], windowSize[Y]);
		
		window = new JFrame(
				"SAD Project - Autonomous Vehicle Intersection Controller");
		window.setLayout(null);
		window.add(simDisp);
		b1 = new JButton("BREAK!");
		b1.setBounds(windowSize[Y] + 20, 50,
				windowSize[X] - windowSize[Y] - 40, 50);
		b1.addActionListener(this);
		b1.setActionCommand("Brake");
		window.add(b1);
		b2 = new JButton("Stop spawning");
		b2.setBounds(windowSize[Y] + 20, 150, windowSize[X] - windowSize[Y]
				- 40, 50);
		b2.addActionListener(this);
		b2.setActionCommand("Spawn");
		window.add(b2);
		window.setSize(windowSize[X], windowSize[Y]);
		window.setLocationRelativeTo(null); // Centers window
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setVisible(true);
		window.setResizable(false);
	}

	// public methods.
	public void run() {
		System.out.print("\nStarting simulation.\n\n");

		long nextTime = System.nanoTime();
		long delay = (long) 1e9 / FPS;
		int fps = 0;
		long fpsTime = nextTime;

		long tickTime = System.nanoTime();
		boolean pause = false;
		while (true) {

			// Drawing
			if (nextTime <= System.nanoTime()) {
				nextTime += delay;
				simDisp.render();
				fps++;

			}

			// ticking
			long now = System.nanoTime();
			while (now - tickTime >= 1e9 / TICKS_PER_SECOND && !pause) {
				try {
					logic.tick(SCALE_TICK / TICKS_PER_SECOND);
				} catch (Exception e) {
					pause = true;
					System.err.println();
					System.err.println("Exception: " + e.toString());
					e.printStackTrace();
				}
				elapsedTime += SCALE_TICK / TICKS_PER_SECOND;
				tickTime += 1e9 / TICKS_PER_SECOND;
			}

			// FPS
			if (fpsTime <= System.nanoTime()) {
				fpsTime += 1e9;
				lastFps = fps;
				fps = 0;
			}
		}
	}

	public double elapsedTime() {
		return elapsedTime;
	}

	public int fps() {
		return lastFps;
	}

	public String drawPhase() {
		return tscs.drawPhase();
	}

	public void actionPerformed(ActionEvent e) {
		if ("Brake".equals(e.getActionCommand())) {
			if (currentlySpawning && !tscs.getEmergencyBreak()) {
				b2.doClick();
			}
			tscs.setEmergencyBreak(!tscs.getEmergencyBreak());
			b1.setText(tscs.getEmergencyBreak() ? "Move again" : "BREAK!");
		} else if (("Spawn").equals(e.getActionCommand())) {
			currentlySpawning = !currentlySpawning;
			logic.setSpawnerOn(currentlySpawning);
			b2.setText(currentlySpawning ? "Stop spawning" : "Start spawning");
		}
	}
}