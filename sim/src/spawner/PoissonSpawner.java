package spawner;

import sim.Logic;
import math.Statistics;

public class PoissonSpawner implements Spawner {
	private double statTimer = 10;
	private double mean = 0.6;
	private int spawnQueue = 0;
	private double spawnTimer = 0;
	private int origin;
	private Logic log;
	
	public PoissonSpawner(Logic log, int origin, double mean){
		this.log = log;
		this.origin = origin;
		this.mean = mean;
		statTimer = Math.random() * 10;
	}

	@Override
	public void tick(double diff) {
		statTimer += diff;
		spawnTimer+= diff;
		if (statTimer > 10){
			spawnQueue += Statistics.getPoissonRandom(mean);
			statTimer-=Statistics.getBellRandom(10, 3);
		}
		
		if (spawnQueue > 0 && spawnTimer > 0.6) {
			spawnQueue--;
			spawnTimer = 0;
			spawn();
		}
	}
	
	private void spawn(){
		int dest = (int)(Math.random() * 3 + 1) + origin;
		log.spawnCar("Mazda3", origin, dest%4);
	}
}
