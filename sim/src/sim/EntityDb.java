package sim;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

import map.intersection.Segment;
import traveldata.TravelData;

import car.ACar;

/**
 * EntityDatabase manages the collection of Entities that are IN the simulator
 * as objects (not data such as CarModel).
 * 
 * @author henrik
 * 
 */
public class EntityDb {

	// private static final fields
	private static final HashMap<Segment, LinkedList<ACar>> segment2car = new HashMap<>();
	// private static fields
	/**
	 * All the cars
	 */
	private static final HashSet<ACar> cars = new HashSet<>();

	// constructor
	private EntityDb() {
	}

	// public static methods
	public static Collection<ACar> getCars() {
		return cars;
	}

	public static void addCar(ACar car, int from, int to, double timeOfCreation) {
		TravelData travelData = TravelData.getTravelData(from, to, timeOfCreation);
		car.setSpeed(Const.SPEED_LIMIT);
		car.setTravelData(travelData);
		cars.add(car);
		if (Simulation.DEBUG) {
			System.out.println("Created car: " + car);
		}
	}

	public static void removeCar(ACar car) {
		cars.remove(car);
		if (Simulation.DEBUG) {
			System.out.println("Removed " + car);
		}
	}

	public static ACar getFirstCar(Segment segment) {
		LinkedList<ACar> carsOnSegment = segment2car.get(segment);
		if (carsOnSegment == null) {
			carsOnSegment = new LinkedList<>();
			segment2car.put(segment, carsOnSegment);
		}
		return carsOnSegment.getFirst();
	}

	public static LinkedList<ACar> getCarsOnSegment(Segment segment) {
		LinkedList<ACar> carsOnSegment = segment2car.get(segment);
		if (carsOnSegment == null) {
			carsOnSegment = new LinkedList<>();
			segment2car.put(segment, carsOnSegment);
		}
		return carsOnSegment;
	}

	public static void addCarToSegment(ACar car) {
		LinkedList<ACar> carsOnSegment = segment2car.get(car.getSegment());
		if (carsOnSegment == null) {
			carsOnSegment = new LinkedList<>();
			segment2car.put(car.getSegment(), carsOnSegment);
		}
		carsOnSegment.add(car);
	}

	public static void removeCarFromSegment(ACar car) {
		LinkedList<ACar> cars = segment2car.get(car.getSegment());
		cars.remove(car);
	}
}
