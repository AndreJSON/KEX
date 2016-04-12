package tscs;

import java.util.LinkedList;

import car.Car;

public class SADSchedule {
	private static LinkedList<Integer>[][] space4Blocks; //[FROM][TO]
	private static LinkedList<Integer>[][] space9Blocks; //[FROM][TO]
	private Grid[] grids;
	private double stepLength;
	private int gridIndex;

	public SADSchedule(double stepLength, double planLength) {
		this.stepLength = stepLength;
		grids = new Grid[(int)(planLength/stepLength)];
	}

	/**
	 * Returns the amount of time until the car will get to the intersection given its curent velocity.
	 */
	private double timeToI(Car car) {
		return 0; //TODO: Actually calculate the time.
	}

	/**
	 * Returns the fastest time the car could get to the intersection given that it accelerates to road max speed as fast as it can.
	 */
	private double fastestTimeToI(Car car) {
		//TODO: Should take ACCELERATION_COEFFIECIENT into account when calculating.
		return 0; //TODO: Actually calculate the time.
	}

	private void stepIndex() {
		grids[gridIndex].wipe();
		gridIndex = (gridIndex + 1) % grids.length;
	}

	public boolean courseCheck(/*Have a decent representation of an object in space*/) {
		//TODO: Check relevant tiles in space4 and space9.
		return true; //TODO: Return calculated value instead of true.
	}

	public boolean fineCheck(/*Have a decent representation of an object in space*/) {
		//TODO: Check relevant tiles in space4 and space9.
		return true; //TODO: Return calculated value instead of true.
	}

	public LinkedList<Car> fineCheckCars(/*Have a decent representation of an object in space*/) {
		return null; //TODO: Return calculated value instead of null.
	}

	/**
	 * Books all tiles taken up by the given object, does not check for collision so be careful when booking!
	 */
	public void book(/*Have a decent representation of an object in space*/) {
		//TODO: Make booking in all 3 schemas.
	}

	private class Grid {
		public boolean[][] space4, space9;
		public Car[][] spaceFine;

		public Grid(int dim) {
			space4 = new boolean[2][2];
			space9 = new boolean[3][3];
			spaceFine = new Car[dim][dim];
		}

		/**
		 * Wipe the Grid clean.
		 */
		public void wipe() {
			for(int i = 0; i < space4.length; i++)
				for(int j = 0; j < space4.length; j++)
					space4[i][j] = false;
			for(int i = 0; i < space9.length; i++)
				for(int j = 0; j < space9.length; j++)
					space9[i][j] = false;
			for(int i = 0; i < spaceFine.length; i++)
				for(int j = 0; j < spaceFine.length; j++)
					spaceFine[i][j] = null;
		}
	}
}