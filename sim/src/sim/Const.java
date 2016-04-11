package sim;

/**
 * Class containing constant values that are shared by all classed.
 * 
 * @author henrik
 * 
 */
public class Const {
	// Begin Intersection segment coordinates
	public static final int NORTH = 0, EAST = 1, SOUTH = 2, WEST = 3;
	public static final int STRAIGHT = 0, RIGHT = 0, LEFT = 1, EXIT = 2;
	public static final int SPLIT_STRAIGHT = 3, SPLIT_LEFT = 4;
	public static final int MAP_ENTRANCE = 5, MAP_EXIT = 6;
	// End Intersection segment coordinates

	private Const() {
	}
}
