package sim;

import math.*;
import sim.map.track.*;
import sim.map.intersection.*;
import sim.vehicle.Car;
import sim.vehicle.VehicleSpec;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

import javax.swing.JFrame;

public class Simulation {
	public static boolean DEBUG = true;
	public static final int X = 0, Y = 1;
	public static final int[] windowSize = { 800, 800 };
	public static final int FPS = 60;
	public static final int[] PPM = {
			windowSize[X] / Intersection.intersectionSize,
			windowSize[Y] / Intersection.intersectionSize }; // Pixles per meter
	public static final double SCALE = PPM[0];
	public static final AffineTransform SCALER = AffineTransform
			.getScaleInstance(SCALE, SCALE);

	private JFrame window;
	private SimDisplay simulationDisplayer;
	private Logic logic;
	private EntityHandler entityHandler;
	private Intersection in;
	private int drawFps;

	/************ Just init stuff in this section *************/

	public Simulation() {
		init();
		testAll();
		run();
	}

	public void init() {
		in = new Intersection();

		entityHandler = new EntityHandler();
		simulationDisplayer = new SimDisplay(this, entityHandler);
		logic = new Logic(this, entityHandler);

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
				simulationDisplayer.render();
				fps++;

			}
			double diff = (System.nanoTime() - tickTime) / 1e9;
			tickTime = System.nanoTime();
			logic.tick(diff);

			// FPS
			if (fpsTime <= System.nanoTime() && DEBUG) {
				fpsTime += 1e9;
				System.out.println("FPS: " + fps);
				drawFps = fps;
				fps = 0;
			}
		}
	}

	public void drawInterface(Graphics2D g2d) {
		
		// Draw SCALE
		int width = 25;
		int length = (int) (100 * SCALE);
		g2d.drawLine(750 - width / 2, 50, 750 + width / 2, 50);
		g2d.drawLine(750 - width / 2, 50 + length, 750 + width / 2, 50 + length);
		g2d.drawLine(750, 50, 750, 50 + length);

		g2d.setFont(new Font("Arial", Font.BOLD, 16));
		AffineTransform orig = g2d.getTransform();
		g2d.translate(755, 25 + length / 2);
		g2d.rotate(Math.PI / 2);
		g2d.setColor(Color.BLACK);
		g2d.drawString("100 m", 0, 0);
		g2d.setTransform(orig);

		if (DEBUG) {
			// Draw FPS
			g2d.drawString("FPS: " + drawFps, windowSize[X] - 100, 25);
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

	public void addTestCar() {
		AbstractTrack track = new SquareCurveTrack(new Vector2D(5, 5),
				new Vector2D(5, 75), new Vector2D(55, 75));
		Car car = new Car(new VehicleSpec("Tesla S", 196.0*0.0254, 77.3*0.0254, Color.cyan));
		entityHandler.addTrack(track);
		car.setTrackPosition(track.getTrackPosition());
		car.setVelocity(track.length()/5);
		entityHandler.addCar(car);

		track = new SquareCurveTrack(new Vector2D(10, 5),
				new Vector2D(10, 70), new Vector2D(55, 70));
		car = new Car(new VehicleSpec());
		entityHandler.addTrack(track);
		car.setTrackPosition(track.getTrackPosition());
		car.setVelocity(track.length()/5);
		entityHandler.addCar(car);
		
		track = new LineTrack(new Vector2D(150, 10), new Vector2D(150, 160));
		car = new Car(new VehicleSpec());
		entityHandler.addTrack(track);
		car.setTrackPosition(track.getTrackPosition());
		car.setVelocity(50 / 3.6);
		entityHandler.addCar(car);
		
		track = new LineTrack(new Vector2D(147, 10), new Vector2D(147, 160));
		entityHandler.addTrack(track);
		car = new Car(new VehicleSpec());
		car.setTrackPosition(track.getTrackPosition(10));
		car.setVelocity(45 / 3.6);
		entityHandler.addCar(car);
	}

	public void testAll() {
		if (!DEBUG)
			return;

		testSquareCurveTrack();
		testLineTrack();
		addTestCar();

		System.out.println("All probably went well, here is a test print.");
	}

	// Testing bezier track.
	public void testSquareCurveTrack() {
		AbstractTrack track = new SquareCurveTrack(new Vector2D(0, 0),
				new Vector2D(0, 30), new Vector2D(30, 30));
		System.out.println(track);
		System.out.println("Track length = " + track.length());
		System.out.println();
	}

	// Testing line track.
	public void testLineTrack() {
		AbstractTrack track = new LineTrack(new Vector2D(10, 10), new Vector2D(
				20, 10));
		System.out.println(track);
		System.out.println("Track length = " + track.length());
		System.out.println();
	}

}