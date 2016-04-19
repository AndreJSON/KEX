package spawner;

import sim.Const;
import math.Statistics;


public class Spawners {
	
	public static Spawner getSpawner(int origin, Distribution distr){
		return new Spawner(origin, distr);
	}
	
	public static Distribution getBinomial(int n, double p){
		return new BinomialDistribution(n, p);
	}

	public static Distribution getPoisson(double mean){
		return new PoissonDistribution(mean);
	}
	
	public static interface Distribution {
		int getRandom();
		double mean();
		double variance();
	}

	static class BinomialDistribution implements Distribution {
		private final int n;
		private final double p;

		BinomialDistribution(int n, double p) {
			assert n > 1;
			assert p > 0 && p < 1;
			this.n = n;
			this.p = p / Const.SPAWN_WAIT_INTERVAL;
		}

		@Override
		public int getRandom() {
			return Statistics.getBinomial(n, p);
		}
		@Override
		public double mean() {
			return n * p;
		}
		
		public String toString(){
			return "Binomial \tn="+n+"\tp="+p;
		}

		@Override
		public double variance() {
			return n*p*(1-p);
		}
	}
	
	static class PoissonDistribution implements Distribution {
		private final double mean;
		public PoissonDistribution(double mean) {
			assert mean > 0;
			this.mean = mean / Const.SPAWN_WAIT_INTERVAL;
			
		}

		@Override
		public int getRandom() {
			return Statistics.getPoissonRandom(mean);
		}

		@Override
		public double mean() {
			return mean;
		}

		@Override
		public double variance() {
			return mean;
		}
		
		public String toString(){
			return "Poisson \tlambda="+mean *  Const.SPAWN_WAIT_INTERVAL;
		}
		
	}
}
