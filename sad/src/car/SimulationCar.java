package car;

import traveldata.TravelData;

/**
 * A simplyfied version of ACar.
 * 
 * @author henrik
 * 
 */
public class SimulationCar extends AbstractCar {
	private final AutonomousCar parent;
	private TravelData travelData;
	private boolean done;

	SimulationCar(AutonomousCar parent) {
		super(parent.carModel);
		this.parent = parent;
		done = false;
	}

	@Override
	public void updateCollisionBox() {
		double x = tPos.getX();
		double y = tPos.getY();
		double s = 1.2;
		double dx = carModel.getLength() * s - carModel.getLength();
		dx /= 2.;

		collisionBox = carModel.getCollisionBox().translate(dx, 0).scale(s)
				.transform(x, y, theta);
	}

	public void tick(double diff) {

		move(diff);
		if (tPos.remaining() <= 0) {
			if (travelData.hasNext()) {
				tPos = travelData.next().getTrackPosition(-tPos.remaining());
			} else {
				done = true;
			}
		}
	}

	public boolean done() {
		return done;
	}

	public void copyParent() {
		travelData = parent.getTravelData().copy();
		setTrackPosition(parent.getTrackPosition().copy());
		theta = parent.theta;
		speed = parent.speed;
		acceleration = parent.acceleration;
	}

	public AutonomousCar getParent() {
		return parent;
	}

}
