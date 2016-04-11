package sim;

import java.awt.List;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.PathIterator;
import java.util.ArrayList;
import java.util.Iterator;

import map.intersection.Intersection;
import map.intersection.Segment;
import math.Vector2D;
import car.Car;
import car.CarModelDb;
import spawner.PoissonSpawner;
import spawner.SpawnerInterface;
import tscs.AbstractTSCS;
import tscs.DSCS;
import util.QuadTree;

/**
 * Logic handles all the logic in the simulation. It accesses objects in the
 * world by using the EntityHandler.
 * 
 * @author henrik
 * 
 */
public class Logic {
	public static final double BREAKING_COEFFICIENT = 2.5; // Factor slower
															// comfortable
															// breaking should
															// compared to the
															// maximum
															// retardation.
	public static final double ACCELERATION_COEFFICIENT = 2;
	// How close to each other vehicles will strive to drive when cruising.
	public static final double COLUMN_DISTANCE = 2;
	private AbstractTSCS tscs;
	private SpawnerInterface[] spawners;

	private static int NORTH = 0, SOUTH = 2, EAST = 1, WEST = 3;

	public Logic(AbstractTSCS tscs) {
		this.tscs = tscs;
		spawners = new SpawnerInterface[] {
				new PoissonSpawner(this, NORTH, 3),
				new PoissonSpawner(this, SOUTH, 3),
				new PoissonSpawner(this, EAST, 3),
				new PoissonSpawner(this, WEST, 3) };
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
				car.setAcceleration(car.getMaxAcceleration());
			} else {
				double dist = EntityDb.distNextCar(car) - COLUMN_DISTANCE;

				if (car.getSpeed() + car.getBreakingDistance() < inFront
						.getSpeed() + dist + inFront.getBreakingDistance()) {
					// If the car will catch up, break.

					car.setAcceleration(car.getMaxAcceleration());
				} else {

					car.setAcceleration(-car.getMaxDeceleration());
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
			(int) Intersection.intersectionSize,
			(int) Intersection.intersectionSize));
	private void checkCollision() {
		ArrayList<Shape> carShapes = new ArrayList<>();
		AffineTransform aF;// = new AffineTransform();
		Vector2D p;
		qT.clear();

		for (Car car : EntityDb.getCars()) {
			if (!car.isCollision()) {
				continue;
			}
			aF = new AffineTransform();
			p = car.getPosition();
			aF.translate(p.x, p.y);
			aF.rotate(car.getHeading());
			Shape shape = aF.createTransformedShape(car.getModel().getShape());
			carShapes.add(shape);
			qT.insert(shape);
		}
		ArrayList<Shape> returnObjects = new ArrayList<Shape>();
		for (int i = 0; i < carShapes.size(); i++) {
			returnObjects.clear();
			returnObjects = qT.retrieve(returnObjects, carShapes.get(i));
			for (int x = 0; x < returnObjects.size(); x++) {
				if(carShapes.get(i).equals(carShapes.get(x)))
						continue;
				if (collision(carShapes.get(i), carShapes.get(x))) {
					throw new RuntimeException("Collision!");
				}
			}
		}

	}

	private boolean collision(Shape shape1, Shape shape2) {
		Vector2D shapeCoords[][] = new Vector2D[2][4];

		// Find all corner points.
		PathIterator pI;
		double coords[] = new double[6];
		for (int i = 0; i < 2; i++) {
			if (i == 0) {
				pI = shape1.getPathIterator(null);
			} else {
				pI = shape2.getPathIterator(null);
			}
			int index = 0;
			while (!pI.isDone()) {
				int code = pI.currentSegment(coords);
				if (PathIterator.SEG_LINETO == code) {
					shapeCoords[i][index] = new Vector2D(coords[0], coords[1]);
					index++;
				}
				pI.next();
			}
		}

		for (int i = 0; i < 4; i++) {
			Line2D.Double line1 = new Line2D.Double(shapeCoords[0][i],
					shapeCoords[0][(i + 1) % 4]);
			for (int j = 0; j < 4; j++) {
				Line2D.Double line2 = new Line2D.Double(shapeCoords[1][j],
						shapeCoords[1][(j + 1) % 4]);
				if (line1.intersectsLine(line2)) {
					return true;
				}
			}
		}
		return false;
	}

	public void spawnCar(String carName, int from, int to) {
		Car car = new Car(CarModelDb.getByName(carName));
		car.setSpeed(AbstractTSCS.SPEED_LIMIT);
		EntityDb.addCar(car, TravelData.createTravelData(car, from, to));
	}
}
