package tscs;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;

import car.ACar;
import car.SimCar;

import map.intersection.Intersection;
import map.intersection.Segment;

import sim.Const;
import sim.EntityDb;
import sim.Simulation;
import util.CollisionBox;
import util.QuadTree;

public class RecursiveBooking extends AbstractTSCS {
	private final static double BUFFER = Intersection.buffer + 1;
	private static final double EXTRA = BUFFER + 5;
	private static final int CACHE = 800;
	private static final int DETECTOR_RANGE = 70;
	private SpaceTime[] spaceTimes;
	private int lastIndex;
	private HashSet<ACar> bookedIn;
	private final Rectangle2D controlArea;
	private boolean book = true;
	private int bookTime = 0;
	private int firstLane = 0;

	public RecursiveBooking() {
		spaceTimes = new SpaceTime[CACHE];
		bookedIn = new HashSet<>();
		double upperLeft = Intersection.getX() - EXTRA- DETECTOR_RANGE;
		double size = Intersection.square + 2 * EXTRA+ DETECTOR_RANGE * 2;
		controlArea = new Rectangle2D.Double(upperLeft ,upperLeft, size, size);

		// Offset by 20 because middle lane is in the middle of the control area
		// (bad for quad tree).
		size *= 1.2;
		Rectangle2D collisionArea = new Rectangle2D.Double(upperLeft ,upperLeft, size, size);
		for (int i = 0; i < CACHE; i++) {
			spaceTimes[i] = new SpaceTime(collisionArea);
		}

		lastIndex = 0;
	}

	private SpaceTime getSpace(int i) {
		return spaceTimes[(i + CACHE) % CACHE];
	}

	public void tick(double diff) {
		if (emergencyBreak) {
			super.tick(diff);
			return;
		}
		getSpace(lastIndex).clear();
		lastIndex++;
		bookTime++;

		if (bookTime >= 10) {
			book = true;
			firstLane = (firstLane + 1) % 4;
			bookTime = 0;
		}
		for (int i = 0; i < 4; i++) {
			// To prioritize different lanes
			int from = firstLane + i % 4;
			int right = (from + 3) % 4;
			for (int to = 0; to < 4; to++) {
				if (from == to || right == to)
					continue;
				bookCheckLane(from % 4, to % 4);
			}
		}
		book = false;
		Iterator<ACar> cars = bookedIn.iterator();
		while (cars.hasNext()) {
			ACar car = cars.next();
			if (controlArea.contains(car.getPos())) {
				car.setAcc(car.getMaxAcceleration());
				car.setAutonomous(false);

			} else {
				car.color = Color.BLUE;
				cars.remove();
			}
		}
	}

	@Override
	public String drawPhase() {
		return "";
	}

	private void bookCheckLane(int from, int to) {
		// Get the segment.
		Segment segment = Intersection.getWaitingSegment(from, to);
		LinkedList<ACar> carsOnSegment = EntityDb.getCarsOnSegment(segment);
		SimCar simCar;
		for (ACar car : carsOnSegment) {
			if (bookedIn.contains(car)) {
				continue;
			}
			if (!controlArea.contains(car.getPos())) {
				return;
			}

			simCar = car.getSimCar();
			simCar.setAcc(car.getMaxAcceleration());
			if (book && book(simCar, lastIndex)) {
				bookedIn.add(car);
				car.color = Color.BLACK;
			} else {
				car.setAutonomous(false);
				double maxDec = car.getMaxDeceleration() / Const.BREAK_COEF;
				double tracRem = car.remainingOnTrack();
				if (car.getBreakDistance(maxDec) > tracRem - BUFFER) {
					car.setAcc(-maxDec);
				} else {
					car.setAutonomous(true);
				}
				car.color = Color.red;
				return;
			}
		}

	}

	public void draw(Graphics2D g2d) {
		SpaceTime sT = getSpace(lastIndex + 120);
		sT.draw(g2d);
		g2d.setColor(Color.white);
		g2d.draw(Simulation.SCALER.createTransformedShape(controlArea));
	}

	private boolean book(SimCar car, int index) {
		SpaceTime sT = getSpace(index);
		car.tick(Const.TIME_STEP);
		car.updateCollisionBox();
		CollisionBox cB = car.getCollisionBox();
		if (!controlArea.contains(car.getPos()))
			return true;
		if (sT.canBook(cB) && book(car, index + 1)) {
			sT.book(cB);
			return true;
		}
		return false;
	}

	private static class SpaceTime {
		private final ArrayList<CollisionBox> returnObjects;
		private final ArrayList<CollisionBox> all;
		private final QuadTree quadTree;
		private final Rectangle2D collisionArea;

		SpaceTime(Rectangle2D collisionArea) {
			returnObjects = new ArrayList<>();
			all = new ArrayList<>();
			this.collisionArea = collisionArea;
			quadTree = new QuadTree(collisionArea);
		}

		public boolean canBook(CollisionBox booker) {
			returnObjects.clear();
			quadTree.retrieve(returnObjects, booker);
			for (CollisionBox other : returnObjects)
				if (CollisionBox.collide(booker, other))
					return false;
			return true;
		}

		public void clear() {
			all.clear();
			quadTree.clear();
		}

		public void book(CollisionBox booker) {
			all.add(booker);
			quadTree.insert(booker);
		}

		public void draw(Graphics2D g2d) {
			for (CollisionBox cB : all) {
				cB.draw(g2d);
			}
		}

	}
}
