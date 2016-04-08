package sim;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferStrategy;
import java.util.Collection;

import sim.map.intersection.Intersection;
import sim.map.track.AbstractTrack;
import sim.vehicle.Car;

/**
 * 
 * @author henrik
 * 
 */
public class SimDisplay extends Canvas {
	private final EntityHandler entityHandler;
	private final Simulation sim;

	public SimDisplay(Simulation sim, EntityHandler entityHandler) {
		this.entityHandler = entityHandler;
		this.sim = sim;
	}

	/**
	 * Call whenever something in the simulations should be drawn.
	 */
	public void render() {
		if (this.getBufferStrategy() == null)
			createBufferStrategy(2);

		BufferStrategy strategy = getBufferStrategy();
		Graphics2D g2d = (Graphics2D) strategy.getDrawGraphics();
		
	    RenderingHints rh = new RenderingHints( // Makes text cleaner.
	             RenderingHints.KEY_TEXT_ANTIALIASING,
	             RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
	    g2d.setRenderingHints(rh);
		
		
		g2d.setColor(Color.white);
		g2d.fill(this.getBounds());

		drawTracks(g2d);
		drawCars(g2d);
		

		sim.drawInterface(g2d);
		
		// paint to graphics object here
		g2d.dispose();
		// flush the buffer to the main graphics
		strategy.show();

	}

	private void drawTracks(Graphics2D g2d) {
		for (AbstractTrack track : entityHandler.getTracks()) {
			track.draw(g2d);
		}
	}

	private void drawCars(Graphics2D g2d) {
		Collection<Car> cars = entityHandler.getCars();
		for (Car car : cars) {
			car.draw(g2d);
		}
	}

}
