package math;

import java.awt.geom.Point2D;

import org.apache.commons.math3.util.FastMath;

@SuppressWarnings("serial")
public class Vector2D extends Point2D.Double {

	/**
	 * 
	 * @param p
	 */
	public Vector2D(Point2D p) {
		super(p.getX(), p.getY());
	}

	/**
	 * 
	 */
	public Vector2D() {
	}

	/**
	 * 
	 * @param x
	 * @param y
	 */
	public Vector2D(double x, double y) {
		super(x, y);
	}

	/**
	 * 
	 * @param x
	 */
	public void setX(double x) {
		this.x = x;
	}

	/**
	 * 
	 * @param y
	 */
	public void setY(double y) {
		this.y = y;
	}

	/**
	 * 
	 * @param dx
	 * @param dy
	 */
	public void move(double dx, double dy) {
		x += dx;
		y += dy;
	}

	/**
	 * The unit vector.
	 * 
	 * @return
	 */
	public Vector2D unit() {
		if (this.norm() == 0.0)
			throw new RuntimeException("Zero-vector has no direction");
		return this.mult(1.0 / this.norm());
	}

	/**
	 * Angle to the x-axis.
	 * 
	 * @return
	 */
	public double theta() {
		return FastMath.atan2(y, x);
	}

	/**
	 * 
	 * @param p
	 * @return
	 */
	public double dot(Vector2D point) {
		return x * point.x + y * point.y;
	}

	/**
	 * 
	 * @return
	 */
	public double norm() {
		return FastMath.sqrt(this.dot(this));
	}

	/**
	 * 
	 * @return
	 */
	public double normSq() {
		return this.dot(this);
	}

	/**
	 * 
	 * @param factor
	 * @return
	 */
	public Vector2D mult(double factor) {
		return new Vector2D(x * factor, y * factor);
	}

	/**
	 * 
	 * @param p
	 * @return
	 */
	public Vector2D plus(Point2D point) {
		return new Vector2D(x + point.getX(), y + point.getY());
	}

	/**
	 * 
	 * @param p
	 * @return
	 */
	public Vector2D minus(Vector2D point) {
		return new Vector2D(x - point.x, y - point.y);
	}

	/**
	 * 
	 * @param p
	 * @return
	 */
	public Vector2D rotate(double theta) {
		double sin = FastMath.sin(theta);
		double cos = FastMath.cos(theta);
		return new Vector2D(x * cos - y * sin, x * sin + y * cos);
	}

	@Override
	public String toString() {
		return "[" + x + ", " + y + "]";
	}
}
