package car;

import car.model.CarModel;
import car.range.RangeData;
import car.range.RangeFinder;

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
	private boolean isAutonomous;
	/**
	 * If the collision is on or off.
	 */
	private boolean collidable;

	private TravelData travelData;

	private boolean finished;
	private SimulationCar simCar;
	private double orderTime;
	private final RangeFinder rangeFinder;

	// Constrtuctors
	/**
	 * Create a new car of the specified car model.
	 * 
	 * @param carModel
	 */
	public AutonomousCar(CarModel carModel) {
		super(carModel);
		isAutonomous = true;
		collidable = false;
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
		return String.format("AutonomousCar[id = %d,  model = %s]", id,
				carModel.getName());
	}

	public void tick(double diff) {
		move(diff);
		orderTime -= diff;
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

	@Override
	public int hashCode() {
		return (int) id;
	}

	/**
	 * Return true if the car is autonomous.
	 * 
	 * @return
	 */
	public boolean isAutonomous() {
		return isAutonomous;
	}

	/**
	 * Set the car autonomous state to the specified value.
	 * 
	 * @return
	 */
	public void setAutonomous(boolean isAutonomous) {
		this.isAutonomous = isAutonomous;
	}

	/**
	 * Check if this car collides.
	 * 
	 * @return
	 */
	public boolean isCollidable() {
		return collidable;
	}

	public RangeData getRangeData() {
		return rangeFinder.getRange(getBreakDistance(getMaxDeceleration()
				/ Const.BREAK_COEF) + 2);
	}

	public void setTravelData(TravelData travelData) {
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

	public boolean needOrder() {
		return orderTime <= 0;
	}

	public void setOrder(double time, double acceleration) {
		orderTime = time;
		this.acceleration = acceleration;
	}

}
