package car;

import car.model.CarModel;
import car.range.RangeData;
import car.range.RangeFinder;
import java.awt.Color;

import map.intersection.Segment;
import sim.Const;
import sim.Drawable;
import sim.EntityDb;
import traveldata.TravelData;

/**
 * @author henrik
 *
 */
public class AutonomousCar extends AbstractCar implements Drawable {
    // private fields

    /**
     * If the car is autonomous or not.
     */
    private boolean autonomous;
    /**
     * If the collision is on or off.
     */
    private boolean collidable;

    private TravelData travelData;

    private boolean finished;
    private final SimulationCar simCar;
    private final RangeFinder rangeFinder;
    private RangeData rangeData;

    // Constrtuctors
    /**
     * Create a new car of the specified car model.
     *
     * @param carModel
     */
    public AutonomousCar(final CarModel carModel) {
        super(carModel);
        autonomous = true;
        collidable = true;
        finished = false;
        simCar = new SimulationCar(this);
        rangeFinder = new RangeFinder(this);
    }

    // public methods
    /**
     * The to string has the form "Car" + id + "[" + Car model name + "]".
     */
    @Override
    public String toString() {
        return String.format("AutonomousCar[id = %d,  model = %s]", carId,
                carModel.getName());
    }

    public void tick(final double diff) {
        move(diff);
        if (tPos.remaining() < 0) {
            EntityDb.removeCarFromSegment(this);
            if (travelData.hasNext()) {
                tPos = travelData.next().getTrackPosition(-tPos.remaining());
                EntityDb.addCarToSegment(this);
                collidable = true;
            } else {
                finished = true;
            }
        }
    }

    /**
     * Return true if the car is autonomous.
     *
     * @return
     */
    public boolean isAutonomous() {
        return autonomous;
    }

    /**
     * Set the car autonomous state to the specified value.
     *
     */
    public void setAutonomous(final boolean isAutonomous) {
        this.autonomous = isAutonomous;
    }

    /**
     * Check if this car collides.
     *
     * @return
     */
    public boolean isCollidable() {
        return collidable;
    }

    public void updateRangeData() {
        rangeData = rangeFinder.getRange(getBreakDistance() + Const.COLUMN_DISTANCE );
    }

    public RangeData getRangeData() {
        return rangeData;
    }
    
    public void setRangeData(RangeData in){
        rangeData = in;
    }
    
    public void setTravelData(final TravelData travelData) {
        this.travelData = travelData;
        this.tPos = travelData.currentSegment().getTrackPosition(0);
        this.theta = tPos.getTheta();
        EntityDb.addCarToSegment(this);
    }

    public boolean isFinished() {
        return finished;
    }

    public Segment getSegment() {
        return travelData.currentSegment();
    }

    public TravelData getTravelData() {
        return travelData;
    }

    public SimulationCar getSimCar() {
        simCar.copyParent();
        return simCar;
    }

    public int getOrigin() {
        return travelData.getOrigin();
    }

}
