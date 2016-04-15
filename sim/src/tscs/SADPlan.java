package tscs;

import java.util.LinkedList;

import math.Pair;

public class SADPlan {
	private static final int[] VALID_DIRECTIVES = new int[]{-1,0,1};
	LinkedList<Pair> directives; //First is when, second is directive.
	int currentDirective; //-1 means deceleration, 0 means keep speed, 1 means acceleration.
	int counter;

	public SADPlan() {
		currentDirective = 0;
		counter = 0;
		directives = new LinkedList<Pair>();
	}

	/**
	 * Should be called once every simulation tick.
	 */
	public void tick() {
		counter++;
		if(directives.peek() != null && directives.peek().first() == counter) { //If the next plan is scheduled for this tick
			currentDirective = directives.pop().second();
		}
	}

	/**
	 * Adds the specified directive to the list of directives, scheduling it inHowManyTicks ticks into the future.
	 */
	public void addDirective(int inHowManyTicks, int directive) {
		if(inHowManyTicks < 0) {
			throw new IllegalArgumentException("Tried creating a directive in the past");
		}
		when = inHowManyTicks + counter;
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