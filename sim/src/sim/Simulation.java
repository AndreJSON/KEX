package sim;

import map.intersection.*;
import tscs.*;

import java.text.DecimalFormat;

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
	public static final double SCALE_TICK = 1; // 1 = normal speed, 2 = double
												// speed etc.

	private JFrame window;
	private SimDisplay simDisp;
	private JButton b1, b2;
	private Logic logic;
	private AbstractTSCS tscs;
	private int drawFps;
	private boolean currentlySpawning = true;
	double timeElapsed = 0;

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
		b1 = new JButton("BREAK!");
		b1.setBounds(windowSize[Y] + 20,50, windowSize[X] - windowSize[Y] - 40,50);
		b1.addActionListener(this);
		b1.setActionCommand("Brake");
		window.add(b1);
		b2 = new JButton("Stop spawning");
		b2.setBounds(windowSize[Y] + 20,150, windowSize[X] - windowSize[Y] - 40,50);
		b2.addActionListener(this);
		b2.setActionCommand("Spawn");
		window.add(b2);
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
				timeElapsed += SCALE_TICK / TICKS_PER_SECOND;
				tickTime += 1e9 / TICKS_PER_SECOND;
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

	public String timeElapsed() {
		return new DecimalFormat("#.0").format(timeElapsed);
	}
	
	public int drawFps() {
		return drawFps;
	}

	public String drawPhase() {
		return tscs.drawPhase();
	}

	public void actionPerformed(ActionEvent e) {
		if("Brake".equals(e.getActionCommand())) {
			if(currentlySpawning && !tscs.getEmergencyBreak()) {
				b2.doClick();
			}
			tscs.setEmergencyBreak(!tscs.getEmergencyBreak());
			b1.setText(tscs.getEmergencyBreak()?  "Move again" : "BREAK!");
		}
		else if(("Spawn").equals(e.getActionCommand())) {
			currentlySpawning = !currentlySpawning;
			logic.setSpawnerOn(currentlySpawning);
			b2.setText(currentlySpawning? "Stop spawning" : "Start spawning");
		}
	}
}