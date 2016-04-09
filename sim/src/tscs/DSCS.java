package tscs;

import java.util.Iterator;

import sim.EntityDatabase;
import car.Car;

public class DSCS extends AbstractTSCS {

	public DSCS() {
	}

	public void tick(double diff) {
		if(emergencyBreak) {
			Iterator<Car> it = EntityDatabase.getCars().iterator();
			while (it.hasNext()) {
				Car car = it.next();
				reduceSpeed(car, car.getMaxRetardation(diff));
			}
		}
	}
}