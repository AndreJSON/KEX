package car;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;

import map.track.TrackPosition;
import math.Vector2D;
import sim.Drawable;
import sim.Simulation;

public class Car implements Drawable {
	private static final double magicCoefficient = 13.8; // my * g / 2
	private final CarModel specs;
	private static long trackId = 0;
	private long id;
	private TrackPosition position;
	private double speed, heading = 0, breakingDistance;
	private boolean isAutonomous = true;

	public Car(CarModel specs) {
		this.specs = specs;
		id = ++trackId;
	}

	@Override
	public String toString() {
		return "Car" + id + "[" + specs.getName() + "]";
	}

	public long getID() {
		return id;
	}

	/**
	 * Move, act on delta time.
	 * 
	 * @param delta
	 */
	public void move(double delta) {
		if (position == null) {
			throw new NullPointerException(this
					+ " has not been assigned a track!");
		}
		if (position.remaining() > 0) {

			position.move(delta * speed);
			double rotation = speed
					* (Math.tan(position.getHeading() - heading) / specs
							.getWheelBase());

			heading += rotation * delta % (2 * Math.PI);

		} else {
			throw new RuntimeException(this + " is out of track!");
		}
	}

	/**
	 * Get the current position of the car.
	 * 
	 * @return
	 */
	public Vector2D getPosition() {
		if (position == null) {
			throw new NullPointerException(this
					+ " has not been assigned a track!");
		}
		return position.getPoint();
	}

	/**
	 * Check how much there is remaining on the track.
	 * 
	 * @return
	 */
	public double remainingOnTrack() {
		if (position == null) {
			throw new NullPointerException(this
					+ " has not been assigned a track!");
		}
		return position.remaining();
	}

	/**
	 * Get the heading of the car.
	 * 
	 * @return
	 */
	public double getHeading() {
		return heading;
	}

	/**
	 * Get the velocity of the car.
	 * 
	 * @return
	 */
	public double getSpeed() {
		return speed;
	}

	/**
	 * Set the speed of the car.
	 * 
	 * @param velocity
	 */
	public void setSpeed(double speed) {
		this.speed = speed;
		breakingDistance = speed * speed / magicCoefficient;
	}

	public void setAutonomy(boolean value) {
		isAutonomous = value;
	}

	public boolean getAutonomy() {
		return isAutonomous;
	}

	/**
	 * Gives the maximum acceleration the vehicle is able to perform in the
	 * given time diff.
	 */
	public double getMaxAcceleration(double diff) {
		return diff * specs.getMaxAcceleration(); // TODO: Actually have a decent value here.
	}

	/**
	 * Gives the maximum retardation the vehicle is able to perform in the given
	 * time diff.
	 */
	public double getMaxRetardation(double diff) {
		double reduction = speed
				- Math.sqrt(magicCoefficient
						* (breakingDistance - speed * diff));
		if (Double.isNaN(reduction))
			reduction = speed;
		return reduction;
	}

	public double getBreakingDistance() {
		return breakingDistance;
	}

	/**
	 * get the car type.
	 * 
	 * @return
	 */
	public CarModel getType() {
		return specs;
	}

	/**
	 * Give the car a TrackPosition to follow.
	 * 
	 * @param tPosition
	 *            the position to follow.
	 */
	public void setTrackPosition(TrackPosition tPosition) {
		if (position == null) {
			heading = tPosition.getHeading();
		}
		position = tPosition;
	}

	@Override
	public void draw(Graphics2D g2d) {
		if (position == null)
			return;

		g2d.setColor(specs.getColor());
		Vector2D p = getPosition().mult(Simulation.SCALE);

		// Default heading is to the right

		AffineTransform aF = new AffineTransform();
		aF.translate(p.x, p.y);
		aF.scale(Simulation.SCALE, Simulation.SCALE);
		g2d.setColor(Color.black);
		aF.rotate(getHeading());

		for (int i = 0; i < 4; i++) {
			g2d.fill(aF.createTransformedShape(specs.wheels[i]));
		}
		g2d.setColor(specs.getColor());
		Shape shape = aF.createTransformedShape(specs.getShape());
		g2d.fill(shape);

		if (!Simulation.DEBUG)
			return;
		p = specs.getCenterPoint(getPosition(), heading).mult(Simulation.SCALE);
		g2d.setColor(Color.black);
		g2d.fillOval((int) p.x - 1, (int) p.y - 1, 3, 3);
		g2d.drawString(this.toString() + " " + (int) (speed * 3.6) + " k/h",
				(int) p.x + 2, (int) p.y - 2);

	}

	public CarModel getModel() {
		return specs;
	}

	public double getLength() {
		return this.getModel().getLength();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		} else if (obj instanceof Car) {
			Car c = (Car) obj;
			return c.id == id;
		}
		return false;
	}

	@Override
	public int hashCode() {
		return (int) id;
	}

	public boolean isAutonomous() {
		return isAutonomous;
	}

	public void setAutonomous(boolean isAutonomous) {
		this.isAutonomous = isAutonomous;
	}
}
