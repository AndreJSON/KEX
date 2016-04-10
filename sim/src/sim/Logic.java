package sim;

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
	private AbstractTSCS tscs;
	private Spawner[] spawners;

	private static int NORTH = 0, SOUTH = 2, EAST = 1, WEST = 3;

	public Logic(AbstractTSCS tscs) {
		this.tscs = tscs;
		spawners = new Spawner[] { new PoissonSpawner(this, NORTH, 4),
				new PoissonSpawner(this, SOUTH, 4),
				new PoissonSpawner(this, EAST, 4),
				new PoissonSpawner(this, WEST, 4), };
	}

	double d;

	public void tick(double diff) {
		d += diff;
		tscs.tick(diff);
		moveCars(diff);

		for (Spawner spawer : spawners) {
			spawer.tick(diff);
		}

		checkCollision();

		// TODO: add logic, such as collision detection etc.
	}

	public void moveCars(double diff) {
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
		int collisions = 0;
		for (int i = 0; i < carShapes.size(); i++) {
			for (int j = i + 1; j < carShapes.size(); j++) {
				boolean collided = collision(carShapes.get(i), carShapes.get(j));
				if (collided){
					// TODO: Collision event.
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
				if (line1.intersectsLine(line2)){
					return true;
				}
			}
		}
		return false;
	}


	public void spawnCar(String carName, int from, int to) {
		Car car = new Car(CarModelDatabase.getByName(carName));
		car.setSpeed(AbstractTSCS.SPEED_LIMIT);
		EntityDatabase.addCar(car, TravelData.createTravelData(from, to));
	}
}
