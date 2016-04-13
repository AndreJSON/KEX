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

public class SADSchedule {
	private static final Vector2D INTERSECTION_POS = new Vector2D(Intersection.straight + Intersection.turn, Intersection.straight + Intersection.turn); //Upper left corner of the intersection.
	private static final double INTERSECTION_SIZE = 3 * Intersection.width + 2 * Intersection.buffer;
	private static final int TILE_AMOUNT = 18; //How many tiles the intersection should have in each dimenstion.
	private static final double TILE_SIZE = (INTERSECTION_SIZE / (double)TILE_AMOUNT);
	private static final CollisionBox[][] GRID_BOXES = initGridBoxes(); //[X][Y] 0 < X,Y < DIM
	private static final int[][] SEG_IDS = {//[FROM][TO]
		{-1, 23, 1, 0},
		{20, -1, 26, 19},
		{9, 10, -1, 21},
		{15, 25, 24, -1}
	};
	private static ArrayList<LinkedList<Pair>>[] SPACE4_OCCUPATION = initOccupation(); //[ID].get(POS) where pos is how the amount of AbstractTrack.POINT_STEPs into the track
	private static LinkedList<Integer>[][] space4Blocks; //[FROM][TO] dessa ska eventuellt inte finnas
	private static LinkedList<Integer>[][] space9Blocks; //[FROM][TO]
	private Grid[] grids;
	private int gridIndex;

	public SADSchedule() {
		grids = new Grid[TILE_AMOUNT];
	}

	/**
	 * Returns the amount of time until the car will get to the intersection given its curent velocity.
	 */
	private double timeToI(Car car) {
		return 0; //TODO: Actually calculate the time.
	}

	/**
	 * Returns the fastest time the car could get to the intersection given that it accelerates to road max speed as fast as it can.
	 */
	private double fastestTimeToI(Car car) {
		//TODO: Should take ACCELERATION_COEFFIECIENT into account when calculating.
		return 0; //TODO: Actually calculate the time.
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

	private static CollisionBox[][] initGridBoxes() {
		CollisionBox[][] tmp = new CollisionBox[TILE_AMOUNT][TILE_AMOUNT];
		for(int i = 0; i < TILE_AMOUNT; i++) {
			for(int j = 0; j < TILE_AMOUNT; j++) {
				tmp[i][j] = new CollisionBox(new Rectangle2D.Double(i * TILE_SIZE, j * TILE_SIZE, TILE_SIZE, TILE_SIZE));
			}
		}
		return tmp;
	}

	private static ArrayList<LinkedList<Pair>>[] initOccupation(){
		ArrayList<LinkedList<Pair>>[] tmp = new ArrayList[Intersection.numberOfSegments()];
		for(int from = 0; from <= 3; from++) {
			for(int to = 0; to <= 3; to++) {
				if(from == to) {
					continue;
				}
				int id = SEG_IDS[from][to];
				tmp[id] = new ArrayList<LinkedList<Pair>>(200);
				CollisionBox[] boxes = Intersection.getByID(id).getCollisionBoxes(CarModelDb.getByName("Mazda3"));
				for(int i = 0; i < boxes.length; i++) {
					tmp[id].add(i, getOccupationTiles(boxes[i]));
				}
			}
		}
		return SPACE4_OCCUPATION;
	}

	private static LinkedList<Pair> getOccupationTiles(CollisionBox b) {
		LinkedList<Pair> list = new LinkedList<Pair>();
		for(int i = 0; i < TILE_AMOUNT; i++) {
			for(int j = 0; j < TILE_AMOUNT; j++) {
				if(b.collide(GRID_BOXES[i][j])) {
					list.add(new Pair(i,j));
				}
			}
		}
		return list;
	}
}