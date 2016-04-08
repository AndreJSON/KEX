package sim;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferStrategy;
import java.util.Collection;

import sim.map.intersection.*;
import sim.map.track.AbstractTrack;
import sim.vehicle.Car;

/**
 * SimDisplay handles the rendering of objects in the simulation.
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

		RenderingHints rh = new RenderingHints(
				// Makes text cleaner.
				RenderingHints.KEY_TEXT_ANTIALIASING,
				RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g2d.setRenderingHints(rh);
		
		drawBackground(g2d);
		drawSegments(g2d);
		drawTracks(g2d);
		drawCars(g2d);
		drawInterface(g2d);

		// paint to graphics object here
		g2d.dispose();
		// flush the buffer to the main graphics
		strategy.show();

	}

	private void drawBackground(Graphics2D g2d) {
		g2d.setColor(Color.GREEN);
		g2d.fillRect(0, 0, Simulation.windowSize[Simulation.X]
				- Simulation.HUDSize, Simulation.windowSize[Simulation.Y]);
		g2d.setColor(Color.WHITE);
		g2d.fillRect(Simulation.windowSize[Simulation.X] - Simulation.HUDSize,
				0, Simulation.HUDSize, Simulation.windowSize[Simulation.Y]);
	}

	public void drawInterface(Graphics2D g2d) {

		// Draw SCALE
		int width = 25;
		int length = (int) (100 * Simulation.SCALE);
		g2d.drawLine(750 - width / 2, 50, 750 + width / 2, 50);
		g2d.drawLine(750 - width / 2, 50 + length, 750 + width / 2, 50 + length);
		g2d.drawLine(750, 50, 750, 50 + length);

		g2d.setFont(new Font("Arial", Font.BOLD, 16));
		AffineTransform orig = g2d.getTransform();
		g2d.translate(755, 25 + length / 2);
		g2d.rotate(Math.PI / 2);
		g2d.setColor(Color.BLACK);
		g2d.drawString("100 m", 0, 0);
		g2d.setTransform(orig);

		if (Simulation.DEBUG) {
			// Draw FPS
			g2d.drawString("FPS: " + sim.drawFps(),
					Simulation.windowSize[0] - 100, 25);
		}
	}

	private void drawSegments(Graphics2D g2d) {
		for (Segment seg : entityHandler.getSegments()) {
			seg.getTrack().draw(g2d);
		}
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
