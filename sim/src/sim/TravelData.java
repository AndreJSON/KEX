package sim;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import tscs.AbstractTSCS;

import car.Car;

import map.intersection.Segment;

public class TravelData {
	private final static HashMap<Integer, TravelPlan> travelPlans = new HashMap<>();
	private final static HashMap<Segment, LinkedList<Car>> segment2cars = new HashMap<>();
	private static double totLostTime = 0;
	private static double totLostTimeSq = 0;
	private static int carsCompleted = 0;

	private final TravelPlan travelPlan;
	private Car car;
	private int currentIndex;
	private double startTime;

	private double realTime; // Time it took in simulation.

	private TravelData(TravelPlan travelPlan, Car car) {
		this.travelPlan = travelPlan;
		currentIndex = 0;
		this.car = car;
		addToSeg();
		startTime = Simulation.timeElapsed;
		realTime = 0;
	}

	public static LinkedList<Car> getCarsOnSegment(Segment seg) {
		LinkedList<Car> cars = segment2cars.get(seg);
		if (cars != null) {
			return cars;
		}
		cars = new LinkedList<Car>();
		segment2cars.put(seg, cars);
		return cars;
	}

	public static String meanTimeLoss() {
		if (carsCompleted == 0)
			return "NAN";
		else
			return new DecimalFormat("0.0").format(totLostTime / carsCompleted);
	}

	public static String sqrtMeanSqTimeLoss() {
		if (carsCompleted == 0)
			return "NAN";
		return new DecimalFormat("0.0").format(Math.sqrt(totLostTimeSq
				/ carsCompleted));
	}

	private void addToSeg() {
		LinkedList<Car> cars = segment2cars.get(currentSegment());
		if (cars == null) {
			cars = new LinkedList<>();
			segment2cars.put(currentSegment(), cars);
		}
		cars.add(car);
	}

	/**
	 * Returns null if last segment has been passed.
	 * 
	 * @return
	 */
	public Segment nextSegment() {
		getCarsOnSegment(currentSegment()).remove(car);
		currentIndex++;
		if (currentIndex >= travelPlan.segments.size()) {
			car = null;
			realTime = Simulation.timeElapsed - startTime;
			double lostTime = Math.max(0, realTime - travelPlan.optimalTime);
			totLostTime += lostTime;
			carsCompleted++;
			totLostTimeSq += lostTime*lostTime;
			return null;
		}
		addToSeg();
		return travelPlan.segments.get(currentIndex);
	}

	public Segment currentSegment() {
		return travelPlan.segments.get(currentIndex);
	}

	public TravelPlan getTravelPlan() {
		return travelPlan;
	}

	public int getDestination() {
		return travelPlan.destinaion;
	}

	public int getOrigin() {
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
	public static TravelData createTravelData(Car car, int origin,
			int destination) {
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
		@SuppressWarnings("unused")
		private final double optimalTime;

		public TravelPlan(Segment seg, int origin, int destination) {
			this.origin = origin;
			this.destinaion = destination;
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
			optimalTime = dist / AbstractTSCS.SPEED_LIMIT;
		}

		public int hashCode() {
			return travelplanhash(origin, destinaion);
		}

	}

	/**
	 * Get the travel plan from origin to destination.
	 * 
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
