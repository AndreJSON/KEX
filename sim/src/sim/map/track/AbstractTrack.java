package sim.map.track;

import math.Vector2D;

public abstract class AbstractTrack {
	
	private static long idTracker = 0;
	
	private final long id;
	
	public AbstractTrack(){
		id = ++idTracker;
	}
	
	public final long getId(){
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

}
