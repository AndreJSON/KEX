package tscs;

import sim.EntityDatabase;
import car.Car;
import java.util.HashMap;
import math.Pair;
import map.intersection.Intersection;

public class DSCS extends AbstractTSCS {
	private static final int NORTH = Intersection.NORTH, EAST = Intersection.EAST, SOUTH = Intersection.SOUTH, WEST = Intersection.WEST;
	private static final int PHASE0 = 0, PHASE1 = 1, PHASE2 = 2, PHASE3 = 3;
	private static final double MAX_PHASE_LENGTH = 20;
	private HashMap<Integer,Pair[]> phases;
	private int currentPhase = NORTH;


	public DSCS() {
		phases = new HashMap<>();
		phases.put(PHASE0, new Pair[]{new Pair(WEST,EAST), new Pair(WEST,SOUTH), new Pair(EAST,WEST), new Pair(EAST,NORTH)});
		phases.put(PHASE1, new Pair[]{new Pair(WEST,NORTH), new Pair(EAST,SOUTH)});
		phases.put(PHASE2, new Pair[]{new Pair(NORTH,SOUTH), new Pair(NORTH,WEST), new Pair(SOUTH,NORTH), new Pair(SOUTH,EAST)});
		phases.put(PHASE3, new Pair[]{new Pair(SOUTH,WEST), new Pair(NORTH,EAST)});
	}

	public void tick(double diff) {
		super.tick(diff);
		//Do stuff specific to DSCS.
	}
}