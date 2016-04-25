package map.intersection;

import java.util.HashMap;

import sim.Const;

import map.track.*;
import math.Vector2D;

public class Segment {

	private static int idTracker = 0;
	private AbstractTrack track;

	private HashMap<Integer, Segment> split; // Tells which Segment a car has to
												// enter given its destination
												// as key. Null instead of a
												// Segment if destination is
												// reached.
	private final int id;
	private int start;

	public Segment(AbstractTrack t) {
		track = t;
		split = new HashMap<>();
		id = idTracker++;
	}

	public void setStart(int start) {
		this.start = start;
	}

	public boolean preIntersection() {
		return start == Const.MAP_ENTRANCE || start == Const.MAP_EXIT;
	}

	/**
	 * Make seg the next segment if travelling towards the given destination.
	 */
	public void linkSegment(int destination, Segment seg) {
		split.put(destination % 4, seg);
	}

	public Segment nextSegment(int destination) {
		return split.get(destination % 4);
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
	 * 
	 * @return
	 */
	public double length() {
		return track.length();
	}

	public TrackPosition getTrackPosition(double dist) {
		return track.getTrackPosition(dist);
	}

}