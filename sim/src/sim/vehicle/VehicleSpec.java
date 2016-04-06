package sim.vehicle;

import java.awt.Shape;

import Math.Vector2D;

public class VehicleSpec {
	private String name;
	private double length, width;
	// Distance from the center to the front axis and rear axis.
	// rear_axis + front_axis = length of wheel base
	private double front_axis, rear_axis;

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
	 * @return
	 */
	public Vector2D center2rear(Vector2D pos, double heading) {
		// EJ KONTROLLERAD
		double x = pos.getX() - Math.cos(heading) * length / 2;
		double y = pos.getY() - Math.sin(heading) * length / 2;
		return new Vector2D(x, y);
	}

	/**
	 * Converts center to rear axis.
	 * 
	 * @param pos
	 * @param heading
	 * @return
	 */
	public Vector2D center2rearaxis(Vector2D pos, double heading) {
		// EJ KONTROLLERAD
		double x = pos.getX() - Math.cos(heading) * rear_axis;
		double y = pos.getY() - Math.sin(heading) * rear_axis;
		return new Vector2D(x, y);
	}

	/**
	 * Converts center to front
	 * @return
	 */
	public Vector2D center2front(Vector2D pos, double heading) {
		// EJ KONTROLLERAD
		double x = pos.getX() + Math.cos(heading) * length / 2;
		double y = pos.getY() + Math.sin(heading) * length / 2;
		return new Vector2D(x, y);
	}

	/**
	 * Converts center to front axis
	 * @param pos
	 * @param heading
	 * @return
	 */
	public Vector2D center2frontaxis(Vector2D pos, double heading) {
		// EJ KONTROLLERAD
		double x = pos.getX() + Math.cos(heading) * front_axis;
		double y = pos.getY() + Math.sin(heading) * front_axis;
		return new Vector2D(x, y);
	}
	
	/**
	 * Converts front axis into center.
	 * @param pos
	 * @param heading
	 * @return
	 */
	public Vector2D frontaxis2center(Vector2D pos, double heading){
		double x = pos.getX() - Math.cos(heading) * front_axis;
		double y = pos.getY() - Math.sin(heading) * front_axis;
		return new Vector2D(x, y);
	}

	/**
	 * Converts rear axis to center.
	 * @param pos
	 * @param heading
	 * @return
	 */
	public Vector2D rearaxis2center(Vector2D pos, double heading){
		double x = pos.getX() - Math.cos(heading) * rear_axis;
		double y = pos.getY() - Math.sin(heading) * rear_axis;
		return new Vector2D(x, y);
	}

	public double getWheelBaseLength() {
		return rear_axis + front_axis;
	}

	/**
	 * Get the shape of the vehicle.
	 * 
	 * @return
	 */
	public Shape getShape(Vector2D pos, double heading) {
		// TODO: hur?
		return null;
	}
}
