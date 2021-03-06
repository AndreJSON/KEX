package car;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

import car.model.CarModel;

import map.track.TrackPosition;
import math.Vector2D;
import sim.Const;
import sim.Drawable;
import sim.Simulation;
import util.Collidable;
import util.CollisionBox;

/**
 * AbstractCar contains all basic functions and values necessary for a basic
 * vehicle.
 *
 * @author henrik
 *
 */
public abstract class AbstractCar implements Drawable, Collidable {

    /**
     * Counts the id number to be assigned to the next car.
     */
    private static long trackId;
    // private fields
    /**
     * The id number, important for hashing
     */
    protected final long carId;
    /**
     * Acceleration in m/s.
     */
    protected double acceleration;
    /**
     * Speed in m/s.
     */
    protected double speed;
    /**
     * The angle of the chassi. 0 is in the direction (1,0). Increase of the
     * value is towards the y-axis.
     */
    protected double theta;
    /**
     * Model of car.
     */
    protected final CarModel carModel;
    /**
     * The current position on a track.
     */
    protected TrackPosition tPos;
    /**
     * Collision box.
     */
    protected CollisionBox collisionBox;
    /**
     * Color of the chassi. Default color comes from the car model.
     */
    protected Color color;

    // Constructor
    public AbstractCar(final CarModel carModel) {
        this.carId = ++trackId;
        this.carModel = carModel;
        this.acceleration = 0;
        this.speed = 0;
        this.theta = 0;
        color = carModel.getColor();
    }

    // public methods
    public void updateCollisionBox() {
        collisionBox = carModel.getCollisionBox().transform(tPos.getX(), tPos.getY(), theta);
    }

    public long getID() {
        return carId;
    }

    @Override
    public Rectangle2D getBounds() {
        return collisionBox.getBounds();
    }

    /**
     *
     * @return
     */
    @Override
    public CollisionBox getCollisionBox() {
        return collisionBox;
    }

    /**
     * Move, act on delta time.
     *
     * @param diff
     */
    public void move(final double diff) {
        if (tPos.remaining() > 0) {
            speed += acceleration * diff;
            speedClamp();
            setSpeed(Math.min(getSpeed(), Const.SPEED_LIMIT));

            tPos.move(diff * getSpeed());
            final double rotation = getSpeed() * (Math.tan(tPos.getTheta() - theta)
                    / carModel.getWheelBase());

            theta += (rotation * diff) % Math.PI;
        }
    }

    public void setTheta(final int theta) {
        this.theta = theta;
    }

    /**
     * Sets the acceleration for the car. The acceleration will be clamped to be
     * between the max deceleration and max acceleration.
     *
     * @param acceleration
     */
    public void setAcc(final double acceleration) {
        this.acceleration = acceleration;
        accelerationClamp();
    }

    public void setSpeed(final double speed) {
        this.speed = speed;
        speedClamp();
    }

    public double getSpeed() {
        return speed;
    }

    public double getAcc() {
        return acceleration;
    }
    private static Object block = new Object();

    @Override
    public void draw(final Graphics2D g2d) {

        final Vector2D position = getPos().mult(Simulation.SCALE);

        // Default heading is to the right
        final AffineTransform affineTransform = new AffineTransform();
        affineTransform.translate(position.x, position.y);
        affineTransform.scale(Simulation.SCALE, Simulation.SCALE);
        g2d.setColor(Color.black);
        affineTransform.rotate(theta);
        final Shape shape = affineTransform.createTransformedShape(carModel.getShape());
        synchronized (block) {
            g2d.setColor(color);
            g2d.fill(shape);
        }

    }

    public Vector2D getPos() {
        return tPos.getPoint();
    }

    public void setTrackPosition(final TrackPosition tPos) {
        this.tPos = tPos;
    }

    public TrackPosition getTrackPosition() {
        return tPos;
    }

    /**
     * Check if the abstract car instances are equal, they are only equal if
     * they are exactly the same instance.
     */
    @Override
    public boolean equals(final Object obj) {
        return obj == this;
    }

    @Override
    public int hashCode() {
        return (int) carId;
    }

    /**
     * The distance the car will travel before halting with the given
     * deceleration.
     *
     * @param deceleration
     * @return
     */
    public double getBreakDistance() {
        return getBreakDistance(getSpeed(), Const.DECELERATION);
    }

    /**
     * Sets the color for the car chassi.
     *
     * @param color of the car.
     */
    public void setColor(final Color color) {
        this.color = color;
    }

    /**
     * The color of the car.
     *
     * @return The color of the car.
     */
    public Color getColor() {
        return color;
    }

    public double getWidth() {
        return carModel.getWidth();
    }

    /**
     * Check how much there is remaining on the track.
     *
     * @return
     */
    public double remainingOnTrack() {
        return tPos.remaining();
    }

    /**
     * Gives the maximum acceleration the vehicle is able to perform in the
     * given time diff.
     *
     * @return
     */
    public double getMaxAcceleration() {
        return carModel.getMaxAcc();
    }

    /**
     * Get the top speed of this car. Same as car.getType().getTopSpeed();
     *
     * @return
     */
    public double getTopSpeed() {
        return carModel.getTopSpeed();
    }

    /**
     * Gives the maximum retardation the vehicle is able to perform in the given
     * time diff.
     *
     * @return
     */
    public double getMaxDeceleration() {
        return carModel.getMaxRet();
    }

    /**
     * Get the car type.
     *
     * @return
     */
    public CarModel getType() {
        return carModel;
    }

    public double getTheta() {
        return theta;
    }

    /**
     * Get the model of the car.
     *
     * @return
     */
    public CarModel getModel() {
        return carModel;
    }

    /**
     * Get the length of the car. Equivalent to getModel().getLength();
     *
     * @return
     */
    public double getLength() {
        return getModel().getLength();
    }

    public Vector2D getRearPoint() {
        return carModel.getRearPoint(tPos.getPoint(), theta);
    }

    /**
     * Get the center point of the car.
     *
     * @return
     */
    public Vector2D getCenterPoint() {
        return carModel.getRearPoint(tPos.getPoint(), theta);
    }

    @Override
    public String toString() {
        return String.format("AbstractCar[id = %d,  model = %s]", carId, carModel.
                getName());
    }

    // private methods
    private void accelerationClamp() {
        acceleration = clamp(-carModel.getMaxRet(), carModel.getMaxAcc(),
                acceleration);
    }

    private void speedClamp() {
        speed = clamp(0, carModel.getTopSpeed(), speed);
    }

    private double clamp(final double lower, final double upper,
            final double value) {
        if (value > upper) {
            return upper;
        } else if (value < lower) {
            return lower;
        }
        return value;
    }

    // public static methods
    /**
     * The distance a car would travel before stopping with given speed and
     * deceleration.
     *
     * @param speed
     * @param deceleration
     * @return
     */
    public static double getBreakDistance(final double speed, final double deceleration) {
        if (deceleration <= 0) {
            throw new IllegalArgumentException("Deceleration must be positiv.");
        }
        return 0.5 * Math.pow(speed, 2.) / deceleration;
    }

    /**
     * The distance the car would travel with the given arguments.
     *
     * @param speed of the car.
     * @param acceleration of the car.
     * @param time for the function
     * @param maxSpeed for the calculation.
     * @return The distance traveled before stopping.
     */
    public static double distance(final double speed, final double acceleration,
            final double time, final double maxSpeed) {
        // Standard formula to calculate distance without considering max speed.
        // d = vt + at^2 / 2.
        if (acceleration != 0 && speed < maxSpeed) {
            // Calculate time until max speed.
            // v1 = v0 + at <=> t = (v1 - v0) / a
            double time2max;
            time2max = (maxSpeed - speed) / acceleration;
            if (time2max <= time) {
                // We will reach max speed.
                // Calculate the distance until max speed.
                // Then add the distance for the remaining time with max speed.
                double distance;
                distance = speed * time2max + acceleration / 2.0 * time2max
                        * time2max;
                // remaining time
                final double restTime = time - time2max;
                distance += maxSpeed * restTime;
                return distance;
            }
        }
        // We do not need to consider max speed, therefore use standard formula.
        return speed * time + acceleration / 2.0 * time * time;
    }
}
