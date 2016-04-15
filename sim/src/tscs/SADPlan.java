package tscs;

import java.util.LinkedList;

import math.Pair;

public class SADPlan {
	public static final int SLOW_DOWN = -1, KEEP_SPEED = 0, SPEED_UP = 1;
	private static final int[] VALID_DIRECTIVES = new int[]{SLOW_DOWN, KEEP_SPEED, SPEED_UP};
	LinkedList<Pair> directives; //First is when, second is directive.
	int currentDirective; //-1 means deceleration, 0 means keep speed, 1 means acceleration.
	int counter;

	public SADPlan() {
		currentDirective = KEEP_SPEED;
		counter = 0;
		directives = new LinkedList<Pair>();
	}

	/**
	 * Should be called once every simulation tick.
	 */
	public void tick() {
		if(directives.peek() != null && directives.peek().first() == counter) { //If the next plan is scheduled for this tick
			currentDirective = directives.pop().second();
		}
		counter++;
	}

	/**
	 * Adds the specified directive to the list of directives, scheduling it inHowManyTicks ticks into the future.
	 */
	public void addDirective(int inHowManyTicks, int directive) {
		if(inHowManyTicks < 0) {
			throw new IllegalArgumentException("Tried creating a directive in the past");
		}
		int when = inHowManyTicks + counter;
		if(contains(VALID_DIRECTIVES, directive)) {
			if(directives.peekLast() != null && when < directives.peekLast().first()) {
				throw new IllegalArgumentException("Tried creating a directive with an incorrect timestamp");
			}
			directives.addLast(new Pair(when, directive));
		} else {
			throw new IllegalArgumentException("Tried creating an incorrect directive.");
		}
	}

	/**
	 * Returns the current directive for the car. -1 means slow down, 0 means keep speed and 1 means speed up.
	 */
	public int getDirective() {
		return currentDirective;
	}

	/**
	 * Checks if the specified int[] contains the specified value.
	 */
	private boolean contains(int[] arr, int value) {
		for(int i : arr) {
			if(i == value)
				return true;
		}
		return false;
	}
}