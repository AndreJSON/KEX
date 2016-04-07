package sim.map.track;

import sim.Drawable;
import math.Vector2D;

public interface TrackPosition extends Drawable {

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
}
