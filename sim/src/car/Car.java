package car;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.util.Iterator;

import map.intersection.Segment;
import map.track.TrackPosition;
import math.Vector2D;
import sim.Drawable;
import sim.EntityDb;
import sim.Simulation;
import traveldata.TravelData;
import util.CollisionBox;

/**
 * @author henrik
 * 
 */
public class Car implements Drawable {
	// private fields
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
	 * The collisionBox for the car.
	 */
	private CollisionBox collisionBox;
	/**
	 * The speed of the car.
	 */
	private double speed;
	/**
	 * The heading of the car chassi.
	 */
	private double heading;
	/**
	 * If the car is autonomous or not.
	 */
	private boolean isAutonomous;
	/**
	 * The cars acceleration.
	 */
	private double acceleration;
	/**
	 * If the collision is on or off.
	 */
	private boolean collidable;

	private TravelData travelData;

	private boolean finished;

	// Constrtuctors
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
		speed = 0;
		heading = 0;
		collidable = false;
		finished = false;
	}

	// public methods
	/**
	 * The to string has the form "Car" + id + "[" + Car model name + "]".
	 */
	@Override
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
			speed += acceleration * diff;

			speedClamp();
			position.move(diff * getSpeed());
			double rotation = getSpeed()
					* (Math.tan(position.getHeading() - heading) / carModel
							.getWheelBase());

			heading += rotation * diff;

		} else {
			throw new RuntimeException(this + " is out of track!");
		}
		if (position.remaining() <= 0) {
			EntityDb.removeCarFromSegment(this);
			if (travelData.hasNext()) {
				position = travelData.next().getTrackPosition(
						-position.remaining());
				EntityDb.addCarToSegment(this);
			} else {
				finished = true;
			}
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
		if (speed < 0.01)
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

	public void setAcc(double value) {
		acceleration += value;
		accelerationClamp();
	}

	// private methods
	/**
	 * Clamps the acceleration.
	 */
	private void accelerationClamp() {
		acceleration = clamp(-getMaxDeceleration(), getMaxAcceleration(),
				acceleration);
	}

	/**
	 * Get the top speed of this car. Same as car.getType().getTopSpeed();
	 * 
	 * @return
	 */
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

	/**
	 * Get the breaking distance with specified deceleration.
	 * 
	 * @param deceleration
	 * @return
	 */
	public double getBreakingDistance(double deceleration) {
		if (deceleration <= 0)
			throw new RuntimeException("Deceleration must be positiv.");
		return 0.5 * Math.pow(getSpeed(), 2.) / deceleration;
	}

	/**
	 * Get the car type.
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

		g2d.setColor(carModel.getColor());
		Shape shape = aF.createTransformedShape(carModel.getShape());
		g2d.fill(shape);

		if (!Simulation.DEBUG)
			return;
		p = carModel.getCenterPoint(getPosition(), heading).mult(
				Simulation.SCALE);
		g2d.setColor(Color.black);
		g2d.drawString((int) (speed * 3.6) + " k/h", (int) p.x + 2,
				(int) p.y - 2);

	}

	/**
	 * Get the model of the car.
	 */
	public CarModel getModel() {
		return carModel;
	}

	/**
	 * Get the length of the car. Equivalent to getModel().getLength();
	 * 
	 * @return
	 */
	public double getLength() {
		return getModel().getLength();
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

	/**
	 * Check if this car collides.
	 * 
	 * @return
	 */
	public boolean isCollidable() {
		return collidable;
	}

	/**
	 * Set collidable.
	 * 
	 * @return
	 */
	public void setCollidable(boolean collidable) {
		this.collidable = collidable;
	}

	/**
	 * Get the cars collision box
	 */
	public CollisionBox getCollisionBox() {
		return collisionBox;
	}

	/**
	 * Set the cars collision box
	 */
	public void setCollisionBox(CollisionBox collisionBox) {
		this.collisionBox = collisionBox;
	}

	public Car nextCar() {
		Segment searchSegment;
		Iterator<Car> carsOnSegment;
		boolean passedSelf = false;

		passedSelf = false;
		searchSegment = travelData.currentSegment();
		while (true) {
			carsOnSegment = EntityDb.getCarsOnSegment(searchSegment)
					.descendingIterator();
			while (carsOnSegment.hasNext()) {
				Car nextCar = carsOnSegment.next();
				if (equals(nextCar)) {
					passedSelf = true;
				} else if (passedSelf) {
					return nextCar;
				}
			}
			searchSegment = searchSegment.nextSegment(travelData
					.getDestination());
			if (searchSegment == null) {
				break;
			}
		}

		// Return null if no car is in front of this car.
		return null;
	}

	public double distNextCar() {
		// If the car has passed itself during the search.
		boolean passedSelf = false;
		// Get the travel data of the car.
		// Get the current segment the car is on.
		Segment searchSegment;
		// Iterator to check all cars on this segment.
		Iterator<Car> carsOnSegment;
		// The distance to next car.
		double distance;
		//

		distance = 0;
		passedSelf = false;
		searchSegment = travelData.currentSegment();
		while (true) {
			carsOnSegment = EntityDb.getCarsOnSegment(searchSegment)
					.descendingIterator();
			while (carsOnSegment.hasNext()) {
				Car nextCar = carsOnSegment.next();
				if (equals(nextCar)) {
					passedSelf = true;
				} else if (passedSelf) {
					distance += -nextCar.remainingOnTrack()
							+ remainingOnTrack() - getModel().getLength();
					return distance;
				}
			}
			searchSegment = searchSegment.nextSegment(travelData
					.getDestination());
			if (searchSegment == null)
				break;
			distance += searchSegment.length();
		}

		// Return -1 if no car is in front of this car.
		return -1;
	}

	/**
	 * Clamps the speed.
	 */
	private void speedClamp() {
		speed = clamp(-getTopSpeed() / 10, getTopSpeed(), speed);
	}

	private double clamp(double lowerBound, double upperBound, double value) {
		if (value < lowerBound)
			return lowerBound;
		else if (value > upperBound)
			return upperBound;
		return value;
	}

	public void setTravelData(TravelData travelData) {
		this.travelData = travelData;
		this.position = travelData.currentSegment().getTrackPosition(0);
		this.heading = position.getHeading();
	}

	public boolean isFinished(){
		return finished;
	}

	public Segment getSegment() {
		return travelData.currentSegment();
	}

	public TravelData getTravelData() {
		return travelData;
	}
}
