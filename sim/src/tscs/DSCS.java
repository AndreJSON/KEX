package tscs;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.ListIterator;

import sim.Logic;
import sim.TravelData;
import car.Car;
import math.Pair;
import map.intersection.*;

public class DSCS extends AbstractTSCS {
	private static final int NORTH = Intersection.NORTH, EAST = Intersection.EAST, SOUTH = Intersection.SOUTH, WEST = Intersection.WEST;
	private static final int PHASE0 = 0, PHASE1 = 1, PHASE2 = 2, PHASE3 = 3, IDLE = 4;
	private static final double[] MAX_PHASE_LENGTH = {10,6,10,6,1.5};
	private HashMap<Integer,Pair[]> phases;
	private int currentPhase = NORTH, lastPhase = IDLE;
	private double currentPhaseTime = 0;


	public DSCS() {
		phases = new HashMap<>();
		phases.put(PHASE0, new Pair[]{new Pair(WEST,EAST), new Pair(WEST,SOUTH), new Pair(EAST,WEST), new Pair(EAST,NORTH)});
		phases.put(PHASE1, new Pair[]{new Pair(WEST,NORTH), new Pair(EAST,SOUTH)});
		phases.put(PHASE2, new Pair[]{new Pair(NORTH,SOUTH), new Pair(NORTH,WEST), new Pair(SOUTH,NORTH), new Pair(SOUTH,EAST)});
		phases.put(PHASE3, new Pair[]{new Pair(SOUTH,WEST), new Pair(NORTH,EAST)});
		phases.put(IDLE, new Pair[]{});
	}

	public void tick(double diff) {
		super.tick(diff);
		currentPhaseTime+=diff;
		if(currentPhaseTime >= MAX_PHASE_LENGTH[currentPhase]) {
			currentPhaseTime = 0;
			if(currentPhase == IDLE) {
				currentPhase = (lastPhase + 1) % 4;
				lastPhase = IDLE;
			}
			else {
				lastPhase = currentPhase;
				currentPhase = IDLE;
			}
		}

		Segment segment;
		ListIterator<Car> cars;
		Car car;
		for(int phase = PHASE0; phase <= PHASE3; phase++) {
			if(phase == currentPhase) {
				continue; //These cars shouldnt be stopped. They belong to the currently driving phase.
			}
			for(Pair pair : phases.get(phase)) {
				if((pair.getFrom() - pair.getTo() + 4) % 4 == 1) { //The right turns should be skipped since they are duplicates of the straights.
					continue;
				}
				segment = Intersection.getWaitingSegment(pair.getFrom(), pair.getTo());
				cars = TravelData.getCarsOnSegment(segment).listIterator();
				while(cars.hasNext()) {
					car = cars.next();
					if(car.remainingOnTrack() >= car.getBreakingDistance() * Logic.BREAKING_COEFFICIENT * 1.15) {
						car.setAutonomy(false);
						if(car.remainingOnTrack() >= Math.max(car.getBreakingDistance() * Logic.BREAKING_COEFFICIENT * 2, 2)) {
							reduceSpeed(car, car.getMaxRetardation(diff / (3.5 * Logic.BREAKING_COEFFICIENT)));
							break;
						}
						reduceSpeed(car, car.getMaxRetardation(diff / (Logic.BREAKING_COEFFICIENT * 1.1)));
						break;
					}
				}
			}
		}
	}

	public String drawPhase() {
		return "Phase: " + currentPhase + " Time left: " + new DecimalFormat("#.0").format(MAX_PHASE_LENGTH[currentPhase] - currentPhaseTime);
	}
}