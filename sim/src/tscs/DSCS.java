package tscs;

import sim.EntityDatabase;

public class DSCS extends AbstractTSCS {

	public DSCS() {
	}

	public void tick() {
		if(emergencyBreak) {
			System.out.println("All cars should break now");
		}
	}
}