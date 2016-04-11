package sim;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import map.intersection.*;

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
	}

	public static TravelData getTravelData(Car car) {
		return car2travelData.get(car);
	}

	/**
	 * Get the segment the car is on. Return null if the car is not on a segment.
	 * @param car
	 * @return
	 */
	public static Segment getSegmentByCar(Car car) {
		return car2travelData.get(car).currentSegment();
	}

	public static void removeCar(Car car) {
		cars.remove(car);
		if (Simulation.DEBUG) {
			System.out.println("Removed " + car);
		}
	}

	public static Segment currentSegment(Car car) {
		return car2travelData.get(car).currentSegment();
	}

	public static Car nextCar(Car car) {
		TravelData tD;
		Segment searchSegment;
		Iterator<Car> carsOnSegment;
		boolean passedSelf = false;

		tD = getTravelData(car);
		passedSelf = false;
		searchSegment = tD.currentSegment();
		while (true) {
			carsOnSegment = TravelData.getCarsOnSegment(searchSegment).descendingIterator();
			while (carsOnSegment.hasNext()) {
				Car nextCar = carsOnSegment.next();
				if (nextCar.equals(car)) {
					passedSelf = true;
				} else if (passedSelf) {
					return nextCar;
				}
			}
			searchSegment = searchSegment.nextSegment(tD.getDestination());
			if (searchSegment == null) {
				break;
			}
		}

		// Return null if no car is in front of this car.
		return null;
	}

	public static double distNextCar(Car car) {
		// If the car has passed itself during the search.
		boolean passedSelf = false;
		// Get the travel data of the car.
		TravelData tD;
		// Get the current segment the car is on.
		Segment searchSegment;
		// Iterator to check all cars on this segment.
		Iterator<Car> carsOnSegment;
		// The distance to next car.
		double distance;
		//

		distance = 0;
		tD = getTravelData(car);
		passedSelf = false;
		searchSegment = tD.currentSegment();
		while (true) {
			carsOnSegment = TravelData.getCarsOnSegment(searchSegment)
					.descendingIterator();
			while (carsOnSegment.hasNext()) {
				Car nextCar = carsOnSegment.next();
				if (nextCar.equals(car)) {
					passedSelf = true;
				} else if (passedSelf) {
					distance += -nextCar.remainingOnTrack()
							+ car.remainingOnTrack();
					return distance;
				}
			}
			searchSegment = searchSegment.nextSegment(tD.getDestination());
			if (searchSegment == null)
				break;
			distance += searchSegment.length();
		}

		// Return -1 if no car is in front of this car.
		return -1;
	}

	public static Intersection getIntersection() {
		return intersection;
	}
}
