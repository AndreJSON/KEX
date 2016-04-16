package car;

import traveldata.TravelData;

public class SimCar extends AbstractCar {
	private final ACar parent;
	private TravelData travelData;
	public SimCar(ACar parent) {
		super(parent.carModel);
		this.parent = parent;
	}
	

	public void updateCollisionBox() {
		double x = tPos.getX();
		double y = tPos.getY();
		double s = 1.15;
		double dx = carModel.getLength() * s - carModel.getLength();
		dx /= 2.;
		
		
		collisionBox = carModel.getCollisionBox().translate(dx, 0).scale(s).transform(x, y, theta);
	}
	
	public void tick(double diff) {
		
		move(diff);
		if (tPos.remaining() <= 0) {
			if (travelData.hasNext()) {
				tPos = travelData.next().getTrackPosition(
						-tPos.remaining());
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
