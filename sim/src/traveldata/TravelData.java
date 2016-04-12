package traveldata;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.LinkedList;

import sim.Simulation;

import car.Car;

import map.intersection.Segment;

public class TravelData {
	// private static fields
	private final static HashMap<Segment, LinkedList<Car>> segment2cars = new HashMap<>();
	private static double total_time_lost = 0;
	private static double total_time_lost_sq = 0;
	private static int cars_completed = 0;

	// public static methods
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
		if (cars_completed == 0)
			return "NAN";
		else
			return new DecimalFormat("0.0").format(total_time_lost
					/ cars_completed);
	}

	public static String sqrtMeanSqTimeLoss() {
		if (cars_completed == 0)
			return "NAN";
		return new DecimalFormat("0.0").format(Math.sqrt(total_time_lost_sq
				/ cars_completed));
	}

	// static factory
	/**
	 * Anropas då då vill skapa en ny bil, ange origin och destination så ordnar
	 * denna metod automatiskt en väg till målet
	 * 
	 * @param origin
	 * @param destination
	 * @return
	 */
	public static TravelData createTravelData(Simulation sim, Car car,
			int origin, int destination) {
		TravelData travelData;
		try {
			travelData = new TravelData(sim, TravelPlan.getTravelPlan(origin,
					destination), car);
		} catch (NullPointerException e) {
			System.out.println("Origin: " + origin);
			System.out.println("Destination: " + destination);
			System.out.println("TravelData error!");
			throw e;
		}
		return travelData;
	}

	// private fields
	private final TravelPlan travelPlan;
	private final double startTime;
	private final Simulation sim;
	private Car car;
	private int currentIndex;

	// Constructor
	private TravelData(Simulation sim, TravelPlan travelPlan, Car car) {
		this.sim = sim;
		this.travelPlan = travelPlan;
		this.car = car;
		currentIndex = 0;
		startTime = sim.elapsedTime();
		addToSeg();
	}

	// public methods
	/**
	 * Returns null if last segment has been passed.
	 * 
	 * @return
	 */
	public Segment nextSegment() {
		getCarsOnSegment(currentSegment()).remove(car);
		currentIndex++;
		if (currentIndex >= travelPlan.numOfSegments()) {
			car = null;
			double endTime = sim.elapsedTime() - startTime;
			double lostTime = Math.max(0, endTime - travelPlan.optimalTime());
			total_time_lost += lostTime;
			cars_completed++;
			total_time_lost_sq += lostTime * lostTime;
			return null;
		}
		addToSeg();
		return travelPlan.getSegment(currentIndex);
	}

	public Segment currentSegment() {
		return travelPlan.getSegment(currentIndex);
	}

	public TravelPlan getTravelPlan() {
		return travelPlan;
	}

	public int getDestination() {
		return travelPlan.getDestination();
	}

	public int getOrigin() {
		return travelPlan.getOrigin();
	}

	// private methods
	private void addToSeg() {
		LinkedList<Car> cars = segment2cars.get(currentSegment());
		if (cars == null) {
			cars = new LinkedList<>();
			segment2cars.put(currentSegment(), cars);
		}
		cars.add(car);
	}

}
