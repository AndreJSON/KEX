package car;

import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;

import math.Vector2D;

public class CarModel {
	private final String name;
	private final double length, width;
	private final Shape shape;
	public final Shape[] wheels;
	private final Color color;
	
	private final double frontAxleDisplacement;
	private final double rearAxleDisplacement;
	
	private final double maxAcceleration;
	private final double maxRetardation;
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
			double frontAxleDisplacement, double rearAxleDisplacement, double maxAcceleration, double maxRetardation, double topSpeed) {
		this.topSpeed = topSpeed;
		this.maxAcceleration = maxAcceleration;
		this.maxRetardation = maxRetardation;
		this.frontAxleDisplacement = frontAxleDisplacement;
		this.rearAxleDisplacement = rearAxleDisplacement;
		this.color = color;
		this.name = name;
		this.length = length;
		this.width = width;
		shape = new Rectangle2D.Double(-length, -width / 2, length, width);

		double wheelDiameter = 0.61, wheelWidth = 0.25;
		wheels = new Shape[4];
		wheels[0] = new Rectangle2D.Double(-frontAxleDisplacement
				- wheelDiameter / 2, -width / 2 - wheelWidth / 2,
				wheelDiameter, wheelWidth);
		wheels[1] = new Rectangle2D.Double(-frontAxleDisplacement
				- wheelDiameter / 2, width / 2 - wheelWidth / 2, wheelDiameter,
				wheelWidth);
		wheels[2] = new Rectangle2D.Double(-rearAxleDisplacement
				- wheelDiameter / 2, -width / 2 - wheelWidth / 2,
				wheelDiameter, wheelWidth);
		wheels[3] = new Rectangle2D.Double(-rearAxleDisplacement
				- wheelDiameter / 2, width / 2 - wheelWidth / 2, wheelDiameter,
				wheelWidth);
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
	 * Gets the rear point of the car.
	 * 
	 * @param pos
	 *            the center point of the car
	 * @param heading
	 *            the heading of the car
	 * @return rear point of the car
	 */
	public Vector2D getRearPoint(Vector2D pos, double heading) {
		// EJ KONTROLLERAD
		double x = pos.getX() - Math.cos(heading) * length;
		double y = pos.getY() - Math.sin(heading) * length;
		return new Vector2D(x, y);
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
	 * Gets the center point of the car.
	 * 
	 * @param pos
	 *            the center point of the car
	 * @param heading
	 *            the heading of the car
	 * @return rear point of the car
	 */
	public Vector2D getFrontWheelPoint(Vector2D pos, double heading) {
		// EJ KONTROLLERAD
		double x = pos.getX() - Math.cos(heading) * frontAxleDisplacement;
		double y = pos.getY() - Math.sin(heading) * frontAxleDisplacement;
		return new Vector2D(x, y);
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
	public Vector2D getRearWheelPoint(Vector2D pos, double heading) {
		// EJ KONTROLLERAD
		double x = pos.getX() - Math.cos(heading) * rearAxleDisplacement;
		double y = pos.getY() - Math.sin(heading) * rearAxleDisplacement;
		return new Vector2D(x, y);
	}

	public double getWheelBase() {
		return rearAxleDisplacement - frontAxleDisplacement;
	}

	/**
	 * Get the shape of the car type.
	 * 
	 * @return
	 */
	public Shape getShape() {
		return shape;
	}

	/**
	 * Get the color of the car type.
	 * 
	 * @return the color of the car type.
	 */
	public Color getColor() {
		return color;
	}

	@Override
	public String toString() {
		return "CarType[" + getName() + "]";
	}

	public double getFrontAxleDisplacement() {
		return frontAxleDisplacement;
	}

	public double getRearAxleDisplacement() {
		return rearAxleDisplacement;
	}

	public double getMaxAcceleration() {
		return maxAcceleration;
	}

	public double getMaxRetardation() {
		return maxRetardation;
	}

	public double getTopSpeed() {
		return topSpeed;
	}
}
