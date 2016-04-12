package tscs;

import java.text.DecimalFormat;
import java.util.HashMap;

import sim.Const;
import sim.TravelData;
import car.Car;
import math.Pair;
import map.intersection.*;
import sim.Logic;

public class DSCS extends AbstractTSCS {
	private static final int PHASE0 = 0, PHASE1 = 1, PHASE2 = 2, PHASE3 = 3,
			IDLE = 4;
	private static final double[] MAX_PHASE_LENGTH = { 13, 9, 13, 9, 2 };
	private HashMap<Integer, Pair[]> phases;
	private int currentPhase = Const.NORTH, lastPhase = IDLE;
	private double currentPhaseTime = 0;

	// Stop x meters from intersection. If no buffer, the cars will spill over.
	private double BUFFER = 3;

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
			if (!TravelData.getCarsOnSegment(segment).isEmpty()) {
				return true;
			}
		}
		return false;
	}

	private double gapOut = 0;

	public void tick(double diff) {
		super.tick(diff);
		currentPhaseTime += diff;
		boolean skipPhase = !haveCars(currentPhase);
		if (skipPhase) {
			gapOut += diff;
		} else {
			gapOut = 0;
		}

		if (currentPhaseTime >= MAX_PHASE_LENGTH[currentPhase]
				|| (gapOut > 0.5 && currentPhase != IDLE)) {
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
				if (TravelData.getCarsOnSegment(segment).isEmpty()) {
					continue;
				}
				segment = Intersection.getWaitingSegment(pair.getFrom(),
						pair.getTo());
				car = TravelData.getCarsOnSegment(segment).getFirst();
				car.setAutonomous(false);
				double maxDec = car.getMaxDeceleration();
				double tracRem = car.remainingOnTrack();
				if (tracRem < car.getBreakingDistance(maxDec)) {
					// Can't break hard enough! Just try to not stand in the
					// intersection.
					car.setAcc(car.getMaxAcceleration() / Logic.ACC_COEF * 1.2);
				} else if (tracRem - BUFFER < car.getBreakingDistance(maxDec
						/ Logic.BREAK_COEF)) {
					// Break medium hard, stop 3 meters from intersection.
					car.setAcc(-maxDec / Logic.BREAK_COEF);
				} else if (tracRem - BUFFER < car.getBreakingDistance(maxDec
						/ (Logic.BREAK_COEF * 1.2))) {
					// Break medium.
					car.setAcc(-maxDec / (Logic.BREAK_COEF * 1.2));
				} else if (car.remainingOnTrack() - BUFFER < car
						.getBreakingDistance(maxDec / (Logic.BREAK_COEF * 1.4))) {
					// Break light.
					car.setAcc(-maxDec / (Logic.BREAK_COEF * 1.4));
				} else if (tracRem - BUFFER < car.getBreakingDistance(maxDec)) {
					// Emergency break.
					car.setAcc(-maxDec);
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