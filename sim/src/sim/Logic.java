package sim;

import java.util.Iterator;

import map.intersection.Segment;
import car.Car;
import car.CarModelDatabase;

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

	double timer = 0;

	public void moveCars(double diff) {
		// TEST CODE
		timer += diff;
		if (timer > 1.2) {
			Car car = new Car(CarModelDatabase.getByName("Tesla S"));
			car.setSpeed(50 / 3.6);
			EntityDatabase.addCar(car, TravelData.createTravelData(0,
					(int) (Math.random() * 3 + 1)));
			timer = 0;
		}
		// TEST CODE END

		Iterator<Car> it = EntityDatabase.getCars().iterator();
		while (it.hasNext()) {
			Car car = it.next();
			car.move(diff);
			double rest = car.remainingOnTrack();
			if (rest <= 0) {
				TravelData tD = EntityDatabase.getTravelData(car);

				if (tD == null) { // Car has no travel plan.
					it.remove();
					continue;
				}

				Segment seg = tD.nextSegment(); // Get the next segment.
				if (seg == null) { // Car reached the end.
					it.remove();
					continue;
				}

				car.setTrackPosition(seg.getTrack().getTrackPosition(-rest));
			}
		}
	}
}
