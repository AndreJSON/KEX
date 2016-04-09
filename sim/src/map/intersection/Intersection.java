package map.intersection;

import map.track.*;
import math.Vector2D;
import sim.Drawable;
import sim.Simulation;
import sim.TravelData;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

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
	
	// Used in building of intersection.
	public HashMap<Vector2D, HashMap<Vector2D, Segment>> points2segment;
	// For drawing the segments.
	public ArrayList<Segment> segments;

	private HashMap<Integer, Segment> startPoints; // Gives the first segment
													// coming from each of the 4
													// directions.

	private Vector2D wayPoints[][];

	public Intersection() {
		points2segment = new HashMap<>();
		startPoints = new HashMap<>();
		init();
	}

	private void init() {
		generateWayPoints();
		generateSegments();
		linkAllSegments();
		createTravelPlans(getEntry(NORTH), NORTH);
		createTravelPlans(getEntry(EAST), EAST);
		createTravelPlans(getEntry(WEST), WEST);
		createTravelPlans(getEntry(SOUTH), SOUTH);
	}

	private void generateSegments() {

		for (int i = 0; i < 4; i++) {
			Segment seg = lineSegment(wayPoints[i][MAP_ENTRANCE],
					wayPoints[i][SPLIT_STRAIGHT]);
			startPoints.put(i, seg);
			lineSegment(wayPoints[i][SPLIT_STRAIGHT], wayPoints[i][STRAIGHT]);
			lineSegment(wayPoints[i][SPLIT_LEFT], wayPoints[i][LEFT]);
			lineSegment(wayPoints[i][EXIT], wayPoints[i][MAP_EXIT]);
			
			boolean vertical = i == NORTH || i == SOUTH;
			curveSegment(wayPoints[i][LEFT], wayPoints[(i+1)%4][EXIT], vertical);
			curveSegment(wayPoints[i][RIGHT], wayPoints[(i+3)%4][EXIT], vertical);
			curveSegment(wayPoints[i][STRAIGHT], wayPoints[(i+2)%4][EXIT], vertical);

		}
		
		segments = new ArrayList<>();
		
		for (HashMap<Vector2D, Segment> map : points2segment.values()){
			for (Segment seg : map.values()){
				segments.add(seg);
			}
		}

	}
	
	private void linkAllSegments(){
		for (int i = 0; i < 4; i++) {
			Segment seg, left, rs, exit;
			seg = getByPoints(wayPoints[i][MAP_ENTRANCE],
					wayPoints[i][SPLIT_STRAIGHT]);
			rs = getByPoints(wayPoints[i][SPLIT_STRAIGHT],
					wayPoints[i][STRAIGHT]); // Straight and right
			left = getByPoints(wayPoints[i][SPLIT_LEFT],
					wayPoints[i][LEFT]); // to the left
			seg.linkSegment((i+1)%4, left);
			seg.linkSegment((i+2)%4, rs);
			seg.linkSegment((i+3)%4, rs);

			seg = getByPoints(wayPoints[i][STRAIGHT],
					wayPoints[(i+2)%4][EXIT]);
			rs.linkSegment((i+2)%4, seg);
			exit = getByPoints(wayPoints[(i+2)%4][EXIT],
					wayPoints[(i+2)%4][MAP_EXIT]);
			seg.linkSegment((i+2)%4, exit);

			seg = getByPoints(wayPoints[i][LEFT],
					wayPoints[(i+1)%4][EXIT]);
			left.linkSegment((i+1)%4, seg);
			exit = getByPoints(wayPoints[(i+1)%4][EXIT],
					wayPoints[(i+1)%4][MAP_EXIT]);
			seg.linkSegment((i+1)%4, exit);

			seg = getByPoints(wayPoints[i][STRAIGHT],
					wayPoints[(i+3)%4][EXIT]);
			rs.linkSegment((i+3)%4, seg);
			exit = getByPoints(wayPoints[(i+3)%4][EXIT],
					wayPoints[(i+3)%4][MAP_EXIT]);
			seg.linkSegment((i+3)%4, exit);
		}
	}

	private void createTravelPlans(Segment start, int cardinalDirection) {
		int i = cardinalDirection;
		i = (i + 1) % 4;
		TravelData.registerTravelPlan(start, cardinalDirection, i);
		i = (i + 1) % 4;
		TravelData.registerTravelPlan(start, cardinalDirection, i);
		i = (i + 1) % 4;
		TravelData.registerTravelPlan(start, cardinalDirection, i);
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

	private Segment getByPoints(Vector2D v1, Vector2D v2) {
		return points2segment.get(v1).get(v2);
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
		wayPoints[cardinalDirection][EXIT] = guide.plus(dir.rotate(Math.PI / 2)
				.mult(width));
		wayPoints[cardinalDirection][RIGHT] = guide.plus(dir.rotate(
				-Math.PI / 2).mult(width));

		guide = guide.plus(dir.mult(turn));
		wayPoints[cardinalDirection][SPLIT_LEFT] = guide;
		wayPoints[cardinalDirection][SPLIT_STRAIGHT] = guide.plus(dir.rotate(
				-Math.PI / 2).mult(width));

		guide = guide.plus(dir.mult(straight));
		wayPoints[cardinalDirection][MAP_ENTRANCE] = guide.plus(dir.rotate(
				-Math.PI / 2).mult(width));
		wayPoints[cardinalDirection][MAP_EXIT] = guide.plus(dir.rotate(
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
		if (shape == null){
			shape = new Area();
			Area a = new Area(new Rectangle2D.Double(intersectionSize/2 - width * 3/2, 0, width * 3, intersectionSize));
			shape.add(a);
			a = new Area(new Rectangle2D.Double(0, intersectionSize/2 - width * 3/2, intersectionSize, width * 3));
			shape.add(a);
			double damn = square*1.8;
			Shape rect = new Rectangle2D.Double(-damn/2, -damn/2, damn, damn);
			AffineTransform aF = new AffineTransform();
			aF.translate(intersectionSize/2, intersectionSize/2);
			aF.rotate(Math.PI/4);
			shape.add(new Area(aF.createTransformedShape(rect)));
		}
		g2d.setColor(Color.gray);
		g2d.fill(Simulation.SCALER.createTransformedShape(shape));
		
		g2d.setColor(Color.red);
		
		for (Segment seg : segments){
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