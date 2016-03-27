package sim.vehicle;

import java.awt.geom.Point2D;

public class VehicleSpec {
	private String name;
	private double length, width;

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
	 * Get the rear point of the vehicle.
	 * 
	 * @return
	 */
	public Point2D getRearPoint(Point2D pos, double heading) {
		// EJ KONTROLLERAD
		double rearX = pos.getX() - Math.cos(heading) * length / 2;
		double rearY = pos.getY() - Math.sin(heading) * length / 2;
		return new Point2D.Double(rearX, rearY);
	}

	/**
	 * Get the front point of the vehicle.
	 * 
	 * @return
	 */
	public Point2D getFrontPoint(Point2D pos, double heading) {
		// EJ KONTROLLERAD
		double frontX = pos.getX() + Math.cos(heading) * length / 2;
		double frontY = pos.getY() + Math.sin(heading) * length / 2;
		return new Point2D.Double(frontX, frontY);
	}

	/**
	 * Get the shape of the vehicle.
	 * 
	 * @return
	 */
	public Point2D getShape(Point2D pos, double heading) {
		// TODO: hur?
		return null;
	}
}
