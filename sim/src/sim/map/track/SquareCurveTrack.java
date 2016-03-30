package sim.map.track;

import java.awt.geom.Point2D;

/**
 * Second degree bezier track.
 * 
 * @author henrik
 * 
 */
public class SquareCurveTrack implements AbstractTrack {

	public static int RIEMANN_STEPS = 1000; // default value

	private final Point2D sPoint, cPoint, ePoint;
	private final double length;

	public SquareCurveTrack(Point2D startPoint, Point2D controlPoint,
			Point2D endPoint) {
		this.sPoint = startPoint;
		this.cPoint = controlPoint;
		this.ePoint = endPoint;

		// Approximate track length using Riemann sum.
		double length = 0;
		Point2D from = startPoint;
		for (int i = 1; i < RIEMANN_STEPS; i++) {
			Point2D to = evaluate(i / (double) RIEMANN_STEPS);
			length += from.distance(to);
			from = to;
		}

		this.length = length;
	}

	/**
	 * Evaluate the bezier curve at point t.
	 * 
	 * @param t
	 * @return
	 */
	private Point2D evaluate(double t) {
		double tm = 1 - t;
		double x = (sPoint.getX() * tm * tm) + (cPoint.getX() * tm * t)
				+ (ePoint.getX() * t * t);
		double y = (sPoint.getY() * tm * tm) + (cPoint.getY() * tm * t)
				+ (ePoint.getY() * t * t);
		return new Point2D.Double(x, y);
	}

	@Override
	public Point2D getStartPoint() {
		return sPoint;
	}

	@Override
	public Point2D getEndPoint() {
		return ePoint;
	}

	@Override
	public double length() {
		return length;
	}

	@Override
	public TrackPosition getTrackPosition() {
		// TODO Inner TrackPosition class
		return null;
	}

	
	
	// TEST CODE
	
	public static void main(String[] args) {

		// Testing arc length.
		SquareCurveTrack curve = new SquareCurveTrack(new Point2D.Double(0, 0),
				new Point2D.Double(0, 1), new Point2D.Double(1, 1));

		System.out.println(curve.length);
	}

}
