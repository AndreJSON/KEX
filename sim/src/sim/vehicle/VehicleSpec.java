package sim.vehicle;

import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;

import math.Vector2D;

public class VehicleSpec {
	private String name;
	private final double length, width;
	private Shape shape;
	private Color color;

	public VehicleSpec() {
		this("Mazda3", 180.3*0.0254, 70.7*0.0254, Color.blue);
	}

	public VehicleSpec(String name, double length, double width, Color color) {
		this.setColor(color);
		this.setName(name);
		this.length = length;
		this.width = width;
		shape = new Rectangle2D.Double(-length / 2, -width / 2, length, width);
	}

	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Get the name of the vehicle type.
	 * 
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * Get the length of the vehicle.
	 * 
	 * @return
	 */
	public double getLength() {
		return length;
	}

	/**
	 * Get the width of the vehicle.
	 * 
	 * @return
	 */
	public double getWidth() {
		return width;
	}

	/**
	 * Converts center to rear.
	 * 
	 * @return
	 */
	public Vector2D center2rear(Vector2D pos, double heading) {
		// EJ KONTROLLERAD
		double x = pos.getX() - Math.cos(heading) * length / 2;
		double y = pos.getY() - Math.sin(heading) * length / 2;
		return new Vector2D(x, y);
	}

	/**
	 * Converts center to front
	 * 
	 * @return
	 */
	public Vector2D center2front(Vector2D pos, double heading) {
		// EJ KONTROLLERAD
		double x = pos.getX() + Math.cos(heading) * length / 2;
		double y = pos.getY() + Math.sin(heading) * length / 2;
		return new Vector2D(x, y);
	}

	/**
	 * Get the shape of the vehicle.
	 * 
	 * @return
	 */
	public Shape getShape() {
		return shape;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}
}
