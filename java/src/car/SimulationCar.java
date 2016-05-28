package car;

import traveldata.TravelData;

/**
 * A simplyfied version of ACar.
 *
 * @author henrik
 *
 */
public class SimulationCar extends AbstractCar {

    private final AutonomousCar parent;
    private TravelData travelData;
    private boolean done;

    SimulationCar(final AutonomousCar parent) {
        super(parent.carModel);
        this.parent = parent;
        done = false;
    }

    @Override
    public void updateCollisionBox() {
        final double xPos = tPos.getX();
        final double yPos = tPos.getY();
        double dx = carModel.getLength()/2 - carModel.getFrontAxleDisplacement();
        double scale = 1.2;

        collisionBox = carModel.getCollisionBox().translate(dx, 0).scale(scale).translate(-dx*scale, 0)
                .transform(xPos, yPos, theta);
    }

    public void tick(final double diff) {

        move(diff);
        if (tPos.remaining() <= 0) {
            if (travelData.hasNext()) {
                tPos = travelData.next().getTrackPosition(-tPos.remaining());
            } else {
                done = true;
            }
        }
    }

    public boolean isDone() {
        return done;
    }

    public void copyParent() {
        travelData = parent.getTravelData().copy();
        setTrackPosition(parent.getTrackPosition().copy());
        theta = parent.theta;
        speed = parent.speed;
        acceleration = parent.acceleration;
    }

    public AutonomousCar getParent() {
        return parent;
    }

}
