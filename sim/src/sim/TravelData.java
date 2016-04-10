package sim;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;

import car.Car;

import map.intersection.Segment;

public class TravelData {
	private final static HashMap<Integer, TravelPlan> travelPlans = new HashMap<>();
	private final static HashMap<Segment, LinkedList<Car>> segment2cars = new HashMap<>();

	private final TravelPlan travelPlan;
	private Segment currentSegment;
	private Car car;
	private int currentIndex;

	// private int realTime; // Time it took in simulation.

	private TravelData(TravelPlan travelPlan, Car car) {
		this.travelPlan = travelPlan;
		currentIndex = 0;
		currentSegment = travelPlan.segments.get(0);
		// realTime = 0;
	}
	
	public static LinkedList<Car> getCarsOnSegment(Segment seg) {
		LinkedList<Car> cars = segment2cars.get(seg);
		if(cars != null){
			return cars;
		}
		cars = new LinkedList<Car>();
		segment2cars.put(seg, cars);
		return cars;
	}

	/**
	 * Returns null if last segment has been passed.
	 * 
	 * @return
	 */
	public Segment nextSegment() {
		getCarsOnSegment(currentSegment).remove(car);
		currentIndex++;
		if (currentIndex >= travelPlan.segments.size()) {
			car = null;
			return null;
		}
		currentSegment = travelPlan.segments.get(currentIndex);
		if(getCarsOnSegment(currentSegment) == null)
		getCarsOnSegment(currentSegment).add(car);
		return travelPlan.segments.get(currentIndex);
	}

	public Segment currentSegment() {
		return currentSegment;
	}
	
	public TravelPlan getTravelPlan(){
		return travelPlan;
	}
	
	public int getDestination(){
		return travelPlan.destinaion;
	}

	public int getOrigin(){
		return travelPlan.origin;
	}

	/**
	 * Anropas då då vill skapa en ny bil, ange origin och destination så ordnar
	 * denna metod automatiskt en väg till målet
	 * 
	 * @param origin
	 * @param destination
	 * @return
	 */
	public static TravelData createTravelData(Car car, int origin, int destination) {
		TravelData travelData;
		try {
			travelData = new TravelData(getTravelPlan(origin, destination), car);
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
		private final ArrayList<Segment> segments;
		private final int origin, destinaion;
		private final double optimalTime;

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

	/**
	 * Get the travel plan from origin to destination.
	 * @param origin
	 * @param destination
	 * @return
	 */
	private static TravelPlan getTravelPlan(int origin, int destination) {
		return travelPlans.get(travelplanhash(origin, destination));
	}

	private static int travelplanhash(int org, int dest) {
		return (org << 4) + dest;
	}

}
