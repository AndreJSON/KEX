package map.intersection;

import java.util.ArrayList;
import java.util.HashMap;

import util.CollisionBox;
import car.CarModel;

import map.track.*;
import math.Vector2D;

public class Segment {

	private static final double POINT_STEP = 0.1;
	private static int idTracker = 0;
	private AbstractTrack track;
	
	private HashMap<Integer, Segment> split; // Tells which Segment a car has to
												// enter given its destination
												// as key. Null instead of a
												// Segment if destination is
												// reached.
	private final int id;

	public Segment(AbstractTrack t) {
		track = t;
		split = new HashMap<>();
		id = idTracker++;
	}

	/**
	 * Make seg the next segment if travelling towards the given destination.
	 */
	public void linkSegment(int destination, Segment seg) {
		split.put(destination, seg);
	}

	public Segment nextSegment(int destination) {
		return split.get(destination);
	}

	public Vector2D[] getPoints() {
		return track.getPoints();
	}

	public AbstractTrack getTrack() {
		return track;
	}

	public int hashCode() {
		return id;
	}

	/**
	 * The length of the segment.
	 * @return
	 */
	public double length() {
		return track.length();
	}

	public TrackPosition getTrackPosition(double dist) {
		return track.getTrackPosition(dist);
	}

	/**
	 * Get a array of collision boxes of the car model with a distance of 0.1 m.
	 */
	public ArrayList<CollisionBox> getCollisionBoxes(CarModel carModel) {
		TrackPosition position = track.getTrackPosition();
		Segment seg = this;
		ArrayList<CollisionBox> cBs = new ArrayList<>();
		// The heading of the CAR.
		double chassiHeading = position.getHeading();
		// How far we wish to go.
		double targetDist = length() + carModel.getLength() * 1.2;
		// How far we are on the current segment.
		double stearingWheel;
		double chassiRotation;
		for (int curr = 0; curr * POINT_STEP < targetDist; curr++) {
			position.move(POINT_STEP);
			if (position.remaining() < 0) {
				// Must change track!
				if (seg.split.size() != 0) {

					seg = seg.split.values().iterator().next();
					position = seg.getTrackPosition(-position.remaining());
				}
			}
			// Calculate the chassi heading.
			stearingWheel = position.getHeading() - chassiHeading;
			chassiRotation = (Math.tan(stearingWheel) / carModel.getWheelBase());
			chassiHeading += chassiRotation;
			// Now we calculate the CollisionBox!
			cBs.add(carModel.getCollisionBox().transform(position.getX(),
					position.getY(), chassiHeading));
		}
		return cBs;
	}

}