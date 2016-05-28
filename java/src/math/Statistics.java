package math;

import java.util.Random;

public final class Statistics {

    private Statistics() {
        throw new AssertionError();
    }

    static {

    }

    private final static Random RNG = new Random(1000);

    public static int getPoissonRandom(double mean) {
        double l = Math.exp(-mean);
        int k = 0;
        double p = 1.0;
        do {
            p = p * RNG.nextDouble();
            k++;
        } while (p > l);
        return k - 1;
    }

    public static void reset() {
        RNG.setSeed(1000);
    }

    public static double getBellRandom(double mean, double variance) {
        double k = RNG.nextGaussian();
        return k * Math.sqrt(variance) + mean;
    }

    public static int getBinomial(int n, double p) {
        int x = 0;
        for (int i = 0; i < n; i++) {
            if (RNG.nextDouble() < p) {
                x++;
            }
        }
        return x;
    }

    public static double uniform() {
        return RNG.nextDouble();
    }

}
