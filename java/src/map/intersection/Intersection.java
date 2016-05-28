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

public final class Intersection {

    // public static fields
    /*
	 * public static final double straight = 130, turn = 70, buffer = 3, width =
	 * 3.2;
     */
    public static final double STRAIGHT = 180, TURN = 70, BUFFER = 3, WIDTH
            = 3.2;
    private static final double ARM = STRAIGHT + TURN + BUFFER;
    public static final double SQUARE = WIDTH * 3;
    private static final double INTERSECTION_SIZE = ARM * 2 + SQUARE;

    private static Area intersectionShape;
    // Used in building of intersection.
    private final static HashMap<Vector2D, HashMap<Vector2D, Segment>> POINT_TO_SEGMENT
            = new HashMap<>();
    // For drawing the segments.
    private static ArrayList<Segment> segments;
    // Gives the first segment coming from each of the 4 directions.
    private final static HashMap<Integer, Segment> START_POINTS = new HashMap<>();
    // Points were segments starts/ends
    private static Vector2D waypoints[][];

    private Intersection() {
        throw new AssertionError();
    }

    public static Segment getWaitingSegment(final int source, final int dest) {

        final int split = Const.SPLIT;
        int direction;
        if (dest == (source + 1) % 4) { // Going left
            direction = Const.LEFT;
        } else {
            direction = Const.STRAIGHT;
        }
        return getByPoints(source, split, source, direction);
    }

    public static double getSize() {
        return INTERSECTION_SIZE;
    }

    public static void draw(final Graphics2D g2d) {
        if (intersectionShape == null) {
            createInterSectionArea();
        }
        g2d.setColor(Color.gray);
        g2d.
                fill(Simulation.SCALER.createTransformedShape(
                        intersectionShape));

        if (Simulation.SHOW_TRACKS) {
            g2d.setColor(Color.red);
            for(Segment seg : segments) {
                seg.getTrack().draw(g2d);
            }
        }
    }

    /**
     * Create the image for the roads.
     */
    private static void createInterSectionArea() {
        final Area area = new Area();
        // Road from top to bottom.
        Area tmpArea = new Area(
                new Rectangle2D.Double(INTERSECTION_SIZE / 2 - WIDTH * 3 / 2, 0,
                        WIDTH * 3, INTERSECTION_SIZE));
        area.add(tmpArea);
        // Road from right to left.
        tmpArea = new Area(new Rectangle2D.Double(0, INTERSECTION_SIZE / 2 - WIDTH
                * 3 / 2, INTERSECTION_SIZE, WIDTH
                * 3));
        area.add(tmpArea);

        // Turn areas.
        final double damn = SQUARE * 2;
        final Shape rect = new Rectangle2D.Double(-damn / 2, -damn / 2, damn, damn);
        final AffineTransform affineTransform = new AffineTransform();
        affineTransform.translate(INTERSECTION_SIZE / 2, INTERSECTION_SIZE / 2);
        affineTransform.rotate(Math.PI / 4);
        area.add(new Area(affineTransform.createTransformedShape(rect)));
        intersectionShape = area;
    }

    public static double getX() {
        return STRAIGHT + TURN + BUFFER;
    }

    public static double getY() {
        return STRAIGHT + TURN + BUFFER;
    }

    public static Segment getEntry(final int cardinalDir) {
        return START_POINTS.get(cardinalDir);
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

    private static void generateWayPoints2(final int cardinalDir, final Vector2D dir) {
        // Guid to where we should draw
        Vector2D guide = new Vector2D(INTERSECTION_SIZE / 2, INTERSECTION_SIZE
                / 2);

        // Level 1, closest to the intersection.
        guide = guide.plus(dir.mult(SQUARE / 2 + BUFFER));
        waypoints[cardinalDir][Const.LEFT] = guide;
        waypoints[cardinalDir][Const.SPLIT_GUIDE1] = guide.plus(dir.
                mult(3 * TURN / 5));
        waypoints[cardinalDir][Const.EXIT] = guide.plus(dir.rotate(
                Math.PI / 2).mult(WIDTH));
        waypoints[cardinalDir][Const.RIGHT] = guide.plus(dir.rotate(
                -Math.PI / 2).mult(WIDTH));

        // Level 2, start of waiting areas.
        guide = guide.plus(dir.mult(TURN));
        waypoints[cardinalDir][Const.SPLIT_GUIDE2] = guide.minus(dir.
                mult(TURN / 10));
        waypoints[cardinalDir][Const.SPLIT] = guide.plus(dir.rotate(
                -Math.PI / 2).mult(WIDTH));

        // Level 3, map exit and entrance
        guide = guide.plus(dir.mult(STRAIGHT));
        waypoints[cardinalDir][Const.MAP_ENTRANCE] = guide.plus(dir.
                rotate(-Math.PI / 2).mult(WIDTH));
        waypoints[cardinalDir][Const.MAP_EXIT] = guide.plus(dir.rotate(
                Math.PI / 2).mult(WIDTH));
    }

    private static void generateSegments() {
        segments = new ArrayList<>();

        for (int i = 0; i < 4; i++) {
            final Segment seg = lineSegment(i, Const.MAP_ENTRANCE, i, Const.SPLIT);
            START_POINTS.put(i, seg);
            lineSegment(i, Const.SPLIT, i, Const.STRAIGHT);
            curveSegment(i, Const.SPLIT, i, Const.LEFT);
            lineSegment(i, Const.EXIT, i, Const.MAP_EXIT);

            final boolean vertical = i == Const.NORTH || i == Const.SOUTH;
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
    private static Segment lineSegment(final int cardinalDir1, final int label1, final int cardinalDir2, final int label2) {
        final Segment seg = new Segment(new LineTrack(waypoints[cardinalDir1][label1],
                waypoints[cardinalDir2][label2]));
        seg.setStart(label1);
        addSegment(waypoints[cardinalDir1][label1], waypoints[cardinalDir2][label2], seg);
        return seg;
    }

    /**
     *
     * @param p1
     * @param p3
     * @param vertical if the enterance to this curve points vertically, else
     * horizontal.
     * @return
     */
    private static Segment curveSegment(final int cardinalDir1, final int label1, final int cardinalDir2, final int label2,
            final boolean vertical) {
        final Vector2D point1 = waypoints[cardinalDir1][label1];
        Vector2D point2;
        final Vector2D point3 = waypoints[cardinalDir2][label2];
        if (vertical) {
            point2 = new Vector2D(point1.x, point3.y);
        } else {
            point2 = new Vector2D(point3.x, point1.y);
        }
        final Segment seg = new Segment(new Bezier2Track(point1, point2, point3));
        addSegment(point1, point3, seg);
        return seg;
    }

    private static Segment curveSegment(final int cardinalDir1, final int label1, final int cardinalDir2, final int label2) {
        final Vector2D point1 = waypoints[cardinalDir1][label1];
        final Vector2D point2 = waypoints[cardinalDir1][Const.SPLIT_GUIDE1];
        final Vector2D point3 = waypoints[cardinalDir1][Const.SPLIT_GUIDE2];
        final Vector2D point4 = waypoints[cardinalDir2][label2];
        final Segment seg = new Segment(new Bezier3Track(point1, point2, point3, point4));
        addSegment(point1, point4, seg);
        return seg;
    }

    /**
     * Add a segment from v1 to v2.
     *
     * @param vector1
     * @param vector2
     * @param seg
     */
    private static void addSegment(final Vector2D vector1, final Vector2D vector2, final Segment seg) {
        segments.add(seg);
        HashMap<Vector2D, Segment> point2segment = POINT_TO_SEGMENT.get(vector1);
        if (point2segment == null) {
            point2segment = new HashMap<>();
            POINT_TO_SEGMENT.put(vector1, point2segment);
        }
        point2segment.put(vector2, seg);
    }

    private static void linkAllSegments() {
        int incr1;
        int incr2;
        int incr3;
        Segment seg;
        Segment left;
        Segment rightStraight;
        Segment exit;
        for (int i = 0; i < 4; i++) {
            incr1 = (i + 1) % 4;
            incr2 = (i + 2) % 4;
            incr3 = (i + 3) % 4;

            seg = getByPoints(i, Const.MAP_ENTRANCE, i, Const.SPLIT);

            rightStraight = getByPoints(i, Const.SPLIT, i, Const.STRAIGHT);
            left = getByPoints(i, Const.SPLIT, i, Const.LEFT);
            seg.linkSegment(incr1, left);
            seg.linkSegment(incr2, rightStraight);
            seg.linkSegment(incr3, rightStraight);

            seg = getByPoints(i, Const.STRAIGHT, incr2, Const.EXIT);
            rightStraight.linkSegment(incr2, seg);
            exit = getByPoints(incr2, Const.EXIT, incr2, Const.MAP_EXIT);
            seg.linkSegment(incr2, exit);

            seg = getByPoints(i, Const.LEFT, incr1, Const.EXIT);
            left.linkSegment(incr1, seg);
            exit = getByPoints(incr1, Const.EXIT, incr1, Const.MAP_EXIT);
            seg.linkSegment(incr1, exit);

            seg = getByPoints(i, Const.STRAIGHT, incr3, Const.EXIT);
            rightStraight.linkSegment(incr3, seg);
            exit = getByPoints(incr3, Const.EXIT, incr3, Const.MAP_EXIT);
            seg.linkSegment(incr3, exit);
        }
    }

    private static Segment getByPoints(final int cardinalDir1, final int label1, final int cardinalDir2, final int label2) {
        return POINT_TO_SEGMENT.get(waypoints[cardinalDir1][label1]).
                get(waypoints[cardinalDir2][label2]);
    }

    private static void createTravelPlans(final Segment start, final int cardinalDir) {
        TravelPlan.registerTravelPlan(start, cardinalDir, (cardinalDir + 1) % 4);
        TravelPlan.registerTravelPlan(start, cardinalDir, (cardinalDir + 2) % 4);
        TravelPlan.registerTravelPlan(start, cardinalDir, (cardinalDir + 3) % 4);
    }
}
