package sim;

import java.util.Collection;
import java.util.HashSet;

import sim.map.track.AbstractTrack;
import sim.vehicle.Car;

public class EntityHandler {

	private final HashSet<Car> cars;
	private final HashSet<AbstractTrack> tracks;

	public EntityHandler() {
		cars = new HashSet<>();
		tracks = new HashSet<>();
	}

	public Collection<Car> getCars() {
		return cars;
	}

	public void addCar(Car newCar) {
		cars.add(newCar);
		if(Simulation.DEBUG){
			System.out.println("Added " + newCar);
		}
	}

	public void removeCar(Car oldCar) {
		cars.remove(oldCar);
		if(Simulation.DEBUG){
			
		}
	}

	
	// v v v May be removed for Intersection. v v v
	public Collection<AbstractTrack> getTracks() {
		return tracks;
	}

	public void addTrack(AbstractTrack newTrack) {
		tracks.add(newTrack);
		if(Simulation.DEBUG){
			System.out.println("Added " + newTrack);
		}
	}

	public void removeTrack(AbstractTrack oldTrack) {
		tracks.remove(oldTrack);
		if(Simulation.DEBUG){
			System.out.println("Removed " + oldTrack);
		}
	}

}
