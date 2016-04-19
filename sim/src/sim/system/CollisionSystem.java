package sim.system;

import sim.EntityDb;

public class CollisionSystem implements SimSystem {

	@Override
	public void tick(double diff) {
		EntityDb.checkCollision();
	}

}
