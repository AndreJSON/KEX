package car;

/**
 * Class containing the distance to the next car and the next car itself.
 * 
 * @author henrik
 * 
 */
public class RangeData {
	private final ACar car;
	private final double distance;

	public RangeData(ACar car, double distance) {
		this.car = car;
		this.distance = distance;
	}

	public ACar getCar() {
		return car;
	}

	public double distance() {
		return distance;
	}
}
