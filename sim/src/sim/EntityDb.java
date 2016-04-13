package sim;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

import map.intersection.Segment;
import traveldata.TravelData;

import car.Car;

/**
 * EntityDatabase manages the collection of Entities that are IN the simulator
 * as objects (not data such as CarModel).
 * 
 * @author henrik
 * 
 */
public class EntityDb {

	// private static final fields
	private static final HashMap<Segment, LinkedList<Car>> segment2car = new HashMap<>();
	// private static fields
	/**
	 * All the cars
	 */
	private static final HashSet<Car> cars = new HashSet<>();

	// constructor
	private EntityDb() {
	}

	// public static methods
	public static Collection<Car> getCars() {
		return cars;
	}

	public static void addCar(Car car, int from, int to) {
		TravelData travelData = TravelData.getTravelData(from, to);
		car.setTravelData(travelData);
		cars.add(car);
		if (Simulation.DEBUG) {
			System.out.println("Created car: " + car);
		}
	}

	public static void removeCar(Car car) {
		cars.remove(car);
		if (Simulation.DEBUG) {
			System.out.println("Removed " + car);
		}
	}

	public static Car getFirstCar(Segment segment) {
		LinkedList<Car> carsOnSegment = segment2car.get(segment);
		if (carsOnSegment == null) {
			carsOnSegment = new LinkedList<>();
			segment2car.put(segment, carsOnSegment);
		}
		return carsOnSegment.getFirst();
	}

	public static LinkedList<Car> getCarsOnSegment(Segment segment) {
		LinkedList<Car> carsOnSegment = segment2car.get(segment);
		if (carsOnSegment == null) {
			carsOnSegment = new LinkedList<>();
			segment2car.put(segment, carsOnSegment);
		}
		return carsOnSegment;
	}

	public static void addCarToSegment(Car car) {
		LinkedList<Car> carsOnSegment = segment2car.get(car.getSegment());
		if (carsOnSegment == null) {
			carsOnSegment = new LinkedList<>();
			segment2car.put(car.getSegment(), carsOnSegment);
		}
		carsOnSegment.add(car);
	}

	public static void removeCarFromSegment(Car car) {
		LinkedList<Car> cars = segment2car.get(car.getSegment());
		cars.remove(car);
	}
}
