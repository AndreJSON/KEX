package sim.system;

import sim.EntityDb;

public class CollisionSystem implements SimulationSystem {

    @Override
    public void tick(double diff) {
        EntityDb.checkCollision();
    }
}
