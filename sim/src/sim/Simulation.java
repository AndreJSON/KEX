package sim;

import Math.Vector2D;
import sim.map.track.*;

public class Simulation {
	public Simulation() {
		init();
		testSquareCurveTrack();
		testLineTrack();
		System.out.println("All probably went well, here is a test print.");
	}

	public void init() {
		// Do some init stuff.
	}


	public static void main(String[] args) {
		new Simulation();
	}
	
// --- TEST CODE FROM THIS POINT ON ---

	// Testing bezier track.
	public void testSquareCurveTrack() {
		AbstractTrack track = new SquareCurveTrack(new Vector2D(0, 0),
				new Vector2D(-1, 2), new Vector2D(1, 1));
		System.out.println(track);
		System.out.println("Track length = " + track.length());
		System.out.println();
	}

	// Testing line track.
	public void testLineTrack() {
		AbstractTrack track = new LineTrack(new Vector2D(0, 0), new Vector2D(1, 1));
		System.out.println(track);
		System.out.println("Track length = " + track.length());
		System.out.println();
	}
}