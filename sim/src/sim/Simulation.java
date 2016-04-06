package sim;

import sim.map.track.*;
import sim.vehicle.*;

public class Simulation {
	public Simulation() {
		init();
		testSquareCurveTrack();
		System.out.println("All probably went well, here is a test print.");
	}

	public void init() {
		//Do some init stuff.
	}

	public void testSquareCurveTrack() {
		// Testing arc.
		SquareCurveTrack curve = new SquareCurveTrack(new Vector2D(0, 0), new Vector2D(0, 1), new Vector2D(1, 1));
		System.out.println(curve);
		System.out.println("Curvev length = " + curve.length());
	}

	public static void main (String[] args) {
		new Simulation();
	}
}