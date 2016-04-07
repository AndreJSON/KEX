package sim.map.intersection;

import math.Vector2D;
import sim.Drawable;
import sim.map.track.*;

import java.awt.Graphics2D;
import java.util.HashMap;

public class Intersection implements Drawable {
	public static final int straight = 50, turn = 30, buffer = 1, width = 3;
	public static final int intersectionSize = (straight + turn + buffer) * 2 + width * 3;
	private Segment[] segments; //Just holds all Segments.
	private HashMap<Integer,Segment> startPoints; //Gives the first segment coming from each of the 4 directions.

	public Intersection() {
		segments = new Segment[3];
		startPoints = new HashMap<>();
		init();
	}

	public Segment[] getSegments() {
		return segments;
	}

	private void init() {
		segments[0] = new Segment(new LineTrack(new Vector2D(straight+turn+buffer+width/2, 0), new Vector2D(straight+turn+buffer+width/2, straight)));
		startPoints.put(0,segments[0]);
	
		//segments[0].linkSegment(1,segments[4]);
		//segments[4].linkSegment(1,segments[5]);
		//segments[5].linkSegment(1,segments[6]);
		//segments[6].linkSegment(1,null);

		segments[1] = new Segment(new LineTrack(new Vector2D(straight+turn+buffer+width/2, straight), new Vector2D(straight+turn+buffer+width/2, straight+turn)));
		segments[0].linkSegment(2,segments[1]);
		segments[2] = new Segment(new SquareCurveTrack(new Vector2D(straight+turn+buffer+width/2, straight+turn), new Vector2D(straight+turn+buffer+width/2, straight+turn+buffer+width/2), new Vector2D(straight+turn, straight+turn+buffer+width/2)));
		segments[1].linkSegment(2,segments[2]);
		//segments[2].linkSegment(2,segments[3]);
		//segments[3].linkSegment(2,null);

		//segments[0].linkSegment(3,segments[1]);
		//segments[1].linkSegment(3,segments[7]);
		//segments[7].linkSegment(3,segments[8]);
		//segments[3].linkSegment(3,null);
	}

	@Override
	public void draw(Graphics2D g2d) {
		// TODO Auto-generated method stub
		
	}
}