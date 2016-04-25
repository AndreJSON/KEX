package math;

import java.util.Random;

public class Statistics {
	private Statistics() {
		throw new AssertionError();
	}

	static {

	}

	private final static Random r = new Random(1000);

	public static int getPoissonRandom(double mean) {
		double L = Math.exp(-mean);
		int k = 0;
		double p = 1.0;
		do {
			p = p * r.nextDouble();
			k++;
		} while (p > L);
		return k - 1;
	}

	public static void reset() {
		r.setSeed(1000);
	}

	public static double getBellRandom(double mean, double variance) {
		double k = r.nextGaussian();
		return k * Math.sqrt(variance) + mean;
	}

	public static int getBinomial(int n, double p) {
		int x = 0;
		for (int i = 0; i < n; i++) {
			if (r.nextDouble() < p)
				x++;
		}
		return x;
	}

	public static double uniform() {
		return r.nextDouble();
	}

}
