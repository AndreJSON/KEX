package sim.vehicle;

import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;

import math.Vector2D;

public class CarType {
	private String name;
	private final double length, width;
	private Shape shape;
	private Color color;
	private int destination;

	/**
	 * Constructs a default car type.
	 */
	public CarType() {
		this("Mazda3", 180.3 * 0.0254, 70.7 * 0.0254, Color.blue);
	}

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
	public CarType(String name, double length, double width, Color color) {
		this.setColor(color);
		this.setName(name);
		this.length = length;
		this.width = width;
		shape = new Rectangle2D.Double(-length / 2, -width / 2, length, width);
	}

	/**
	 * Set a new name for the car type.
	 * 
	 * @param name
	 *            the new name
	 */
	public void setName(String name) {
		this.name = name;
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
	public Vector2D center2rear(Vector2D pos, double heading) {
		// EJ KONTROLLERAD
		double x = pos.getX() - Math.cos(heading) * length / 2;
		double y = pos.getY() - Math.sin(heading) * length / 2;
		return new Vector2D(x, y);
	}

	/**
	 * Gets the front point of the car.
	 * 
	 * @param pos
	 *            the center point of the car
	 * @param heading
	 *            the heading of the car
	 * @return front point of the car
	 */
	public Vector2D center2front(Vector2D pos, double heading) {
		// EJ KONTROLLERAD
		double x = pos.getX() + Math.cos(heading) * length / 2;
		double y = pos.getY() + Math.sin(heading) * length / 2;
		return new Vector2D(x, y);
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

	/**
	 * Set a new color to the car type.
	 * 
	 * @param color
	 *            the new color of the car type.
	 */
	public void setColor(Color color) {
		this.color = color;
	}
}
