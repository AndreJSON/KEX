package sim;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferStrategy;
import java.text.DecimalFormat;

import car.AbstractCar;

import map.intersection.Intersection;

/**
 * SimDisplay handles the rendering of objects in the simulation.
 * 
 * @author henrik
 * 
 */
@SuppressWarnings("serial")
class SimDisplay extends Canvas {
	private static final DecimalFormat double0format = new DecimalFormat("00");
	private final Simulation sim;

	// Simulation display.
	SimDisplay(Simulation sim) {
		this.sim = sim;
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
		g2d.fillRect(0, 0, Simulation.WINDOW_SIZE[Simulation.X]
				- Simulation.HUDSize, Simulation.WINDOW_SIZE[Simulation.Y]);
		g2d.setColor(Color.WHITE);
		g2d.fillRect(Simulation.WINDOW_SIZE[Simulation.X] - Simulation.HUDSize,
				0, Simulation.HUDSize, Simulation.WINDOW_SIZE[Simulation.Y]);
	}

	Runtime runTime = Runtime.getRuntime();

	private void drawIntersection(Graphics2D g2d) {
		Intersection.draw(g2d);
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

	public void drawCars(Graphics2D g2d) {
		for (AbstractCar car : EntityDb.getCars()) {
			car.draw(g2d);
		}
	}

	private void drawInterface(Graphics2D g2d) {
		g2d.setColor(Color.black);
		drawTime(g2d);
		//drawPerfData(g2d);
		drawScale(g2d);
		drawTFPS(g2d);
		if (Simulation.DEBUG)
			memoryDebug(g2d);
		
	}

	private void drawTime(Graphics2D g2d) {
		int paddingX = 20;
		// Draw time elapsed
		g2d.drawString("Time: " + timeElapsedFormated(), paddingX, 25);
		g2d.drawString("Time multiplier: " + Simulation.SCALE_TICK, paddingX,
				40);
		if (!Simulation.SAD) {
			g2d.drawString("DSCS: " + sim.getDSCS().drawPhase(), paddingX, 55);
		}
	}

	@SuppressWarnings("unused")
	private void drawPerfData(Graphics2D g2d) {
		int paddingX = 20;
		g2d.drawString("Performance data: " + PerfDb.getWholeData(), paddingX,
				75);
		
	}

	private void drawScale(Graphics2D g2d) {
		// Draw SCALE
		int width = (int) (4 * Simulation.SCALE);
		int length = (int) (100 * Simulation.SCALE);
		g2d.drawLine(750 - width / 2, 50, 750 + width / 2, 50);
		g2d.drawLine(750 - width / 2, 50 + length, 750 + width / 2, 50 + length);
		g2d.drawLine(750, 50, 750, 50 + length);

		AffineTransform orig = g2d.getTransform();
		g2d.translate(755, 30 + length / 2);
		g2d.rotate(Math.PI / 2);
		g2d.setColor(Color.BLACK);
		g2d.drawString("100 m", 0, 0);
		g2d.setTransform(orig);
	}

	private void drawTFPS(Graphics2D g2d) {

		int paddingX = 700;
		g2d.drawString("FPS: " + sim.fps() + " Hz", paddingX, 25);
		g2d.drawString("TPS: " + (int) sim.tps() + " Hz", paddingX, 40);
	}

	private void memoryDebug(Graphics2D g2d) {
		int paddingX = ((int) Simulation.WINDOW_SIZE[1]) - 140;
		int y = Simulation.SimulationSize - 80;
		g2d.drawString("MEMORY ", paddingX, y);
		y += 15;
		g2d.drawString("MAX: " + (int) runTime.maxMemory() / 1024, paddingX, y);
		y += 15;
		g2d.drawString("ALLOCATED: " + (int) runTime.totalMemory() / 1024,
				paddingX, y);
		y += 15;
		g2d.drawString("FREE: " + (int) runTime.freeMemory() / 1024, paddingX,
				y);
	}
}