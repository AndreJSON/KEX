package traveldata;

import java.util.ArrayList;
import java.util.HashMap;

import map.intersection.Segment;
import sim.Const;

public class TravelPlan {
	private final static HashMap<Integer, TravelPlan> travelPlans = new HashMap<>();

	/**
	 * Class containing precomputer travel paths, optimal time etc.
	 * 
	 * @author henrik
	 * 
	 */
	private final ArrayList<Segment> segments;
	private final int origin, destination;
	private final double optimalTime;

	public TravelPlan(Segment seg, int origin, int destination) {
		this.origin = origin;
		this.destination = destination;
		segments = new ArrayList<>();
		double dist = 0;
		// Bygga upp segments listan.
		while (seg != null) {
			segments.add(seg);
			seg = seg.nextSegment(destination);
		}
		for (Segment seg1 : segments) {
			dist += seg1.length();
		}
		// TODO: Iterate through segments to calculate optimalTime.
		optimalTime = dist / Const.SPEED_LIMIT;
	}

	public double optimalTime() {
		return optimalTime;
	}

	public int numOfSegments() {
		return segments.size();
	}

	public Segment getSegment(int i) {
		return segments.get(i);
	}

	public int hashCode() {
		return travelplanhash(origin, destination);
	}

	public static void registerTravelPlan(Segment startSegment, int origin,
			int destination) {
		TravelPlan travelplan = new TravelPlan(startSegment, origin,
				destination);
		travelPlans.put(travelplan.hashCode(), travelplan);
	}

	/**
	 * Get the travel plan from origin to destination.
	 * 
	 * @param origin
	 * @param destination
	 * @return
	 */
	public static TravelPlan getTravelPlan(int origin, int destination) {
		return travelPlans.get(travelplanhash(origin, destination));
	}

	private static int travelplanhash(int org, int dest) {
		return (org << 4) + dest;
	}

	public int getDestination() {
		return destination;
	}

	public int getOrigin() {
		return origin;
	}
}
