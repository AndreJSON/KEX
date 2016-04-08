package sim.vehicle;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.text.DecimalFormat;

import math.Vector2D;

import sim.Drawable;
import sim.Simulation;
import sim.map.track.TrackPosition;

public class Car implements Drawable {
	private static long trackId = 0;

	private long id;
	private final VehicleSpec specs;
	private TrackPosition position;
	private double velocity;
	private double realVel;

	public Car(VehicleSpec specs) {
		this.specs = specs;
		id = ++trackId;
	}

	@Override
	public String toString() {
		return "Car" + id + "[" + specs.getName() + "]";
	}
	
	/**
	 * 
	 * @param delta
	 */
	public void tick(double delta) {
		Vector2D pb = position.getPoint();
		if (position != null && position.remaining() > 0){
			position.move(delta * velocity);
		}
		Vector2D pf = position.getPoint();
		realVel = pb.distance(pf)/delta;
	}

	public Vector2D getPosition() {
		return position.getPoint();
	}

	public double remainingOnTrack() {
		return position.remaining();
	}

	public double getHeading() {
		return position.getHeading();
	}

	public double getVelocity() {
		return velocity;
	}

	public void setVelocity(double velocity) {
		this.velocity = velocity;
	}

	public VehicleSpec getSpecs() {
		return specs;
	}

	public void setTrackPosition(TrackPosition tPosition) {
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
		aF.rotate(getHeading());
		aF.scale(Simulation.SCALE, Simulation.SCALE);
		
		Shape shape = aF.createTransformedShape(specs.getShape());
		g2d.fill(shape);

		if (!Simulation.DEBUG)
			return;
		g2d.setColor(Color.black);
		g2d.fillOval((int) p.x - 1, (int) p.y - 1, 3, 3);
		DecimalFormat df = new DecimalFormat("0.0"); 
		g2d.drawString(this.toString() + " " + df.format(realVel*3.6) + " kph", (int) p.x + 2, (int) p.y - 2);

	}

	@Override
	public int hashCode() {
		return (int) id;
	}
}
