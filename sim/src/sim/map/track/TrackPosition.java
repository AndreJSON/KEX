package sim.map.track;

public interface TrackPosition {

	/**
	 * Get a double position that is on the track.
	 * 
	 * @return
	 */
	Vector2D getPoint();

	/**
	 * Get the heading of the track.
	 * 
	 * @return
	 */
	double getHeading();

	/**
	 * Move the position by the distance specified along the track.
	 * 
	 * @param dist
	 */
	void move(double dist);

	/**
	 * How much of the track that remains.
	 * 
	 * @return
	 */
	double remaining();
}
