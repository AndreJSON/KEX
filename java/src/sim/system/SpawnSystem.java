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

public class SpawnSystem implements SimulationSystem {

    private final Spawner[] spawners;
    private final CarModel carModel;
    private final Simulation sim;

    public SpawnSystem(Simulation sim, double[] freq) {
        this.sim = sim;
        // Average m cars per second.
        spawners = new Spawner[4];
        for (int i = 0; i < 4; i++) {
            spawners[i] = Spawners.getPoisson(i, freq[i]);
        }

        carModel = CarModels.getCarModel("Mazda3");
    }

    // private methods
    @Override
    public void tick(double diff) {
        for (Spawner spawner : spawners) {
            spawner.tick(diff);
            handleSpawner(spawner);
        }
    }

    private void handleSpawner(Spawner spawner) {

        if (spawner.getSpawnQueue() <= 0
                || spawner.getSpawnTimer() < Const.SPAWN_INTERVAL) {
            return;
        }
        spawner.reduceSpawnQueue();
        spawner.resetSpawnTimer();

        int origin = spawner.origin();
        int destination = (int) (origin + Statistics.uniform() * 3 + 1);
        destination %= 4;
        EntityDb.addCar(new AutonomousCar(carModel), origin, destination,
                sim.getElapsedTime());
    }
}
