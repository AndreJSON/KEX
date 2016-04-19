package car.range;

import car.AbstractCar;

/**
 * Class containing the distance to the next car and the next car itself.
 * 
 * @author henrik
 * 
 */
public class RangeData {
	private final AbstractCar car;
	private final double distance;

	public RangeData(AbstractCar car, double distance) {
		this.car = car;
		this.distance = distance;
	}

	public AbstractCar getCar() {
		return car;
	}

	public double distance() {
		return distance;
	}

	public String toString() {
		return car.getID() + "[" + distance + "]";
	}
}
