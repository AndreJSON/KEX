package sim;

import java.util.ArrayList;
import java.util.HashMap;

import map.intersection.Segment;

public class TravelData {
	private final static HashMap<Integer, TravelPlan> travelPlans = new HashMap<>();

	private final TravelPlan travelPlan;
	private Segment currentSegment;

	private int currentIndex;

	// private int realTime; // Time it took in simulation.

	private TravelData(TravelPlan travelPlan) {
		this.travelPlan = travelPlan;
		currentIndex = 0;
		currentSegment = travelPlan.segments.get(0);
		// realTime = 0;
	}

	/**
	 * Returns null if last segment has been passed.
	 * 
	 * @return
	 */
	public Segment nextSegment() {
		currentIndex++;
		if (currentIndex >= travelPlan.segments.size()) {
			return null;
		}
		return travelPlan.segments.get(currentIndex);
	}

	public Segment currentSegment() {
		return currentSegment;
	}

	/**
	 * Anropas då då vill skapa en ny bil, ange origin och destination så ordnar
	 * denna metod automatiskt en väg till målet
	 * 
	 * @param origin
	 * @param destination
	 * @return
	 */
	public static TravelData createTravelData(int origin, int destination) {
		TravelData travelData;
		try {
			travelData = new TravelData(getTravelPlan(origin, destination));
		} catch (NullPointerException e) {
			System.out.println("Origin: " + origin);
			System.out.println("Destination: " + destination);
			System.out.println("TravelData error!");
			throw e;
		}
		return travelData;
	}

	public static void registerTravelPlan(Segment startSegment, int origin,
			int destination) {
		TravelPlan travelplan = new TravelPlan(startSegment, origin,
				destination);
		travelPlans.put(travelplan.hashCode(), travelplan);
	}

	/**
	 * Class containing precomputer travel paths, optimal time etc.
	 * 
	 * @author henrik
	 * 
	 */
	private static class TravelPlan {
		final ArrayList<Segment> segments;
		final int origin, destinaion;
		final double optimalTime;

		public TravelPlan(Segment seg, int origin, int destination) {
			this.origin = origin;
			this.destinaion = destination;
			segments = new ArrayList<>();
			// Bygga upp segments listan.
			while (seg != null) {
				segments.add(seg);
				seg = seg.nextSegment(destination);
			}

			// TODO: Iterate through segments to calculate optimalTime.
			optimalTime = -1;
		}

		public int hashCode() {
			return travelplanhash(origin, destinaion);
		}
	}

	private static TravelPlan getTravelPlan(int org, int dest) {
		return travelPlans.get(travelplanhash(org, dest));
	}

	private static int travelplanhash(int org, int dest) {
		return (org << 4) + dest;
	}

}
