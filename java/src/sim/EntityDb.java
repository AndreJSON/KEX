package sim;

import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import map.intersection.Intersection;
import map.intersection.Segment;
import traveldata.TravelData;
import util.CollisionBox;
import util.QuadTree;

import car.AutonomousCar;
import java.awt.Graphics2D;

/**
 * EntityDatabase manages the collection of Entities that are IN the simulator
 * as objects (not data such as CarModel). The entity database is also
 * responsible to check the consistency of the objects in the car such as making
 * sure that there are no collisions.
 *
 * @author henrik
 *
 */
public final class EntityDb {

    // private static final fields
    private static final HashMap<Segment, LinkedList<AutonomousCar>> SEGMENT_TO_CAR;
    // private static fields
    /**
     * All the cars
     */
    private static final HashSet<AutonomousCar> CARS;

    /**
     * For checking collision.
     */
    private static final QuadTree<AutonomousCar> QUAD_TREE;

    // constructor
    static {
        SEGMENT_TO_CAR = new HashMap<>();
        CARS = new HashSet<AutonomousCar>();
        QUAD_TREE = new QuadTree<>(
                new Rectangle(0, 0, (int) Intersection.getSize() + 40,
                        (int) Intersection.getSize() + 40));
    }

    private EntityDb() {
        throw new AssertionError();
    }

    public static void draw(Graphics2D g2d) {
        QUAD_TREE.draw(g2d);
    }

    // public static methods
    public static Collection<AutonomousCar> getCars() {
        return CARS;
    }

    public static void addCar(final AutonomousCar car, final int source, final int destination,
            final double timeOfCreation) {
        final TravelData travelData = TravelData.getTravelData(source, destination,
                timeOfCreation);
        car.setSpeed(Const.SPEED_LIMIT);
        car.setTravelData(travelData);
        CARS.add(car);
    }

    public static void removeCar(final AutonomousCar car) {
        CARS.remove(car);
        removeCarFromSegment(car);
    }

    public static AutonomousCar getFirstCar(final Segment segment) {
        LinkedList<AutonomousCar> carsOnSegment = SEGMENT_TO_CAR.get(segment);
        if (carsOnSegment == null) {
            carsOnSegment = new LinkedList<>();
            SEGMENT_TO_CAR.put(segment, carsOnSegment);
        }
        return carsOnSegment.getFirst();
    }

    public static LinkedList<AutonomousCar> getCarsOnSegment(
            final Segment segment) {
        LinkedList<AutonomousCar> carsOnSegment = SEGMENT_TO_CAR.get(segment);
        if (carsOnSegment == null) {
            carsOnSegment = new LinkedList<>();
            SEGMENT_TO_CAR.put(segment, carsOnSegment);
        }
        return carsOnSegment;
    }

    public static void addCarToSegment(final AutonomousCar car) {
        LinkedList<AutonomousCar> carsOnSegment = SEGMENT_TO_CAR.get(car.
                getSegment());
        if (carsOnSegment == null) {
            carsOnSegment = new LinkedList<>();
            SEGMENT_TO_CAR.put(car.getSegment(), carsOnSegment);
        }
        carsOnSegment.add(car);
    }

    public static void removeCarFromSegment(final AutonomousCar car) {
        final LinkedList<AutonomousCar> cars = SEGMENT_TO_CAR.get(car.getSegment());
        cars.remove(car);
    }

    public static long retrieveCollision(final ArrayList<AutonomousCar> returnObjects,
            final Rectangle2D rect) {
        return QUAD_TREE.retrieve(returnObjects, rect);
    }

    public static void checkCollision() {
        QUAD_TREE.clear();
        final List<AutonomousCar> returnObjects = new ArrayList<>();
        for (final AutonomousCar car : getCars()) {
            car.updateCollisionBox();
            if (Simulation.COLLISION && car.isCollidable()) {
                returnObjects.clear();
                QUAD_TREE.retrieve(returnObjects, car.getBounds());
                for (final AutonomousCar other : returnObjects) {
                    if (CollisionBox.collide(car.getCollisionBox(), other.
                            getCollisionBox())) {
                        throw new CollisionException();
                    }
                }
            }
            QUAD_TREE.insert(car);
        }
    }

}
