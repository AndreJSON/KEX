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
public class Bezier2Track extends AbstractTrack {

    // Control points 1,2,3.
    /**
     * Curves goes from c1 to c3, with c2 as a control point.
     */
    private transient final Vector2D cPoint1, cPoint2, cPoint3;

    /**
     * Used for stepping through the curve with a certain length.
     */
    private transient final Vector2D helpPoint1, helpPoint2;

    /**
     * The length of the curve.
     */
    private transient final double length;

    /**
     * The shape of the curve.
     */
    private transient final Path2D.Double shape;

    /**
     * Discretized points of the bezier curve.
     */
    private transient Vector2D[] points;

    public Bezier2Track(final Vector2D controlPoint1, final Vector2D controlPoint2, final Vector2D controlPoint3) {
        this.cPoint1 = controlPoint1;
        this.cPoint2 = controlPoint2;
        this.cPoint3 = controlPoint3;

        helpPoint1 = controlPoint1.mult(2).minus(controlPoint2.mult(4)).plus(controlPoint3.mult(2));
        helpPoint2 = controlPoint2.minus(controlPoint1).mult(2);

        generatePoints();

        shape = new Path2D.Double();
        shape.moveTo(controlPoint1.x, controlPoint1.y);
        shape.quadTo(controlPoint2.x, controlPoint2.y, controlPoint3.x, controlPoint3.y);

        // Approximate track length.
        double length = 0;
        final Position position = new Position(0);
        while (position.tVal < 1) {
            position.move(0.001);
            length += 0.001;
        }
        this.length = length;
    }

    /**
     * Generate the discretized points.
     */
    private void generatePoints() {
        final ArrayList<Vector2D> list = new ArrayList<>();

        final Position position = new Position(0);

        list.add(cPoint1);

        while (position.remaining() > 0) {
            position.move(POINT_STEP);
            list.add(position.point);
        }

        list.add(cPoint3);

        points = list.toArray(new Vector2D[list.size()]);
    }

    /**
     * Evaluate the bezier curve with parameter t.
     *
     * @param tVal
     * @return
     */
    private Vector2D evaluate(final double tVal) {
        final double helpTVal = 1 - tVal;
        return cPoint1.mult(helpTVal * helpTVal).plus(cPoint2.mult(2 * helpTVal * tVal)).plus(cPoint3.mult(tVal
                * tVal));
    }

    @Override
    public Vector2D getStartPoint() {
        return cPoint1;
    }

    @Override
    public Vector2D getEndPoint() {
        return cPoint3;
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
    public TrackPosition getTrackPosition(final double distance) {
        return new Position(distance);
    }

    @Override
    public String toString() {
        return getClass().getName() + "{" + cPoint1 + ", " + cPoint2 + ", " + cPoint3 + "}";
    }

    @Override
    public Vector2D[] getPoints() {
        return points;
    }

    @Override
    public void draw(final Graphics2D g2d) {
        g2d.setColor(Color.RED);
        g2d.draw(Simulation.SCALER.createTransformedShape(shape));
        if (!Simulation.DEBUG) {
            return;
        }
        g2d.setColor(Color.MAGENTA);
        Vector2D position;
        position = cPoint1.mult(Simulation.SCALE);
        g2d.drawOval((int) (position.x - 1.5), (int) (position.y - 1.5), 3, 3);
        position = cPoint3.mult(Simulation.SCALE);
        g2d.drawOval((int) (position.x - 1.5), (int) (position.y - 1.5), 3, 3);
    }

    private class Position implements TrackPosition {

        private double tVal, heading, totalDistance;
        private Vector2D point;

        public Position(final double distance) {
            point = new Vector2D();
            tVal = 0;
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
        public void move(final double distance) {

            calcT(distance);
            totalDistance += distance;

            point = evaluate(tVal);
        }

        /**
         * Calculate the new t value when moving with distance. Using Heun's
         * method.
         *
         * @param distance
         */
        private void calcT(final double distance) {
            final double kVal1 = 1. / derive(0).norm();
            final double kVal2 = 1. / derive(distance / 2. * kVal1).norm();
            final double kVal3 = 1. / derive(distance / 2. * kVal2).norm();
            final double kVal4 = 1. / derive(distance * kVal3).norm();
            tVal += distance * (kVal1 + 2 * kVal2 + 2 * kVal3 + kVal4) / 6.;
        }

        public Vector2D derive(final double deltaT) {
            return helpPoint1.mult(tVal + deltaT).plus(helpPoint2);
        }

        @Override
        public double remaining() {
            return length - totalDistance;
        }

        @Override
        public TrackPosition copy() {
            final Position copy = new Position(0);
            copy.tVal = tVal;
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
