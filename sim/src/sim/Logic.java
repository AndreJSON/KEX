package sim;

import java.util.Collection;

import sim.vehicle.Car;

public class Logic {
	private final EntityHandler entities;
	private final Simulation sim;

	public Logic(Simulation sim, EntityHandler entities) {
		this.entities = entities;
		this.sim = sim;
	}

	public void tick(double diff) {

		moveCars(diff);

		// TODO: add logic, such as collision detection, intersection manager,
		// etc.
	}

	public void moveCars(double diff) {
		Collection<Car> cars = entities.getCars();
		for (Car car : cars) {
			car.tick(diff);
		}
	}
}
