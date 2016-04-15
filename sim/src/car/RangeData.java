package car;

/**
 * Class containing the distance to the next car and the next car itself.
 * 
 * @author henrik
 * 
 */
public class RangeData {
	private final Car car;
	private final double distance;

	public RangeData(Car car, double distance) {
		this.car = car;
		this.distance = distance;
	}

	public Car getCar() {
		return car;
	}

	public double distance() {
		return distance;
	}
}
