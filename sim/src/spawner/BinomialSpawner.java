package spawner;

import sim.Logic;
import math.Statistics;

public class BinomialSpawner implements SpawnerInterface {
	private double statTimer = 10;
	private int n;
	private double p;
	private int spawnQueue = 0;
	private double spawnTimer = 0;
	private int origin;
	private Logic log;
	private boolean on;

	public BinomialSpawner(Logic log, int origin, int n, double p) {
		this.log = log;
		this.origin = origin;
		statTimer = Math.random() * 10;
		on = true;
		this.n = n;
		this.p = p;
	}

	@Override
	public void tick(double diff) {
		statTimer += diff;
		spawnTimer += diff;
		if (!on)
			return;
		if (statTimer > 10) {
			spawnQueue += Statistics.getBinomial(n, p);
			statTimer = Statistics.getBellRandom(0, 3);
		}

		if (spawnQueue > 0 && spawnTimer > 0.8) {
			spawnQueue--;
			spawnTimer = Statistics.getBellRandom(0, 1 / 2);
			spawn();
		}
	}

	private void spawn() {
		int dest = (int) (Math.random() * 3 + 1) + origin;
		log.spawnCar("Mazda3", origin, dest % 4);
	}

	@Override
	public void on() {
		this.on = true;
	}

	@Override
	public void off() {
		this.on = false;
	}
}
