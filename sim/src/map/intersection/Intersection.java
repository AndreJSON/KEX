package map.intersection;

import map.track.*;
import math.Vector2D;
import sim.Drawable;
import sim.Simulation;
import sim.TravelData;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.HashMap;

public class Intersection implements Drawable {
	/*** Access waypoints ***/
	public static final int NORTH = 0, EAST = 1, SOUTH = 2, WEST = 3;

	public static final int STRAIGHT = 0, RIGHT = 0, LEFT = 1, EXIT = 2;
	public static final int SPLIT_STRAIGHT = 3, SPLIT_LEFT = 4;
	public static final int MAP_ENTRANCE = 5, MAP_EXIT = 6;
	/******/

	public static final double straight = 30, turn = 10, buffer = 1.5,
			width = 3.1;
	public static final double arm = straight + turn + buffer;
	public static final double square = width * 3;
	public static final double intersectionSize = arm * 2 + square;
	public static final int NUMBER_OF_SEGMENTS = 17;
	private Segment[] segments; // Just holds all Segments.
	private HashMap<Integer, Segment> startPoints; // Gives the first segment
													// coming from each of the 4
													// directions.

	private Vector2D wayPoints[][];

	public Intersection() {
		segments = new Segment[NUMBER_OF_SEGMENTS];
		startPoints = new HashMap<>();
		init();
	}

	public Segment[] getSegments() {
		return segments;
	}

	private void init() {
		generateWayPoints();

		/***** Startpoint North *****/
		segments[0] = lineSegment(new Vector2D(arm + width / 2, 0),
				new Vector2D(arm + width / 2, straight));
		startPoints.put(NORTH, segments[0]);

		segments[4] = lineSegment(new Vector2D(arm + width * 3 / 2, straight),
				new Vector2D(arm + width * 3 / 2, straight + turn));
		segments[0].linkSegment(1, segments[4]);
		segments[5] = curveSegment(new Vector2D(arm + width * 3 / 2, straight
				+ turn), new Vector2D(arm + square + buffer, arm + width * 5
				/ 2), true);
		segments[4].linkSegment(1, segments[5]);
		segments[6] = lineSegment(new Vector2D(arm + square + buffer, arm
				+ width * 5 / 2), new Vector2D(arm * 2 + square, arm + width
				* 5 / 2));
		segments[5].linkSegment(1, segments[6]);
		segments[6].linkSegment(1, null);

		segments[1] = lineSegment(new Vector2D(arm + width / 2, straight),
				new Vector2D(arm + width / 2, straight + turn));
		segments[0].linkSegment(3, segments[1]);
		segments[2] = curveSegment(new Vector2D(arm + width / 2, straight
				+ turn), new Vector2D(straight + turn, arm + width / 2), true);
		segments[1].linkSegment(3, segments[2]);
		segments[3] = lineSegment(
				new Vector2D(straight + turn, arm + width / 2), new Vector2D(0,
						arm + width / 2));
		segments[2].linkSegment(3, segments[3]);
		segments[3].linkSegment(3, null);

		segments[0].linkSegment(2, segments[1]);
		segments[7] = lineSegment(
				new Vector2D(arm + width / 2, straight + turn), new Vector2D(
						arm + width / 2, arm + square + buffer));
		segments[1].linkSegment(2, segments[7]);
		segments[8] = lineSegment(new Vector2D(arm + width / 2, arm + square
				+ buffer), new Vector2D(arm + width / 2, arm * 2 + square));
		segments[7].linkSegment(2, segments[8]);
		segments[3].linkSegment(2, null);

		TravelData.registerTravelPlan(segments[0], 0, 1);
		TravelData.registerTravelPlan(segments[0], 0, 2);
		TravelData.registerTravelPlan(segments[0], 0, 3);

		/***** Startpoint East *****/
		segments[9] = lineSegment(new Vector2D(arm * 2 + square, arm + width
				/ 2), new Vector2D(arm + square + buffer + turn, arm + width
				/ 2));
		startPoints.put(EAST, segments[9]);

		segments[10] = lineSegment(new Vector2D(arm + square + buffer + turn,
				arm + width / 2), new Vector2D(arm + square + buffer, arm
				+ width / 2));
		segments[9].linkSegment(0, segments[10]);
		segments[11] = curveSegment(new Vector2D(arm + square + buffer, arm
				+ width / 2),
				new Vector2D(arm + width * 5 / 2, straight + turn), false);
		segments[10].linkSegment(0, segments[11]);
		segments[12] = lineSegment(new Vector2D(arm + width * 5 / 2, straight
				+ turn), new Vector2D(arm + width * 5 / 2, 0));
		segments[11].linkSegment(0, segments[12]);
		segments[12].linkSegment(0, null);

		segments[13] = lineSegment(new Vector2D(arm + square + buffer + turn,
				arm + width * 3 / 2), new Vector2D(arm + square + buffer, arm
				+ width * 3 / 2));
		segments[9].linkSegment(2, segments[13]);
		segments[14] = curveSegment(new Vector2D(arm + square + buffer, arm
				+ width * 3 / 2), new Vector2D(arm + width / 2, arm + square
				+ buffer), false);
		segments[13].linkSegment(2, segments[14]);
		segments[14].linkSegment(2, segments[8]);
		segments[8].linkSegment(2, null);

		segments[15] = lineSegment(new Vector2D(arm + square + buffer, arm
				+ width / 2), new Vector2D(straight + turn, arm + width / 2));
		segments[10].linkSegment(3, segments[15]);
		segments[16] = lineSegment(new Vector2D(straight + turn, arm + width
				/ 2), new Vector2D(0, arm + width / 2));
		segments[15].linkSegment(3, segments[16]);
		segments[16].linkSegment(3, null);

	}

	private void generateWayPoints() {
		wayPoints = new Vector2D[4][7];

		/**
		 * Send the index to fill and the cardinal direction as a vector.
		 */
		generateWayPoints2(NORTH, new Vector2D(0, -1)); // Note that the vector
														// points to north.
		generateWayPoints2(EAST, new Vector2D(1, 0));
		generateWayPoints2(SOUTH, new Vector2D(0, 1));
		generateWayPoints2(WEST, new Vector2D(-1, 0));
	}

	private void generateWayPoints2(int cardinalDirection, Vector2D dir) {
		dir = dir.unit(); // Make sure it is a unit vector.
		// We begin by disregarding the center of the intersection, we will add
		// them last.
		Vector2D guide = new Vector2D(); // this is now at the center of the
											// intersection.
		Vector2D drawPoint;//
		// Move it to the first intersection pairs.
		guide = guide.plus(dir.mult(square / 2 + buffer));
		wayPoints[cardinalDirection][LEFT] = guide;
		wayPoints[cardinalDirection][EXIT] = guide.plus(dir.rotate(Math.PI/2).mult(width));
		wayPoints[cardinalDirection][RIGHT] = guide.plus(dir.rotate(-Math.PI/2).mult(width));

		guide = guide.plus(dir.mult(turn));
		wayPoints[cardinalDirection][SPLIT_LEFT] = guide;
		wayPoints[cardinalDirection][SPLIT_STRAIGHT] = guide.plus(dir.rotate(-Math.PI/2).mult(width));

		guide = guide.plus(dir.mult(straight));
		wayPoints[cardinalDirection][MAP_ENTRANCE] = guide.plus(dir.rotate(-Math.PI/2).mult(width));
		wayPoints[cardinalDirection][MAP_EXIT] = guide.plus(dir.rotate(Math.PI/2).mult(width));


		// Move this section to the middle.
		for (int i = 0; i < 7; i++) {
			if (wayPoints[cardinalDirection][i] != null)
				wayPoints[cardinalDirection][i].move(intersectionSize / 2,
						intersectionSize / 2);
		}
	}

	private static Segment curveSegment(Vector2D p1, Vector2D p3,
			boolean vertical) {
		Vector2D p2;
		if (vertical) {
			p2 = new Vector2D(p1.x, p3.y);
		} else {
			p2 = new Vector2D(p3.x, p1.y);
		}
		return new Segment(new Bezier2Track(p1, p2, p3));
	}

	private static Segment lineSegment(Vector2D p1, Vector2D p2) {
		return new Segment(new LineTrack(p1, p2));
	}

	@Override
	public void draw(Graphics2D g2d) {
		for (Segment seg : segments) {
			seg.getTrack().draw(g2d);
		}
		g2d.setColor(Color.yellow);
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 7; j++) {
				if (wayPoints[i][j] != null) {
					Vector2D v = wayPoints[i][j].mult(Simulation.SCALE);
					g2d.fillOval((int) v.x - 2, (int) v.y - 2, 4, 4);
				}
			}
		}
	}

	public Segment getEntry(int cardinalDirection) {
		return startPoints.get(cardinalDirection);
	}
}