package traveldata;

import java.util.ArrayList;
import java.util.HashMap;

import map.intersection.Segment;
import sim.Const;

public class TravelPlan {

    private final static HashMap<Integer, TravelPlan> TRAVEL_PLANS
            = new HashMap<>();

    /**
     * Class containing precomputer travel paths, optimal time etc.
     *
     * @author henrik
     *
     */
    private final ArrayList<Segment> segments;
    private final int origin, destination;
    private final double optimalTime;

    public TravelPlan(Segment seg, int origin, int destination) {
        this.origin = origin;
        this.destination = destination;
        segments = new ArrayList<>();
        double dist = 0;

        Segment build = seg;
        // Bygga upp segments listan.
        while (build != null) {
            segments.add(build);
            build = build.nextSegment(destination);
        }
        dist = segments.stream().map((seg1) -> seg1.length()).reduce(dist, (
                accumulator,
                _item)
                -> accumulator
                + _item);
        // TODO: Iterate through segments to calculate optimalTime.
        optimalTime = dist / Const.SPEED_LIMIT;
    }

    public double optimalTime() {
        return optimalTime;
    }

    public int numOfSegments() {
        return segments.size();
    }

    public Segment getSegment(int i) {
        return segments.get(i);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final TravelPlan other = (TravelPlan) obj;
        if (this.origin != other.origin) {
            return false;
        }
        return this.destination == other.destination;
    }

    @Override
    public int hashCode() {
        return travelplanhash(origin, destination);
    }

    public static void registerTravelPlan(Segment startSegment, int origin,
            int destination) {
        TravelPlan travelplan = new TravelPlan(startSegment, origin,
                destination);
        TRAVEL_PLANS.put(travelplan.hashCode(), travelplan);
    }

    /**
     * Get the travel plan from origin to destination.
     *
     * @param origin
     * @param destination
     * @return
     */
    public static TravelPlan getTravelPlan(int origin, int destination) {
        return TRAVEL_PLANS.get(travelplanhash(origin, destination));
    }

    private static int travelplanhash(int org, int dest) {
        return (org << 4) + dest;
    }

    public int getDestination() {
        return destination;
    }

    public int getOrigin() {
        return origin;
    }
}
