package sim;

import java.util.ArrayList;
import sim.system.SimSystem;

/**
 * Logic is responsible for the order of logic operations on the database.
 * 
 * @author henrik
 * 
 */
public class SystemHandler {

	// private fields
	private final ArrayList<SimSystem> systems;

	// constructor
	public SystemHandler(Simulation sim) {
		systems = new ArrayList<SimSystem>();
	}

	public void tick(double diff) {
		for (SimSystem system : systems) {
			system.tick(diff);
		}
	}

	public void addSystem(SimSystem system) {
		systems.add(system);
	}

}
