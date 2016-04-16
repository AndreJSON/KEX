package tscs;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;

import car.ACar;
import car.SimCar;

import map.intersection.Intersection;
import map.intersection.Segment;

import sim.Const;
import sim.EntityDb;
import util.CollisionBox;
import util.QuadTree;

public class RecursiveBooking extends AbstractTSCS {
	private static final int CACHE = 800;
	private static final int DETECTOR_RANGE = 70;
	private SpaceTime[] spaceTimes;
	private int lastIndex;
	private HashSet<ACar> bookedIn;
	private final Rectangle2D controlArea;
	private final static double BUFFER = 3;
	private boolean book = true;
	private int bookTime = 0;

	public RecursiveBooking() {
		spaceTimes = new SpaceTime[CACHE];
		bookedIn = new HashSet<>();
		double x = Intersection.getX();
		double y = Intersection.getY();
		double width = Intersection.square;
		double height = Intersection.square;
		controlArea = new Rectangle2D.Double(x - DETECTOR_RANGE, y
				- DETECTOR_RANGE, width + DETECTOR_RANGE * 2, height
				+ DETECTOR_RANGE * 2);

		for (int i = 0; i < CACHE; i++) {
			spaceTimes[i] = new SpaceTime();
		}

		lastIndex = 0;
	}

	private SpaceTime getSpace(int i) {
		return spaceTimes[i % CACHE];
	}

	public void tick(double diff) {
		spaceTimes[(lastIndex + CACHE - 1) % CACHE].returnObjects.clear();
		lastIndex++;
		bookTime++;

		if (bookTime >= 10) {
			book = true;
			bookTime = 0;
		}
		for (int i = 0; i < 4; i++) {
			// To prioritize different lanes
			int from = lastIndex + i % 4;
			int right = (from + 3) % 4;
			for (int to = 0; to < 4; to++) {
				if (from == to || right == to)
					continue;
				bookCheckLane(from % 4, to % 4);
			}
		}
		book = false;
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
				car.setAcc(car.getMaxAcceleration() / Const.ACC_COEF);
				continue;
			}
			if (!controlArea.contains(car.getPos())) {
				return;
			}

			simCar = new SimCar(car);
			simCar.copyParent();
			if (book && book(simCar, lastIndex)) {
				car.setAutonomous(false);
				bookedIn.add(car);
				car.color = Color.green;
				car.setAcc(car.getMaxAcceleration() / Const.ACC_COEF);
			} else {
				car.setAutonomous(false);
				double maxDec = car.getMaxDeceleration() / Const.BREAK_COEF;
				double tracRem = car.remainingOnTrack();
				if (car.getBreakDistance(maxDec) > tracRem - BUFFER) {
					car.setAcc(-maxDec);
				} else if (car.getBreakDistance(maxDec / 1.5) > tracRem
						- BUFFER) {
					car.setAcc(-maxDec / 1.5);
				} else {
					car.setAutonomous(true);
				}
				car.color = Color.red;
				return;
			}
		}

	}

	private boolean book(SimCar car, int index) {
		SpaceTime sT = getSpace(index);
		car.setAcc(car.getParent().getMaxAcceleration() / Const.ACC_COEF);
		car.setSpeed(Math.min(car.getSpeed(), Const.SPEED_LIMIT));
		car.tick(Const.TIME_STEP);
		car.updateCollisionBox();
		CollisionBox cB = car.getCollisionBox();
		if (!controlArea.contains(car.getPos()))
			return true;
		if (sT.canBook(cB) && book(car, (index + 1))) {
			sT.book(cB);
			return true;
		}
		return false;
	}

	private static class SpaceTime {
		private final ArrayList<CollisionBox> returnObjects;

		SpaceTime() {
			returnObjects = new ArrayList<>();
		}

		public boolean canBook(CollisionBox booker) {
			for (CollisionBox other : returnObjects)
				if (CollisionBox.collide(booker, other))
					return false;
			return true;
		}

		public void book(CollisionBox booker) {
			returnObjects.add(booker);
		}

	}
}
