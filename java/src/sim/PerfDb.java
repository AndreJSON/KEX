package sim;

import java.util.ArrayList;

import traveldata.TravelData;

/**
 * Stores the performance of the intersection manager.
 *
 * @author henrik
 *
 */
public class PerfDb {

    private static final ArrayList<Double> DATA = new ArrayList<>();

    private PerfDb() {
        throw new AssertionError();
    }

    public static void addData(double elapsedTime, TravelData travelData) {
        double travelTime = elapsedTime - travelData.getStartTime();
        double controlDelay = Math.max(0, travelTime - travelData.
                getOptimalTime());
        DATA.add(controlDelay);
    }

    public static PerformanceData compileData() {
        if (DATA.size() < 2) {
            return null;
        }
        return new PerformanceData(DATA);
    }

    public static class PerformanceData {

        private final double meancd;
        private final double mscd;
        private final double variance;
        private final int samples;
        private final double maxCD;

        public PerformanceData(ArrayList<Double> data) {
            double tmpMCD = 0;
            double tmpMSCD = 0;
            double tmpVariance = 0;
            double tmpMaxCD = 0;
            for (Double d : data) {
                tmpMCD += d;
                tmpMaxCD = Math.max(tmpMaxCD, d);
                tmpMSCD += d * d;
            }
            tmpMCD /= data.size();
            tmpMSCD /= data.size();
            tmpVariance = 0;
            for (Double d : data) {
                tmpVariance += Math.pow(d - tmpMCD, 2);
            }
            tmpVariance /= data.size();
            meancd = tmpMCD;
            mscd = tmpMSCD;
            variance = tmpVariance;
            samples = data.size();
            maxCD = tmpMaxCD;
        }

        public double getMCD() {
            return meancd;
        }

        public double getMSCD() {
            return mscd;
        }

        public double getVariance() {
            return variance;
        }
        
        
        public int sampleSize(){
            return samples;
        }
        
        public double maxCD(){
            return maxCD;
        }
    }
}
