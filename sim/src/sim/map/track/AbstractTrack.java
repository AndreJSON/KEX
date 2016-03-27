package sim.map.track;

import java.awt.geom.Point2D;

public interface AbstractTrack {

	/**
	 * Starting point of the track.
	 * 
	 * @return
	 */
	public Point2D getStartPoint();

	/**
	 * Ending point of the track.
	 * 
	 * @return
	 */
	public Point2D getEndPoint();

	/**
	 * Length of the track.
	 * 
	 * @return
	 */
	public double length();

	/**
	 * Get a position following the track. Starts at distance 0 and moves along
	 * the track.
	 * 
	 * @return
	 */
	public TrackPosition getTrackPosition();

}
