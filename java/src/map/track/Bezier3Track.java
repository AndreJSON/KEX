package map.track;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import java.util.ArrayList;

import sim.Simulation;

import math.Vector2D;

/**
 * Second degree bezier track.
 *
 * @author henrik
 *
 */
public class Bezier3Track extends AbstractTrack {

    // Control points 1,2,3.
    /**
     * Curves goes from c1 to c3, with c2 as a control point.
     */
    private final Vector2D c1, c2, c3, c4;

    /**
     * Used for stepping through the curve with a certain length.
     */
    private final Vector2D v1, v2, v3;

    /**
     * The length of the curve.
     */
    private final double length;

    /**
     * The shape of the curve.
     */
    private final Path2D.Double shape;

    /**
     * Discretized points of the bezier curve.
     */
    private Vector2D[] points;

    public Bezier3Track(Vector2D c1, Vector2D c2, Vector2D c3, Vector2D c4) {
        this.c1 = c1;
        this.c2 = c2;
        this.c3 = c3;
        this.c4 = c4;

        // v1 = -3A + 9B - 9C + 3D
        // v2 = 6A - 12B + 6C
        // v3 = -3A + 3B
        v1 = c1.mult(-3).plus(c2.mult(9).plus(c3.mult(-9).plus(c4.
                mult(3))));
        v2 = c1.mult(6).plus(c2.mult(-12).plus(c3.mult(6)));
        v3 = c1.mult(-3).plus(c2.mult(3));

        generatePoints();

        shape = new Path2D.Double();
        shape.moveTo(c1.x, c1.y);
        shape.curveTo(c2.x, c2.y, c3.x, c3.y, c4.x, c4.y);

        // Approximate track length.
        double length = 0;
        Position p = new Position(0);
        while (p.t < 1) {
            p.move(0.001);
            length += 0.001;
        }
        this.length = length;
    }

    /**
     * Generate the discretized points.
     */
    private void generatePoints() {
        ArrayList<Vector2D> list = new ArrayList<>();

        Position position = new Position(0);

        list.add(c1);

        while (position.remaining() > 0) {
            position.move(POINT_STEP);
            list.add(position.point);
        }

        list.add(c3);

        points = list.toArray(new Vector2D[list.size()]);
    }

    /**
     * Evaluate the bezier curve with parameter t.
     *
     * @param t
     * @return
     */
    private Vector2D evaluate(double t) {
        double t2 = 1 - t;
        return c1.mult(t2 * t2 * t2).plus(c2.mult(3 * t2 * t2 * t))
                .plus(c3.mult(3 * t2 * t * t)).plus(c4.mult(t * t * t));
    }

    @Override
    public Vector2D getStartPoint() {
        return c1;
    }

    @Override
    public Vector2D getEndPoint() {
        return c4;
    }

    @Override
    public double getLength() {
        return length;
    }

    @Override
    public TrackPosition getTrackPosition() {
        return new Position(0);
    }

    @Override
    public TrackPosition getTrackPosition(double distance) {
        return new Position(distance);
    }

    @Override
    public String toString() {
        return getClass().getName() + "{" + c1 + ", " + c2 + ", " + c3 + ","
                + c4 + "}";
    }

    @Override
    public Vector2D[] getPoints() {
        return points;
    }

    @Override
    public void draw(Graphics2D g2d) {
        g2d.setColor(Color.RED);
        g2d.draw(Simulation.SCALER.createTransformedShape(shape));
        if (!Simulation.DEBUG) {
            return;
        }
        g2d.setColor(Color.MAGENTA);
        Vector2D p;
        p = c1.mult(Simulation.SCALE);
        g2d.drawOval((int) (p.x - 1.5), (int) (p.y - 1.5), 3, 3);
        p = c2.mult(Simulation.SCALE);
        g2d.drawOval((int) (p.x - 1.5), (int) (p.y - 1.5), 3, 3);
        p = c3.mult(Simulation.SCALE);
        g2d.drawOval((int) (p.x - 1.5), (int) (p.y - 1.5), 3, 3);
        p = c4.mult(Simulation.SCALE);
        g2d.drawOval((int) (p.x - 1.5), (int) (p.y - 1.5), 3, 3);
    }

    private class Position implements TrackPosition {

        private double t, heading, totalDistance;
        private Vector2D point;

        public Position(double distance) {
            point = new Vector2D();
            t = 0;
            move(distance);
        }

        @Override
        public Vector2D getPoint() {
            return point;
        }

        @Override
        public double getTheta() {
            return derive(0).theta();
        }

        @Override
        public void move(double distance) {

            calcT(distance);
            totalDistance += distance;

            point = evaluate(t);
        }

        /**
         * Calculate the new t value when moving with distance. Using Heun's
         * method.
         *
         * @param distance
         */
        private void calcT(double distance) {
            double k1 = 1. / derive(0).norm();
            double k2 = 1. / derive(distance / 2. * k1).norm();
            double k3 = 1. / derive(distance / 2. * k2).norm();
            double k4 = 1. / derive(k3).norm();
            t += distance * (k1 + 2 * k2 + 2 * k3 + k4) / 6.;
        }

        private Vector2D derive(double dt) {
            double t = this.t + dt;
            return v1.mult(t * t).plus(v2.mult(t)).plus(v3);
        }

        @Override
        public double remaining() {
            return length - totalDistance;
        }

        @Override
        public TrackPosition copy() {
            Position copy = new Position(0);
            copy.t = t;
            copy.heading = heading;
            copy.totalDistance = totalDistance;
            copy.point = (Vector2D) point.clone();
            return copy;
        }

        @Override
        public double getX() {
            return point.x;
        }

        @Override
        public double getY() {
            return point.y;
        }

    }
}
