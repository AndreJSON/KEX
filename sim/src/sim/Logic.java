package sim;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Iterator;

import map.intersection.*;
import car.ACar;
import car.CarModelDb;
import car.RangeData;
import spawner.*;
import tscs.AbstractTSCS;
import util.CollisionBox;
import util.QuadTree;

/**
 * Logic handles all the logic in the simulation. It accesses objects in the
 * world by using the EntityHandler.
 * 
 * @author henrik
 * 
 */
public class Logic {

	// private fields
	private final AbstractTSCS tscs;
	private final SpawnerInterface[] spawners;
	private final Simulation sim;
	/**
	 * For checking collision.
	 */
	private final QuadTree quadTree = new QuadTree(new Rectangle(0, 0,
			(int) Intersection.getSize() + 20,
			(int) Intersection.getSize() + 20));

	// constructor
	Logic(Simulation sim, AbstractTSCS tscs) {
		this.tscs = tscs;
		this.sim = sim;
		// Use BinomialSpawner for heavy traffic.
		// Use PoissonSpawner for light traffic.
		spawners = new SpawnerInterface[] {
				new BinomialSpawner(this, Const.NORTH, 12, 0.5),
				// 10 * 0.5 = 5 <= mean value
				new BinomialSpawner(this, Const.SOUTH, 12, 0.5),
				new BinomialSpawner(this, Const.WEST, 12, 0.5),
				new BinomialSpawner(this, Const.EAST, 12, 0.5) };
	}

	// package methods
	void tick(double diff) {
		updateCollisionBoxes();
		checkCollision();
		handleAutonomous(diff);
		tscs.tick(diff);
		moveCars(diff);
		updateSpawners(diff);
	}

	void setSpawnerOn(boolean isOn) {
		for (SpawnerInterface spawer : spawners) {
			spawer.setOn(isOn);
		}
	}

	// public methods
	public void spawnCar(String carName, int from, int to) {
		ACar car = new ACar(CarModelDb.getByName(carName));
		EntityDb.addCar(car, from, to, sim.elapsedTime());
	}

	// private methods
	private void updateSpawners(double diff) {
		for (SpawnerInterface spawner : spawners) {
			spawner.tick(diff);
		}
	}

	private void handleAutonomous(double diff) {
		Iterator<ACar> it = EntityDb.getCars().iterator();
		while (it.hasNext()) {
			ACar car = it.next();

			if (!car.isAutonomous()) {
				car.setAutonomous(true);
				continue;
			}

			RangeData rangeData = car.getRangeData();
			if (rangeData == null) {
				car.setAcc(car.getMaxAcceleration() / Const.ACC_COEF);
			} else {
				ACar inFront = rangeData.getCar();
				double dist = rangeData.distance() - Const.COLUMN_DISTANCE;
				double carBreakVal = car.getMaxDeceleration()
						/ Const.BREAK_COEF;
				double frontBreakVal = inFront.getMaxDeceleration()
						/ Const.BREAK_COEF;

				double carBreakDistance = car.getBreakDistance(carBreakVal);
				double carMax = car.getMaxAcceleration() / Const.ACC_COEF;
				double otherBreakDistance = inFront
						.getBreakDistance(frontBreakVal);
				double newSpeed = car.getSpeed() + carMax * diff * diff;
				newSpeed = Math.min(newSpeed, Const.SPEED_LIMIT);
				double newBreakDist = ACar.getBreakDistance(newSpeed,
						carBreakVal);
				if (newBreakDist < dist + otherBreakDistance) {
					car.setAcc(carMax);
				} else if (carBreakDistance > dist + otherBreakDistance) {
					car.setAcc(-carBreakVal);
				} else {
					car.setAcc(0);
				}

			}

			if (car.getSpeed() > Const.SPEED_LIMIT) {
				car.setSpeed(Const.SPEED_LIMIT);
			}
		}
	}

	private void moveCars(double diff) {
		Iterator<ACar> it = EntityDb.getCars().iterator();
		while (it.hasNext()) {
			ACar car = it.next();
			car.tick(diff);
			if (car.isFinished()) {
				PerfDb.addData(sim.elapsedTime(), car.getTravelData());
				it.remove();
				continue;
			}
		}
	}

	private void updateCollisionBoxes() {
		for (ACar car : EntityDb.getCars()) {
			if (!car.isCollidable())
				continue;
			car.updateCollisionBox();
		}
	}

	private void checkCollision() {
		quadTree.clear();
		ArrayList<CollisionBox> returnObjects = new ArrayList<>();

		for (ACar car : EntityDb.getCars()) {
			if (!car.isCollidable())
				continue;
			returnObjects.clear();
			returnObjects = quadTree.retrieve(returnObjects,
					car.getCollisionBox());
			for (CollisionBox other : returnObjects) {
				if (CollisionBox.collide(car.getCollisionBox(), other)) {
					// throw new RuntimeException("Collision");
					// System.out.println("Collision!");
				}
			}
			quadTree.insert(car.getCollisionBox());
		}
	}

}
