package map.intersection;

import map.track.*;
import math.Vector2D;
import sim.Const;
import sim.Simulation;
import traveldata.TravelPlan;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashMap;

public class Intersection {

	// public static fields
	/*
	 * public static final double straight = 130, turn = 70, buffer = 3, width =
	 * 3.2;
	 */
	public static final double straight = 130, turn = 70, buffer = 3,
			width = 3.2;
	private static final double arm = straight + turn + buffer;
	public static final double square = width * 3;
	private static final double intersectionSize = arm * 2 + square;

	private static Area intersectionShape;
	// Used in building of intersection.
	private static HashMap<Vector2D, HashMap<Vector2D, Segment>> points2segment = new HashMap<>();
	// For drawing the segments.
	private static ArrayList<Segment> segments;
	// Gives the first segment coming from each of the 4 directions.
	private static HashMap<Integer, Segment> startPoints = new HashMap<>();
	// Points were segments starts/ends
	private static Vector2D waypoints[][];

	private Intersection() {
		throw new AssertionError();
	}

	public static Segment getWaitingSegment(int from, int to) {

		int split = Const.SPLIT;
		int direction = Const.STRAIGHT;
		if (to == (from + 1) % 4) { // Going left
			direction = Const.LEFT;
		}
		return getByPoints(from, split, from, direction);
	}

	public static double getSize() {
		return intersectionSize;
	}

	public static void draw(Graphics2D g2d) {
		if (intersectionShape == null)
			createInterSectionArea();
		g2d.setColor(Color.gray);
		g2d.fill(Simulation.SCALER.createTransformedShape(intersectionShape));

		if (Simulation.SHOW_TRACKS) {
			g2d.setColor(Color.red);
			for (Segment seg : segments) {
				seg.getTrack().draw(g2d);
			}
		}
	}

	/**
	 * Create the image for the roads.
	 */
	private static void createInterSectionArea() {
		Area area = new Area();
		// Road from top to bottom.
		Area a = new Area(new Rectangle2D.Double(intersectionSize / 2 - width
				* 3 / 2, 0, width * 3, intersectionSize));
		area.add(a);
		// Road from right to left.
		a = new Area(new Rectangle2D.Double(0, intersectionSize / 2 - width * 3
				/ 2, intersectionSize, width * 3));
		area.add(a);

		// Turn areas.
		double damn = square * 2;
		Shape rect = new Rectangle2D.Double(-damn / 2, -damn / 2, damn, damn);
		AffineTransform aF = new AffineTransform();
		aF.translate(intersectionSize / 2, intersectionSize / 2);
		aF.rotate(Math.PI / 4);
		area.add(new Area(aF.createTransformedShape(rect)));
		intersectionShape = area;
	}

	public static double getX() {
		return straight + turn + buffer;
	}

	public static double getY() {
		return straight + turn + buffer;
	}

	public static Segment getEntry(int cardinalDirection) {
		return startPoints.get(cardinalDirection);
	}

	static {
		generateWayPoints();
		generateSegments();
		linkAllSegments();
		createTravelPlans(getEntry(Const.NORTH), Const.NORTH);
		createTravelPlans(getEntry(Const.EAST), Const.EAST);
		createTravelPlans(getEntry(Const.WEST), Const.WEST);
		createTravelPlans(getEntry(Const.SOUTH), Const.SOUTH);
	}

	private static void generateWayPoints() {
		waypoints = new Vector2D[4][9];

		/*
		 * Send the index to fill and the cardinal direction as a vector.
		 */
		generateWayPoints2(Const.NORTH, new Vector2D(0, -1));
		generateWayPoints2(Const.EAST, new Vector2D(1, 0));
		generateWayPoints2(Const.SOUTH, new Vector2D(0, 1));
		generateWayPoints2(Const.WEST, new Vector2D(-1, 0));
	}

	private static void generateWayPoints2(int cardinalDirection, Vector2D dir) {
		// The direction we should go when drawing.
		dir = dir.unit();
		// Guid to where we should draw
		Vector2D guide = new Vector2D(intersectionSize / 2,
				intersectionSize / 2);

		// Level 1, closest to the intersection.
		guide = guide.plus(dir.mult(square / 2 + buffer));
		waypoints[cardinalDirection][Const.LEFT] = guide;
		waypoints[cardinalDirection][Const.SPLIT_GUIDE1] = guide.plus(dir
				.mult(3 * turn / 5));
		waypoints[cardinalDirection][Const.EXIT] = guide.plus(dir.rotate(
				Math.PI / 2).mult(width));
		waypoints[cardinalDirection][Const.RIGHT] = guide.plus(dir.rotate(
				-Math.PI / 2).mult(width));

		// Level 2, start of waiting areas.
		guide = guide.plus(dir.mult(turn));
		waypoints[cardinalDirection][Const.SPLIT_GUIDE2] = guide.minus(dir
				.mult(turn / 10));
		waypoints[cardinalDirection][Const.SPLIT] = guide.plus(dir.rotate(
				-Math.PI / 2).mult(width));

		// Level 3, map exit and entrance
		guide = guide.plus(dir.mult(straight));
		waypoints[cardinalDirection][Const.MAP_ENTRANCE] = guide.plus(dir
				.rotate(-Math.PI / 2).mult(width));
		waypoints[cardinalDirection][Const.MAP_EXIT] = guide.plus(dir.rotate(
				Math.PI / 2).mult(width));
	}

	private static void generateSegments() {
		segments = new ArrayList<>();

		for (int i = 0; i < 4; i++) {
			Segment seg = lineSegment(i, Const.MAP_ENTRANCE, i, Const.SPLIT);
			startPoints.put(i, seg);
			lineSegment(i, Const.SPLIT, i, Const.STRAIGHT);
			curveSegment(i, Const.SPLIT, i, Const.LEFT);
			lineSegment(i, Const.EXIT, i, Const.MAP_EXIT);

			boolean vertical = i == Const.NORTH || i == Const.SOUTH;
			curveSegment(i, Const.LEFT, (i + 1) % 4, Const.EXIT, vertical);
			curveSegment(i, Const.RIGHT, (i + 3) % 4, Const.EXIT, vertical);
			curveSegment(i, Const.STRAIGHT, (i + 2) % 4, Const.EXIT, vertical);
		}
	}

	/**
	 * Make a line segment from p1 to p2.
	 * 
	 * @param p1
	 * @param p2
	 * @return
	 */
	private static Segment lineSegment(int d1, int s1, int d2, int s2) {
		Segment seg = new Segment(new LineTrack(waypoints[d1][s1],
				waypoints[d2][s2]));
		seg.setStart(s1);
		addSegment(waypoints[d1][s1], waypoints[d2][s2], seg);
		return seg;
	}

	/**
	 * 
	 * @param p1
	 * @param p3
	 * @param vertical
	 *            if the enterance to this curve points vertically, else
	 *            horizontal.
	 * @return
	 */
	private static Segment curveSegment(int d1, int s1, int d2, int s2,
			boolean vertical) {
		Vector2D p1 = waypoints[d1][s1];
		Vector2D p2;
		Vector2D p3 = waypoints[d2][s2];
		if (vertical) {
			p2 = new Vector2D(p1.x, p3.y);
		} else {
			p2 = new Vector2D(p3.x, p1.y);
		}
		Segment seg = new Segment(new Bezier2Track(p1, p2, p3));
		addSegment(p1, p3, seg);
		return seg;
	}

	private static Segment curveSegment(int d1, int s1, int d2, int s2) {
		Vector2D p1 = waypoints[d1][s1];
		Vector2D p2 = waypoints[d1][Const.SPLIT_GUIDE1];
		Vector2D p3 = waypoints[d1][Const.SPLIT_GUIDE2];
		Vector2D p4 = waypoints[d2][s2];
		Segment seg = new Segment(new Bezier3Track(p1, p2, p3, p4));
		addSegment(p1, p4, seg);
		return seg;
	}

	/**
	 * Add a segment from v1 to v2.
	 * 
	 * @param v1
	 * @param v2
	 * @param s
	 */
	private static void addSegment(Vector2D v1, Vector2D v2, Segment s) {
		segments.add(s);
		HashMap<Vector2D, Segment> point2segment = points2segment.get(v1);
		if (point2segment == null) {
			point2segment = new HashMap<>();
			points2segment.put(v1, point2segment);
		}
		point2segment.put(v2, s);
	}

	private static void linkAllSegments() {
		int i1, i2, i3;
		Segment seg, left, rs, exit;
		for (int i = 0; i < 4; i++) {
			i1 = (i + 1) % 4;
			i2 = (i + 2) % 4;
			i3 = (i + 3) % 4;

			seg = getByPoints(i, Const.MAP_ENTRANCE, i, Const.SPLIT);

			rs = getByPoints(i, Const.SPLIT, i, Const.STRAIGHT);
			left = getByPoints(i, Const.SPLIT, i, Const.LEFT);
			seg.linkSegment(i1, left);
			seg.linkSegment(i2, rs);
			seg.linkSegment(i3, rs);

			seg = getByPoints(i, Const.STRAIGHT, i2, Const.EXIT);
			rs.linkSegment(i2, seg);
			exit = getByPoints(i2, Const.EXIT, i2, Const.MAP_EXIT);
			seg.linkSegment(i2, exit);

			seg = getByPoints(i, Const.LEFT, i1, Const.EXIT);
			left.linkSegment(i1, seg);
			exit = getByPoints(i1, Const.EXIT, i1, Const.MAP_EXIT);
			seg.linkSegment(i1, exit);

			seg = getByPoints(i, Const.STRAIGHT, i3, Const.EXIT);
			rs.linkSegment(i3, seg);
			exit = getByPoints(i3, Const.EXIT, i3, Const.MAP_EXIT);
			seg.linkSegment(i3, exit);
		}
	}

	private static Segment getByPoints(int cd1, int p1, int cd2, int p2) {
		return points2segment.get(waypoints[cd1][p1]).get(waypoints[cd2][p2]);
	}

	private static void createTravelPlans(Segment start, int cardinalDirection) {
		int i = cardinalDirection;
		TravelPlan.registerTravelPlan(start, i, (i + 1) % 4);
		TravelPlan.registerTravelPlan(start, i, (i + 2) % 4);
		TravelPlan.registerTravelPlan(start, i, (i + 3) % 4);
	}
}