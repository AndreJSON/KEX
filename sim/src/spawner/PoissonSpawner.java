package spawner;

import sim.Logic;
import math.Statistics;

public class PoissonSpawner implements SpawnerInterface {
	// private fields
	private double statTimer;
	private double mean;
	private int spawnQueue;
	private double spawnTimer;
	private int origin;
	private Logic log;
	private boolean isOn;

	// constructor
	public PoissonSpawner(Logic log, int origin, double mean) {
		this.log = log;
		this.origin = origin;
		this.mean = mean;
		statTimer = Math.random() * 10;
		isOn = true;
	}

	@Override
	public void tick(double diff) {
		statTimer += diff;
		spawnTimer += diff;
		if (!isOn)
			return;
		if (statTimer > 10) {
			spawnQueue += Statistics.getPoissonRandom(mean);
			statTimer = Statistics.getBellRandom(0, 3);
		}

		if (spawnQueue > 0 && spawnTimer > 0.8) {
			spawnQueue--;
			spawnTimer = Statistics.getBellRandom(0, 1 / 2);
			spawn();
		}
	}


	@Override
	public void setOn(boolean isOn) {
		this.isOn = isOn;
	}

	@Override
	public boolean isOn() {
		return isOn;
	}

	// private methods
	private void spawn() {
		int dest = (int) (Math.random() * 3 + 1) + origin;
		log.spawnCar("Mazda3", origin, dest % 4);
	}
}
