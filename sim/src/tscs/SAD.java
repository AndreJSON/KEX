package tscs;

import car.Car;

public class SAD extends AbstractTSCS {
	private SADSchedule schedule;

	public SAD() {
		schedule = new SADSchedule();
	}

	public void tick(double diff) {
		super.tick(diff);
		//car.setAutonomous(false); //All autonomy should be toggled off when running SAD.
	}

	public String drawPhase(){
		return "";
	}
}