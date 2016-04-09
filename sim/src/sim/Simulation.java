package sim;

import map.intersection.*;
import map.track.*;
import math.*;

import java.awt.geom.AffineTransform;

import javax.swing.JFrame;

public class Simulation {
	public static boolean DEBUG = true;
	public static final int X = 0, Y = 1;
	public static final int[] windowSize = { 1100, 800 };
	public static final int HUDSize = windowSize[X] - windowSize[Y];
	public static final int FPS = 60;
	public static final double SCALE = windowSize[Y]
			/ Intersection.intersectionSize;
	public static final AffineTransform SCALER = AffineTransform
			.getScaleInstance(SCALE, SCALE);
	public static final int TICKS_PER_SECOND = 120;
	public static final double SCALE_TICK = 1; // 1 = normal speed, 2 = double
												// speed etc.

	private JFrame window;
	private SimDisplay simDisp;
	private Logic logic;
	private int drawFps;

	/************ Just init stuff in this section *************/

	public Simulation() {
		init();
		testAll();
		run();
	}

	public void init() {
		simDisp = new SimDisplay(this);
		logic = new Logic(this);

		window = new JFrame("SAD Project - Traffic Simulation");
		window.add(simDisp);
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
				simDisp.render();
				fps++;

			}

			// ticking
			long now = System.nanoTime();
			while (now - tickTime >= 1e9 / TICKS_PER_SECOND) {
				logic.tick(SCALE_TICK / TICKS_PER_SECOND);
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

	public int drawFps() {
		return drawFps;
	}

	/************ TEST CODE FROM THIS POINT ON *************/

	public void addTestCar() {
		/*
		 * AbstractTrack track = new SquareCurveTrack(new Vector2D(5, 5), new
		 * Vector2D(5, 75), new Vector2D(55, 75)); Car car = new Car(new
		 * CarType("Tesla S", 196.0*0.0254, 77.3*0.0254, Color.cyan));
		 * entityHandler.addTrack(track);
		 * car.setTrackPosition(track.getTrackPosition());
		 * car.setSpeed(track.length()/5); entityHandler.addCar(car);
		 * 
		 * track = new SquareCurveTrack(new Vector2D(10, 5), new Vector2D(10,
		 * 70), new Vector2D(55, 70)); car = new Car(new CarType());
		 * entityHandler.addTrack(track);
		 * car.setTrackPosition(track.getTrackPosition());
		 * car.setSpeed(track.length()/5); entityHandler.addCar(car);
		 * 
		 * track = new LineTrack(new Vector2D(150, 10), new Vector2D(150, 160));
		 * car = new Car(new CarType()); entityHandler.addTrack(track);
		 * car.setTrackPosition(track.getTrackPosition()); car.setSpeed(50 /
		 * 3.6); entityHandler.addCar(car);
		 * 
		 * track = new LineTrack(new Vector2D(147, 10), new Vector2D(147, 160));
		 * entityHandler.addTrack(track); car = new Car(new CarType());
		 * car.setTrackPosition(track.getTrackPosition(10)); car.setSpeed(45 /
		 * 3.6); car.setVelocity(45 / 3.6); entityHandler.addCar(car);
		 * 
		 * 
		 * AbstractTrack track =
		 * EntityDatabase.getIntersection().getStartPoint(0) .getTrack(); Car
		 * car; car = new Car(CarModelDatabase.getByName("Tesla S"));
		 * car.setTrackPosition(track.getTrackPosition()); car.setSpeed(30 /
		 * 3.6); EntityDatabase.addCar(car, null);
		 */
	}

	public void testAll() {
		if (!DEBUG)
			return;
		addTestCar();
		System.out.println("All probably went well, here is a test print.");
	}

	// Testing bezier track.
	public void testSquareCurveTrack() {
		AbstractTrack track = new Bezier2Track(new Vector2D(0, 0),
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