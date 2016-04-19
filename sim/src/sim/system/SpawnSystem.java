package sim.system;

import math.Statistics;

import car.AutonomousCar;
import car.model.CarModel;
import car.model.CarModels;

import sim.Const;
import sim.EntityDb;
import sim.Simulation;
import spawner.Spawner;
import spawner.Spawners;
import spawner.Spawners.Distribution;

public class SpawnSystem implements SimSystem {

	private final Spawner[] spawners;
	private final CarModel carModel;
	private final Simulation sim;

	public SpawnSystem(Simulation sim) {
		this.sim = sim;
		// Average m cars per second.
		Distribution distr = Spawners.getPoisson(0.2/* m */);
		spawners = new Spawner[] { Spawners.getSpawner(Const.NORTH, distr),
				Spawners.getSpawner(Const.SOUTH, distr),
				Spawners.getSpawner(Const.WEST, distr),
				Spawners.getSpawner(Const.EAST, distr) };
		for (Spawner spawner : spawners) {
			System.out.println(spawner);
		}
		carModel = CarModels.getCarModel("Mazda3");
	}

	// private methods
	public void tick(double diff) {
		for (Spawner spawner : spawners) {
			spawner.tick(diff);
			handleSpawner(spawner);
		}
	}

	private void handleSpawner(Spawner spawner) {

		if (spawner.getSpawnQueue() < 0
				|| spawner.getSpawnTimer() < Const.SPAWN_WAIT_INTERVAL) {
			return;
		}
		spawner.reduceSpawnQueue();
		spawner.resetSpawnTimer();

		int origin = spawner.origin();
		int destination = (int) (origin + Statistics.uniform() * 3 + 1);
		destination %= 4;
		EntityDb.addCar(new AutonomousCar(carModel), origin, destination,
				sim.elapsedTime());
	}
}
