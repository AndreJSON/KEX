package util;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

public class QuadTree<T extends Collidable> {
	private static int MAX_LEVEL = 5;
	private static int MAX_OBJECTS = 10;

	private final Rectangle2D rect;
	private final ArrayList<T> objects;
	private final int level;
	private final QuadTree<T>[] nodes;

	private final double verticalMidpoint;
	private final double horizontalMidpoint;

	public QuadTree(Rectangle2D area) {
		this(0, area);
	}

	@SuppressWarnings("unchecked")
	private QuadTree(int level, Rectangle2D rect) {
		this.rect = rect;
		this.level = level;
		this.objects = new ArrayList<>();
		nodes = new QuadTree[4];
		verticalMidpoint = rect.getX() + (rect.getWidth() / 2);
		horizontalMidpoint = rect.getY() + (rect.getHeight() / 2);
	}

	public void clear() {
		objects.clear();
		for (int i = 0; i < nodes.length; i++) {
			if (nodes[i] != null) {
				nodes[i].clear();
			}
		}
	}

	public void insert(T object) {
		Rectangle2D rect = object.getBounds();
		if (nodes[0] != null) {
			int index = getIndex(rect);

			if (index != -1) {
				getNode(index).insert(object);
				return;
			}
		}

		objects.add(object);

		if (objects.size() > MAX_OBJECTS && level < MAX_LEVEL) {
			if (nodes[0] == null) {
				split();
			}

			int i = 0;
			while (i < objects.size()) {
				int index = getIndex(objects.get(i).getBounds());
				if (index != -1) {
					getNode(index).insert(objects.remove(i));
				} else {
					i++;
				}
			}
		}
	}

	private QuadTree<T> getNode(int index) {
		return nodes[index];
	}

	/*
	 * Return all objects that could collide with the given object
	 */
	public ArrayList<T> retrieve(ArrayList<T> returnObjects, Rectangle2D shape) {
		int index = getIndex(shape.getBounds());
		if (index != -1 && nodes[0] != null) {
			getNode(index).retrieve(returnObjects, shape);
		}
		returnObjects.addAll(objects);
		return returnObjects;
	}

	public ArrayList<T> retrieve(ArrayList<T> returnObjects, Point2D point) {
		int index = getIndex(point);
		if (index != -1 && nodes[0] != null) {
			getNode(index).retrieve(returnObjects, point);
		}
		returnObjects.addAll(objects);
		return returnObjects;
	}

	private void split() {
		double subWidth = rect.getWidth() / 2;
		double subHeight = rect.getHeight() / 2;
		double x = rect.getX();
		double y = rect.getY();
		nodes[0] = new QuadTree<T>(level + 1, new Rectangle2D.Double(x, y,
				subWidth, subHeight));
		nodes[1] = new QuadTree<T>(level + 1, new Rectangle2D.Double(x
				+ subWidth, y, subWidth, subHeight));
		nodes[2] = new QuadTree<T>(level + 1, new Rectangle2D.Double(x, y
				+ subHeight, subWidth, subHeight));
		nodes[3] = new QuadTree<T>(level + 1, new Rectangle2D.Double(x
				+ subWidth, y + subHeight, subWidth, subHeight));
	}

	private int getIndex(Point2D point) {
		int index = -1;

		// Object can completely fit within the top quadrants
		boolean topQuadrant = point.getY() < horizontalMidpoint;
		// Object can completely fit within the bottom quadrants
		boolean bottomQuadrant = point.getY() > horizontalMidpoint;
		// assert topQuadrant != bottomQuadrant;

		boolean leftQuadrant = point.getX() < verticalMidpoint;
		boolean rightQuadrant = point.getX() > verticalMidpoint;
		// assert leftQuadrant != rightQuadrant;

		if (leftQuadrant) {
			// Object can completely fit within the left quadrants
			if (topQuadrant) {
				index = 0;
			} else if (bottomQuadrant) {
				index = 2;
			}
		} else if (rightQuadrant) {
			// Object can completely fit within the right quadrants
			if (topQuadrant) {
				index = 1;
			} else if (bottomQuadrant) {
				index = 3;
			}
		}

		return index;
	}

	private int getIndex(Rectangle2D pRect) {
		int index = -1;
		double verticalMidpoint = rect.getX() + (rect.getWidth() / 2);
		double horizontalMidpoint = rect.getY() + (rect.getHeight() / 2);

		// Object can completely fit within the top quadrants
		boolean topQuadrant = pRect.getY() + pRect.getHeight() < horizontalMidpoint;
		// Object can completely fit within the bottom quadrants
		boolean bottomQuadrant = pRect.getY() > horizontalMidpoint;
		// assert topQuadrant != bottomQuadrant;

		boolean leftQuadrant = pRect.getX() + pRect.getWidth() < verticalMidpoint;
		boolean rightQuadrant = pRect.getX() > verticalMidpoint;
		// assert leftQuadrant != rightQuadrant;

		if (leftQuadrant) {
			// Object can completely fit within the left quadrants
			if (topQuadrant) {
				index = 0;
			} else if (bottomQuadrant) {
				index = 2;
			}
		} else if (rightQuadrant) {
			// Object can completely fit within the right quadrants
			if (topQuadrant) {
				index = 1;
			} else if (bottomQuadrant) {
				index = 3;
			}
		}

		return index;
	}

}
