package car.range;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import sim.Const;
import sim.EntityDb;
import traveldata.TravelData;

import map.track.TrackPosition;
import math.Vector2D;

import car.AbstractCar;
import car.AutonomousCar;

public class RangeFinder {
	private final AutonomousCar parent;
	private final Vector2D[] points;
	private final static ArrayList<AbstractCar> cars = new ArrayList<>();

	public RangeFinder(AutonomousCar parent) {
		this.parent = parent;
		double width = parent.getWidth() / 2 * 1.05;
		points = new Vector2D[2];
		points[0] = new Vector2D(0, -width);
		points[1] = new Vector2D(0, width);
	}

	private Line2D getLine(Point2D point, double theta) {
		Point2D p1 = points[0].rotate(theta).plus(point);
		Point2D p2 = points[1].rotate(theta).plus(point);
		return new Line2D.Double(p1, p2);
	}

	public RangeData getRange(double maxDist) {
		TrackPosition trackP = parent.getTrackPosition().copy();
		TravelData td = parent.getTravelData().copy();
		double distance = 0;
		Line2D line;
		RangeData rangeData = null;
		while (distance < maxDist) {
			trackP.move(1);
			distance += 1;
			cars.clear();
			line = getLine(trackP.getPoint(), trackP.getTheta());
			EntityDb.retrieveCollision(cars, line.getBounds2D());
			for (AbstractCar car : cars) {
				if (car.equals(parent)) {
					continue;
				}
				if (car.getCollisionBox().collide(line)) {
					if (rangeData == null) {
						rangeData = new RangeData(car, distance - 1);
					} else {
						RangeData newData = new RangeData(car, distance - 1);
						rangeData = getClosest(rangeData, newData);
					}
				}
			}
			if (trackP.remaining() < 0 && td.hasNext()) {
				trackP = td.next().getTrackPosition(-trackP.remaining());
			} else if (!td.hasNext()) {
				return rangeData;
			}
		}
		return rangeData;
	}

	private RangeData getClosest(RangeData rangeData, RangeData newData) {
		double dist1 = distance(rangeData);
		double dist2 = distance(newData);
		if (dist1 > dist2)
			return newData;
		return rangeData;
	}

	private double distance(RangeData rangeData) {
		AbstractCar car = rangeData.getCar();
		double dist = rangeData.distance();
		return car
				.getBreakDistance(car.getMaxDeceleration() / Const.BREAK_COEF)
				+ dist;
	}
}
