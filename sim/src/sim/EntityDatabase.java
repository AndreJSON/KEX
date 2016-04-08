package sim;

import java.util.Collection;
import java.util.HashSet;

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

	private static final HashSet<Car> cars = new HashSet<>();;
	private static final HashSet<AbstractTrack> tracks = new HashSet<>();;
	private static final Intersection intersection = new Intersection();

	private EntityDatabase() {
	}

	public static Collection<Car> getCars() {
		return cars;
	}

	public static void addCar(Car car) {
		cars.add(car);
		if (Simulation.DEBUG) {
			System.out.println("Added " + car);
		}
	}

	public static void removeCar(Car car) {
		cars.remove(car);
		if (Simulation.DEBUG) {
			System.out.println("Removed " + car);
		}
	}

	public static Segment[] getSegments() {
		return intersection.getSegments();
	}

	public static void addTrack(AbstractTrack track) {
		tracks.add(track);
		if (Simulation.DEBUG) {
			System.out.println("Added " + track);
		}
	}

	public static void removeTrack(AbstractTrack track) {
		tracks.remove(track);
		if (Simulation.DEBUG) {
			System.out.println("Removed " + track);
		}
	}

	public static Intersection getIntersection() {
		return intersection;
	}
}
