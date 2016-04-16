package car;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;

import map.track.TrackPosition;
import math.Vector2D;
import sim.Drawable;
import sim.Simulation;
import util.CollisionBox;

/**
 * @author henrik
 * 
 */
public abstract class AbstractCar implements Drawable {
	private static long trackId = 0;
	// private fields
	protected final long id;
	protected double acceleration;
	protected double speed;
	protected double theta;
	protected CarModel carModel;
	protected TrackPosition tPos;
	protected CollisionBox collisionBox;
	public Color color;

	public AbstractCar(CarModel carModel) {
		this.id = ++trackId;
		this.carModel = carModel;
		this.acceleration = 0;
		this.speed = 0;
		this.theta = 0;
		color = carModel.getColor();
	}

	@Override
	public String toString() {
		return "AbstractCar" + id + "[" + carModel.getName() + "]";
	}

	public void updateCollisionBox() {
		double x = tPos.getX();
		double y = tPos.getY();
		collisionBox = carModel.getCollisionBox().transform(x, y, theta);
	}

	public long getID() {
		return id;
	}

	public CollisionBox getCollisionBox() {
		return collisionBox;
	}

	/**
	 * Move, act on delta time.
	 * 
	 * @param diff
	 */
	public void move(double diff) {
		if (tPos.remaining() > 0) {
			speed += acceleration * diff;
			speedClamp();

			tPos.move(diff * getSpeed());
			double rotation = getSpeed()
					* (Math.tan(tPos.getHeading() - theta) / carModel
							.getWheelBase());

			theta += rotation * diff;

		}
	}

	public void setHeading(double heading) {
		this.theta = heading;
	}

	public void setAcc(double acceleration) {
		this.acceleration = acceleration;
		accelerationClamp();
	}

	public void setSpeed(double speed) {
		this.speed = speed;
		speedClamp();
	}

	private void accelerationClamp() {
		acceleration = clamp(-carModel.getMaxRet(), carModel.getMaxAcc(),
				acceleration);
	}

	private void speedClamp() {
		speed = clamp(0, carModel.getTopSpeed(), speed);
	}

	private double clamp(double lower, double upper, double value) {
		if (value > upper)
			return upper;
		if (value < lower)
			return lower;
		return value;
	}

	public double getSpeed() {
		return speed;
	}

	public double getAcc() {
		return acceleration;
	}

	@Override
	public void draw(Graphics2D g2d) {

		g2d.setColor(carModel.getColor());
		Vector2D p = getPos().mult(Simulation.SCALE);

		// Default heading is to the right

		AffineTransform aF = new AffineTransform();
		aF.translate(p.x, p.y);
		aF.scale(Simulation.SCALE, Simulation.SCALE);
		g2d.setColor(Color.black);
		aF.rotate(theta);
		g2d.setColor(color);
		Shape shape = aF.createTransformedShape(carModel.getShape());
		g2d.fill(shape);

	}

	public Vector2D getPos() {
		return tPos.getPoint();
	}

	public void setTrackPosition(TrackPosition tPos) {
		this.tPos = tPos;
	}

	public TrackPosition getTrackPosition() {
		return tPos;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		} else if (obj instanceof ACar) {
			AbstractCar c = (AbstractCar) obj;
			return c.id == id;
		}
		return false;
	}

	public static double getBreakDistance(double speed, double deceleration) {
		if (deceleration <= 0)
			throw new RuntimeException("Deceleration must be positiv.");
		return 0.5 * Math.pow(speed, 2.) / deceleration;
	}


	public static double distance(double speed, double acceleration,
			double time, double maxSpeed) {
		double distance = 0;
		for (double t = 0; t < time; t += 0.01) {
			speed += acceleration * 0.01;
			speed = Math.min(speed, maxSpeed);
			distance += speed * 0.01;
		}
		return distance;
	}
	

	public double getBreakDistance(double deceleration) {
		return getBreakDistance(getSpeed(), deceleration);
	}
}
