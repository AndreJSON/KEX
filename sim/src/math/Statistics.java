package math;

import java.util.Random;

public class Statistics {
	private Statistics() {
	}

	private static Random r = new Random();

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

	public static double getBellRandom(double mean, double variance) {
		double k = r.nextGaussian();
		return k * Math.sqrt(variance) + mean;
	}

	public static int getBinomial(int n, double p) {
		int x = 0;
		for (int i = 0; i < n; i++) {
			if (Math.random() < p)
				x++;
		}
		return x;
	}

}
