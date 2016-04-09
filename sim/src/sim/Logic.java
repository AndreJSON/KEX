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
	private static final double SPEED_LIMIT = 50 / 3.6;

	public Logic() {
	}

	double d;

	public void tick(double diff) {
		// Call control system here.

		moveCars(diff);

		// TODO: add logic, such as collision detection etc.
	}

	public void moveCars(double diff) {
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

	public void spawnCar(String carName, int from, int to) {
		Car car = new Car(CarModelDatabase.getByName(carName));
		car.setSpeed(SPEED_LIMIT);
		EntityDatabase.addCar(car, TravelData.createTravelData(from, to));
	}
}
