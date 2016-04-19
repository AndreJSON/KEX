package sim.system;

import java.util.Iterator;

import sim.Const;
import sim.EntityDb;
import sim.PerfDb;
import sim.Simulation;

import car.AbstractCar;
import car.AutonomousCar;
import car.range.RangeData;

public class ACarSystem implements SimSystem {
	private final Simulation sim;
	public ACarSystem(Simulation sim) {
		this.sim = sim;
	}

	@Override
	public void tick(double diff) {
		Iterator<AutonomousCar> it = EntityDb.getCars().iterator();
		while (it.hasNext()) {
			AutonomousCar car = it.next();
			handleCar(diff, car);

			car.tick(diff);
			if (car.isFinished()) {
				PerfDb.addData(sim.elapsedTime(), car.getTravelData());
				it.remove();
				continue;
			}

		}
	}

	private void handleCar(double diff, AutonomousCar car) {

		if (!car.isAutonomous()) {
			car.setAutonomous(true);
			return;
		}

		RangeData rangeData;
		AbstractCar inFront;
		double carMax;
		double dist;
		double carBreakVal;
		double carBreakDistance;
		double otherBreakDistance;

		rangeData = car.getRangeData();

		if (rangeData == null) {
			car.setAcc(car.getMaxAcceleration() / Const.ACC_COEF);
		} else {
			inFront = rangeData.getCar();
			carMax = car.getMaxAcceleration() / Const.ACC_COEF;
			dist = rangeData.distance() - Const.COLUMN_DISTANCE;
			carBreakVal = car.getMaxDeceleration() / Const.BREAK_COEF;
			otherBreakDistance = inFront.getBreakDistance(inFront
					.getMaxDeceleration() / Const.BREAK_COEF);
			carBreakDistance = car.getBreakDistance(carBreakVal);
			if (carBreakDistance > dist + otherBreakDistance) {
				car.setAcc(-carBreakVal);
			} else if (carBreakDistance < dist + otherBreakDistance) {
				car.setAcc(carMax);
			} else {
				car.setAcc(0);
			}

		}
	}

}
