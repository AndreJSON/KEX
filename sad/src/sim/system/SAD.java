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

public class SAD implements SimSystem {
	/**
	 * ?
	 */
	private final static double BUFFER = 5;
	/**
	 * The number of space-times to keep in memory. The longer the time is to
	 * pass the intersection the more is needed in the memory.
	 */
	private static final int CACHE = 2000;
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
	private int bookTimer = 0;
	private int first2BookLane = 0;

	public SAD() {
		spaceTimes = new SpaceTime[CACHE];
		bookedIn = new HashSet<>();
		double upperLeft = Intersection.getX() - BUFFER - DETECTOR_RANGE;
		double size = Intersection.square + 2 * BUFFER + DETECTOR_RANGE * 2;
		controlArea = new Rectangle2D.Double(upperLeft, upperLeft, size, size);

		// Offset by 20 because middle lane is in the middle of the control area
		// (bad for quad tree).
		size *= 1.2;
		Rectangle2D collisionArea = new Rectangle2D.Double(upperLeft,
				upperLeft, size, size);

		for (int i = 0; i < CACHE; i++) {
			spaceTimes[i] = new SpaceTime(collisionArea);
		}

		currentIndex = 0;
	}

	public void tick(double diff) {
		getSpace(currentIndex).clear();
		currentIndex++;
		bookTimer++;

		if (bookTimer >= 20) {
			isBookingIndex = true;
			first2BookLane = (first2BookLane + 1) % 4;
			bookTimer = 0;
		}

		for (int i = 0; i < 4; i++) {
			// To prioritize different lanes at different times.
			int from = (first2BookLane + i) % 4;
			int right = (from + 3) % 4;
			boolean bookedAllWaiting = true;
			for (int to = 0; to < 4; to++) {
				if (from == to || right == to)
					continue;
				// Get the segment.
				Segment segment = Intersection.getWaitingSegment(from, to);
				bookedAllWaiting = bookCheckLane(segment) && bookedAllWaiting;
			}
			if (bookedAllWaiting) {
				Segment segment = Intersection.getEntry(from);
				bookCheckLane(segment);
			}
		}
		isBookingIndex = false;

		// All cars that are booked in are controlled so they follow the plan.
		Iterator<AutonomousCar> it = bookedIn.iterator();
		ArrayList<AutonomousCar> removeList = new ArrayList<>();
		while (it.hasNext()) {
			AutonomousCar car = it.next();
			if (controlArea.contains(car.getPos())) {
				// Accelerate as fast as possible as planned.
				car.setAcc(car.getMaxAcceleration());
				car.setAutonomous(false);
			} else {
				// Car is outside the control area. Remove it from the
				// controller.
				car.setColor(Color.BLUE);
				removeList.add(car);
			}
		}
		bookedIn.removeAll(removeList);
	}

	/**
	 * Get the space time of the specified index.
	 * 
	 * @param i
	 * @return
	 */
	private SpaceTime getSpace(int i) {
		return spaceTimes[(i + CACHE) % CACHE];
	}

	/**
	 * Try to book as many cars as possible in this segment.
	 * 
	 * @param from
	 * @param to
	 */
	private boolean bookCheckLane(Segment segment) {
		LinkedList<AutonomousCar> carsOnSegment = EntityDb
				.getCarsOnSegment(segment);
		SimulationCar simCar;
		for (AutonomousCar car : carsOnSegment) {
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
				simCar.setAcc(car.getMaxAcceleration());
				if (tryBooking(simCar, currentIndex)) {
					bookedIn.add(car);
					car.setColor(Color.BLACK);
					continue;
				}
			}
			// No booking attempts, slow it down to the intersection.
			car.setColor(Color.RED);
			car.setAutonomous(false);
			double maxDec = car.getMaxDeceleration() / Const.BREAK_COEF;
			double tracRem = car.remainingOnTrack();
			if (car.getBreakDistance(maxDec) > tracRem - BUFFER) {
				car.setAcc(-maxDec);
			} else {
				car.setColor(Color.YELLOW);
				car.setAutonomous(true);
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
	public void draw(Graphics2D g2d) {
		g2d.setColor(Color.white);
		g2d.draw(Simulation.SCALER.createTransformedShape(controlArea));
	}

	/**
	 * Check if a the car can book a space in the time with the index.
	 * 
	 * @param simulationCar
	 *            to check if it can book.
	 * @param index
	 *            of the discretized time.
	 * @return if the car has booked a space-time.
	 */
	private boolean tryBooking(SimulationCar simulationCar, int index) {
		if (!controlArea.contains(simulationCar.getPos())) {
			// Car has gone out of the control area, ok!
			return true;
		}
		SpaceTime sT = getSpace(index);
		simulationCar.updateCollisionBox();
		CollisionBox cB = simulationCar.getCollisionBox();
		if (!sT.canBook(cB)) {
			// Found a collision! No booking.
			return false;
		}
		// Update the car's position.
		simulationCar.tick(Const.TIME_STEP);
		if (tryBooking(simulationCar, index + 1)) {
			// We can book the whole space-time. Book it!
			sT.book(cB);
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
		 * For drawing objects.
		 */
		private final ArrayList<CollisionBox> allObjects;
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
		public SpaceTime(Rectangle2D collisionArea) {
			returnObjects = new ArrayList<>();
			allObjects = new ArrayList<>();
			quadTree = new QuadTree<>(collisionArea);
		}

		/**
		 * Clear space of all bookings.
		 */
		public void clear() {
			allObjects.clear();
			quadTree.clear();
			returnObjects.clear();
		}

		/**
		 * Whenever the CollisionBox have a space in this instance.
		 * 
		 * @param booker
		 * @return
		 */
		public boolean canBook(CollisionBox booker) {
			returnObjects.clear();
			quadTree.retrieve(returnObjects, booker.getBounds());
			for (CollisionBox other : returnObjects) {
				if (CollisionBox.collide(booker, other)) {
					return false;
				}
			}
			return true;
		}

		/**
		 * Add a CollisionBox to this space time.
		 * 
		 * @param booker
		 */
		public void book(CollisionBox booker) {
			allObjects.add(booker);
			quadTree.insert(booker);
		}

	}
}
