package spawner;

import math.Statistics;
import sim.Const;
import spawner.Spawners.Distribution;

/**
 * Spawner
 *
 * @author henrik
 *
 */
public class Spawner {

    private final Distribution numGen;
    private final int origin;
    // private fields
    private double statTimer;
    private double spawnTimer;
    private int spawnQueue;

    Spawner(int origin, Distribution distro) {
        this.numGen = distro;
        this.origin = origin;
        statTimer = Statistics.uniform() * Const.SPAWN_INTERVAL;
        spawnQueue = 0;
    }

    // public methods
    public void tick(double diff) {
        statTimer += diff;
        spawnTimer += diff;

        if (statTimer > 1 / Const.SPAWN_INTERVAL) {
            spawnQueue += numGen.getRandom();
            statTimer = 0;
        }
    }

    public int getSpawnQueue() {
        return spawnQueue;
    }

    public void reduceSpawnQueue() {
        spawnQueue--;
    }

    public void resetSpawnTimer() {
        spawnTimer = 0;
    }

    public double getSpawnTimer() {
        return spawnTimer;
    }

    public int origin() {
        return origin;
    }

    @Override
    public String toString() {
        return numGen.toString();
    }
}
