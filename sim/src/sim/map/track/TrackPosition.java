package sim.map.track;

import java.awt.geom.Point2D;

public interface TrackPosition {

	
	/**
	 * Get a double position that is on the track.
	 * 
	 * @return
	 */
	Point2D.Double getPoint();

	/**
	 * Get the current X position on the track.
	 * 
	 * @return
	 */
	double getX();

	/**
	 * Get the current Y position on the track.
	 * 
	 * @return
	 */
	double getY();

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
	 * @return The remaining distance on the track. Negative if passed the track.
	 */
	double move(double dist);
}
