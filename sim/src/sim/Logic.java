package sim;

import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.PathIterator;
import java.util.ArrayList;
import java.util.Iterator;

import map.intersection.Segment;
import math.Vector2D;
import car.Car;
import car.CarModelDatabase;
import spawner.PoissonSpawner;
import spawner.Spawner;
import tscs.AbstractTSCS;

/**
 * Logic handles all the logic in the simulation. It accesses objects in the
 * world by using the EntityHandler.
 * 
 * @author henrik
 * 
 */
public class Logic {
	public static final double BREAKING_COEFFICIENT = 2.5; // Factor slower comfortable breaking should compared to the maximum retardation.
	public static final double ACCELERATION_COEFFICIENT = 2;
	public static final double COLUMN_DISTANCE = 2; //How close to each other vehicles will strive to drive when cruising.
	private AbstractTSCS tscs;
	private Spawner[] spawners;

	private static int NORTH = 0, SOUTH = 2, EAST = 1, WEST = 3;

	public Logic(AbstractTSCS tscs) {
		this.tscs = tscs;
		spawners = new Spawner[] { new PoissonSpawner(this, NORTH, 3),
				new PoissonSpawner(this, SOUTH, 3),
				new PoissonSpawner(this, EAST, 3),
				new PoissonSpawner(this, WEST, 3), };
	}

	public void tick(double diff) {
		tscs.tick(diff);
		handleAutonomy(diff);
		moveCars(diff);

		for (Spawner spawer : spawners) {
			spawer.tick(diff);
		}

		checkCollision();

		// TODO: add logic, such as collision detection etc.
	}

	public void setSpawnerOn(boolean on) {
		if (on) {
			for (Spawner spawer : spawners) {
				spawer.on();
			}
		} else {
			for (Spawner spawer : spawners) {
				spawer.off();
			}
		}
	}

	private void handleAutonomy(double diff) {
		Iterator<Car> it = EntityDatabase.getCars().iterator();
		while (it.hasNext()) {
			Car car = it.next();
			if(!car.getAutonomy()) {
				car.setAutonomy(true);
				continue;
			}
			Car inFront = EntityDatabase.nextCar(car);
			if(inFront == null) {
				AbstractTSCS.increaseSpeed(car, car.getMaxAcceleration(diff) / ACCELERATION_COEFFICIENT);
			}
			else {
				double dist = EntityDatabase.distNextCar(car) - inFront.getLength() - COLUMN_DISTANCE;
				double relSpeed = car.getSpeed() - inFront.getSpeed();
				if(dist < 0) { // Closer to the car ahead than desired.
					AbstractTSCS.reduceSpeed(car, car.getMaxRetardation(diff) / BREAKING_COEFFICIENT);
				}
				else if(relSpeed < 0) { // Driving slower than the car ahead.
					AbstractTSCS.increaseSpeed(car, car.getMaxAcceleration(diff) / ACCELERATION_COEFFICIENT);
				}
				else { // Driving faster than the car ahead.
					if(dist < car.getBreakingDistance() * BREAKING_COEFFICIENT * 1.1) {
						AbstractTSCS.reduceSpeed(car, car.getMaxRetardation(diff) / BREAKING_COEFFICIENT);
					}
					else if(dist < car.getBreakingDistance() * BREAKING_COEFFICIENT * 2) {
						AbstractTSCS.reduceSpeed(car, Math.min(car.getMaxRetardation(diff) / BREAKING_COEFFICIENT, relSpeed * relSpeed / (1.9 * dist)));
					}
				}
			}
			car.setSpeed(Math.min(AbstractTSCS.SPEED_LIMIT, car.getSpeed()));
		}
	}

	private void moveCars(double diff) {
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

	private void checkCollision() {
		ArrayList<Shape> carShapes = new ArrayList<>();
		AffineTransform aF;// = new AffineTransform();
		Shape shape;
		Vector2D p;

		for (Car car : EntityDatabase.getCars()) {
			aF = new AffineTransform();
			p = car.getPosition();
			aF.translate(p.x, p.y);
			aF.rotate(car.getHeading());
			shape = aF.createTransformedShape(car.getModel().getShape());
			carShapes.add(shape);
		}
		for (int i = 0; i < carShapes.size(); i++) {
			for (int j = i + 1; j < carShapes.size(); j++) {
				boolean collided = collision(carShapes.get(i), carShapes.get(j));
				if (collided) {
					// TODO: Collision event.
					System.out.println("Collision!");
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
		Car car = new Car(CarModelDatabase.getByName(carName));
		car.setSpeed(AbstractTSCS.SPEED_LIMIT);
		EntityDatabase.addCar(car, TravelData.createTravelData(car, from, to));
		double dist = EntityDatabase.distNextCar(car);
	}
}
