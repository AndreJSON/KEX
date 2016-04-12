package map.track;

import math.Vector2D;

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
	void move(double distance);

	/**
	 * How much of the track that remains.
	 * 
	 * @return
	 */
	double remaining();

	/**
	 * Give an exact copy of the TrackPosition.
	 * 
	 * @return
	 */
	TrackPosition copy();
}
