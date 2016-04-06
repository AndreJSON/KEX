package sim.map.track;

import math.Vector2D;

public class LineTrack implements AbstractTrack {
	private final double length;
	private Vector2D startPoint, endPoint, unit;

	public LineTrack(Vector2D startPoint, Vector2D endPoint) {
		this.startPoint = startPoint;
		this.endPoint = endPoint;
		length = startPoint.distance(endPoint);
		unit = (endPoint.minus(startPoint)).unit();
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

}
