package sim;

import sim.map.track.*;
import sim.vehicle.*;
import javax.swing.JFrame;

public class Simulation {
	private static final int X = 0, Y = 1;
	private static final int[] windowSize = {800, 600};
	private JFrame window;

	public Simulation() {
		init();
		testSquareCurveTrack();
		System.out.println("All probably went well, here is a test print.");
	}

	public void init() {
		window = new JFrame("Traffic Simulation");
		window.setSize(windowSize[X], windowSize[Y]);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setVisible(true);
	}

	public void testSquareCurveTrack() {
		//Testing arc.
		SquareCurveTrack curve = new SquareCurveTrack(new Vector2D(0, 0), new Vector2D(0, 1), new Vector2D(1, 1));
		System.out.println(curve);
		System.out.println("Curvev length = " + curve.length());
	}

	public static void main (String[] args) {
		new Simulation();
	}
}