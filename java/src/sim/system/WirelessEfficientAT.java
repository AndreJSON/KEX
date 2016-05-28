package sim.system;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;

import car.AutonomousCar;
import car.SimulationCar;

import map.intersection.Intersection;
import map.intersection.Segment;

import sim.Const;
import sim.EntityDb;
import sim.Simulation;
import util.CollisionBox;
import util.QuadTree;

public class WirelessEfficientAT implements SimulationSystem {

    /**
     * ?
     */
    private final static double BUFFER = 2;
    /**
     * The number of space-times to keep in memory. The longer the time is to
     * pass the intersection the more is needed in the memory.
     */
    private static final int CACHE = 3000;
    /**
     * The distance from the intersection before IM takes control.
     */
    private static final int DETECTOR_RANGE = 70;
    /**
     * Bookings, each index is for a different time in the future.
     */
    private final SpaceTime[] spaceTimes;
    /**
     * The cars that have been booked to the system and is in control by the
     * system.
     */
    private final HashSet<AutonomousCar> bookedIn;
    private final Rectangle2D controlArea;
    private int currentIndex;
    private boolean isBookingIndex = true;
    private int bookTimer;
    private int first2BookLane;

    public WirelessEfficientAT() {
        spaceTimes = new SpaceTime[CACHE];
        bookedIn = new HashSet<>();
        final double upperLeft = Intersection.getX() - BUFFER - DETECTOR_RANGE;
        double size = Intersection.SQUARE + 2 * BUFFER + DETECTOR_RANGE * 2;
        controlArea = new Rectangle2D.Double(upperLeft, upperLeft, size, size);

        // Offset by 20 because middle lane is in the middle of the control area
        // (bad for quad tree).
        size *= 1.2;
        final Rectangle2D collisionArea = new Rectangle2D.Double(upperLeft,
                upperLeft, size,
                size);

        for (int i = 0; i < CACHE; i++) {
            spaceTimes[i] = new SpaceTime(collisionArea);
        }

        currentIndex = 0;
    }

    @Override
    public void tick(final double diff) {
        getSpace(currentIndex).clear();
        currentIndex++;
        bookTimer++;

        if (bookTimer >= 20) {
            isBookingIndex = true;
            first2BookLane = (first2BookLane + 1) % 4;
            bookTimer = 0;
        }
        Segment segment;
        for (int i = 0; i < 4; i++) {
            // To prioritize different lanes at different times.
            final int from = (first2BookLane + i) % 4;
            final int right = (from + 3) % 4;
            boolean bookedAllWaiting = true;
            for (int to = 0; to < 4; to++) {
                if (from == to || right == to) {
                    continue;
                }
                // Get the segment.
                segment = Intersection.getWaitingSegment(from, to);
                bookedAllWaiting = bookCheckLane(segment) && bookedAllWaiting;
            }
            if (bookedAllWaiting) {
                segment = Intersection.getEntry(from);
                bookCheckLane(segment);
            }
        }
        isBookingIndex = false;

        // All cars that are booked in are controlled so they follow the plan.
        final Iterator<AutonomousCar> iterator = bookedIn.iterator();
        while (iterator.hasNext()) {
            final AutonomousCar car = iterator.next();
            if (controlArea.contains(car.getPos())) {
                // Accelerate as fast as possible as planned.
                car.setAutonomous(false);
            } else {
                // Car is outside the control area. Remove it from the
                // controller.
                car.setColor(Color.BLUE);
                iterator.remove();
            }
        }
    }

    /**
     * Get the space time of the specified index.
     *
     * @param index
     * @return
     */
    private SpaceTime getSpace(final int index) {
        return spaceTimes[(index + CACHE) % CACHE];
    }

    /**
     * Try to book as many cars as possible in this segment.
     *
     * @param from
     * @param to
     */
    private boolean bookCheckLane(final Segment segment) {
        final LinkedList<AutonomousCar> carsOnSegment = EntityDb
                .getCarsOnSegment(segment);
        SimulationCar simCar;
        for (final AutonomousCar car : carsOnSegment) {
            if (bookedIn.contains(car)) {
                // Already booked to the system.
                continue;
            }
            if (!controlArea.contains(car.getPos())) {
                // Car is not in the control area. Cars are sorted in order of
                // closeness.
                return false;
            }
            if (isBookingIndex) {
                // Create a simulation copy of the car and try to book.
                simCar = car.getSimCar();
                simCar.setAcc(Const.ACCELERATION);
                if (tryBooking(simCar, currentIndex)) {
                    bookedIn.add(car);
                    car.setColor(Color.BLACK);
                    car.setAcc(Const.ACCELERATION);
                    continue;
                }
            }
            // No booking attempts, slow it down to the intersection.
            car.setColor(Color.RED);
            car.setAutonomous(false);
            final double tracRem = car.remainingOnTrack();
            if (car.getBreakDistance() > tracRem - BUFFER - car.getModel().getFrontAxleDisplacement()) {
                car.setAcc(-Const.DECELERATION);
            } else {
                car.setColor(Color.YELLOW);
                car.setAcc(0);
            }
            return false;
        }
        return true;
    }

    /**
     * Draw the space time 120 ticks forwards and the control area.
     *
     * @param g2d
     */
    public void draw(final Graphics2D g2d) {
        g2d.setColor(Color.white);
        g2d.draw(Simulation.SCALER.createTransformedShape(controlArea));
        getSpace(currentIndex).draw(g2d);
    }

    /**
     * Check if a the car can book a space in the time with the index.
     *
     * @param simulationCar to check if it can book.
     * @param index of the discretized time.
     * @return if the car has booked a space-time.
     */
    private boolean tryBooking(final SimulationCar simulationCar, final int index) {
        // Update the car's position.
        simulationCar.tick(Const.TIME_STEP);
        if (!controlArea.contains(simulationCar.getPos())) {
            // Car has gone out of the control area, ok!
            return true;
        }
        final SpaceTime spaceTime = getSpace(index);
        simulationCar.updateCollisionBox();
        final CollisionBox collisionBox = simulationCar.getCollisionBox();
        if (!spaceTime.canBook(collisionBox)) {
            // Found a collision! No booking.
            return false;
        }
        if (tryBooking(simulationCar, index + 1)) {
            // We can book the whole space-time. Book it!
            spaceTime.book(collisionBox);
            return true;
        }
        // We can't book.
        return false;
    }

    private static class SpaceTime {

        /**
         * For retrieving objects from the QuadTree.
         */
        private final ArrayList<CollisionBox> returnObjects;
        /**
         * For effective collision detection.
         */
        private final QuadTree<CollisionBox> quadTree;

        /**
         * Space-time to keep track of collision in order to know where vehicles
         * can travel. CollisionArea is the area to keep track of.
         *
         * @param collisionArea
         */
        public SpaceTime(final Rectangle2D collisionArea) {
            returnObjects = new ArrayList<>();
            quadTree = new QuadTree<>(collisionArea);
        }

        /**
         * Clear space of all bookings.
         */
        public void clear() {
            quadTree.clear();
            returnObjects.clear();
        }

        /**
         * Whenever the CollisionBox have a space in this instance.
         *
         * @param booker
         * @return
         */
        public boolean canBook(final CollisionBox booker) {
            returnObjects.clear();
            quadTree.retrieve(returnObjects, booker.getBounds());
            return returnObjects.stream().noneMatch((other) -> (CollisionBox.
                    collide(booker, other)));
        }

        /**
         * Add a CollisionBox to this space time.
         *
         * @param booker
         */
        public void book(final CollisionBox booker) {
            quadTree.insert(booker);
        }

        private void draw(final Graphics2D g2d) {
            quadTree.draw(g2d);
        }

    }
}
