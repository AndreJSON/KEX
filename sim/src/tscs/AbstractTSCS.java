package tscs;

import sim.EntityDatabase;

public abstract class AbstractTSCS {
	protected boolean emergencyBreak = false;

	public AbstractTSCS() {
	}

	public abstract void tick();

	public void setEmergencyBreak(boolean value) {
		emergencyBreak = value;
	}
}