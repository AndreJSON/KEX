package map.intersection;

import java.util.HashMap;
import java.util.ArrayList;

public class Intersection {
	private Segment[] segments; //Just holds all Segments.
	private HashMap<Integer,Segment> startPoints; //Gives the first segment coming from each of the 4 directions.

	public Intersection() {
		segments = new Segment[9];
		startPoints = new HashMap<>();
		init();
	}

	private class Segment {
		private ArrayList<Segment> siblingSegments; //Holds a list of all segments that can cause collision with this one.
		private HashMap<Integer,Segment> split; //Tells which Segment a car has to enter given its destination as key. Null instead of a Segment if destination is reached.

		public Segment() {
			siblingSegments = new ArrayList<>();
			split = new HashMap<>();
		}

		public void linkSegment(int destination, Segment seg) {
			split.put(destination,seg);
		}
	}

	private void init() {
		for(int i = 0; i < segments.length; i++) {
			segments[0] = new Segment();
		}
		startPoints.put(0,segments[0]);
		segments[0].linkSegment(1,segments[4]);
		segments[4].linkSegment(1,segments[5]);
		segments[5].linkSegment(1,segments[6]);
		segments[6].linkSegment(1,null);
		segments[0].linkSegment(2,segments[1]);
		segments[1].linkSegment(2,segments[2]);
		segments[2].linkSegment(2,segments[3]);
		segments[3].linkSegment(2,null);
		segments[0].linkSegment(3,segments[1]);
		segments[1].linkSegment(3,segments[7]);
		segments[7].linkSegment(3,segments[8]);
		segments[3].linkSegment(3,null);
	}
}