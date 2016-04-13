package sim;

import java.text.DecimalFormat;

import traveldata.TravelData;

/**
 * Stores the performance of the intersection manager.
 * 
 * @author henrik
 * 
 */
public class PerfDb {
	private static double totalTime = 0;
	private static double totalTimeSq = 0;
	private static int numOfCars = 0;
	private static final DecimalFormat dF = new DecimalFormat("0.0");

	private PerfDb() {
	}

	public static void addData(double currentTime, TravelData travelData) {
		double elapsedTime = currentTime - travelData.getStartTime();
		double controlDelay = elapsedTime - travelData.getOptimalTime();
		controlDelay = Math.max(0, controlDelay);
		totalTime += controlDelay;
		totalTimeSq += controlDelay * controlDelay;
		numOfCars++;
	}

	public static String getMean() {
		double res = totalTime / numOfCars;
		return dF.format(res);
	}

	public static String getSqrtMeanSq() {
		double res = totalTimeSq / numOfCars;
		res = Math.sqrt(res);
		return dF.format(res);
	}
}
