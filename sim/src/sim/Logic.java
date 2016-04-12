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
	// Factor slower comfortable breaking should compared to the maximum
	// retardation.
	public static final double BREAK_COEF = 2.5;
	public static final double ACC_COEF = 1.5;

	// How close to each other vehicles will strive to drive when cruising.
	// If this value is too low, the cars will collide in curves.
	public static final double COLUMN_DISTANCE = 1.2;
	private AbstractTSCS tscs;
	private SpawnerInterface[] spawners;

	public Logic(AbstractTSCS tscs) {
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

	public void tick(double diff) {
		tscs.tick(diff);
		handleAutonomous(diff);
		moveCars(diff);

		for (SpawnerInterface spawer : spawners) {
			spawer.tick(diff);
		}
		checkCollision();

		// TODO: add logic, such as collision detection etc.
	}

	public void setSpawnerOn(boolean on) {
		if (on) {
			for (SpawnerInterface spawer : spawners) {
				spawer.on();
			}
		} else {
			for (SpawnerInterface spawer : spawners) {
				spawer.off();
			}
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
			Car inFront = EntityDb.nextCar(car);
			if (inFront == null) {
				car.setAcc(car.getMaxAcceleration() / ACC_COEF);
			} else {
				double dist = EntityDb.distNextCar(car) - COLUMN_DISTANCE;
				double car1breakVal = car.getMaxDeceleration() / BREAK_COEF;
				double car2breakVal = inFront.getMaxDeceleration() / BREAK_COEF;

				double car1distance = car.getBreakingDistance(car1breakVal);
				double car2distance = inFront.getBreakingDistance(car2breakVal);

				if (dist < 0.5 && car.getSpeed() > inFront.getSpeed()) {
					car.setAcc(-car1breakVal * 1.1);
				} else if (car1distance < dist + car2distance) {
					car.setAcc(car.getMaxAcceleration() / ACC_COEF);
				} else {
					car.setAcc(-car1breakVal);
				}

			}
			if (car.getSpeed() > AbstractTSCS.SPEED_LIMIT) {
				car.setSpeed(AbstractTSCS.SPEED_LIMIT);
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
				car.setCollision(true);
				car.setTrackPosition(seg.getTrack().getTrackPosition(-rest));
			}
		}
	}

	private QuadTree qT = new QuadTree(0, new Rectangle(0, 0,
			(int) Intersection.intersectionSize + 20,
			(int) Intersection.intersectionSize + 20));
	ArrayList<CollisionBox> collisionBoxes = new ArrayList<>();

	private void checkCollision() {
		AffineTransform aF;
		Vector2D p;
		CollisionBox collisionBox;

		qT.clear();
		collisionBoxes.clear();

		for (Car car : EntityDb.getCars()) {
			if (!car.isCollision())
				continue;
			aF = new AffineTransform();
			p = car.getPosition();
			aF.translate(p.x, p.y);
			aF.rotate(car.getHeading());
			collisionBox = car.getModel().getCollisionBox().transform(aF);
			collisionBoxes.add(collisionBox);
			qT.insert(collisionBox);
		}
		ArrayList<CollisionBox> returnObjects = new ArrayList<>();
		for (int i = 0; i < collisionBoxes.size(); i++) {
			returnObjects.clear();
			returnObjects = qT.retrieve(returnObjects, collisionBoxes.get(i));
			for (int x = 0; x < returnObjects.size(); x++) {
				if (collisionBoxes.get(i).equals(collisionBoxes.get(x)))
					continue;
				if (collisionBoxes.get(i).collide(collisionBoxes.get(x))) {
					throw new RuntimeException("Collision");
				}
			}
		}

	}

	public void spawnCar(String carName, int from, int to) {
		Car car = new Car(CarModelDb.getByName(carName));
		car.setSpeed(AbstractTSCS.SPEED_LIMIT);
		EntityDb.addCar(car, TravelData.createTravelData(car, from, to));
	}
}
