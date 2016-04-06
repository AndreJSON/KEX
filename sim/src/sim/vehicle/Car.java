package sim.vehicle;

import math.Vector2D;

public class Car {
	private int id;
	private Vector2D center_point;
	private VehicleSpec specs;
	private double steering_direction, heading, velocity;

	@Override
	public String toString() {
		return "Car" + id + "{" + specs.getName() + ", " + center_point + "}";
	}

	/**
	 * 
	 * @param delta
	 */
	public void tick(double delta) { // 'delta' has to be small for accuracy.
		double dx = velocity * Math.cos(heading) * delta;
		double dy = velocity * Math.sin(heading) * delta;
		Vector2D front_axis = specs.center2frontaxis(center_point, heading);
		front_axis.move(dx, dy);
		center_point = specs.frontaxis2center(front_axis, heading);
		heading += velocity * Math.tan(steering_direction)
				/ specs.getWheelBaseLength() * delta;
	}

}
