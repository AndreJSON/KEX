package sim;

import java.util.Iterator;

import map.intersection.Intersection;
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
		if (timer > 0.6) {
			Car car = new Car(CarModelDatabase.getByName("Mazda3"));
			car.setSpeed(50 / 3.6);
			int origin = (int)(Math.random() * 4);
			EntityDatabase.addCar(car, TravelData.createTravelData(origin,
					(int) (Math.random() * 3 + 1 + origin)%4));
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
