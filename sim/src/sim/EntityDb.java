package sim;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

import traveldata.TravelData;

import car.Car;

import map.intersection.*;

/**
 * EntityDatabase manages the collection of Entities that are IN the simulator
 * as objects (not data such as CarModel).
 * 
 * @author henrik
 * 
 */
public class EntityDb {

	// private static fields
	/**
	 * All the cars
	 */
	private static final HashSet<Car> cars = new HashSet<>();

	/**
	 * Maps the car to its destination. By the end the collection of TravelData
	 * will be used for statistics.
	 */
	private static final HashMap<Car, TravelData> car2travelData = new HashMap<>();

	// constructor
	private EntityDb() {
	}

	// public static methods
	public static Collection<Car> getCars() {
		return cars;
	}

	public static void addCar(Car car, TravelData travelData) {
		car.setTrackPosition(travelData.currentSegment().getTrack()
				.getTrackPosition());
		cars.add(car);
		car2travelData.put(car, travelData);
	}

	public static TravelData getTravelData(Car car) {
		return car2travelData.get(car);
	}

	/**
	 * Get the segment the car is on. Return null if the car is not on a
	 * segment.
	 * 
	 * @param car
	 * @return
	 */
	public static Segment getSegmentByCar(Car car) {
		return car2travelData.get(car).currentSegment();
	}

	public static void removeCar(Car car) {
		cars.remove(car);
		car2travelData.remove(car);
		if (Simulation.DEBUG) {
			System.out.println("Removed " + car);
		}
	}

	public static Segment currentSegment(Car car) {
		return car2travelData.get(car).currentSegment();
	}

}
