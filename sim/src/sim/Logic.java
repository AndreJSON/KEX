package sim;

import car.Car;

/**
 * Logic handles all the logic in the simulation. It accesses objects in the
 * world by using the EntityHandler.
 * 
 * @author henrik
 * 
 */
public class Logic {
	@SuppressWarnings("unused")
	private final Simulation sim;

	public Logic(Simulation sim) {
		this.sim = sim;
	}

	public void tick(double diff) {

		moveCars(diff);

		// TODO: add logic, such as collision detection, intersection manager,
		// etc.
	}

	public void moveCars(double diff) {
		for (Car car : EntityDatabase.getCars()) {
			car.move(diff);
		}
	}
}
