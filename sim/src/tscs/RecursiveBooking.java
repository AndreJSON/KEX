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
import util.CollisionBox;

public class RecursiveBooking extends AbstractTSCS {
	private final static double BUFFER = Intersection.buffer + 1;
	private static final double EXTRA = BUFFER + 5;
	private static final int CACHE = 800;
	private static final int DETECTOR_RANGE = 30;
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
		double x = Intersection.getX() - EXTRA;
		double y = Intersection.getY() - EXTRA;
		double width = Intersection.square + 2 * EXTRA;
		double height = Intersection.square + 2 * EXTRA;
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
		if (emergencyBreak) {
			super.tick(diff);
			return;
		}
		getSpace(lastIndex).returnObjects.clear();
		lastIndex++;
		bookTime++;

		if (bookTime >= 20) {
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
		while (cars.hasNext()){
			ACar car = cars.next();
			if (controlArea.contains(car.getPos())){
				car.setAcc(car.getMaxAcceleration() / Const.ACC_COEF);
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
			simCar.setAcc(car.getMaxAcceleration() / Const.ACC_COEF);
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
		SpaceTime sT = getSpace(lastIndex);
		for (CollisionBox cb : sT.returnObjects){
			cb.draw(g2d);
		}
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
