package sim.system;

public interface SimSystem {
	public void tick(double diff);

	// Used to implement back-end visualization.
	// public void draw(Graphics2D g2d);
}
