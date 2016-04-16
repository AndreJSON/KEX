package util;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import sim.Simulation;

public class QuadTree {
	private static int MAX_LEVEL = 5;
	private static int MAX_OBJECTS = 8;

	private final Rectangle2D rect;
	private final ArrayList<CollisionBox> shapes;
	private final int level;
	private final QuadTree[] nodes;

	public QuadTree(Rectangle2D area) {
		this(0, area);
	}

	private QuadTree(int level, Rectangle2D rect) {
		this.rect = rect;
		this.level = level;
		this.shapes = new ArrayList<>();
		nodes = new QuadTree[4];
	}

	public void clear() {
		shapes.clear();
		for (int i = 0; i < nodes.length; i++) {
			if (nodes[i] != null) {
				nodes[i].clear();
				nodes[i] = null;
			}
		}
	}

	public void insert(CollisionBox collisionBox) {
		Rectangle2D rect = collisionBox.getBounds();
		if (nodes[0] != null) {
			int index = getIndex(rect);

			if (index != -1) {
				nodes[index].insert(collisionBox);

				return;
			}
		}

		shapes.add(collisionBox);

		if (shapes.size() > MAX_OBJECTS && level < MAX_LEVEL) {
			if (nodes[0] == null) {
				split();
			}

			int i = 0;
			while (i < shapes.size()) {
				int index = getIndex(shapes.get(i).getBounds());
				if (index != -1) {
					nodes[index].insert(shapes.remove(i));
				} else {
					i++;
				}
			}
		}
	}

	/*
	 * Return all objects that could collide with the given object
	 */
	public ArrayList<CollisionBox> retrieve(
			ArrayList<CollisionBox> returnObjects, CollisionBox shape) {
		int index = getIndex(shape.getBounds());
		if (index != -1 && nodes[0] != null) {
			nodes[index].retrieve(returnObjects, shape);
		}
		returnObjects.addAll(shapes);
		return returnObjects;
	}

	private void split() {
		double subWidth = rect.getWidth() / 2;
		double subHeight = rect.getHeight() / 2;
		double x = rect.getX();
		double y = rect.getY();
		nodes[0] = new QuadTree(level + 1, new Rectangle2D.Double(x, y,
				subWidth, subHeight));
		nodes[1] = new QuadTree(level + 1, new Rectangle2D.Double(x + subWidth,
				y, subWidth, subHeight));
		nodes[2] = new QuadTree(level + 1, new Rectangle2D.Double(x, y
				+ subHeight, subWidth, subHeight));
		nodes[3] = new QuadTree(level + 1, new Rectangle2D.Double(x + subWidth,
				y + subHeight, subWidth, subHeight));
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
		//assert leftQuadrant != rightQuadrant;

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

	public void draw(Graphics2D g2d) {

		for (CollisionBox cB : shapes) {
			g2d.draw(Simulation.SCALER.createTransformedShape(cB.getBounds()));
		}
		if (nodes[0] == null) {
			g2d.setColor(Color.white);
			g2d.draw(Simulation.SCALER.createTransformedShape(rect));
		} else {
			for (QuadTree tree : nodes) {
				tree.draw(g2d);
			}
		}

	}
}
