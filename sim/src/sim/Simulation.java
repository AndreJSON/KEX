package sim;

import Math.Vector2D;
import sim.map.track.*;
<<<<<<< HEAD
import sim.vehicle.*;
import javax.swing.JFrame;
=======
>>>>>>> 5a6a58d88d31dd71be223a6070e8464c8dd3aa4c

public class Simulation {
	private static final int X = 0, Y = 1;
	private static final int[] windowSize = {800, 600};
	private JFrame window;

	public Simulation() {
		init();
		testSquareCurveTrack();
		testLineTrack();
		System.out.println("All probably went well, here is a test print.");
	}

	public void init() {
<<<<<<< HEAD
		window = new JFrame("Traffic Simulation");
		window.setSize(windowSize[X], windowSize[Y]);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setVisible(true);
=======
		// Do some init stuff.
>>>>>>> 5a6a58d88d31dd71be223a6070e8464c8dd3aa4c
	}


	public static void main(String[] args) {
		new Simulation();
	}
	
// --- TEST CODE FROM THIS POINT ON ---

	// Testing bezier track.
	public void testSquareCurveTrack() {
<<<<<<< HEAD
		//Testing arc.
		SquareCurveTrack curve = new SquareCurveTrack(new Vector2D(0, 0), new Vector2D(0, 1), new Vector2D(1, 1));
		System.out.println(curve);
		System.out.println("Curvev length = " + curve.length());
=======
		AbstractTrack track = new SquareCurveTrack(new Vector2D(0, 0),
				new Vector2D(-1, 2), new Vector2D(1, 1));
		System.out.println(track);
		System.out.println("Track length = " + track.length());
		System.out.println();
>>>>>>> 5a6a58d88d31dd71be223a6070e8464c8dd3aa4c
	}

	// Testing line track.
	public void testLineTrack() {
		AbstractTrack track = new LineTrack(new Vector2D(0, 0), new Vector2D(1, 1));
		System.out.println(track);
		System.out.println("Track length = " + track.length());
		System.out.println();
	}
}