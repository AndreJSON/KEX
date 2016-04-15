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
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;

public class Intersection {

	// public static fields
	public static final double straight = 0.0001, turn = 200, buffer = 4,
			width = 3.1;
	private static final double arm = straight + turn + buffer;
	private static final double square = width * 3;
	private static final double intersectionSize = arm * 2 + square;

	// Used in building of intersection.
	private static HashMap<Vector2D, HashMap<Vector2D, Segment>> points2segment = new HashMap<>();
	// For drawing the segments.
	private static ArrayList<Segment> segments;
	// Gives the first segment coming from each of the 4 directions.
	private static HashMap<Integer, Segment> startPoints = new HashMap<>();
	// Points were segments starts/ends
	private static Vector2D waypoints[][];

	// The images representing the intersection.
	private static BufferedImage trackImage;
	private static BufferedImage intersectionImage;

	private Intersection() {
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


	private static void generateSegments() {
		segments = new ArrayList<>();

		for (int i = 0; i < 4; i++) {
			Segment seg = lineSegment(waypoints[i][Const.MAP_ENTRANCE],
					waypoints[i][Const.SPLIT_STRAIGHT]);
			startPoints.put(i, seg);
			lineSegment(waypoints[i][Const.SPLIT_STRAIGHT],
					waypoints[i][Const.STRAIGHT]);
			lineSegment(waypoints[i][Const.SPLIT_LEFT],
					waypoints[i][Const.LEFT]);
			lineSegment(waypoints[i][Const.EXIT], waypoints[i][Const.MAP_EXIT]);

			boolean vertical = i == Const.NORTH || i == Const.SOUTH;
			curveSegment(waypoints[i][Const.LEFT],
					waypoints[(i + 1) % 4][Const.EXIT], vertical);
			curveSegment(waypoints[i][Const.RIGHT],
					waypoints[(i + 3) % 4][Const.EXIT], vertical);
			curveSegment(waypoints[i][Const.STRAIGHT],
					waypoints[(i + 2) % 4][Const.EXIT], vertical);

		}


	}

	private static void linkAllSegments() {
		for (int i = 0; i < 4; i++) {
			Segment seg, left, rs, exit;
			seg = getByPoints(waypoints[i][Const.MAP_ENTRANCE],
					waypoints[i][Const.SPLIT_STRAIGHT]);
			rs = getByPoints(waypoints[i][Const.SPLIT_STRAIGHT],
					waypoints[i][Const.STRAIGHT]); // Straight and right
			left = getByPoints(waypoints[i][Const.SPLIT_LEFT],
					waypoints[i][Const.LEFT]); // to
			// the
			// left
			seg.linkSegment((i + 1) % 4, left);
			seg.linkSegment((i + 2) % 4, rs);
			seg.linkSegment((i + 3) % 4, rs);

			seg = getByPoints(waypoints[i][Const.STRAIGHT],
					waypoints[(i + 2) % 4][Const.EXIT]);
			rs.linkSegment((i + 2) % 4, seg);
			exit = getByPoints(waypoints[(i + 2) % 4][Const.EXIT],
					waypoints[(i + 2) % 4][Const.MAP_EXIT]);
			seg.linkSegment((i + 2) % 4, exit);

			seg = getByPoints(waypoints[i][Const.LEFT],
					waypoints[(i + 1) % 4][Const.EXIT]);
			left.linkSegment((i + 1) % 4, seg);
			exit = getByPoints(waypoints[(i + 1) % 4][Const.EXIT],
					waypoints[(i + 1) % 4][Const.MAP_EXIT]);
			seg.linkSegment((i + 1) % 4, exit);

			seg = getByPoints(waypoints[i][Const.STRAIGHT],
					waypoints[(i + 3) % 4][Const.EXIT]);
			rs.linkSegment((i + 3) % 4, seg);
			exit = getByPoints(waypoints[(i + 3) % 4][Const.EXIT],
					waypoints[(i + 3) % 4][Const.MAP_EXIT]);
			seg.linkSegment((i + 3) % 4, exit);
		}
	}

	private static void createTravelPlans(Segment start, int cardinalDirection) {
		int i = cardinalDirection;
		TravelPlan.registerTravelPlan(start, i, (i + 1) % 4);
		TravelPlan.registerTravelPlan(start, i, (i + 2) % 4);
		TravelPlan.registerTravelPlan(start, i, (i + 3) % 4);
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
		return getByPoints(waypoints[from][split], waypoints[from][direction]);
	}

	public static Segment getSquareSegment(int from, int to) {

		int start = Const.STRAIGHT;
		if (to == (from + 1) % 4) { // Going left
			start = Const.LEFT;
		}
		return getByPoints(waypoints[from][start], waypoints[to][Const.EXIT]);
	}

	public static Segment getByID(int id) {
		return segments.get(id);
	}

	public static int numberOfSegments() {
		return segments.size();
	}

	public static double getSize() {
		return intersectionSize;
	}

	private static void generateWayPoints() {
		waypoints = new Vector2D[4][7];

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
		waypoints[cardinalDirection][Const.EXIT] = guide.plus(dir.rotate(
				Math.PI / 2).mult(width));
		waypoints[cardinalDirection][Const.RIGHT] = guide.plus(dir.rotate(
				-Math.PI / 2).mult(width));

		// Level 2, start of waiting areas.
		guide = guide.plus(dir.mult(turn));
		waypoints[cardinalDirection][Const.SPLIT_LEFT] = guide;
		waypoints[cardinalDirection][Const.SPLIT_STRAIGHT] = guide.plus(dir
				.rotate(-Math.PI / 2).mult(width));

		// Level 3, map exit and entrance
		guide = guide.plus(dir.mult(straight));
		waypoints[cardinalDirection][Const.MAP_ENTRANCE] = guide.plus(dir
				.rotate(-Math.PI / 2).mult(width));
		waypoints[cardinalDirection][Const.MAP_EXIT] = guide.plus(dir.rotate(
				Math.PI / 2).mult(width));
	}

	public static Vector2D getWaypoint(int from, int point) {
		return waypoints[from][point];
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
	private static Segment curveSegment(Vector2D p1, Vector2D p3,
			boolean vertical) {
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

	/**
	 * Make a line segment from p1 to p2.
	 * 
	 * @param p1
	 * @param p2
	 * @return
	 */
	private static Segment lineSegment(Vector2D p1, Vector2D p2) {
		Segment seg = new Segment(new LineTrack(p1, p2));
		addSegment(p1, p2, seg);
		return seg;
	}

	public static void draw(Graphics2D g2d) {
		if (intersectionImage == null)
			createRoadImage();
		g2d.drawImage(intersectionImage, null, null);

		if (Simulation.SHOW_TRACKS) {
			if (trackImage == null)
				createTrackImage();
			g2d.drawImage(trackImage, null, null);
		}
	}

	/**
	 * Create the image for the roads.
	 */
	private static void createRoadImage() {
		// Create the background road
		intersectionImage = new BufferedImage(Simulation.SimulationSize,
				Simulation.SimulationSize, BufferedImage.TYPE_INT_ARGB);

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

		Graphics2D g = intersectionImage.createGraphics();
		g.setColor(Color.gray);
		Shape s = Simulation.SCALER.createTransformedShape(area);
		g.fill(s);
	}

	/**
	 * Create an image of the tracks.
	 */
	private static void createTrackImage() {
		trackImage = new BufferedImage(Simulation.SimulationSize,
				Simulation.SimulationSize, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = trackImage.createGraphics();
		g.setColor(Color.red);
		for (Segment seg : segments) {
			seg.getTrack().draw(g);
		}
	}

	public static Segment getEntry(int cardinalDirection) {
		return startPoints.get(cardinalDirection);
	}
}