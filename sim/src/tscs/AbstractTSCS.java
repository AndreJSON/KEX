package tscs;

import java.util.Iterator;

import sim.EntityDb;
import car.Car;

public abstract class AbstractTSCS {
	protected boolean emergencyBreak = false;

	public abstract String drawPhase();

	public void tick(double diff) {
		if (emergencyBreak) {
			Iterator<Car> it = EntityDb.getCars().iterator();
			while (it.hasNext()) {
				Car car = it.next();
				car.setAcc(car.getMaxDeceleration());
				car.setAutonomous(false);
			}
		}
	}

	public void setEmergencyBreak(boolean value) {
		emergencyBreak = value;
	}

	public boolean getEmergencyBreak() {
		return emergencyBreak;
	}

}