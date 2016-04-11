package car;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;

import map.track.TrackPosition;
import math.Vector2D;
import sim.Drawable;
import sim.Simulation;

/**
 * @author henrik
 * 
 */
public class Car implements Drawable {
	/**
	 * my * g / 2
	 */
	// private static final double magicCoefficient = 13.8; //
	/**
	 * Hold track of the currently highest id.
	 */
	private static long trackId = 0;

	/**
	 * // The car id.
	 */
	private final long id;
	/**
	 * The car model this car is of.
	 */
	private final CarModel carModel;
	/**
	 * The position and movement data of the car.
	 */
	private TrackPosition position;
	/**
	 * The speed of the car.
	 */
	private double speed;
	/**
	 * The heading of the car chassi.
	 */
	private double heading = 0;
	/**
	 * If the car is autonomous or not.
	 */
	private boolean isAutonomous;
	/**
	 * The cars acceleration.
	 */
	private double acceleration;
	private double relAcceleration;
	private boolean collision = false;

	/**
	 * Create a new car of the specified car model.
	 * 
	 * @param carModel
	 */
	public Car(CarModel carModel) {
		this.carModel = carModel;
		id = ++trackId;
		isAutonomous = true;
		acceleration = 0;
		relAcceleration = 0;
	}

	/**
	 * The to string has the form "Car" + id + "[" + Car model name + "]".
	 */
	public String toString() {
		return "Car" + id + "[" + carModel.getName() + "]";
	}

	/**
	 * Get the id of the car.
	 * 
	 * @return
	 */
	public long getID() {
		return id;
	}

	/**
	 * Move, act on delta time.
	 * 
	 * @param diff
	 */
	public void move(double diff) {
		if (position == null) {
			throw new NullPointerException(this
					+ " has not been assigned a track!");
		}
		if (position.remaining() > 0) {
			relAcceleration = -speed;
			speed += acceleration * diff;
		
			speedClamp();
			relAcceleration += speed;
			relAcceleration /= diff;
			position.move(diff * getSpeed());
			double rotation = getSpeed()
					* (Math.tan(position.getHeading() - heading) / carModel
							.getWheelBase());

			heading += rotation * diff % (2 * Math.PI);

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
		if (speed < 0.05)
			return 0;
		return speed;
	}

	/**
	 * Set the speed of the car.
	 * 
	 * @param velocity
	 */
	public void setSpeed(double speed) {
		this.speed = speed;
		speedClamp();
	}

	/**
	 * Gives the maximum acceleration the vehicle is able to perform in the
	 * given time diff.
	 */
	public double getMaxAcceleration() {
		return carModel.getMaxAcceleration();
	}

	public void setAcceleration(double value) {
		acceleration += value;
		accelerationClamp();
	}
	
	public double getAcceleration(){
		return relAcceleration;
	}

	private void accelerationClamp() {
		if (acceleration > carModel.getMaxAcceleration())
			acceleration = carModel.getMaxAcceleration();
		if (acceleration < -carModel.getMaxDeceleration())
			acceleration = -carModel.getMaxDeceleration();
	}

	private void speedClamp() {
		if (speed < 0)
			speed = 0;
		else if (speed > carModel.getTopSpeed())
			speed = carModel.getTopSpeed();
	}

	public double getTopSpeed() {
		return carModel.getTopSpeed();
	}

	/**
	 * Gives the maximum retardation the vehicle is able to perform in the given
	 * time diff.
	 * 
	 * @param diff
	 * @return
	 */
	public double getMaxDeceleration() {
		return carModel.getMaxDeceleration();
	}

	public double getBreakingDistance() {
		return Math.pow(getSpeed() * 3.6 / 9, 2);
	}

	/**
	 * get the car type.
	 * 
	 * @return
	 */
	public CarModel getType() {
		return carModel;
	}

	/**
	 * Give the car a TrackPosition to follow.
	 * 
	 * @param trackPosition
	 *            the position to follow.
	 */
	public void setTrackPosition(TrackPosition trackPosition) {
		if (position == null) {
			heading = trackPosition.getHeading();
		}
		position = trackPosition;
	}

	@Override
	public void draw(Graphics2D g2d) {
		if (position == null)
			return;

		g2d.setColor(carModel.getColor());
		Vector2D p = getPosition().mult(Simulation.SCALE);

		// Default heading is to the right

		AffineTransform aF = new AffineTransform();
		aF.translate(p.x, p.y);
		aF.scale(Simulation.SCALE, Simulation.SCALE);
		g2d.setColor(Color.black);
		aF.rotate(getHeading());
		/*
		for (int i = 0; i < 4; i++) {
			g2d.fill(aF.createTransformedShape(carModel.wheels[i]));
		}*/
		g2d.setColor(carModel.getColor());
		Shape shape = aF.createTransformedShape(carModel.getShape());
		g2d.fill(shape);

		if (!Simulation.DEBUG)
			return;
		p = carModel.getCenterPoint(getPosition(), heading).mult(
				Simulation.SCALE);
		g2d.setColor(Color.black);
		g2d.fillOval((int) p.x - 1, (int) p.y - 1, 3, 3);
		g2d.drawString(/* this.toString() + " " + */(int) (speed * 3.6)
				+ " k/h", (int) p.x + 2, (int) p.y - 2);

	}

	/**
	 * Get the model of the car.
	 */
	public CarModel getModel() {
		return carModel;
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

	/**
	 * Return true if the car is autonomous.
	 * 
	 * @return
	 */
	public boolean isAutonomous() {
		return isAutonomous;
	}

	/**
	 * Set the car autonomous state to the specified value.
	 * 
	 * @return
	 */
	public void setAutonomous(boolean isAutonomous) {
		this.isAutonomous = isAutonomous;
	}

	public boolean isCollision() {
		return collision;
	}

	public void setCollision(boolean collision) {
		this.collision = collision;
	}
}
