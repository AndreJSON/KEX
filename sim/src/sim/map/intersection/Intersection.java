package map.intersection;

import java.util.HashMap;
import java.util.ArrayList;

public class Intersection {
	private ArrayList<Segment> segments; //Just holds all Segments.
	private HashMap<Integer,Segment> startPoints; //Gives the first segment coming from each of the 4 directions.

	public Intersection() {
		segments = new ArrayList<>();
		startPoints = new HashMap<>();
		init();
	}

	private class Segment {
		private ArrayList<Segment> siblingSegments; //Holds a list of all segments that can cause collision with this one.
		private HashMap<Integer,Segment> split; //Tells which Segment a car has to enter given its destination as key. Null instead of a Segment if destination is reached.

		public Segment() {
			siblingSegments = new ArrayList<>();
			split = new HashMap<>();
			split.put(0,null);
			split.put(1,null);
			split.put(2,null);
			split.put(3,null);
		}
	}

	private void init() {
	}
}