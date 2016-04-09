package tscs;

import sim.EntityDatabase;
import car.Car;

public abstract class AbstractTSCS {
	protected boolean emergencyBreak = false;

	public AbstractTSCS() {
	}

	public abstract void tick(double diff);

	public void setEmergencyBreak(boolean value) {
		emergencyBreak = value;
	}

	protected void reduceSpeed(Car car, double amount) {
		if(amount < 0) {
			System.out.println("TRYING TO REDUCE SPEED BY A NEGATIVE VALUE");
		}
		car.setSpeed(car.getSpeed() - amount);
	}
}