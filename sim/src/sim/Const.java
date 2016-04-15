package sim;

/**
 * Class containing constant values that are shared by all classed.
 * 
 * @author henrik
 * 
 */
public class Const {
	// public static constants
	
	// Begin Intersection segment coordinates
	public static final int NORTH = 0, EAST = 1, SOUTH = 2, WEST = 3;
	public static final int STRAIGHT = 0, RIGHT = 0, LEFT = 1, EXIT = 2;
	public static final int SPLIT_STRAIGHT = 3, SPLIT_LEFT = 4;
	public static final int MAP_ENTRANCE = 5, MAP_EXIT = 6;
	// End Intersection segment coordinates

	// Factor slower comfortable breaking should compared to the maximum
	// retardation.
	public static final double BREAK_COEF = 2.5;
	public static final double ACC_COEF = 1.5;

	public static final double SPEED_LIMIT = 50 / 3.6;


	// How close to each other vehicles will strive to drive when cruising.
	// If this value is too low, the cars will collide in curves.
	public static final double COLUMN_DISTANCE = 2;
	
	// constructor
	private Const() {
	}
}
