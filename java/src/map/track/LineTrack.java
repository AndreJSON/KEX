package map.track;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.util.ArrayList;

import sim.Simulation;
import math.Vector2D;

public class LineTrack extends AbstractTrack {

    private final double length;
    private final Vector2D startPoint, endPoint, unit;

    private final Line2D.Double shape;
    private Vector2D[] points;

    public LineTrack(Vector2D startPoint, Vector2D endPoint) {
        this.startPoint = startPoint;
        this.endPoint = endPoint;
        length = startPoint.distance(endPoint);
        unit = endPoint.minus(startPoint).unit();
        generatePoints();
        shape = new Line2D.Double(startPoint, endPoint);
    }

    private void generatePoints() {
        ArrayList<Vector2D> list = new ArrayList<>();
        list.add(startPoint);

        int curr = 1;
        while (curr * POINT_STEP < length) {
            list.add(startPoint.plus(unit.mult(curr * super.POINT_STEP)));
            curr++;
        }

        list.add(endPoint);
        points = (Vector2D[]) list.toArray(new Vector2D[list.size()]);
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
    public Vector2D getStartPoint() {
        return startPoint;
    }

    @Override
    public Vector2D getEndPoint() {
        return endPoint;
    }

    @Override
    public String toString() {
        return "LineTrack{" + startPoint + ", " + endPoint + "}";
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
        p = startPoint.mult(Simulation.SCALE);
        g2d.drawOval((int) (p.x - 1.5), (int) (p.y - 1.5), 3, 3);
        p = endPoint.mult(Simulation.SCALE);
        g2d.drawOval((int) (p.x - 1.5), (int) (p.y - 1.5), 3, 3);
    }

    public class Position implements TrackPosition {

        private Vector2D point;
        private double totalDistance;

        private Position(double dist) {
            point = new Vector2D(startPoint);
            move(dist);
        }

        @Override
        public double getTheta() {
            return unit.theta();
        }

        @Override
        public void move(double distance) {
            totalDistance += distance;
            point = startPoint.plus(unit.mult(totalDistance));
        }

        @Override
        public Vector2D getPoint() {
            return point;
        }

        @Override
        public double remaining() {
            return length - totalDistance;
        }

        @Override
        public TrackPosition copy() {
            Position copy = new Position(0);
            copy.point = (Vector2D) point.clone();
            copy.totalDistance = totalDistance;
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
