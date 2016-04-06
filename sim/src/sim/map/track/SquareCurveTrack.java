package sim.map.track;

import math.Vector2D;

/**
 * Second degree bezier track.
 * 
 * @author henrik
 * 
 */
public class SquareCurveTrack extends AbstractTrack {

	public static int RIEMANN_STEPS = 1000; // default value

	// Control points 1,2,3.
	private final Vector2D c1, c2, c3;
	private final double length;

	public SquareCurveTrack(Vector2D c1, Vector2D c2, Vector2D c3) {
		this.c1 = c1;
		this.c2 = c2;
		this.c3 = c3;

		// Approximate track length using Riemann sum.
		double length = 0;
		Vector2D from = c1;
		for (int i = 1; i < RIEMANN_STEPS; i++) {
			Vector2D to = evaluate(i / (double) RIEMANN_STEPS);
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
	private Vector2D evaluate(double t) {
		double tm = 1 - t;
		// A(1-t)^2 + 2B(1-t)t + Ct^2.
		return c1.mult(tm * tm).plus(c2.mult(tm * t)).plus(c3.mult(t * t));
	}

	@Override
	public Vector2D getStartPoint() {
		return c1;
	}

	@Override
	public Vector2D getEndPoint() {
		return c3;
	}

	@Override
	public double length() {
		return length;
	}

	@Override
	public TrackPosition getTrackPosition() {
		return new Position(0);
	}

	@Override
	public TrackPosition getTrackPosition(double distance) {
		return new Position(distance);
	}

	private class Position implements TrackPosition {
		// TODO: Complete this class.
		
		private double t, heading, totDist;
		private Vector2D point;

		public Position(double distance) {
			point = new Vector2D();
			t = 0;
			move(distance);
		}

		private void calcHeading() {
			Vector2D p = evaluate(t + 0.001);
			heading = Math.atan2(p.getY() - point.getY(),
					p.getX() - point.getX());
		}

		@Override
		public Vector2D getPoint() {
			return null;
		}

		@Override
		public double getHeading() {
			return heading;
		}

		@Override
		public void move(double distance) {
			// TODO: move algorithm.
			calcHeading();
			totDist += distance;
		}

		@Override
		public double remaining() {
			return length - totDist;
		}

	}

	@Override
	public String toString() {
		return "SquareCurveTrack{" + c1 + ", " + c2 + ", " + c3 + "}";
	}
}
