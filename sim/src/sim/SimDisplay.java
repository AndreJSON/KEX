package sim;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferStrategy;
import java.util.Collection;

import car.Car;

/**
 * SimDisplay handles the rendering of objects in the simulation.
 * 
 * @author henrik
 * 
 */
@SuppressWarnings("serial")
public class SimDisplay extends Canvas {
	private final Simulation sim;

	public SimDisplay(Simulation sim) {
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
		drawIntersection(g2d);
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
		g2d.setColor(Color.black);

		// Draw SCALE
		int width = (int) (1 * Simulation.SCALE);
		int length = (int) (10 * Simulation.SCALE);
		g2d.drawLine(750 - width / 2, 50, 750 + width / 2, 50);
		g2d.drawLine(750 - width / 2, 50 + length, 750 + width / 2, 50 + length);
		g2d.drawLine(750, 50, 750, 50 + length);

		g2d.setFont(new Font("Arial", Font.BOLD, 12));
		AffineTransform orig = g2d.getTransform();
		g2d.translate(755, 30 + length / 2);
		g2d.rotate(Math.PI / 2);
		g2d.setColor(Color.BLACK);
		g2d.drawString("10 m", 0, 0);
		g2d.setTransform(orig);
		// Draw time elapsed
		g2d.drawString("Time: " + sim.timeElapsed(), 80, 25);
		// Draw current phase
		g2d.drawString(sim.drawPhase(), 25, 50);

		if (Simulation.DEBUG) {
			// Draw FPS
			g2d.drawString("FPS: " + sim.drawFps(), 25, 25);
		}
		// Draw time elapsed
		g2d.drawString("Time: " + sim.timeElapsed(), 77, 25);
		// Draw current phase
		g2d.drawString(sim.drawPhase(), 25, 50);
	}

	private void drawIntersection(Graphics2D g2d) {
		EntityDb.getIntersection().draw(g2d);
	}

	private void drawCars(Graphics2D g2d) {
		Collection<Car> cars = EntityDb.getCars();
		for (Car car : cars) {
			car.draw(g2d);
		}
	}
}