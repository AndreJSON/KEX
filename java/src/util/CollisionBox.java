package util;

import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import org.apache.commons.math3.util.FastMath;

import sim.Simulation;

public class CollisionBox implements Collidable {

    private final double xs[];
    private final double ys[];
    private Rectangle2D.Double boundingBox;
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
        if (npoints <= 2) {
            throw new IllegalArgumentException("Too small collision box.");
        }
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
        double sinTheta = FastMath.sin(theta);
        double cosTheta = FastMath.cos(theta);
        CollisionBox newBox = new CollisionBox(npoints);
        for (int i = 0; i < npoints; i++) {
            newBox.xs[i] = xs[i] * cosTheta - ys[i] * sinTheta + dx;
            newBox.ys[i] = xs[i] * sinTheta + ys[i] * cosTheta + dy;
        }
        return newBox;
    }

    public CollisionBox scale(double s) {
        CollisionBox newBox = new CollisionBox(npoints);
        for (int i = 0; i < npoints; i++) {
            newBox.xs[i] = xs[i] * s;
            newBox.ys[i] = ys[i] * s;
        }
        return newBox;
    }

    public CollisionBox scaleX(double s) {
        CollisionBox newBox = new CollisionBox(npoints);
        for (int i = 0; i < npoints; i++) {
            newBox.xs[i] = xs[i] * s;
            newBox.ys[i] = ys[i];
        }
        return newBox;
    }

    public CollisionBox translate(double dx, double dy) {
        CollisionBox newBox = new CollisionBox(npoints);
        for (int i = 0; i < npoints; i++) {
            newBox.xs[i] = xs[i] + dx;
            newBox.ys[i] = ys[i] + dy;
        }
        return newBox;
    }

    public CollisionBox scaleY(double s) {
        CollisionBox newBox = new CollisionBox(npoints);
        for (int i = 0; i < npoints; i++) {
            newBox.xs[i] = xs[i];
            newBox.ys[i] = ys[i] * s;
        }
        return newBox;
    }

    @Override
    public Rectangle2D getBounds() {
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
            boundingBox = new Rectangle2D.Double(minX, minY, width, height);
        }
        return boundingBox;
    }

    public boolean collide(Line2D line) {
        int cp;
        for (int i = 0; i < npoints; i++) {
            cp = (i + 1) % npoints;
            if (line.intersectsLine(xs[i], ys[i], xs[cp], ys[cp])) {
                return true;
            }
        }
        return false;
    }

    public boolean collide(double x1, double y1, double x2, double y2) {
        int cp;
        for (int i = 0; i < npoints; i++) {
            cp = (i + 1) % npoints;
            if (Line2D.linesIntersect(x1, y1, x2, y2, xs[i], ys[i], xs[cp],
                    ys[cp])) {
                return true;
            }
        }
        return false;
    }

    public static boolean collide(CollisionBox c1, CollisionBox c2) {
        int c1p;
        int c2p;
        for (int i = 0; i < c1.npoints; i++) {
            for (int j = 0; j < c2.npoints; j++) {
                c1p = (i + 1) % c1.npoints;
                c2p = (j + 1) % c2.npoints;
                if (Line2D.linesIntersect(c1.xs[i], c1.ys[i], c1.xs[c1p],
                        c1.ys[c1p], c2.xs[j], c2.ys[j],
                        c2.xs[c2p],
                        c2.ys[c2p])) {
                    return true;
                }
            }
        }
        return false;
    }

    public void draw(Graphics2D g2d) {
        int c1p;
        for (int i = 0; i < npoints; i++) {
            c1p = (i + 1) % npoints;
            g2d.drawLine((int) (xs[i] * Simulation.SCALE), (int) (ys[i]
                    * Simulation.SCALE),
                    (int) (xs[c1p] * Simulation.SCALE), (int) (ys[c1p]
                    * Simulation.SCALE));
        }
    }

    @Override
    public CollisionBox getCollisionBox() {
        return this;
    }

}
