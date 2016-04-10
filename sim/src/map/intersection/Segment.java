package map.intersection;

import java.util.HashMap;
import java.util.ArrayList;

import map.track.*;
import math.Vector2D;

public class Segment {
	private AbstractTrack track;
	@SuppressWarnings("unused")
	private ArrayList<Segment> siblingSegments; // Holds a list of all segments
												// that can cause collision with
												// this one.
	private HashMap<Integer, Segment> split; // Tells which Segment a car has to
												// enter given its destination
												// as key. Null instead of a
												// Segment if destination is
												// reached.
	private int id;
	private static int trackId = 0;

	public Segment(AbstractTrack t) {
		track = t;
		siblingSegments = new ArrayList<>();
		split = new HashMap<>();
		id = ++trackId;
	}

	/**
	 * Make seg the next segment if travelling towards the given destination.
	 */
	public void linkSegment(int destination, Segment seg) {
		split.put(destination, seg);
	}
	
	public Segment nextSegment(int destination){
		return split.get(destination);
	}

	public Vector2D[] getPoints() {
		return track.getPoints();
	}

	public AbstractTrack getTrack() {
		return track;
	}
	
	public int hashCode(){
		return id;
	}
}