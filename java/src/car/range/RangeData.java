package car.range;

import car.AbstractCar;
import car.AutonomousCar;

/**
 * Class containing the distance to the next car and the next car itself.
 *
 * @author henrik
 *
 */
public class RangeData {

    private AutonomousCar car;
    private double distanceToCar;

    public void set(final AutonomousCar car, final double distance) {
        this.car = car;
        this.distanceToCar = distance;
    }

    public AutonomousCar getCar() {
        return car;
    }

    public double distance() {
        return distanceToCar;
    }

    public String toString() {
        return car.getID() + "[" + distanceToCar + "]";
    }
}
