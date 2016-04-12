package car;

import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;

import util.CollisionBox;

import math.Vector2D;

public class CarModel {
	/**
	 * The name of this car model.
	 */
	private final String name;
	/**
	 * The length and the width of the chassi
	 */
	private final double length, width;
	/**
	 * The shape for this car model in meters. Centered at the car front.
	 * Heading in 0 theta.
	 */
	private final Shape carShape;
	private final CollisionBox collisionBox;

	/**
	 * Color of this model of car.
	 */
	private final Color modelColor;

	/**
	 * The distance from the front to the front axel and the rear axel.
	 */
	private final double frontAxleDisplacement;
	private final double rearAxleDisplacement;

	private final double maxAcceleration;
	private final double maxDeceleration;
	private final double topSpeed;

	/**
	 * 
	 * @param name
	 *            the name of the car type
	 * @param length
	 *            the length of the car type
	 * @param width
	 *            the width of the car type
	 * @param color
	 *            the color of the car type
	 */
	public CarModel(String name, double length, double width, Color color,
			double frontAxleDisplacement, double rearAxleDisplacement,
			double maxAcceleration, double maxRetardation, double topSpeed) {
		this.topSpeed = topSpeed;
		this.maxAcceleration = maxAcceleration;
		this.maxDeceleration = maxRetardation;
		this.frontAxleDisplacement = frontAxleDisplacement;
		this.rearAxleDisplacement = rearAxleDisplacement;
		this.modelColor = color;
		this.name = name;
		this.length = length;
		this.width = width;
		carShape = new Rectangle2D.Double(-length, -width / 2, length, width);
		
		double halfWidth = width / 2;
		collisionBox = new CollisionBox(4, -length, -halfWidth);
		collisionBox.lineTo(0, -halfWidth);
		collisionBox.lineTo(0, halfWidth);
		collisionBox.lineTo(-length, halfWidth);
		collisionBox.close();
	}

	/**
	 * Get the name of the car type.
	 * 
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * Get the length of the car type.
	 * 
	 * @return
	 */
	public double getLength() {
		return length;
	}

	/**
	 * Get the width of the car type.
	 * 
	 * @return
	 */
	public double getWidth() {
		return width;
	}

	/**
	 * Gets the center point of the car.
	 * 
	 * @param pos
	 *            the center point of the car
	 * @param heading
	 *            the heading of the car
	 * @return rear point of the car
	 */
	public Vector2D getCenterPoint(Vector2D pos, double heading) {
		// EJ KONTROLLERAD
		double x = pos.getX() - Math.cos(heading) * length / 2;
		double y = pos.getY() - Math.sin(heading) * length / 2;
		return new Vector2D(x, y);
	}

	/**
	 * Get the distance between front wheel axel and rear wheel axel.
	 * 
	 * @return
	 */
	public double getWheelBase() {
		return rearAxleDisplacement - frontAxleDisplacement;
	}

	/**
	 * Get the shape of the car type.
	 * 
	 * @return
	 */
	public Shape getShape() {
		return carShape;
	}

	/**
	 * Get the color of the car type.
	 * 
	 * @return the color of the car type.
	 */
	public Color getColor() {
		return modelColor;
	}

	@Override
	public String toString() {
		return "CarType[" + getName() + "]";
	}

	/**
	 * Get the distance from the front to the front axle.
	 * 
	 * @return
	 */
	public double getFrontAxleDisplacement() {
		return frontAxleDisplacement;
	}

	/**
	 * Get the distance from the front to the rear axle.
	 * 
	 * @return
	 */
	public double getRearAxleDisplacement() {
		return rearAxleDisplacement;
	}

	/**
	 * Get the maximum acceleration.
	 * 
	 * @return
	 */
	public double getMaxAcceleration() {
		return maxAcceleration;
	}

	/**
	 * Get the maximum retardation.
	 * 
	 * @return
	 */
	public double getMaxDeceleration() {
		return maxDeceleration;
	}

	/**
	 * Get the top speed.
	 * 
	 * @return
	 */
	public double getTopSpeed() {
		return topSpeed;
	}

	/**
	 * @return the collisionBox
	 */
	public CollisionBox getCollisionBox() {
		return collisionBox;
	}
}
