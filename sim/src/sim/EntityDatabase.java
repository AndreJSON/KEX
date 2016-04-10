package sim;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import map.intersection.*;
import map.track.AbstractTrack;

import car.Car;

/**
 * EntityDatabase manages the collection of Entities that are IN the simulator
 * as objects (not data such as CarModel).
 * 
 * @author henrik
 * 
 */
public class EntityDatabase {

	/**
	 * All the cars
	 */
	private static final HashSet<Car> cars = new HashSet<>();

	/**
	 * Maps the car to its destination. By the end the collection of TravelData
	 * will be used for statistics.
	 */
	private static final HashMap<Car, TravelData> car2travelData = new HashMap<>();
	private static final Intersection intersection = new Intersection();

	private EntityDatabase() {
	}

	public static Collection<Car> getCars() {
		return cars;
	}

	public static void addCar(Car car, TravelData travelData) {
		car.setTrackPosition(travelData.currentSegment().getTrack()
				.getTrackPosition());
		cars.add(car);
		car2travelData.put(car, travelData);
		if (Simulation.DEBUG) {
			System.out.println("Added " + car);
		}
	}

	public static TravelData getTravelData(Car car) {
		return car2travelData.get(car);
	}

	public static void removeCar(Car car) {
		cars.remove(car);
		if (Simulation.DEBUG) {
			System.out.println("Removed " + car);
		}
	}

	public static double nextCar(Car car) {
		TravelData tD = getTravelData(car);
		Segment tDSegment = tD.currentSegment();
		int destination = tD.getDestination();
		return 0;
	}

	public static Intersection getIntersection() {
		return intersection;
	}
}
