package math;

import java.awt.geom.Point2D;


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
     * The unit vector.
     *
     * @return
     */
    public Vector2D unit() {
        if (this.norm() == 0.0) {
            throw new IllegalArgumentException("Zero-vector has no direction");
        }
        return this.mult(1.0 / this.norm());
    }

    /**
     * Angle to the x-axis.
     *
     * @return
     */
    public double theta() {
        return Math.atan2(y, x);
    }

    /**
     *
     * @param point
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
        return Math.sqrt(this.dot(this));
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
     * @param point
     * @return
     */
    public Vector2D plus(Point2D point) {
        return new Vector2D(x + point.getX(), y + point.getY());
    }

    /**
     *
     * @param point
     * @return
     */
    public Vector2D minus(Vector2D point) {
        return new Vector2D(x - point.x, y - point.y);
    }

    /**
     *
     * @param theta
     * @return
     */
    public Vector2D rotate(double theta) {
        double sin = Math.sin(theta);
        double cos = Math.cos(theta);
        return new Vector2D(x * cos - y * sin, x * sin + y * cos);
    }

    @Override
    public String toString() {
        return "[" + x + ", " + y + "]";
    }
}
