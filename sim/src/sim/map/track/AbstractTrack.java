package sim.map.track;

public interface AbstractTrack {

	/**
	 * Starting point of the track.
	 * 
	 * @return
	 */
	public Vector2D getStartPoint();

	/**
	 * Ending point of the track.
	 * 
	 * @return
	 */
	public Vector2D getEndPoint();

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

	/**
	 * Get a position following the track. Starts at distance dist and moves
	 * along the track.
	 * 
	 * @return
	 */
	public TrackPosition getTrackPosition(double dist);

}
