package util;

import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public class CollisionBox {
	private final Line2D.Double[] lines;
	private Rectangle boundingBox;
	private int linesAdded;
	private Point2D position;
	private final Point2D start;

	public CollisionBox(int numOfLines, double x, double y) {
		this(numOfLines, new Point2D.Double(x, y));
	}

	public CollisionBox(Rectangle2D rect) {
		this(4, rect.getMinX(), rect.getMinY());
		lineTo(rect.getMaxX(), rect.getMinY());
		lineTo(rect.getMaxX(), rect.getMaxY());
		lineTo(rect.getMinX(), rect.getMaxY());
		close();
		boundingBox = rect.getBounds();
	}

	public CollisionBox(int numOfLines, Point2D start) {
		if (numOfLines <= 0)
			throw new RuntimeException("#lines = " + numOfLines + " <= 0");
		lines = new Line2D.Double[numOfLines];
		linesAdded = 0;
		this.start = start;
		position = start;
	}

	public void lineTo(double x, double y) {
		lineTo(new Point2D.Double(x, y));
	}

	public void lineTo(Point2D point) {
		if (linesAdded == lines.length)
			throw new RuntimeException("Too many lines!");
		lines[linesAdded] = new Line2D.Double(position, point);
		position = point;
		linesAdded++;
	}

	public void close() {
		if (linesAdded != lines.length - 1)
			throw new RuntimeException("Not done yet!");
		lineTo(start);
	}

	public CollisionBox transform(AffineTransform aF) {
		checkCompletion();
		Point2D start = aF.transform(this.start, null);
		CollisionBox newBox = new CollisionBox(lines.length, start);
		for (Line2D.Double line : lines) {
			newBox.lineTo(aF.transform(line.getP2(), null));
		}
		return newBox;
	}

	public Rectangle getBounds() {
		checkCompletion();
		if (boundingBox == null) {
			boundingBox = lines[0].getBounds();
			for (int i = 1; i < lines.length; i++) {
				boundingBox.add(lines[i].getBounds());
			}
		}
		return boundingBox;
	}

	public static boolean collide(CollisionBox c1, CollisionBox c2) {
		for (Line2D.Double line1 : c1.lines) {
			for (Line2D.Double line2 : c2.lines) {
				if (line1.intersectsLine(line2)) {
					return true;
				}
			}
		}
		return false;
	}

	private void checkCompletion() {
		if (linesAdded < lines.length)
			throw new RuntimeException(
					"CollisionBox not completed: lines added = " + linesAdded
							+ " < " + lines.length);
	}

	public Point2D getPosition() {
		return position;
	}
}
