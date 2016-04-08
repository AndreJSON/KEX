package sim;

import java.util.Collection;

import sim.vehicle.Car;

/**
 * Logic handles all the logic in the simulation. It accesses objects in the
 * world by using the EntityHandler.
 * 
 * @author henrik
 * 
 */
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
			car.move(diff);
		}
	}
}
