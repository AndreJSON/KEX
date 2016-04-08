package map.track;

import sim.Drawable;
import math.Vector2D;

/**
 * AbstractTrack represents a path that a car can follow. It contains a
 * getTrackPosition() method that gives a point which a car can follow and move
 * along the track.
 * 
 * @author henrik
 */
public abstract class AbstractTrack implements Drawable {
	/**
	 * The distance each point in the discretized representation should have.
	 */
	protected final double POINT_STEP = 0.2;

	/**
	 * Keeps the currently highest id.
	 */
	private static long idTracker = 0;

	/**
	 * Id of the track
	 */
	private final long id;

	public AbstractTrack() {
		id = ++idTracker;
	}

	/**
	 * Returns the id of the track.
	 * 
	 * @return
	 */
	public final long getId() {
		return id;
	}

	/**
	 * Starting point of the track.
	 * 
	 * @return
	 */
	public abstract Vector2D getStartPoint();

	/**
	 * Ending point of the track.
	 * 
	 * @return
	 */
	public abstract Vector2D getEndPoint();

	/**
	 * Length of the track.
	 * 
	 * @return
	 */
	public abstract double length();

	/**
	 * Get a position following the track. Starts at distance 0 and moves along
	 * the track.
	 * 
	 * @return
	 */
	public abstract TrackPosition getTrackPosition();

	/**
	 * Get a position following the track. Starts at distance dist and moves
	 * along the track.
	 * 
	 * @return
	 */
	public abstract TrackPosition getTrackPosition(double distance);

	/**
	 * Returns the discretized representation of the track.
	 * 
	 * @return
	 */
	public abstract Vector2D[] getPoints();

	@Override
	public int hashCode() {
		return (int) id;
	}

}
