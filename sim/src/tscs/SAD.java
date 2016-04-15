package tscs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import car.Car;
import map.intersection.Intersection;
import map.intersection.Segment;
import sim.Const;
import sim.EntityDb;

public class SAD extends AbstractTSCS {
	private SADSchedule sched;
	private HashMap<Car, SADPlan> plans;
	private ArrayList<Segment> incomingSegments;

	public SAD() {
		sched = new SADSchedule();
		plans = new HashMap<Car, SADPlan>();
		incomingSegments = initIncomingSegments();
	}

	public void tick(double diff) {
		//super.tick(diff);
		sched.step(); //Step the schedule one step forward per tick.
		LinkedList<Car> cars = new LinkedList<Car>();
		for(Segment seg : incomingSegments) {
			cars = EntityDb.getCarsOnSegment(seg); //With the current intersection model, this can only ever return 0 or 1 car.
			for(Car car : cars) {
				makePlan(diff, car);
			}
		}
		executePlans();
		//car.setAutonomous(false); //All autonomy should be toggled off when running SAD.
	}

	private void makePlan(double diff, Car car) {
		//Make a plan for the car and add it to the hashmap.
	}

	private void executePlans() {
		//Follow the plans.
	}

	private ArrayList<Segment> initIncomingSegments() {
		ArrayList<Segment> tmp = new ArrayList<Segment>();
		for(int direction = Const.NORTH; direction <= Const.WEST; direction++) {
			//Get each of the four starting segments.
			tmp.add(Intersection.getEntry(direction));
		}
		return tmp;
	}

	public String drawPhase(){
		return "";
	}
}