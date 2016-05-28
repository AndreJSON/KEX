package map.intersection;

import java.util.HashMap;

import sim.Const;

import map.track.*;
import math.Vector2D;

public class Segment {

    private static int idTracker;
    private final AbstractTrack track;

    static {
        idTracker = 0;
    }

    private HashMap<Integer, Segment> split; // Tells which Segment a car has to
    // enter given its destination
    // as key. Null instead of a
    // Segment if destination is
    // reached.
    private final int segId;
    private int start;

    public Segment(final AbstractTrack track) {
        this.track = track;
        this.split = new HashMap<>();
        this.segId = ++idTracker;
    }

    public void setStart(final int start) {
        this.start = start;
    }

    public boolean preIntersection() {
        return start == Const.MAP_ENTRANCE || start == Const.MAP_EXIT;
    }

    /**
     * Make seg the next segment if travelling towards the given destination.
     */
    public void linkSegment(final int dest, final Segment seg) {
        split.put(dest % 4, seg);
    }

    public Segment nextSegment(final int dest) {
        return split.get(dest % 4);
    }

    public Vector2D[] getPoints() {
        return track.getPoints();
    }

    public AbstractTrack getTrack() {
        return track;
    }

    @Override
    public int hashCode() {
        return segId;
    }

    @Override
    public boolean equals(final Object other) {
        if (other instanceof Segment) {
            final Segment otherSegment = (Segment) other;
            return otherSegment.segId == segId;
        }
        return false;
    }

    /**
     * The length of the segment.
     *
     * @return
     */
    public double length() {
        return track.getLength();
    }

    public TrackPosition getTrackPosition(final double dist) {
        return track.getTrackPosition(dist);
    }

}
