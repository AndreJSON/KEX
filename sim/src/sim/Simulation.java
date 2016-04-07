package sim;

import math.*;
import sim.map.track.*;
import sim.vehicle.*;
import sim.map.intersection.*;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class Simulation {
	public static boolean DEBUG = true;
	private static final int X = 0, Y = 1;
	private static final int[] windowSize = { 800, 800 };

	private static final int[] ppm = {
			windowSize[X] / Intersection.intersectionSize,
			windowSize[Y] / Intersection.intersectionSize }; // Pixles per meter
	private JFrame window;
	private SimDisplay simulationDisplayer;
	private Intersection in;

	/************ Just init stuff in this section *************/

	public Simulation() {
		init();
		testAll();
		run();
	}

	public void init() {
		in = new Intersection();
		simulationDisplayer = new SimDisplay(this, windowSize[X], windowSize[Y]);
		window = new JFrame("Traffic Simulation");
		window.add(simulationDisplayer);
		window.setSize(windowSize[X], windowSize[Y]);
		window.setLocationRelativeTo(null); // Centers window
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setVisible(true);
	}

	public static void main(String[] args) {
		new Simulation();
	}

	/************ Code *************/

	public void run() {
		System.out.print("\n\nStarting simulation.\n\n");
		
		long nextTime = System.nanoTime();
		long delay = (long) 1e9 / 60;
		int fps = 0;
		long fpsTime = nextTime;

		while (true) {

			// Drawing
			if (nextTime <= System.nanoTime()) {
				nextTime += delay;
				simulationDisplayer.render();
				fps++;
			}

			// FPS
			if (fpsTime <= System.nanoTime() && DEBUG) {
				fpsTime += 1e9;
				System.out.println("FPS: " + fps);
				fps = 0;
			}
		}
	}

	public void draw(Graphics2D g2d) {
		// Test drawing stuff.
		// Test drawing shapes.
		g2d.setColor(Color.RED);
		g2d.fillOval(0, 0, 30, 30);
		g2d.drawOval(0, 50, 30, 30);
		g2d.fillRect(50, 0, 30, 30);
		g2d.drawRect(50, 50, 30, 30);

		// Create and draw a SquareCurveTrack
		AbstractTrack track = new SquareCurveTrack(new Vector2D(0, 0),
				new Vector2D(0, 300), new Vector2D(300, 300));
		track.draw(g2d);

		// Create and draw a LineTrack
		AbstractTrack track2 = new LineTrack(new Vector2D(100, 100),
				new Vector2D(200, 100));
		track2.draw(g2d);
	}

	/************ TEST CODE FROM THIS POINT ON *************/

	public void testAll() {
		if (!DEBUG)
			return;

		testSquareCurveTrack();
		testLineTrack();

		System.out.println("All probably went well, here is a test print.");
	}

	// Testing bezier track.
	public void testSquareCurveTrack() {
		AbstractTrack track = new SquareCurveTrack(new Vector2D(0, 0),
				new Vector2D(0, 1), new Vector2D(1, 1));
		System.out.println(track);
		System.out.println("Track length = " + track.length());
		System.out.println();
	}

	// Testing line track.
	public void testLineTrack() {
		AbstractTrack track = new LineTrack(new Vector2D(0, 0), new Vector2D(1,
				1));
		System.out.println(track);
		System.out.println("Track length = " + track.length());
		System.out.println();
	}

}