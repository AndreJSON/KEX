package tscs;

import java.text.DecimalFormat;
import java.util.HashMap;

import sim.Const;
import sim.TravelData;
import car.Car;
import math.Pair;
import map.intersection.*;

public class DSCS extends AbstractTSCS {
	private static final int PHASE0 = 0, PHASE1 = 1, PHASE2 = 2, PHASE3 = 3,
			IDLE = 4;
	private static final double[] MAX_PHASE_LENGTH = { 14, 8, 15, 8, 1.5 };
	private HashMap<Integer, Pair[]> phases;
	private int currentPhase = Const.NORTH, lastPhase = IDLE;
	private double currentPhaseTime = 0;

	public DSCS() {
		phases = new HashMap<>();
		phases.put(PHASE0, new Pair[] { new Pair(Const.WEST, Const.EAST),
				new Pair(Const.WEST, Const.SOUTH),
				new Pair(Const.EAST, Const.WEST),
				new Pair(Const.EAST, Const.NORTH) });
		phases.put(PHASE1, new Pair[] { new Pair(Const.WEST, Const.NORTH),
				new Pair(Const.EAST, Const.SOUTH) });
		phases.put(PHASE2, new Pair[] { new Pair(Const.NORTH, Const.SOUTH),
				new Pair(Const.NORTH, Const.WEST),
				new Pair(Const.SOUTH, Const.NORTH),
				new Pair(Const.SOUTH, Const.EAST) });
		phases.put(PHASE3, new Pair[] { new Pair(Const.SOUTH, Const.WEST),
				new Pair(Const.NORTH, Const.EAST) });
		phases.put(IDLE, new Pair[] {});
	}

	public void tick(double diff) {
		super.tick(diff);
		currentPhaseTime += diff;
		boolean skipPhase = true;
		for (Pair pair : phases.get(currentPhase)) {
			Segment segment = Intersection.getWaitingSegment(pair.getFrom(),
					pair.getTo());
			if (!TravelData.getCarsOnSegment(segment).isEmpty()) {
				skipPhase = false;
			}
		}

		if (currentPhaseTime >= MAX_PHASE_LENGTH[currentPhase]
				|| (skipPhase && currentPhase != IDLE)) {
			currentPhaseTime = 0;
			if (currentPhase == IDLE) {
				currentPhase = (lastPhase + 1) % 4;
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
				if (car.remainingOnTrack() - 3 < car
						.getBreakingDistance()) {
					// Have to break hard!
					car.setAcceleration(-car.getMaxDeceleration());
				} else if (car.remainingOnTrack() - 3 < car
						.getBreakingDistance() * 1.2) {
					// Break medium.
					car.setAcceleration(-car.getMaxDeceleration() / 1.2);
				} else if (car.remainingOnTrack() - 3 < car
						.getBreakingDistance() * 1.5) {
					// Break light.
					car.setAcceleration(-car.getMaxDeceleration() / 1.5);
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
				+ new DecimalFormat("#.0")
						.format(MAX_PHASE_LENGTH[currentPhase]
								- currentPhaseTime);
	}
}