package util;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import sim.Simulation;

/**
 * Quadtree for objects that implements Collidable.
 *
 * @author henrik
 *
 * @param <T>
 */
public class QuadTree<T extends Collidable> {
    private static Random rand = new Random();
    private final long id;
    /**
     * Max number of quad tree levels
     */
    private static final int MAX_LEVEL = 5;
    /**
     * Max number of objects in one quadtree before the tree splits, (assuming
     * that the tree does not reach the max level.
     */
    private static final int MAX_OBJECTS = 4;

    /**
     * The area this quad tree covers.
     */
    private final Rectangle2D area;
    /**
     * Holds all items in this collision tree.
     */
    private final List<T> items;
    /**
     * The level of this tree.
     */
    private final int level;
    /**
     * The subtrees of the quadtree
     */
    private final QuadTree<T>[] nodes;

    /**
     * The y value that marks the horizontal midline of this quadtree.
     */
    private final double yMidline;

    /**
     * The x value that marks the vertical midline of this quadtree.
     */
    private final double xMidline;

    /**
     * Create a quadtree for the specified area.
     *
     * @param area
     */
    public QuadTree(final Rectangle2D area) {
        this(0, area);
    }

    @SuppressWarnings("unchecked")
    private QuadTree(final int level, final Rectangle2D area) {
        this.area = area;
        this.level = level;
        this.items = new ArrayList<>();
        nodes = new QuadTree[4];
        yMidline = area.getY() + area.getHeight() / 2;
        xMidline = area.getX() + area.getWidth() / 2;
        id = rand.nextLong();
    }

    /**
     * Clear the quad tree of all objects.
     */
    public void clear() {
        items.clear();

        if (nodes[0] != null) {
            for (int i = 0; i < 4; i++) {
                nodes[i].clear();
                nodes[i] = null;
            }
        }

    }

    /**
     * Insert an object to the quad tree.
     *
     * @param object
     */
    public void insert(final T object) {
        if (nodes[0] != null) {
            final int index = getIndex(object.getBounds());

            if (index != -1) {
                nodes[index].insert(object);

                return;
            }
        }

        items.add(object);
        //assert area.contains(object.getBounds());
        if (items.size() > MAX_OBJECTS && level < MAX_LEVEL) {
            if (nodes[0] == null) {
                split();
            }

            final Iterator<T> iterator = items.iterator();
            while (iterator.hasNext()) {
                final T item = iterator.next();
                final int index = getIndex(item.getBounds());
                if (index != -1) {
                    nodes[index].insert(item);
                    iterator.remove();
                }
            }
        }
    }

    /**
     * Return all objects that could collide with the given object
     *
     * @param returnObjects
     * @param shape
     * @return
     */
    public long retrieve(final List<T> returnObjects,
            final Rectangle2D shape) {
        final int index = getIndex(shape);
        long id;
        if (index != -1 && nodes[0] != null) {
            id = nodes[index].retrieve(returnObjects, shape);
        } else {
            id = this.id;
        }
        returnObjects.addAll(items);
        return id;
    }

    /**
     * Return all objects that could collide with the given object
     *
     * @param returnObjects
     * @param shape
     * @return
     */
    public long retrieve(final List<T> returnObjects,
            final T shape) {
        return retrieve(returnObjects, shape.getBounds());
    }

    private void split() {
        final double subWidth = area.getWidth() / 2;
        final double subHeight = area.getHeight() / 2;
        final double xPos = area.getX();
        final double yPos = area.getY();

        nodes[0] = new QuadTree<>(level + 1, new Rectangle2D.Double(xPos, yPos, subWidth, subHeight));
        nodes[1] = new QuadTree<>(level + 1, new Rectangle2D.Double(xPos + subWidth, yPos, subWidth, subHeight));
        nodes[2] = new QuadTree<>(level + 1, new Rectangle2D.Double(xPos, yPos + subHeight, subWidth, subHeight));
        nodes[3] = new QuadTree<>(level + 1, new Rectangle2D.Double(xPos + subWidth, yPos + subHeight, subWidth, subHeight));
    }

    private int getIndex(final Rectangle2D pRect) {
        
        boolean upper = pRect.getMaxY()< yMidline;
        boolean lower = pRect.getMinY() > yMidline;
        // Object can completely fit within the left quadrants
        if (pRect.getMaxX() < xMidline) {
            if (upper) {
                return 0;
            } else if (lower) {
                return 2;
            }
        } // Object can completely fit within the right quadrants
        else if (pRect.getMinX() > xMidline) {
            if (upper) {
                return 1;
            } else if (lower) {
                return 3;
            }
        }
        return -1;
    }

    public void draw(Graphics2D g2d) {
        if (nodes[0] != null) {
            for (QuadTree<T> tree : nodes) {
                tree.draw(g2d);
            }
        } else {
            g2d.draw(Simulation.SCALER.createTransformedShape(area));
        }
        for (T item : items) {
            item.getCollisionBox().draw(g2d);
        }
    }
}
