package map.intersection;

import java.util.HashMap;

import map.track.*;
import math.Vector2D;

public class Segment {
	private AbstractTrack track;
	private HashMap<Integer, Segment> split; // Tells which Segment a car has to
												// enter given its destination
												// as key. Null instead of a
												// Segment if destination is
												// reached.
	private int id = -1; //Given a value later

	public Segment(AbstractTrack t) {
		track = t;
		split = new HashMap<>();
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

	public void setHashCode(int value) {
		id = value;
	}

	public double length() {
		return track.length();
	}

}