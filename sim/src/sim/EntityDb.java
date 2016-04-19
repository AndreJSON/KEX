package sim;

import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

import map.intersection.Intersection;
import map.intersection.Segment;
import traveldata.TravelData;
import util.CollisionBox;
import util.QuadTree;

import car.AbstractCar;
import car.AutonomousCar;

/**
 * EntityDatabase manages the collection of Entities that are IN the simulator
 * as objects (not data such as CarModel). The entity database is also
 * responsible to check the consistency of the objects in the car such as making
 * sure that there are no collisions.
 * 
 * @author henrik
 * 
 */
public class EntityDb {

	// private static final fields
	private static final HashMap<Segment, LinkedList<AutonomousCar>> segment2car;
	// private static fields
	/**
	 * All the cars
	 */
	private static final HashSet<AutonomousCar> cars;

	/**
	 * For checking collision.
	 */
	private static final QuadTree<AbstractCar> QUAD_TREE;

	// constructor
	static {
		segment2car = new HashMap<>();
		cars = new HashSet<>();
		QUAD_TREE = new QuadTree<AbstractCar>(new Rectangle(0, 0,
				(int) Intersection.getSize() + 20,
				(int) Intersection.getSize() + 20));
	}

	private EntityDb() {
		throw new AssertionError();
	}

	// public static methods
	public static Collection<AutonomousCar> getCars() {
		return cars;
	}

	public static void addCar(AutonomousCar car, int from, int to,
			double timeOfCreation) {
		TravelData travelData = TravelData.getTravelData(from, to,
				timeOfCreation);
		car.setSpeed(Const.SPEED_LIMIT);
		car.setTravelData(travelData);
		cars.add(car);
	}

	public static void removeCar(AutonomousCar car) {
		cars.remove(car);
		removeCarFromSegment(car);
	}

	public static AutonomousCar getFirstCar(Segment segment) {
		LinkedList<AutonomousCar> carsOnSegment = segment2car.get(segment);
		if (carsOnSegment == null) {
			carsOnSegment = new LinkedList<>();
			segment2car.put(segment, carsOnSegment);
		}
		return carsOnSegment.getFirst();
	}

	public static LinkedList<AutonomousCar> getCarsOnSegment(Segment segment) {
		LinkedList<AutonomousCar> carsOnSegment = segment2car.get(segment);
		if (carsOnSegment == null) {
			carsOnSegment = new LinkedList<>();
			segment2car.put(segment, carsOnSegment);
		}
		return carsOnSegment;
	}

	public static void addCarToSegment(AutonomousCar car) {
		LinkedList<AutonomousCar> carsOnSegment = segment2car.get(car
				.getSegment());
		if (carsOnSegment == null) {
			carsOnSegment = new LinkedList<>();
			segment2car.put(car.getSegment(), carsOnSegment);
		}
		carsOnSegment.add(car);
	}

	public static void removeCarFromSegment(AutonomousCar car) {
		LinkedList<AutonomousCar> cars = segment2car.get(car.getSegment());
		cars.remove(car);
	}

	public static void retrieveCollision(ArrayList<AbstractCar> returnObjects,
			Rectangle2D rect) {
		QUAD_TREE.retrieve(returnObjects, rect);
	}

	@SuppressWarnings("unused")
	public static void checkCollision() {
		QUAD_TREE.clear();
		ArrayList<AbstractCar> returnObjects = new ArrayList<>();

		for (AutonomousCar car : getCars()) {
			car.updateCollisionBox();
			if (!car.isCollidable() || !Simulation.COLLISION) {
				QUAD_TREE.insert(car);
				continue;
			}
			returnObjects.clear();
			returnObjects = QUAD_TREE.retrieve(returnObjects, car.getBounds());
			for (AbstractCar other : returnObjects) {
				if (CollisionBox.collide(car.getCollisionBox(),
						other.getCollisionBox())) {
					throw new RuntimeException("Collision");
				}
			}
			QUAD_TREE.insert(car);
		}
	}
}
