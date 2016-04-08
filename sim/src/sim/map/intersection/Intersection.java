package sim.map.intersection;

import math.Vector2D;
import sim.Drawable;
import sim.EntityHandler;
import sim.map.track.*;

import java.awt.Graphics2D;
import java.util.HashMap;

public class Intersection implements Drawable {
	public static final int straight = 5, turn = 10, buffer = 1, width = 3;
	public static final int arm = straight + turn + buffer;
	public static final int square = width * 3;
	public static final int intersectionSize = arm * 2 + square;
	public static final int NUMBER_OF_SEGMENTS = 9;
	private Segment[] segments; // Just holds all Segments.
	private HashMap<Integer, Segment> startPoints; // Gives the first segment
													// coming from each of the 4
													// directions.

	public Intersection() {
		segments = new Segment[NUMBER_OF_SEGMENTS];
		startPoints = new HashMap<>();
		init();
	}

	public Segment[] getSegments() {
		return segments;
	}

	public Segment getStartPoint(int i) {
		return startPoints.get(i);
	}

	private void init() {
		/***** Startpoint North *****/
		segments[0] = new Segment(new LineTrack(new Vector2D(arm + width / 2, 0), new Vector2D(arm + width / 2, straight)));
		startPoints.put(0, segments[0]);

		segments[4] = new Segment(new LineTrack(new Vector2D(arm + width * 3 / 2, straight), new Vector2D(arm + width * 3 / 2, straight + turn)));
		segments[0].linkSegment(1,segments[4]);
		segments[5] = new Segment(new SquareCurveTrack(new Vector2D(arm + width * 3 / 2, straight + turn), new Vector2D(arm + square / 2, arm + square * 4 / 5), new Vector2D(arm + square + buffer, arm + width * 5 / 2)));
		segments[4].linkSegment(1,segments[5]);
		segments[6] = new Segment(new LineTrack(new Vector2D(arm + square + buffer, arm + width * 5 / 2), new Vector2D(arm + square + buffer + turn, arm + width * 5 / 2)));
		segments[5].linkSegment(1,segments[6]);
		segments[6].linkSegment(1,null);

		segments[1] = new Segment(new LineTrack(new Vector2D(arm + width / 2, straight), new Vector2D(arm + width / 2, straight + turn)));
		segments[0].linkSegment(3, segments[1]);
		segments[2] = new Segment(new SquareCurveTrack(new Vector2D(arm + width / 2, straight + turn), new Vector2D(arm + width / 2, arm + width / 2), new Vector2D(straight + turn, arm + width / 2)));
		segments[1].linkSegment(3, segments[2]);
		segments[3] = new Segment(new LineTrack(new Vector2D(straight + turn, arm + width / 2), new Vector2D(0, arm + width / 2)));
		segments[2].linkSegment(3,segments[3]);
		segments[3].linkSegment(3,null);

		segments[0].linkSegment(2,segments[1]);
		segments[7] = new Segment(new LineTrack(new Vector2D(arm + width / 2, straight + turn), new Vector2D(arm + width / 2, arm + square + buffer)));
		segments[1].linkSegment(2,segments[7]);
		segments[8] = new Segment(new LineTrack(new Vector2D(arm + width / 2, arm + square + buffer), new Vector2D(arm + width / 2, arm * 2 + square)));
		segments[7].linkSegment(2,segments[8]);
		segments[3].linkSegment(2,null);

		/***** Startpoint East *****/
		
	}

	@Override
	public void draw(Graphics2D g2d) {
		// TODO Auto-generated method stub

	}
}