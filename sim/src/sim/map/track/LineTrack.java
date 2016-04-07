package sim.map.track;

import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import math.Vector2D;

public class LineTrack extends AbstractTrack {
	private final double length;
	private Vector2D startPoint, endPoint, unit;

	private Vector2D[] points;

	public LineTrack(Vector2D startPoint, Vector2D endPoint) {
		this.startPoint = startPoint;
		this.endPoint = endPoint;
		length = startPoint.distance(endPoint);
		unit = (endPoint.minus(startPoint)).unit();
		generatePoints();
	}

	private void generatePoints() {
		ArrayList<Vector2D> list = new ArrayList<>();
		list.add(startPoint);

		int curr = 1;
		while (curr * super.POINT_STEP < length) {
			list.add(startPoint.plus(unit.mult(curr * super.POINT_STEP)));
			curr++;
		}

		list.add(endPoint);
		points = (Vector2D[]) list.toArray(new Vector2D[list.size()]);
	}

	@Override
	public double length() {
		return length;
	}

	public class Position implements TrackPosition {
		private Vector2D point;
		private double totDist;

		private Position(double dist) {
			point = new Vector2D(startPoint);
			move(dist);
		}

		@Override
		public double getHeading() {
			return unit.theta();
		}

		@Override
		public void move(double dist) {
			totDist += dist;
			point = startPoint.plus(unit.mult(totDist));
		}

		@Override
		public Vector2D getPoint() {
			return point;
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

	@Override
	public TrackPosition getTrackPosition() {
		return new Position(0);
	}

	@Override
	public TrackPosition getTrackPosition(double distance) {
		return new Position(distance);
	}

	@Override
	public Vector2D getStartPoint() {
		return startPoint;
	}

	@Override
	public Vector2D getEndPoint() {
		return endPoint;
	}

	@Override
	public String toString() {
		return "LineTrack{" + startPoint + ", " + endPoint + "}";
	}

	@Override
	public Vector2D[] getPoints() {
		return points;
	}

	@Override
	public void draw(Graphics2D g2d) {
		g2d.draw(new Line2D.Double(startPoint, endPoint));
	}

}
