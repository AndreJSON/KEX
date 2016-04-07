package sim.map.intersection;

import sim.map.track.*;
import java.util.HashMap;
import java.util.ArrayList;

public class Segment {
	private AbstractTrack track;
	private ArrayList<Segment> siblingSegments; //Holds a list of all segments that can cause collision with this one.
	private HashMap<Integer,Segment> split; //Tells which Segment a car has to enter given its destination as key. Null instead of a Segment if destination is reached.

	public Segment(AbstractTrack t) {
		track = t;
		siblingSegments = new ArrayList<>();
		split = new HashMap<>();
	}

	public void linkSegment(int destination, Segment seg) {
		split.put(destination,seg);
	}

	public Vector2D[] getPoints() {
		return track.getPoints();
	}
}