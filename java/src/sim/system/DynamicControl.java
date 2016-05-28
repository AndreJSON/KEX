package sim.system;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.LinkedList;

import sim.Const;
import sim.EntityDb;
import car.AutonomousCar;
import car.AbstractCar;
import math.Pair;
import map.intersection.*;

public class DynamicControl implements SimulationSystem {

    private static final int PHASE0 = 0, PHASE1 = 1, PHASE2 = 2, PHASE3 = 3, IDLE
            = 4;
    private static final double A = 11.5, B = 8;
    private static final double GAP_OUT = 0;

    private final HashMap<Integer, Pair[]> phases;
    private int currentPhase = Const.NORTH, lastPhase = IDLE;
    private double currentPhaseTime = 0;
    private double gapOutTimer = 0;

    // Stop x meters from intersection. If no buffer, the cars will spill over.
    private static final double BUFFER = 1;
    private double[] max_phase_length;

    public DynamicControl(double[] max_phase_length) {
        this.max_phase_length = max_phase_length;
        
        phases = new HashMap<>();
        phases.put(PHASE0, new Pair[]{new Pair(Const.WEST, Const.EAST),
            new Pair(Const.WEST, Const.SOUTH),
            new Pair(Const.EAST, Const.WEST), new Pair(Const.EAST,
            Const.NORTH)});
        phases.put(PHASE1, new Pair[]{new Pair(Const.WEST, Const.NORTH),
            new Pair(Const.EAST, Const.SOUTH)});
        phases.put(PHASE2, new Pair[]{new Pair(Const.NORTH, Const.SOUTH),
            new Pair(Const.NORTH, Const.WEST),
            new Pair(Const.SOUTH, Const.NORTH), new Pair(Const.SOUTH,
            Const.EAST)});
        phases.put(PHASE3, new Pair[]{new Pair(Const.SOUTH, Const.WEST),
            new Pair(Const.NORTH, Const.EAST),});
        phases.put(IDLE, new Pair[]{});

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

    @Override
    public void tick(double diff) {
        currentPhaseTime += diff;
        boolean skipPhase = !haveCars(currentPhase);
        if (skipPhase) {
            gapOutTimer += diff;
        } else {
            gapOutTimer = 0;
        }

        if (currentPhaseTime >= max_phase_length[currentPhase] || gapOutTimer
                > GAP_OUT && currentPhase != IDLE) {
            currentPhaseTime = 0;
            if (currentPhase == IDLE) {
                currentPhase = lastPhase;
                boolean lap;
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
        for (int phase = PHASE0; phase <= PHASE3; phase++) {
            if (phase == currentPhase) {
                continue;
            }
            for (Pair pair : phases.get(phase)) {
                if (pair.getFrom() == (1 + pair.getTo()) % 4) {
                    continue;
                }
                segment = Intersection.getWaitingSegment(pair.getFrom(), pair.
                        getTo());
                if (EntityDb.getCarsOnSegment(segment).isEmpty()) {
                    continue;
                }
                AutonomousCar car = EntityDb.getCarsOnSegment(segment).
                        getFirst();
                stopCar(car);
            }
        }
        for (Pair pair : phases.get(currentPhase)) {
            if (pair.getFrom() == (1 + pair.getTo()) % 4) {
                continue;
            }
            segment = Intersection.getWaitingSegment(pair.getFrom(), pair.
                    getTo());

            if (EntityDb.getCarsOnSegment(segment).isEmpty()) {
                continue;
            }
            LinkedList<AutonomousCar> car = EntityDb.getCarsOnSegment(segment);
            processCars(car);
        }
    }

    private void stopCar(AutonomousCar car) {
        car.setAutonomous(false);
        double tracRem = car.remainingOnTrack();
        double instBuffer = BUFFER + car.getModel().getFrontAxleDisplacement();
        if (car.getBreakDistance() > tracRem - instBuffer) {
            car.setAcc(-Const.DECELERATION);
        } else {
            car.setAutonomous(true);
        }
    }

    private void processCars(LinkedList<AutonomousCar> cars) {
        for(AutonomousCar car : cars){
            double tracRem = car.remainingOnTrack();
            double maxDist = AbstractCar.distance(car.getSpeed(), Const.ACCELERATION,
                    phaseTimeLeft(),
                    Const.SPEED_LIMIT);
            double maxBreak = car.getBreakDistance();
        double instBuffer = BUFFER + car.getModel().getFrontAxleDisplacement();
            if (maxDist - instBuffer <= tracRem && tracRem - instBuffer < maxBreak
                    && maxBreak < tracRem) {
                car.setAutonomous(false);
                car.setAcc(-Const.DECELERATION);
            }
        }
    }

    private double phaseTimeLeft() {
        return max_phase_length[currentPhase] - currentPhaseTime;
    }

    public String drawPhase() {
        return "Phase: " + currentPhase + " Time left: " + new DecimalFormat(
                "0.0").format(phaseTimeLeft());
    }

}
