package sim;

import java.awt.Canvas;
import java.awt.Graphics2D;
import java.awt.image.BufferStrategy;

/**
 * 
 * @author henrik
 * 
 */
public class SimDisplay extends Canvas {
	private final Simulation simulation; // Temp, may be replace with World or a
											// similar object that contains
											// objects to be drawn.

	public SimDisplay(Simulation simulation, int windowsize, int windowsize2) {
		this.setSize(windowsize, windowsize2);
		this.simulation = simulation;
	}

	/**
	 * Call whenever something in the simulations should be drawn.
	 */
	public void render() {
		if (this.getBufferStrategy() == null)
			createBufferStrategy(2);

		BufferStrategy strategy = getBufferStrategy();
		Graphics2D g2d = (Graphics2D) strategy.getDrawGraphics();

		simulation.draw(g2d);
		// paint to graphics object here
		g2d.dispose();
		// flush the buffer to the main graphics
		strategy.show();

	}

}
