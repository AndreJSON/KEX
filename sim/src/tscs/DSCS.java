package tscs;

import java.text.DecimalFormat;
import java.util.HashMap;

import sim.Const;
import sim.EntityDb;
import car.Car;
import math.Pair;
import map.intersection.*;

public class DSCS extends AbstractTSCS {
	private static final int PHASE0 = 0, PHASE1 = 1, PHASE2 = 2, PHASE3 = 3,
			IDLE = 4;
	private static final double[] MAX_PHASE_LENGTH = { 13, 7, 13, 7, 2 };
	private static double GAP_OUT = 1;

	private HashMap<Integer, Pair[]> phases;
	private int currentPhase = Const.NORTH, lastPhase = IDLE;
	private double currentPhaseTime = 0;
	private double gapOutTimer = 0;

	// Stop x meters from intersection. If no buffer, the cars will spill over.
	private static final double BUFFER = Const.COLUMN_DISTANCE;

	public DSCS() {
		phases = new HashMap<>();
		phases.put(PHASE0, new Pair[] { new Pair(Const.WEST, Const.EAST),
				new Pair(Const.WEST, Const.SOUTH),
				new Pair(Const.EAST, Const.WEST),
				new Pair(Const.EAST, Const.NORTH) });
		phases.put(PHASE1, new Pair[] { new Pair(Const.WEST, Const.NORTH),
				new Pair(Const.EAST, Const.SOUTH),
				new Pair(Const.NORTH, Const.WEST),
				new Pair(Const.SOUTH, Const.EAST) });
		phases.put(PHASE2, new Pair[] { new Pair(Const.NORTH, Const.SOUTH),
				new Pair(Const.NORTH, Const.WEST),
				new Pair(Const.SOUTH, Const.NORTH),
				new Pair(Const.SOUTH, Const.EAST) });
		phases.put(PHASE3, new Pair[] { new Pair(Const.SOUTH, Const.WEST),
				new Pair(Const.NORTH, Const.EAST),
				new Pair(Const.WEST, Const.SOUTH),
				new Pair(Const.EAST, Const.NORTH) });
		phases.put(IDLE, new Pair[] {});
	}

	private boolean haveCars(int phase) {
		for (Pair pair : phases.get(phase)) {
			Segment segment = Intersection.getWaitingSegment(pair.getFrom(),
					pair.getTo());
			if (!EntityDb.getCarsOnSegment(segment).isEmpty()) {
				return true;
			}
		}
		return false;
	}

	public void tick(double diff) {
		super.tick(diff);
		currentPhaseTime += diff;
		boolean skipPhase = !haveCars(currentPhase);
		if (skipPhase) {
			gapOutTimer += diff;
		} else {
			gapOutTimer = 0;
		}

		if (currentPhaseTime >= MAX_PHASE_LENGTH[currentPhase]
				|| (gapOutTimer > GAP_OUT && currentPhase != IDLE)) {
			currentPhaseTime = 0;
			if (currentPhase == IDLE) {
				currentPhase = lastPhase;
				boolean lap = false;
				do {
					currentPhase = (currentPhase + 1) % 4;
					lap = currentPhase == lastPhase;
				} while (!haveCars(currentPhase) && !lap);
				lastPhase = IDLE;
			} else {
				lastPhase = currentPhase;
				currentPhase = IDLE;
			}
		}
		Segment segment;
		Car car;
		for (int phase = PHASE0; phase <= PHASE3; phase++) {
			if (phase == currentPhase) {
				continue;
			}
			for (Pair pair : phases.get(phase)) {
				if (pair.getFrom() == (1 + pair.getTo()) % 4) {
					continue;
				}
				segment = Intersection.getWaitingSegment(pair.getFrom(),
						pair.getTo());
				if (EntityDb.getCarsOnSegment(segment).isEmpty()) {
					continue;
				}
				segment = Intersection.getWaitingSegment(pair.getFrom(),
						pair.getTo());
				car = EntityDb.getFirstCar(segment);
				car.setAutonomous(false);
				double maxDec = car.getMaxDeceleration() / Const.BREAK_COEF;
				double tracRem = car.remainingOnTrack();
				if (tracRem < car.getBreakingDistance(maxDec)) {
					// Can't break hard enough! Just try to not stand in the
					// intersection.
					car.setAcc(-car.getMaxDeceleration());
				} else if (tracRem - BUFFER < car.getBreakingDistance(maxDec)) {
					// Break medium hard, stop 3 meters from intersection.
					car.setAcc(-maxDec);
				} else if (tracRem - BUFFER < car
						.getBreakingDistance(maxDec / 1.2)) {
					// Break medium.
					car.setAcc(-maxDec / 1.2);
				} else if (car.remainingOnTrack() - BUFFER < car
						.getBreakingDistance(maxDec / 1.4)) {
					// Break light.
					car.setAcc(-maxDec / 1.4);
				} else {

					car.setAutonomous(true);
				}
			}
		}
	}

	public String drawPhase() {
		return "Phase: "
				+ currentPhase
				+ " Time left: "
				+ new DecimalFormat("0.0")
						.format(MAX_PHASE_LENGTH[currentPhase]
								- currentPhaseTime);
	}
}