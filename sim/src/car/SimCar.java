package car;

import traveldata.TravelData;

public class SimCar extends AbstractCar {
	private final ACar parent;
	private TravelData travelData;
	public boolean b = false;
	public SimCar(ACar parent) {
		super(parent.carModel);
		this.parent = parent;
	}
	

	public void updateCollisionBox() {
		double x = tPos.getX();
		double y = tPos.getY();
		collisionBox = carModel.getCollisionBox().translate(0.2, 0).scale(1.3).transform(x, y, theta);
	}
	
	public void tick(double diff) {
		move(diff);
		if (tPos.remaining() <= 0) {
			if (travelData.hasNext()) {
				tPos = travelData.next().getTrackPosition(
						-tPos.remaining());
			} else {
				b = true;
			}
		}
	}
	
	
	
	public void copyParent(){
		travelData = parent.getTravelData().copy();
		setTrackPosition(parent.getTrackPosition().copy());
		theta = parent.theta;
		speed = parent.speed;
		acceleration = parent.acceleration;
	}
	
	public ACar getParent(){
		return parent;
	}

}
