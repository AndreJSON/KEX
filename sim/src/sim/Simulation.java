package sim;

import math.*;
import sim.map.track.*;
import sim.vehicle.*;
import sim.map.intersection.*;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class Simulation {
	private static final int X = 0, Y = 1;
	private static final int[] windowSize = {800, 800};
	private static final int[] ppm = {windowSize[X]/Intersection.intersectionSize, windowSize[Y]/Intersection.intersectionSize}; //Pixles per meter
	private JFrame window;
	private BufferedImage im;
	private Graphics2D g;
	private Intersection in;

	public Simulation() {
		init();
		testDraw();
		testSquareCurveTrack();
		testLineTrack();
		System.out.println("All probably went well, here is a test print.");
	}

/************ Just init stuff in this section *************/

	public void init() {
		in = new Intersection();
		window = new JFrame("Traffic Simulation");
		window.setSize(windowSize[X], windowSize[Y]);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		im = new BufferedImage(100, 50, BufferedImage.TYPE_INT_ARGB);
		g = im.createGraphics();
		window.add(new JLabel () {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				g.drawImage(im, 0, 0, null);
			}
		});
		window.setVisible(true);
	}


	public static void main(String[] args) {
		new Simulation();
	}
	
/************ TEST CODE FROM THIS POINT ON *************/

	// Test drawing
	public void testDraw () {
		g.setColor(Color.GRAY);
		Segment[] segs = in.getSegments();
		Vector2D[] points;
		for(int i = 0; i < segs.length; i++) {
			points = segs[i].getPoints();
			for(int j = 0; j < points.length; j++) {
				g.fillRect(ppm[X]*points[j].getX(),ppm[X]*points[j].getY(),1,1);
			}
		}
	};

	// Testing bezier track.
	public void testSquareCurveTrack() {
		//Testing arc.
		SquareCurveTrack curve = new SquareCurveTrack(new Vector2D(0, 0), new Vector2D(0, 1), new Vector2D(1, 1));
		System.out.println(curve);
		System.out.println("Curvev length = " + curve.length());
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