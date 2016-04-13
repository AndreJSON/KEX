package sim;

import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.Iterator;

import map.intersection.*;
import math.Vector2D;
import car.Car;
import car.CarModelDb;
import spawner.*;
import traveldata.TravelData;
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
	private AbstractTSCS tscs;
	private SpawnerInterface[] spawners;
	private Simulation sim;
	/**
	 * For checking collision.
	 */
	private final QuadTree quadTree = new QuadTree(0, new Rectangle(0, 0,
			(int) Intersection.getSize() + 20,
			(int) Intersection.getSize() + 20));

	// constructor
	Logic(Simulation sim, AbstractTSCS tscs) {
		this.sim = sim;
		this.tscs = tscs;
		// Use BinomialSpawner for heavy traffic.
		// Use PoissonSpawner for light traffic.
		spawners = new SpawnerInterface[] {
				new BinomialSpawner(this, Const.NORTH, 8, 0.5),
				// 10 * 0.5 = 5 <= mean value
				new BinomialSpawner(this, Const.SOUTH, 8, 0.5),
				new BinomialSpawner(this, Const.WEST, 8, 0.5),
				new BinomialSpawner(this, Const.EAST, 8, 0.5) };
	}

	// package methods
	void tick(double diff) {
		updateCollisionBoxes();
		checkCollision();
		tscs.tick(diff);
		handleAutonomous(diff);
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
		Car car = new Car(CarModelDb.getByName(carName));
		car.setSpeed(Const.SPEED_LIMIT);
		EntityDb.addCar(car, TravelData.createTravelData(sim, car, from, to));
	}

	// private methods
	private void updateSpawners(double diff) {
		for (SpawnerInterface spawner : spawners) {
			spawner.tick(diff);
		}
	}

	private void handleAutonomous(double diff) {
		Iterator<Car> it = EntityDb.getCars().iterator();
		while (it.hasNext()) {
			Car car = it.next();

			if (!car.isAutonomous()) {
				car.setAutonomous(true);
				continue;
			}

			Car inFront = car.nextCar();
			if (inFront == null) {
				car.setAcc(car.getMaxAcceleration() / Const.ACC_COEF);
			} else {
				double dist = car.distNextCar() - Const.COLUMN_DISTANCE;
				double car1breakVal = car.getMaxDeceleration()
						/ Const.BREAK_COEF;
				double car2breakVal = inFront.getMaxDeceleration()
						/ Const.BREAK_COEF;

				double car1distance = car.getBreakingDistance(car1breakVal);
				double car2distance = inFront.getBreakingDistance(car2breakVal);

				if (dist < 0.5 && car.getSpeed() > inFront.getSpeed()) {
					car.setAcc(-car1breakVal);
				} else if (car1distance < dist + car2distance) {
					car.setAcc(car.getMaxAcceleration() / Const.ACC_COEF);
				} else {
					car.setAcc(-car1breakVal);
				}

			}

			if (car.getSpeed() > Const.SPEED_LIMIT) {
				car.setSpeed(Const.SPEED_LIMIT);
			}
		}
	}

	private void moveCars(double diff) {
		Iterator<Car> it = EntityDb.getCars().iterator();
		while (it.hasNext()) {
			Car car = it.next();
			car.move(diff);
			double rest = car.remainingOnTrack();
			if (rest <= 0) {
				TravelData tD = EntityDb.getTravelData(car);

				if (tD == null) {
					// Car has no travel plan.
					it.remove();
					continue;
				}
				// Get the next segment.
				Segment seg = tD.nextSegment();
				if (seg == null) {
					// Car reached the end.
					it.remove();
					continue;
				}
				car.setCollidable(true);
				car.setTrackPosition(seg.getTrack().getTrackPosition(-rest));
			}
		}
	}

	private void updateCollisionBoxes() {
		AffineTransform aF;
		Vector2D p;

		for (Car car : EntityDb.getCars()) {
			if (!car.isCollidable())
				continue;
			aF = new AffineTransform();
			p = car.getPosition();
			aF.translate(p.x, p.y);
			aF.rotate(car.getHeading());
			car.setCollisionBox(car.getModel().getCollisionBox().transform(aF));
		}
	}

	private void checkCollision() {
		quadTree.clear();
		ArrayList<CollisionBox> returnObjects = new ArrayList<>();

		for (Car car : EntityDb.getCars()) {
			if (!car.isCollidable())
				continue;
			returnObjects.clear();
			returnObjects = quadTree.retrieve(returnObjects,
					car.getCollisionBox());
			for (CollisionBox other : returnObjects) {
				if (CollisionBox.collide(car.getCollisionBox(), other)) {
					throw new RuntimeException("Collision");
				}
			}
			quadTree.insert(car.getCollisionBox());
		}
	}

}
