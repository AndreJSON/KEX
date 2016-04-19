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
	// for whole lifetime
	private static double totalTime = 0;
	private static double totalTimeSq = 0;
	private static int numOfCars = 0;

	// for a certain interval
	private static double totalTimeInt = 0;
	private static double totalTimeSqInt = 0;
	private static int numOfCarsInt = 0;

	private static final DecimalFormat dF = new DecimalFormat("0.0");

	private PerfDb() {
		throw new AssertionError();
	}

	public static void addData(double currentTime, TravelData travelData) {
		double elapsedTime = currentTime - travelData.getStartTime();
		double controlDelay = elapsedTime - travelData.getOptimalTime();
		controlDelay = Math.max(0, controlDelay);

		totalTime += controlDelay;
		totalTimeSq += controlDelay * controlDelay;
		numOfCars++;

		totalTimeInt += controlDelay;
		totalTimeSqInt += controlDelay * controlDelay;
		numOfCarsInt++;
	}

	/**
	 * Data collected for the whole lifetime
	 * 
	 * @return
	 */
	public static String getWholeData() {
		return format(numOfCars, totalTime, totalTimeSq);
	}

	/**
	 * Data collected since latest clear().
	 * 
	 * @return
	 */
	public static String getIntervalData() {
		return format(numOfCarsInt, totalTimeInt, totalTimeSqInt);
	}

	private static String format(int numCars, double mean, double meanSq) {
		if (numCars == 0) {
			return String.format("%d\t%.3f\t%.3f", numCars, 0., 0.).toString();
		}
		return String.format("%d\t%.3f\t%.3f", numCars, mean / numCars,
				Math.sqrt(meanSq / numCars)).toString();
	}

	public static String getSqrtMeanSq() {
		if (numOfCars == 0)
			return "NAN";
		double res = totalTimeSq / numOfCars;
		res = Math.sqrt(res);
		return dF.format(res);
	}

	public static void resetIntervalStatistics() {
		totalTimeInt = 0;
		totalTimeSqInt = 0;
		numOfCarsInt = 0;
	}

}
