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
	private static final int NORTH = Intersection.NORTH,
			EAST = Intersection.EAST, SOUTH = Intersection.SOUTH,
			WEST = Intersection.WEST;
	private static final int PHASE0 = 0, PHASE1 = 1, PHASE2 = 2, PHASE3 = 3;
	private static final double[] MAX_PHASE_LENGTH = { 20, 10, 20, 10 };
	private HashMap<Integer, Pair[]> phases;
	private int currentPhase = NORTH;
	private double currentPhaseTime = 0;

	public DSCS() {
		phases = new HashMap<>();
		phases.put(PHASE0, new Pair[] { new Pair(WEST, EAST),
				new Pair(WEST, SOUTH), new Pair(EAST, WEST),
				new Pair(EAST, NORTH) });
		phases.put(PHASE1, new Pair[] { new Pair(WEST, NORTH),
				new Pair(EAST, SOUTH) });
		phases.put(PHASE2, new Pair[] { new Pair(NORTH, SOUTH),
				new Pair(NORTH, WEST), new Pair(SOUTH, NORTH),
				new Pair(SOUTH, EAST) });
		phases.put(PHASE3, new Pair[] { new Pair(SOUTH, WEST),
				new Pair(NORTH, EAST) });
	}

	private double yellow = 0;

	private double phaseTick = 2;

	public void tick(double diff) {
		super.tick(diff);
		currentPhaseTime += diff;
		int segs = 0;
		phaseTick += diff;
		for (Pair pair : phases.get(currentPhase)) {
			if (pair.getFrom() == (1 + pair.getTo()) % 4) {
				continue;
			}
			Segment segment = Intersection.getWaitingSegment(pair.getFrom(),
					pair.getTo());
			if (!TravelData.getCarsOnSegment(segment).isEmpty()) {
				segs++;
				phaseTick = 0;
			}
		}

		if (currentPhaseTime >= MAX_PHASE_LENGTH[currentPhase]
				|| (segs == 0 && phaseTick > 2)) {
			currentPhaseTime = 0;
			currentPhase = (currentPhase + 1) % 4;
			yellow = 2;
			phaseTick = 0;
		}
		yellow -= diff;
		Segment segment;
		Car car;
		for (int phase = PHASE0; phase <= PHASE3; phase++) {
			if (yellow > 0) {

			} else if (phase == currentPhase) {
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
				car = TravelData.getCarsOnSegment(segment).getFirst();
				car.setAutonomy(false);
				if (car.remainingOnTrack() < car.getSpeed() * diff){
					car.setSpeed(0);
					car.setAutonomy(false);
				} else
				if (car.remainingOnTrack() - 2 < car.getBreakingDistance() * 2
						&& car.getSpeed() > 0) {
					car.setAutonomy(false);
					if (car.remainingOnTrack() - 2 < car
							.getBreakingDistance() / 2) {
						reduceSpeed(car, car.getModel().getMaxRetardation()
								* diff * 3);
					} else if (car.remainingOnTrack() - 2 < car
							.getBreakingDistance()) {
						AbstractTSCS.reduceSpeed(car, car.getModel()
								.getMaxRetardation() * diff);

					} else {
						AbstractTSCS.reduceSpeed(car, car.getModel()
								.getMaxRetardation() / 1.5 * diff);
					}

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