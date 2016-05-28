package sim;

/**
 * Class containing constant values that are shared by all classed.
 *
 * @author henrik
 *
 */
public final class Const {
    // public static constants

    // Begin Intersection segment coordinates
    public static final int NORTH = 0, EAST = 1, SOUTH = 2, WEST = 3;
    public static final int STRAIGHT = 0, RIGHT = 0, LEFT = 1, EXIT = 2;
    public static final int SPLIT = 3;
    public static final int MAP_ENTRANCE = 4, MAP_EXIT = 5;
    public static final int SPLIT_GUIDE1 = 6, SPLIT_GUIDE2 = 7;
    // End Intersection segment coordinates

    // Factor slower comfortable breaking compared to the maximum retardation.
    public static final double DECELERATION = 1.7;
    public static final double ACCELERATION = 1.7;

    // Speed limit in m/s.
    public static final double SPEED_LIMIT = 50 / 3.6;

    // How close to each other vehicles will strive to drive when cruising.
    // If this value is too low, the cars will collide in curves.
    public static final double COLUMN_DISTANCE = 2;
    

    public static final double TIME_STEP = 1 / 100.;

    // The time between new vehicles spawning.
    public static final double SPAWN_INTERVAL = (COLUMN_DISTANCE + 4.415)
            / SPEED_LIMIT;
    public static int THREAD_COUNT = 4;

    // constructor, prohibit instantiation
    private Const() {
        throw new AssertionError();
    }
}
