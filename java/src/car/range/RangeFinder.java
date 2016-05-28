package car.range;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import sim.Const;
import sim.EntityDb;
import traveldata.TravelData;

import map.track.TrackPosition;
import math.Vector2D;

import car.AutonomousCar;
import java.util.HashSet;

public class RangeFinder {

    private final AutonomousCar parent;
    private final Vector2D[] points;

    public RangeFinder(final AutonomousCar parent) {
        this.parent = parent;
        final double hWidth = parent.getWidth() / 2 * 1.25;
        points = new Vector2D[2];
        points[0] = new Vector2D(0, -hWidth);
        points[1] = new Vector2D(0, hWidth);
    }

    private Line2D getLine(final Point2D point, final double theta) {
        final Point2D point1 = points[0].rotate(theta).plus(point);
        final Point2D point2 = points[1].rotate(theta).plus(point);
        return new Line2D.Double(point1, point2);
    }

    public RangeData getRange(final double maxDist) {
        TrackPosition trackPos = parent.getTrackPosition().copy();
        final TravelData travelData = parent.getTravelData().copy();
        double distance = 0;
        Line2D line;
        final RangeData rangeData = new RangeData();
        final RangeData newData = new RangeData();
        ArrayList<AutonomousCar> cars = new ArrayList<>();
        HashSet<AutonomousCar> checked = new HashSet<>();
        checked.add(parent);
        while (distance < maxDist) {
            trackPos.move(1);
            distance +=1;
            cars.clear();
            line = getLine(trackPos.getPoint(), trackPos.getTheta());
            EntityDb.retrieveCollision(cars, line.getBounds2D());
            for (final AutonomousCar car : cars) {
                if (checked.contains(car)) {
                    continue;
                }
                if (car.getCollisionBox().collide(line)) {
                    if (rangeData.getCar() == null) {
                        rangeData.set(car, distance - 1);
                    } else {
                        newData.set(car, distance - 1);
                        if (distance(rangeData) > distance(newData)) {
                            rangeData.set(car, distance - 1);
                        }
                    }
                }
                checked.add(car);
            }
            if (trackPos.remaining() < 0 && travelData.hasNext()) {
                trackPos = travelData.next().getTrackPosition(-trackPos.remaining());
            } else if (!travelData.hasNext()) {
                return rangeData;
            }
        }
        return rangeData;
    }

    private double distance(final RangeData rangeData) {
        final AutonomousCar car = rangeData.getCar();
        final double dist = rangeData.distance();
        return car
                .getBreakDistance()
                + dist;
    }
}
