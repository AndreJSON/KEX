package sim;

import map.intersection.*;
import sim.system.*;

import java.awt.geom.AffineTransform;

import javax.swing.JFrame;

public class Simulation {

	// public static final fields
	public static final boolean SHOW_TRACKS = true;
	public static final boolean DEBUG = true;
	public static final boolean COLLISION = false;
	public static final int X = 0, Y = 1;
	public static final int[] WINDOW_SIZE = { 1000, 800 };
	public static final int HUDSize = WINDOW_SIZE[X] - WINDOW_SIZE[Y];
	public static final int SimulationSize = WINDOW_SIZE[Y];
	public static final int FPS = 30;
	public static final double SCALE = WINDOW_SIZE[Y] / Intersection.getSize();
	public static final AffineTransform SCALER = AffineTransform
			.getScaleInstance(SCALE, SCALE);
	// 1 = normal speed, 2 = double speed etc.
	public static final double SCALE_TICK = 0.75;
	public static final int TICKS_PER_SECOND = (int) (SCALE_TICK / Const.TIME_STEP);
	// Time between printing data (seconds)
	public static final double PRINT_TIME = 10 * 60;

	// private fields
	private double elapsedTime = 0;
	private final JFrame window;
	private final SimDisplay simDisp;
	private final SystemHandler systemHandler;
	private int lastFps;
	private int lastTps;

	// main
	public static void main(String[] args) {
		Simulation simulation = new Simulation();
		simulation.run();
	}

	// Constructor
	public Simulation() {
		window = new JFrame(
				"SAD Project - Autonomous Vehicle Intersection Controller");
		systemHandler = new SystemHandler(this);
		simDisp = new SimDisplay(this);
		initFrame();
		initSystems();
	}

	// Initializer
	private void initFrame() {
		simDisp.setBounds(0, 0, WINDOW_SIZE[Y], WINDOW_SIZE[Y]);

		window.setLayout(null);
		window.add(simDisp);
		window.setSize(WINDOW_SIZE[X], WINDOW_SIZE[Y]);
		window.setLocationRelativeTo(null); // Centers window
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setVisible(true);
		window.setResizable(false);
	}

	private void initSystems() {
		systemHandler.addSystem(new CollisionSystem());
		systemHandler.addSystem(new SpawnSystem(this));
		systemHandler.addSystem(new ACarSystem(this));
		systemHandler.addSystem(new DSCS());
	}

	// public methods.
	public void run() {
		System.out.print("\nStarting simulation.\n\n");

		long nextTime = System.nanoTime();
		long delay = (long) 1e9 / FPS;
		int fps = 0;
		int tps = 0;
		long fpsTime = nextTime;
		long tickTime = System.nanoTime();

		double printTimer = PRINT_TIME;

		boolean pause = false;
		while (true) {
			long now = System.nanoTime();

			// Drawing

			if (nextTime <= now) {
				nextTime = now + delay;
				simDisp.render();
				fps++;

			}

			// ticking
			while (now - tickTime >= 1e9 / TICKS_PER_SECOND && !pause) {
				try {
					systemHandler.tick(Const.TIME_STEP);
				} catch (Exception e) {
					pause = true;
					System.err.println();
					System.err.println("Exception: " + e.toString());
					// e.printStackTrace();
				}
				tps++;
				elapsedTime += Const.TIME_STEP;
				tickTime += 1e9 / TICKS_PER_SECOND;
			}

			if (printTimer - elapsedTime <= 0) {
				// Print data.
				System.out.printf("%4.0f\t%s\t%s\n", printTimer / 60,
						PerfDb.getWholeData(), PerfDb.getIntervalData());
				printTimer += PRINT_TIME;
				PerfDb.resetIntervalStatistics();
			}

			// FPS
			if (fpsTime <= System.nanoTime()) {
				fpsTime += 1e9;
				lastFps = fps;
				lastTps = tps;
				tps = 0;
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

	public int tps() {
		return lastTps;
	}
}