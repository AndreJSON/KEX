package sim;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferStrategy;
import java.text.DecimalFormat;
import java.util.Collection;

import traveldata.TravelData;

import car.Car;

/**
 * SimDisplay handles the rendering of objects in the simulation.
 * 
 * @author henrik
 * 
 */
@SuppressWarnings("serial")
class SimDisplay extends Canvas {
	private final DecimalFormat double0format;
	private final Simulation sim;

	// Simulation display.
	SimDisplay(Simulation sim) {
		this.sim = sim;
		double0format = new DecimalFormat("00");
	}

	// public methods;
	/**
	 * Call whenever something in the simulations should be drawn.
	 */
	void render() {
		if (this.getBufferStrategy() == null)
			createBufferStrategy(2);

		BufferStrategy strategy = getBufferStrategy();
		Graphics2D g2d = (Graphics2D) strategy.getDrawGraphics();

		// Makes text smooth.
		RenderingHints rh = new RenderingHints(
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

	// private methods

	private void drawBackground(Graphics2D g2d) {
		g2d.setColor(Color.GREEN);
		g2d.fillRect(0, 0, Simulation.windowSize[Simulation.X]
				- Simulation.HUDSize, Simulation.windowSize[Simulation.Y]);
		g2d.setColor(Color.WHITE);
		g2d.fillRect(Simulation.windowSize[Simulation.X] - Simulation.HUDSize,
				0, Simulation.HUDSize, Simulation.windowSize[Simulation.Y]);
	}

	private void drawInterface(Graphics2D g2d) {
		g2d.setColor(Color.black);

		// Draw SCALE
		int width = (int) (4 * Simulation.SCALE);
		int length = (int) (100 * Simulation.SCALE);
		g2d.drawLine(750 - width / 2, 50, 750 + width / 2, 50);
		g2d.drawLine(750 - width / 2, 50 + length, 750 + width / 2, 50 + length);
		g2d.drawLine(750, 50, 750, 50 + length);

		g2d.setFont(new Font("Arial", Font.BOLD, 12));
		AffineTransform orig = g2d.getTransform();
		g2d.translate(755, 30 + length / 2);
		g2d.rotate(Math.PI / 2);
		g2d.setColor(Color.BLACK);
		g2d.drawString("100 m", 0, 0);
		g2d.setTransform(orig);

		int paddingX = 20;
		// Draw time elapsed
		g2d.drawString("Time: " + timeElapsedFormated(), paddingX, 25);
		g2d.drawString("Time multiplier: " + Simulation.SCALE_TICK, paddingX,
				40);
		// Draw current phase
		g2d.drawString(sim.drawPhase(), paddingX, 55);

		g2d.drawString("Mean Time Lost: " + TravelData.meanTimeLoss(),
				paddingX, 75);
		g2d.drawString(
				"Sqrt of Mean Sq Time Lost: " + TravelData.sqrtMeanSqTimeLoss(),
				paddingX, 90);

		g2d.drawString("FPS: " + sim.fps(), 700, 25);
	}

	private void drawIntersection(Graphics2D g2d) {
		EntityDb.getIntersection().draw(g2d);
	}

	public String timeElapsedFormated() {
		double elapsedTime = sim.elapsedTime();
		int hours = (int) (elapsedTime / 60 / 60);
		int minutes = (int) (elapsedTime / 60 - hours * 60);
		int seconds = (int) (elapsedTime % 60);
		return double0format.format(hours) + ":"
				+ double0format.format(minutes) + ":"
				+ double0format.format(seconds);
	}

	private void drawCars(Graphics2D g2d) {
		Collection<Car> cars = EntityDb.getCars();
		for (Car car : cars) {
			car.draw(g2d);
		}
	}
}