package map.intersection;

import map.track.*;
import math.Vector2D;
import sim.Const;
import sim.Drawable;
import sim.Simulation;
import sim.TravelData;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashMap;

public class Intersection implements Drawable {

	public static final double straight = 200, turn = 70, buffer = 5,
			width = 3.1;
	public static final double arm = straight + turn + buffer;
	public static final double square = width * 3;
	public static final double intersectionSize = arm * 2 + square;

	// Used in building of intersection.
	private static HashMap<Vector2D, HashMap<Vector2D, Segment>> points2segment;
	// For drawing the segments.
	private ArrayList<Segment> segments;

	private HashMap<Integer, Segment> startPoints; // Gives the first segment
													// coming from each of the 4
													// directions.

	private static Vector2D wayPoints[][];

	public Intersection() {
		points2segment = new HashMap<>();
		startPoints = new HashMap<>();
		init();
	}

	private void init() {
		generateWayPoints();
		generateSegments();
		linkAllSegments();
		createTravelPlans(getEntry(Const.NORTH), Const.NORTH);
		createTravelPlans(getEntry(Const.EAST), Const.EAST);
		createTravelPlans(getEntry(Const.WEST), Const.WEST);
		createTravelPlans(getEntry(Const.SOUTH), Const.SOUTH);
	}

	private void generateSegments() {

		for (int i = 0; i < 4; i++) {
			Segment seg = lineSegment(wayPoints[i][Const.MAP_ENTRANCE],
					wayPoints[i][Const.SPLIT_STRAIGHT]);
			startPoints.put(i, seg);
			lineSegment(wayPoints[i][Const.SPLIT_STRAIGHT],
					wayPoints[i][Const.STRAIGHT]);
			lineSegment(wayPoints[i][Const.SPLIT_LEFT],
					wayPoints[i][Const.LEFT]);
			lineSegment(wayPoints[i][Const.EXIT], wayPoints[i][Const.MAP_EXIT]);

			boolean vertical = i == Const.NORTH || i == Const.SOUTH;
			curveSegment(wayPoints[i][Const.LEFT],
					wayPoints[(i + 1) % 4][Const.EXIT], vertical);
			curveSegment(wayPoints[i][Const.RIGHT],
					wayPoints[(i + 3) % 4][Const.EXIT], vertical);
			curveSegment(wayPoints[i][Const.STRAIGHT],
					wayPoints[(i + 2) % 4][Const.EXIT], vertical);

		}

		segments = new ArrayList<>();

		for (HashMap<Vector2D, Segment> map : points2segment.values()) {
			for (Segment seg : map.values()) {
				segments.add(seg);
			}
		}

	}

	private void linkAllSegments() {
		for (int i = 0; i < 4; i++) {
			Segment seg, left, rs, exit;
			seg = getByPoints(wayPoints[i][Const.MAP_ENTRANCE],
					wayPoints[i][Const.SPLIT_STRAIGHT]);
			rs = getByPoints(wayPoints[i][Const.SPLIT_STRAIGHT],
					wayPoints[i][Const.STRAIGHT]); // Straight and right
			left = getByPoints(wayPoints[i][Const.SPLIT_LEFT],
					wayPoints[i][Const.LEFT]); // to
			// the
			// left
			seg.linkSegment((i + 1) % 4, left);
			seg.linkSegment((i + 2) % 4, rs);
			seg.linkSegment((i + 3) % 4, rs);

			seg = getByPoints(wayPoints[i][Const.STRAIGHT],
					wayPoints[(i + 2) % 4][Const.EXIT]);
			rs.linkSegment((i + 2) % 4, seg);
			exit = getByPoints(wayPoints[(i + 2) % 4][Const.EXIT],
					wayPoints[(i + 2) % 4][Const.MAP_EXIT]);
			seg.linkSegment((i + 2) % 4, exit);

			seg = getByPoints(wayPoints[i][Const.LEFT],
					wayPoints[(i + 1) % 4][Const.EXIT]);
			left.linkSegment((i + 1) % 4, seg);
			exit = getByPoints(wayPoints[(i + 1) % 4][Const.EXIT],
					wayPoints[(i + 1) % 4][Const.MAP_EXIT]);
			seg.linkSegment((i + 1) % 4, exit);

			seg = getByPoints(wayPoints[i][Const.STRAIGHT],
					wayPoints[(i + 3) % 4][Const.EXIT]);
			rs.linkSegment((i + 3) % 4, seg);
			exit = getByPoints(wayPoints[(i + 3) % 4][Const.EXIT],
					wayPoints[(i + 3) % 4][Const.MAP_EXIT]);
			seg.linkSegment((i + 3) % 4, exit);
		}
	}

	private void createTravelPlans(Segment start, int cardinalDirection) {
		int i = cardinalDirection;
		TravelData.registerTravelPlan(start, i, (i + 1) % 4);
		TravelData.registerTravelPlan(start, i, (i + 2) % 4);
		TravelData.registerTravelPlan(start, i, (i + 3) % 4);
	}

	/**
	 * Add a segment from v1 to v2.
	 * 
	 * @param v1
	 * @param v2
	 * @param s
	 */
	private void addSegment(Vector2D v1, Vector2D v2, Segment s) {
		HashMap<Vector2D, Segment> point2segment = points2segment.get(v1);
		if (point2segment == null) {
			point2segment = new HashMap<>();
			points2segment.put(v1, point2segment);
		}
		point2segment.put(v2, s);
	}

	private static Segment getByPoints(Vector2D v1, Vector2D v2) {
		return points2segment.get(v1).get(v2);
	}

	public static Segment getWaitingSegment(int from, int to) {

		int split = Const.SPLIT_STRAIGHT;
		int direction = Const.STRAIGHT;
		if (to == (from + 1) % 4) { // Going left
			split = Const.SPLIT_LEFT;
			direction = Const.LEFT;
		}
		return getByPoints(wayPoints[from][split], wayPoints[from][direction]);
	}

	private void generateWayPoints() {
		wayPoints = new Vector2D[4][7];

		/**
		 * Send the index to fill and the cardinal direction as a vector.
		 */
		generateWayPoints2(Const.NORTH, new Vector2D(0, -1)); // Note that the
																// vector
		// points to north.
		generateWayPoints2(Const.EAST, new Vector2D(1, 0));
		generateWayPoints2(Const.SOUTH, new Vector2D(0, 1));
		generateWayPoints2(Const.WEST, new Vector2D(-1, 0));
	}

	private void generateWayPoints2(int cardinalDirection, Vector2D dir) {
		dir = dir.unit(); // Make sure it is a unit vector.
		// We begin by disregarding the center of the intersection, we will add
		// them last.
		Vector2D guide = new Vector2D(); // this is now at the center of the
											// intersection.
		// Move it to the first intersection pairs.
		guide = guide.plus(dir.mult(square / 2 + buffer));
		wayPoints[cardinalDirection][Const.LEFT] = guide;
		wayPoints[cardinalDirection][Const.EXIT] = guide.plus(dir.rotate(
				Math.PI / 2).mult(width));
		wayPoints[cardinalDirection][Const.RIGHT] = guide.plus(dir.rotate(
				-Math.PI / 2).mult(width));

		guide = guide.plus(dir.mult(turn));
		wayPoints[cardinalDirection][Const.SPLIT_LEFT] = guide;
		wayPoints[cardinalDirection][Const.SPLIT_STRAIGHT] = guide.plus(dir
				.rotate(-Math.PI / 2).mult(width));

		guide = guide.plus(dir.mult(straight));
		wayPoints[cardinalDirection][Const.MAP_ENTRANCE] = guide.plus(dir
				.rotate(-Math.PI / 2).mult(width));
		wayPoints[cardinalDirection][Const.MAP_EXIT] = guide.plus(dir.rotate(
				Math.PI / 2).mult(width));

		// Move this section to the middle.
		for (int i = 0; i < 7; i++) {
			if (wayPoints[cardinalDirection][i] != null)
				wayPoints[cardinalDirection][i].move(intersectionSize / 2,
						intersectionSize / 2);
		}
	}

	private Segment curveSegment(Vector2D p1, Vector2D p3, boolean vertical) {
		Vector2D p2;
		if (vertical) {
			p2 = new Vector2D(p1.x, p3.y);
		} else {
			p2 = new Vector2D(p3.x, p1.y);
		}
		Segment seg = new Segment(new Bezier2Track(p1, p2, p3));
		addSegment(p1, p3, seg);
		return seg;
	}

	private Segment lineSegment(Vector2D p1, Vector2D p2) {
		Segment seg = new Segment(new LineTrack(p1, p2));
		addSegment(p1, p2, seg);
		return seg;
	}

	Area shape;

	@Override
	public void draw(Graphics2D g2d) {
		if (shape == null) {
			shape = new Area();
			Area a = new Area(new Rectangle2D.Double(intersectionSize / 2
					- width * 3 / 2, 0, width * 3, intersectionSize));
			shape.add(a);
			a = new Area(new Rectangle2D.Double(0, intersectionSize / 2 - width
					* 3 / 2, intersectionSize, width * 3));
			shape.add(a);
			double damn = square * 1.8;
			Shape rect = new Rectangle2D.Double(-damn / 2, -damn / 2, damn,
					damn);
			AffineTransform aF = new AffineTransform();
			aF.translate(intersectionSize / 2, intersectionSize / 2);
			aF.rotate(Math.PI / 4);
			shape.add(new Area(aF.createTransformedShape(rect)));
		}
		g2d.setColor(Color.gray);
		g2d.fill(Simulation.SCALER.createTransformedShape(shape));

		g2d.setColor(Color.red);

		if (Simulation.SHOW_TRACKS)
			for (Segment seg : segments) {
				seg.getTrack().draw(g2d);
			}
	}

	public Segment getEntry(int cardinalDirection) {
		return startPoints.get(cardinalDirection);
	}
}