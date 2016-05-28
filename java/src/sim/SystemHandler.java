package sim;

import java.util.ArrayList;
import sim.system.SimulationSystem;

/**
 * Logic is responsible for the order of logic operations on the database.
 *
 * @author henrik
 *
 */
public class SystemHandler {

    // private fields
    private final ArrayList<SimulationSystem> systems;

    // constructor
    public SystemHandler() {
        systems = new ArrayList<>();
    }

    public void tick(double diff) {
        systems.stream().forEach((sys) -> {
            sys.tick(diff);
        });
    }

    public void addSystem(SimulationSystem system) {
        systems.add(system);
    }

}
