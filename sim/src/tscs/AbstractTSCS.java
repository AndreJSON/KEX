package tscs;

import java.util.Iterator;

import sim.EntityDatabase;
import car.Car;

public abstract class AbstractTSCS {
	public static final double SPEED_LIMIT = 50 / 3.6;
	protected boolean emergencyBreak = false;

	public AbstractTSCS() {
	}

	public abstract String drawPhase();

	public void tick(double diff) {
		Iterator<Car> it = EntityDatabase.getCars().iterator();
		while (it.hasNext()) {
			Car car = it.next();
			if(emergencyBreak) {
				reduceSpeed(car, car.getMaxRetardation(diff));
			}
			else if(car.getSpeed() < SPEED_LIMIT) {
				increaseSpeed(car, Math.min(car.getMaxAcceleration(diff), SPEED_LIMIT - car.getSpeed()));
			}
		}
	}

	public void setEmergencyBreak(boolean value) {
		emergencyBreak = value;
	}

	public boolean getEmergencyBreak() {
		return emergencyBreak;
	}

	protected void reduceSpeed(Car car, double amount) {
		if(amount < 0) {
			System.out.println("TRYING TO REDUCE SPEED BY A NEGATIVE VALUE");
		}
		car.setSpeed(car.getSpeed() - amount);
	}

	protected void increaseSpeed(Car car, double amount) {
		if(amount < 0) {
			System.out.println("TRYING TO INCREASE SPEED BY A NEGATIVE VALUE");
		}
		car.setSpeed(car.getSpeed() + amount);
	}
}