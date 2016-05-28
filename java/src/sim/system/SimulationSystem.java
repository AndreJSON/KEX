package sim.system;

public interface SimulationSystem {

    void tick(double diff);

    // Used to implement back-end visualization.
    // public void draw(Graphics2D g2d);
}
