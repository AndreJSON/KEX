package tscs;

import car.Car;

public class SAD extends AbstractTSCS {
	private SADSchedule schedule;

	public SAD(double stepLength, double planLength) {
		schedule = new SADSchedule(stepLength, planLength);
	}

	public void tick(double diff) {
		super.tick(diff);
		//car.setAutonomous(false); //All autonomy should be toggled off.
	}

	public String drawPhase(){
		return "";
	}
}