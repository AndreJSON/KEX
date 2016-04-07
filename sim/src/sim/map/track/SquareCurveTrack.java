package sim.map.track;

import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import java.util.ArrayList;

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

	private Vector2D[] points;

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

		generatePoints();
	}

	private void generatePoints() {
		ArrayList<Vector2D> list = new ArrayList<>();

		Position position = new Position(0);

		list.add(c1);

		while (position.remaining() > 0) {
			position.move(0.2);
			list.add(position.point);
		}

		list.add(c3);

		points = list.toArray(new Vector2D[list.size()]);
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
		return c1.mult(tm * tm).plus(c2.mult(2 * tm * t).plus(c3.mult(t * t)));
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

	@Override
	public String toString() {
		return "SquareCurveTrack{" + c1 + ", " + c2 + ", " + c3 + "}";
	}

	@Override
	public Vector2D[] getPoints() {
		return points;
	}

	@Override
	public void draw(Graphics2D g2d) {
		Path2D.Double s = new Path2D.Double();
		s.moveTo(c1.x, c1.y);
		s.quadTo(c2.x, c2.y, c3.x, c3.y);
		g2d.draw(s);

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
			double movedDistance = 0;
			double step = 0.2;
			while (movedDistance + step < distance) { // move by 0.2 at the
														// time.
				move2(step);
				movedDistance += step;
			}
			if (distance != movedDistance)
				move2(distance - movedDistance);

			totDist += distance;
			calcHeading();
		}

		private void move2(double distance) {
			// 1 / norm( 2*(A-2*B+C)*t + (-2*A+2*B) );
			Vector2D first = c3.plus(c1.minus(c2.mult(2))).mult(2 * t); // =
																		// 2*(A-2*B+C)*t
			Vector2D second = c2.minus(c1).mult(2);// = -2*A+2*B

			t += distance * 1 / first.plus(second).norm();
			point = evaluate(t);
		}

		@Override
		public double remaining() {
			return length - totDist;
		}

		@Override
		public void draw(Graphics2D g2d) {
			g2d.drawOval((int) (point.x - 1), (int) (point.y - 1), 2, 2);
		}

	}
}
