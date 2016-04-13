package util;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import sim.Simulation;

public class CollisionBox {
	private final double xs[];
	private final double ys[];
	private Rectangle boundingBox;
	private final int npoints;
	private int points;

	public CollisionBox(double x, double y, int npoints) {
		this(npoints);
		set(0, x, y);
		points++;
	}

	public CollisionBox(Rectangle2D rect) {
		this(4);
		set(0, rect.getMinX(), rect.getMinY());
		set(1, rect.getMaxX(), rect.getMinY());
		set(2, rect.getMaxX(), rect.getMaxY());
		set(3, rect.getMinX(), rect.getMaxY());
	}

	private CollisionBox(int npoints) {
		if (npoints <= 2)
			throw new RuntimeException("Too small collision box.");
		this.npoints = npoints;
		xs = new double[npoints];
		ys = new double[npoints];
	}

	private void set(int i, double x, double y) {
		xs[i] = x;
		ys[i] = y;
	}

	public void lineTo(double x, double y) {
		points++;
		set(points, x, y);
	}

	public void lineTo(Point2D point) {
		lineTo(point.getX(), point.getY());
	}

	public CollisionBox transform(double dx, double dy, double theta) {
		double sinTheta = Math.sin(theta);
		double cosTheta = Math.cos(theta);
		CollisionBox newBox = new CollisionBox(npoints);
		for (int i = 0; i < npoints; i++) {
			newBox.xs[i] = xs[i] * cosTheta - ys[i] * sinTheta + dx;
			newBox.ys[i] = xs[i] * sinTheta + ys[i] * cosTheta + dy;
		}
		return newBox;
	}

	public Rectangle getBounds() {
		if (boundingBox == null) {
			double minX = xs[0];
			double maxX = xs[0];
			double minY = ys[0];
			double maxY = ys[0];
			for (int i = 1; i < npoints; i++) {
				if (xs[i] < minX) {
					minX = xs[i];

				} else if (xs[i] > maxX) {
					maxX = xs[i];
				}
				if (ys[i] < minY) {
					minY = ys[i];

				} else if (ys[i] > maxY) {
					maxY = ys[i];
				}
			}
			double width = maxX - minX;
			double height = maxY - minY;
			boundingBox = new Rectangle((int) minX, (int) maxX, (int) width,
					(int) height);
		}
		return boundingBox;
	}

	public static boolean collide(CollisionBox c1, CollisionBox c2) {
		int c1p;
		int c2p;
		for (int i = 0; i < c1.npoints; i++) {
			for (int j = 0; j < c2.npoints; j++) {
				c1p = (i + 1) % c1.npoints;
				c2p = (j + 1) % c2.npoints;
				if (Line2D.linesIntersect(c1.xs[i], c1.ys[i], c1.xs[c1p],
						c1.ys[c1p], c2.xs[j], c2.ys[j], c2.xs[c2p], c2.ys[c2p])) {
					return true;
				}
			}
		}
		return false;
	}

	public void draw(Graphics2D g2d) {
		int c1p;
		g2d.setColor(Color.pink);
		for (int i = 0; i < npoints; i++) {
			c1p = (i + 1) % npoints;
			g2d.drawLine((int) (xs[i] * Simulation.SCALE),
					(int) (ys[i] * Simulation.SCALE),
					(int) (xs[c1p] * Simulation.SCALE),
					(int) (ys[c1p] * Simulation.SCALE));
		}
	}

}
