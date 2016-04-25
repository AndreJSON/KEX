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
	private static final ArrayList<Double> data = new ArrayList<>();

	private PerfDb() {
		throw new AssertionError();
	}

	public static void addData(double currentTime, TravelData travelData) {
		double elapsedTime = currentTime - travelData.getStartTime();
		double controlDelay = elapsedTime - travelData.getOptimalTime();
		controlDelay = Math.max(0, controlDelay);
		data.add(controlDelay);
	}

	public static Data compileData() {
		if (data.size() < 2)
			return null;
		return new Data(data);
	}

	public static class Data {
		private final double meancd;
		private final double mscd;
		private final double variance;

		public Data(ArrayList<Double> data) {
			double tmpMeanCD = 0, tmpMSCD = 0, tmpVariance = 0;

			for (Double d : data) {
				tmpMeanCD += d;
				tmpMSCD += d * d;
			}
			tmpMeanCD /= (double) data.size();
			tmpMSCD /= (double) data.size();
			tmpVariance = 0;
			for (Double d : data) {
				tmpVariance += Math.pow(d - tmpMeanCD, 2);
			}
			tmpVariance /= (double) (data.size() - 1);
			meancd = tmpMeanCD;
			mscd = tmpMSCD;
			variance = tmpVariance;

		}

		public double getMean() {
			return meancd;
		}

		public double getMSCD() {
			return mscd;
		}

		public double getVariance() {
			return variance;
		}
	}
}
