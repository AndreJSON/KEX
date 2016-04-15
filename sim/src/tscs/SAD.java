package tscs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.LinkedList;
import java.util.Iterator;

import car.Car;
import map.intersection.Intersection;
import map.intersection.Segment;
import sim.Const;
import sim.EntityDb;

public class SAD extends AbstractTSCS {
	private SADSchedule sched;
	private HashMap<Car, SADPlan> plans; //SAD has a plan for everyone...
	private ArrayList<Segment> incomingSegments;

	public SAD() {
		sched = new SADSchedule();
		plans = new HashMap<Car,SADPlan>();
		incomingSegments = initIncomingSegments();
	}

	public void tick(double diff) {
		//super.tick(diff);
		sched.tick(); //Step the schedule one step forward.
		LinkedList<Car> cars = new LinkedList<Car>();
		for(Segment seg : incomingSegments) {
			cars = EntityDb.getCarsOnSegment(seg); //With the current intersection model, this can only ever return 0 or 1 car.
			for(Car car : cars) {
				makePlan(diff, car);
			}
		}
		Iterator<Map.Entry<Car,SADPlan>> it = plans.entrySet().iterator();
		Map.Entry<Car,SADPlan> entry;
		while(it.hasNext()) {
			entry = it.next();
			executePlan(entry.getKey(), entry.getValue());
			//End by ticking the plan to step it forward.
			entry.getValue().tick();
		}
	}

	private void makePlan(double diff, Car car) {
		SADPlan plan = new SADPlan();
		plan.addDirective(0,SADPlan.SLOW_DOWN);
		plans.put(car, plan);
		//TODO: Take the schedule into account
	}

	private void executePlan(Car car, SADPlan plan) {
		//All autonomy should be toggled off when running SAD.
		car.setAutonomous(false);
		int directive = plan.getDirective();
		switch(directive) {
			case -1: car.setAcc(-car.getMaxDec() / Const.BREAK_COEF); break;
			case  0: car.setAcc(0); break;
			case  1: car.setAcc(car.getMaxAcceleration() / Const.ACC_COEF); break;
			default: throw new IllegalArgumentException("The plan is corrupt!");
		}
		//TODO: Actually follow the plan.
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