package tscs;

import java.awt.geom.Rectangle2D;
import java.util.LinkedList;
import java.util.ArrayList;

import car.Car;
import car.CarModelDb;
import map.intersection.Intersection;
import math.Vector2D;
import math.Pair;
import util.CollisionBox;
import sim.Const;

public class SADSchedule {
	//How many tiles the intersection should have in each dimenstion.
	private static final int TILE_AMOUNT = 18; 
	// Distance from map edge to the intersection.
	private static final double INTERSECTION_DIST = Intersection.straight + Intersection.turn; 
	private static final double INTERSECTION_SIZE = 3 * Intersection.width + 2 * Intersection.buffer;
	//[FROM][TO]
	public static final int[][] SEG_IDS = initSegID();
	private static final double TILE_SIZE_FINE = (INTERSECTION_SIZE / (double)TILE_AMOUNT);
	private static final double TILE_SIZE_9 = (INTERSECTION_SIZE / (double)3);
	private static final double TILE_SIZE_4 = (INTERSECTION_SIZE / (double)2);
	//[X][Y] 0 < X,Y < TILE_AMOUNT
	public static final CollisionBox[][] GRID_BOXES_FINE = initGridBox(TILE_AMOUNT); 
	//[X][Y] 0 < X,Y < 3
	public static final CollisionBox[][] GRID_BOXES_9 = initGridBox(3); 
	//[X][Y] 0 < X,Y < 2
	public static final CollisionBox[][] GRID_BOXES_4 = initGridBox(2); 
	//[ID].get(POS) where pos is how the amount of AbstractTrack.POINT_STEPs into the track
	private static final ArrayList<LinkedList<Pair>>[] OCCUPATION_FINE = initOccupation(TILE_AMOUNT, GRID_BOXES_FINE); 
	//[ID].get(POS) where pos is how the amount of AbstractTrack.POINT_STEPs into the track
	private static final ArrayList<LinkedList<Pair>>[] OCCUPATION_9 = initOccupation(3, GRID_BOXES_9); 
	//[ID].get(POS) where pos is how the amount of AbstractTrack.POINT_STEPs into the track
	private static final ArrayList<LinkedList<Pair>>[] OCCUPATION_4 = initOccupation(2, GRID_BOXES_4); 
	private static final Vector2D INTERSECTION_POINTS[] = initPoints();
	private Grid[] grids;
	private int gridIndex;

	public SADSchedule() {
		grids = new Grid[TILE_AMOUNT];
		for(Pair p : OCCUPATION_FINE[SEG_IDS[2][3]].get(105)) {
			System.out.println(p.first() + " " + p.second());
		}
	}

	/**
	 * Returns the cars distance to the intersection.
	 */
	public double distanceToI(Car car) {
		return distance(car.getPosition(),INTERSECTION_POINTS[car.getOrigin()]);
	}

	/**
	 * Returns the amount of time until the car will get to the intersection given its curent velocity.
	 */
	public double timeToI(Car car) {
		return distanceToI(car) / car.getSpeed();
	}

	/**
	 * Returns the fastest time the car could get to the intersection given that it accelerates to road max speed as fast as it can.
	 */
	private double fastestTimeToI(Car car) {
		double vel = car.getSpeed();
		double acc = car.getMaxAcceleration() * Const.ACC_COEF;
		double timeToMaxSpeed = (Const.SPEED_LIMIT - vel) / acc;
		if(timeToMaxSpeed * (vel + (timeToMaxSpeed * acc / 2)) > distanceToI(car)) { //The car will reach max speed before the intersection.
			return timeToMaxSpeed + (distanceToI(car) - timeToMaxSpeed * (vel + (timeToMaxSpeed * acc / 2))) / Const.SPEED_LIMIT;
		}
		else {
			return Math.sqrt(Math.pow(vel/acc, 2) + 2 * distanceToI(car) / acc) - vel / acc;
		}
	}

	private void stepIndex() {
		grids[gridIndex].wipe();
		gridIndex = (gridIndex + 1) % grids.length;
	}

	public boolean courseCheck(/*Have a decent representation of an object in space*/) {
		//TODO: Check relevant tiles in space4 and space9.
		return true; //TODO: Return calculated value instead of true.
	}

	public boolean fineCheck(/*Have a decent representation of an object in space*/) {
		//TODO: Check relevant tiles in space4 and space9.
		return true; //TODO: Return calculated value instead of true.
	}

	public LinkedList<Car> fineCheckCars(/*Have a decent representation of an object in space*/) {
		return null; //TODO: Return calculated value instead of null.
	}

	/**
	 * Books all tiles taken up by the given object, does not check for collision so be careful when booking!
	 */
	public void book(/*Have a decent representation of an object in space*/) {
		//TODO: Make booking in all 3 schemas.
	}

	private class Grid {
		public boolean[][] space4, space9;
		public Car[][] spaceFine;

		public Grid(int dim) {
			space4 = new boolean[2][2];
			space9 = new boolean[3][3];
			spaceFine = new Car[dim][dim];
		}

		/**
		 * Wipe the Grid clean.
		 */
		public void wipe() {
			for(int i = 0; i < space4.length; i++)
				for(int j = 0; j < space4.length; j++)
					space4[i][j] = false;
			for(int i = 0; i < space9.length; i++)
				for(int j = 0; j < space9.length; j++)
					space9[i][j] = false;
			for(int i = 0; i < spaceFine.length; i++)
				for(int j = 0; j < spaceFine.length; j++)
					spaceFine[i][j] = null;
		}
	}

	private double distance(Vector2D p1, Vector2D p2) {
		double d1 = Math.pow(p1.getX() - p2.getX(), 2);
		double d2 = Math.pow(p1.getY() - p2.getY(), 2);
		return Math.sqrt(d1 + d2);
	}

	private static Vector2D[] initPoints() {
		Vector2D[] tmp = new Vector2D[4];
		tmp[0] = Intersection.getWaypoint(Const.NORTH, Const.STRAIGHT);
		tmp[1] = Intersection.getWaypoint(Const.EAST, Const.STRAIGHT);
		tmp[2] = Intersection.getWaypoint(Const.SOUTH, Const.STRAIGHT);
		tmp[3] = Intersection.getWaypoint(Const.WEST, Const.STRAIGHT);
		return tmp;
	}

	private static int[][] initSegID() {
		int[][] tmp = new int[4][4];
		for(int from = 0; from <= 3; from++) {
			for(int to = 0; to <= 3; to++) {
				if(from == to) {
					continue;
				}
				tmp[from][to] = Intersection.getSquareSegment(from,to).hashCode();
			}
		}
		return tmp;
	}

	private static CollisionBox[][] initGridBox(int dim) {
		CollisionBox[][] tmp = new CollisionBox[dim][dim];
		double tileSize = INTERSECTION_SIZE / (double)dim;
		for(int i = 0; i < dim; i++) {
			for(int j = 0; j < dim; j++) {
				tmp[i][j] = new CollisionBox(new Rectangle2D.Double(INTERSECTION_DIST + i * tileSize, INTERSECTION_DIST + j * tileSize, tileSize, tileSize));
			}
		}
		return tmp;
	}

	private static ArrayList<LinkedList<Pair>>[] initOccupation(int dim, CollisionBox[][] grid){
		ArrayList<LinkedList<Pair>>[] tmp = new ArrayList[Intersection.numberOfSegments()];
		for(int from = 0; from <= 3; from++) {
			for(int to = 0; to <= 3; to++) {
				if(from == to) {
					continue;
				}				
				int id = SEG_IDS[from][to];
				tmp[id] = new ArrayList<LinkedList<Pair>>(200);
				ArrayList<CollisionBox> boxes = Intersection.getByID(id).getCollisionBoxes(CarModelDb.getByName("Mazda3"));
				for(int i = 0; i < boxes.size(); i++) {
					tmp[id].add(i, getOccupationTiles(dim, grid, boxes.get(i)));
				}
			}
		}
		return tmp;
	}

	private static LinkedList<Pair> getOccupationTiles(int dim, CollisionBox[][] grid, CollisionBox b) {
		LinkedList<Pair> list = new LinkedList<Pair>();
		for(int i = 0; i < dim; i++) {
			for(int j = 0; j < dim; j++) {
				if(CollisionBox.collide(b, grid[i][j])) {
					list.add(new Pair(i,j));
				}
			}
		}
		return list;
	}
}