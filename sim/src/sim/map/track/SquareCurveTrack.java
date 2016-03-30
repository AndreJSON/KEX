package sim.map.track;

/**
 * Second degree bezier track.
 * 
 * @author henrik
 * 
 */
public class SquareCurveTrack implements AbstractTrack {

	public static int RIEMANN_STEPS = 1000; // default value

	private final Vector2D sPoint, cPoint, ePoint;
	private final double length;

	public SquareCurveTrack(Vector2D startPoint, Vector2D controlPoint,
			Vector2D endPoint) {
		this.sPoint = startPoint;
		this.cPoint = controlPoint;
		this.ePoint = endPoint;

		// Approximate track length using Riemann sum.
		double length = 0;
		Vector2D from = startPoint;
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
		return sPoint.mult(tm * tm).plus(cPoint.mult(tm * t)).plus(ePoint.mult(t * t));
	}

	@Override
	public Vector2D getStartPoint() {
		return sPoint;
	}

	@Override
	public Vector2D getEndPoint() {
		return ePoint;
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
	public TrackPosition getTrackPosition(double dist) {
		return new Position(dist);
	}

	private class Position implements TrackPosition {
		private double t, heading, totDist;
		private Vector2D point;

		public Position(double dist) {
			point = new Vector2D();
			t = 0;
			move(dist);
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
		public void move(double dist) {
			// TODO: move algorithm.
			calcHeading();
			totDist += dist;
		}
		

		@Override
		public double remaining() {
			return length - totDist;
		}

	}
	

	@Override
	public String toString(){
		return "SquareCurveTrack{["+sPoint.x+", "+sPoint.y+"], ["+cPoint.x+", "+cPoint.y+"], ["+ePoint.x+", "+ePoint.y+"]}";
	}

	// TEST CODE

	public static void main(String[] args) {

		// Testing arc length.
		SquareCurveTrack curve = new SquareCurveTrack(new Vector2D(0, 0),
				new Vector2D(0, 1), new Vector2D(1, 1));

		System.out.println(curve);
		System.out.println(curve.length);
	}


}
