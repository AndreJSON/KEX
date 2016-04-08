package sim.map.track;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import java.util.ArrayList;

import sim.Simulation;

import math.Vector2D;

/**
 * Second degree bezier track.
 * 
 * @author henrik
 * 
 */
public class SquareCurveTrack extends AbstractTrack {

	/**
	 * Number of iterations when computing the length of the curve.
	 */
	public static int RIEMANN_STEPS = 1000; // default value

	// Control points 1,2,3.
	/**
	 * Curves goes from c1 to c3, with c2 as a control point.
	 */
	private final Vector2D c1, c2, c3;

	/**
	 * Used for stepping through the curve with a certain length.
	 */
	private final Vector2D v1, v2;

	/**
	 * The length of the curve.
	 */
	private final double length;

	/**
	 * The shape of the curve.
	 */
	private final Path2D.Double shape;

	/**
	 * Discretized points of the bezier curve.
	 */
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

		v1 = (c1.mult(2)).minus(c2.mult(4)).plus(c3.mult(2));
		v2 = (c2.minus(c1)).mult(2);

		generatePoints();

		shape = new Path2D.Double();
		shape.moveTo(c1.x, c1.y);
		shape.quadTo(c2.x, c2.y, c3.x, c3.y);

	}

	/**
	 * Generate the discretized points.
	 */
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
	 * Evaluate the bezier curve with parameter t.
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
		g2d.setColor(Color.red);
		g2d.draw(Simulation.SCALER.createTransformedShape(shape));
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
			return point;
		}

		@Override
		public double getHeading() {
			return heading;
		}

		@Override
		public void move(double distance) {
			calcT(distance);
			totDist += distance;

			point = evaluate(t);
			calcHeading();
		}

		/**
		 * Calculate the new t value when moving with distance.
		 * 
		 * @param distance
		 */
		private void calcT(double distance) {
			t += distance * 1. / v1.mult(t).plus(v2).norm();

		}

		@Override
		public double remaining() {
			return length - totDist;
		}

		@Override
		public void draw(Graphics2D g2d) {
			Vector2D p = point.mult(Simulation.SCALE);
			g2d.drawOval((int) (p.x - 1), (int) (p.y - 1), 2, 2);
		}

	}
}
