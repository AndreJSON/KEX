package sim.map.track;

import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;

public class LineTrack implements AbstractTrack {
	private final double length;
	private Point2D startPoint, endPoint;

	private double dx, dy, slope;

	public LineTrack(Point2D startPoint, Point2D endPoint) {
		this.startPoint = startPoint;
		this.endPoint = endPoint;
		length = startPoint.distance(endPoint);

		dx = endPoint.getX() - startPoint.getX();
		dy = endPoint.getY() - startPoint.getY();
		slope = dy / dx;
	}

	@Override
	public double length() {
		return length;
	}

	public class Position implements TrackPosition {
		private double x;
		private double y;
		private double dist;

		private Position() {
			this.x = startPoint.getX();
			this.y = startPoint.getX();
			dist = 0;
		}

		@Override
		public double getX() {
			return x;
		}

		@Override
		public double getY() {
			return y;
		}

		@Override
		public double getHeading() {
			return slope;
		}

		@Override
		public double move(double dist) {
			x += dx / length * dist;
			y += dy / length * dist;
			this.dist += dist;
			return length - this.dist;
		}

		@Override
		public Double getPoint() {
			return new Point2D.Double(x, y);
		}

	}

	@Override
	public TrackPosition getTrackPosition() {
		return new Position();
	}

	@Override
	public Point2D getStartPoint() {
		// TODO Auto-generated method stub
		return startPoint;
	}

	@Override
	public Point2D getEndPoint() {
		// TODO Auto-generated method stub
		return endPoint;
	}

}
