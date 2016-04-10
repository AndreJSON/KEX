package tscs;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.ListIterator;

import sim.TravelData;
import car.Car;
import math.Pair;
import map.intersection.*;

public class DSCS extends AbstractTSCS {
	private static final int NORTH = Intersection.NORTH, EAST = Intersection.EAST, SOUTH = Intersection.SOUTH, WEST = Intersection.WEST;
	private static final int PHASE0 = 0, PHASE1 = 1, PHASE2 = 2, PHASE3 = 3;
	private static final double[] MAX_PHASE_LENGTH = {14,8,14,8};
	private HashMap<Integer,Pair[]> phases;
	private int currentPhase = NORTH;
	private double currentPhaseTime = 0;


	public DSCS() {
		phases = new HashMap<>();
		phases.put(PHASE0, new Pair[]{new Pair(WEST,EAST), new Pair(WEST,SOUTH), new Pair(EAST,WEST), new Pair(EAST,NORTH)});
		phases.put(PHASE1, new Pair[]{new Pair(WEST,NORTH), new Pair(EAST,SOUTH)});
		phases.put(PHASE2, new Pair[]{new Pair(NORTH,SOUTH), new Pair(NORTH,WEST), new Pair(SOUTH,NORTH), new Pair(SOUTH,EAST)});
		phases.put(PHASE3, new Pair[]{new Pair(SOUTH,WEST), new Pair(NORTH,EAST)});
	}

	public void tick(double diff) {
		super.tick(diff);
		currentPhaseTime+=diff;
		if(currentPhaseTime >= MAX_PHASE_LENGTH[currentPhase]) {
			currentPhaseTime = 0;
			currentPhase = (currentPhase + 1) % 4;
		}

		//The turn segment of the traveldirection.
		Segment segment;
		for(Pair p : phases.get(currentPhase)) {
			segment = Intersection.getWaitingSegment(p.getFrom(), p.getTo());
			ListIterator<Car> cars = TravelData.getCarsOnSegment(segment).listIterator();
			Car car;
			while(cars.hasNext()) {
				car = cars.next();
				if(car.remainingOnTrack() >= car.getBreakingDistance() * COMFORT_COEFFICIENT) {
					//Break this car. else look for another one.
					break;
				}
			}
		}
	}

	public String drawPhase() {
		return "Phase: " + currentPhase + " Time left: " + new DecimalFormat("#.0").format(MAX_PHASE_LENGTH[currentPhase] - currentPhaseTime);
	}
}