package sim;

import java.awt.Graphics2D;
import java.util.Collection;
import java.util.HashSet;

import sim.map.track.AbstractTrack;
import sim.vehicle.Car;
import sim.map.intersection.*;

/**
 * Entity handler manages the collection of Entities in the simulation.
 * 
 * @author henrik
 * 
 */
public class EntityHandler {

	private final HashSet<Car> cars;
	private final HashSet<AbstractTrack> tracks;
	private Intersection intersection;

	public EntityHandler() {
		cars = new HashSet<>();
		tracks = new HashSet<>();
	}

	public Collection<Car> getCars() {
		return cars;
	}

	public void addCar(Car newCar) {
		cars.add(newCar);
		if (Simulation.DEBUG) {
			System.out.println("Added " + newCar);
		}
	}

	public void removeCar(Car oldCar) {
		cars.remove(oldCar);
		if (Simulation.DEBUG) {

		}
	}

	public void setIntersection(Intersection i) {
		intersection = i;
	}

	public Segment[] getSegments() {
		return intersection.getSegments();
	}

	public void addTrack(AbstractTrack newTrack) {
		tracks.add(newTrack);
		if (Simulation.DEBUG) {
			System.out.println("Added " + newTrack);
		}
	}

	public void removeTrack(AbstractTrack oldTrack) {
		tracks.remove(oldTrack);
		if (Simulation.DEBUG) {
			System.out.println("Removed " + oldTrack);
		}
	}

	public Intersection getIntersection() {
		return intersection;
	}

}
