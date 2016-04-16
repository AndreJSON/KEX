package car;

import java.util.Iterator;

import map.intersection.Segment;
import map.track.TrackPosition;
import sim.Drawable;
import sim.EntityDb;
import traveldata.TravelData;

/**
 * @author henrik
 * 
 */
public class ACar extends AbstractCar implements Drawable {
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

	// Constrtuctors
	/**
	 * Create a new car of the specified car model.
	 * 
	 * @param carModel
	 */
	public ACar(CarModel carModel) {
		super(carModel);
		isAutonomous = true;
		collidable = false;
		finished = false;
	}

	// public methods
	/**
	 * The to string has the form "Car" + id + "[" + Car model name + "]".
	 */
	@Override
	public String toString() {
		return "Car" + id + "[" + carModel.getName() + "]";
	}
	
	public void tick(double diff) {
		move(diff);
		if (tPos.remaining() <= 0) {
			EntityDb.removeCarFromSegment(this);
			if (travelData.hasNext()) {
				tPos = travelData.next().getTrackPosition(
						-tPos.remaining());
				EntityDb.addCarToSegment(this);
				collidable = true;
			} else {
				finished = true;
			}
		}
	}

	/**
	 * Check how much there is remaining on the track.
	 * 
	 * @return
	 */
	public double remainingOnTrack() {
		return tPos.remaining();
	}


	/**
	 * Gives the maximum acceleration the vehicle is able to perform in the
	 * given time diff.
	 */
	public double getMaxAcceleration() {
		return carModel.getMaxAcc();
	}



	/**
	 * Get the top speed of this car. Same as car.getType().getTopSpeed();
	 * 
	 * @return
	 */
	public double getTopSpeed() {
		return carModel.getTopSpeed();
	}

	/**
	 * Gives the maximum retardation the vehicle is able to perform in the given
	 * time diff.
	 * 
	 * @param diff
	 * @return
	 */
	public double getMaxDeceleration() {
		return carModel.getMaxRet();
	}



	/**
	 * Get the car type.
	 * 
	 * @return
	 */
	public CarModel getType() {
		return carModel;
	}

	/**
	 * Give the car a TrackPosition to follow.
	 * 
	 * @param trackPosition
	 *            the position to follow.
	 */
	public void setTrackPosition(TrackPosition trackPosition) {
		if (getTrackPosition() == null) {
			theta = trackPosition.getHeading();
		}
		setTrackPosition(trackPosition);
	}

	public int getOrigin() {
		return travelData.getOrigin();
	}



	/**
	 * Get the model of the car.
	 */
	public CarModel getModel() {
		return carModel;
	}

	/**
	 * Get the length of the car. Equivalent to getModel().getLength();
	 * 
	 * @return
	 */
	public double getLength() {
		return getModel().getLength();
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
		// If the car has passed itself during the search.
		boolean passedSelf = false;
		// Get the travel data of the car.
		// Get the current segment the car is on.
		Segment searchSegment;
		// Iterator to check all cars on this segment.
		Iterator<ACar> carsOnSegment;
		// The distance to next car.
		double distance;

		distance = 0;
		passedSelf = false;
		searchSegment = travelData.currentSegment();
		while (true) {
			carsOnSegment = EntityDb.getCarsOnSegment(searchSegment)
					.descendingIterator();
			while (carsOnSegment.hasNext()) {
				ACar nextCar = carsOnSegment.next();
				if (equals(nextCar)) {
					passedSelf = true;
				} else if (passedSelf) {
					distance += -nextCar.remainingOnTrack()
							+ remainingOnTrack()
							- nextCar.getModel().getLength()*1.1;
					return new RangeData(nextCar, distance);
				}
			}
			searchSegment = searchSegment.nextSegment(travelData
					.getDestination());
			if (searchSegment == null)
				return null;
			distance += searchSegment.length();
		}
	}

	public void setTravelData(TravelData travelData) {
		this.travelData = travelData;
		this.tPos = travelData.currentSegment().getTrackPosition(0);
		this.theta = tPos.getHeading();
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

}
