package util;

import java.awt.Rectangle;
import java.awt.Shape;
import java.util.ArrayList;

public class QuadTree {
	private static int MAX_LEVEL = 5;
	private static int MAX_OBJECTS = 10;

	private final Rectangle rect;
	private final ArrayList<Shape> shapes;
	private final int level;
	private final QuadTree[] nodes;

	public QuadTree(int level, Rectangle rect) {
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

	private void split() {
		int subWidth = (int) rect.width / 2;
		int subHeight = (int) rect.height / 2;
		int x = (int) rect.x;
		int y = (int) rect.y;
		nodes[0] = new QuadTree(level + 1, new Rectangle(x + subWidth, y
				+ subHeight, subWidth, subHeight));
		nodes[1] = new QuadTree(level + 1, new Rectangle(x, y, subWidth,
				subHeight));
		nodes[2] = new QuadTree(level + 1, new Rectangle(x, y + subHeight,
				subWidth, subHeight));
		nodes[3] = new QuadTree(level + 1, new Rectangle(x + subWidth, y,
				subWidth, subHeight));
	}

	private int getIndex(Rectangle pRect) {
		int index = -1;
		double verticalMidpoint = rect.getX() + (rect.getWidth() / 2);
		double horizontalMidpoint = rect.getY() + (rect.getHeight() / 2);

		// Object can completely fit within the top quadrants
		boolean topQuadrant = (pRect.getY() < horizontalMidpoint && pRect
				.getY() + pRect.getHeight() < horizontalMidpoint);
		// Object can completely fit within the bottom quadrants
		boolean bottomQuadrant = (pRect.getY() > horizontalMidpoint);

		if (pRect.getX() < verticalMidpoint
				&& pRect.getX() + pRect.getWidth() < verticalMidpoint) {
			// Object can completely fit within the left quadrants
			if (topQuadrant) {
				index = 1;
			} else if (bottomQuadrant) {
				index = 2;
			}
		} else if (pRect.getX() > verticalMidpoint) {
			// Object can completely fit within the right quadrants
			if (topQuadrant) {
				index = 0;
			} else if (bottomQuadrant) {
				index = 3;
			}
		}

		return index;
	}

	public void insert(Shape shape) {
		Rectangle rect = shape.getBounds();
		if (nodes[0] != null) {
		     int index = getIndex(rect);
		 
		     if (index != -1) {
		       nodes[index].insert(rect);
		 
		       return;
		     }
		   }
		 
		   shapes.add(shape);
		 
		   if (shapes.size() > MAX_OBJECTS && level < MAX_LEVEL) {
		      if (nodes[0] == null) { 
		         split(); 
		      }
		 
		     int i = 0;
		     while (i < shapes.size()) {
		       int index = getIndex(shapes.get(i).getBounds());
		       if (index != -1) {
		         nodes[index].insert(shapes.remove(i));
		       }
		       else {
		         i++;
		       }
		     }
		   }

	}

}
