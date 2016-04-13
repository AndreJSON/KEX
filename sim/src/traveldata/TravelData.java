package traveldata;

import map.intersection.Segment;

public class TravelData {

	// private final fields
	private final TravelPlan travelPlan;
	private final double startTime;

	// private final fields
	private int segmentIndex;

	// constructor
	private TravelData(TravelPlan travelPlan, double startTime) {
		this.travelPlan = travelPlan;
		this.startTime = startTime;
		segmentIndex = 0;
	}
	
	public static TravelData getTravelData(int origin, int destination, double startTime){
		return new TravelData(TravelPlan.getTravelPlan(origin, destination), startTime);
	}

	/**
	 * If there is another segment.
	 * 
	 * @return
	 */
	public boolean hasNext() {
		return (segmentIndex + 1) < travelPlan.numOfSegments();
	}
	
	public Segment next(){
		segmentIndex++;
		if (segmentIndex >= travelPlan.numOfSegments()){
			throw new RuntimeException("Out of segments!");
		}
		return currentSegment();
	}

	public Segment currentSegment() {
		return travelPlan.getSegment(segmentIndex);
	}

	public int getOrigin() {
		return travelPlan.getOrigin();
	}

	public int getDestination() {
		return travelPlan.getDestination();
	}

	public double getStartTime() {
		return startTime;
	}

	public double getOptimalTime() {
		return travelPlan.optimalTime();
	}

}
