package car.model;

import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;

import util.CollisionBox;

import math.Vector2D;

public class CarModel {
    // private fields

    /**
     * The name of this car model.
     */
    private final String name;
    /**
     * The length and the width of the chassi
     */
    private final double length, width;
    /**
     * The shape for this car model in meters. Centered at the car front.
     * Heading in 0 theta.
     */
    private final Shape carShape;
    private final CollisionBox collisionBox;

    /**
     * Color of this model of car.
     */
    private final Color modelColor;

    /**
     * The distance from the front to the front axel and the rear axel.
     */
    private final double frontAxleDisp;
    private final double rearAxleDisp;

    private final double maxAcceleration;
    private final double maxDeceleration;
    private final double topSpeed;

    // constructor
    /**
     *
     * @param name the name of the car type
     * @param length the length of the car type
     * @param width the width of the car type
     * @param color the color of the car type
     */
    CarModel(final String name, final double length, final double width, final Color color,
            final double frontAxleDisp, final double rearAxleDisp,
            final double maxAcceleration, final double maxRetardation, final double topSpeed) {
        this.topSpeed = topSpeed;
        this.maxAcceleration = maxAcceleration;
        this.maxDeceleration = maxRetardation;
        this.frontAxleDisp = frontAxleDisp;
        this.rearAxleDisp = rearAxleDisp;
        this.modelColor = color;
        this.name = name;
        this.length = length;
        this.width = width;
        carShape = new Rectangle2D.Double(-length + frontAxleDisp,
                -width / 2, length, width);
        collisionBox = new CollisionBox((Rectangle2D) carShape);
    }

    // public methods
    /**
     * Get the name of the car type.
     *
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * Get the length of the car type.
     *
     * @return
     */
    public double getLength() {
        return length;
    }

    /**
     * Get the width of the car type.
     *
     * @return
     */
    public double getWidth() {
        return width;
    }

    /**
     * Get the distance between front wheel axel and rear wheel axel.
     *
     * @return
     */
    public double getWheelBase() {
        return rearAxleDisp - frontAxleDisp;
    }

    /**
     * Get the shape of the car type.
     *
     * @return
     */
    public Shape getShape() {
        return carShape;
    }

    /**
     * Get the color of the car type.
     *
     * @return the color of the car type.
     */
    public Color getColor() {
        return modelColor;
    }

    @Override
    public String toString() {
        return "CarType[" + getName() + "]";
    }

    /**
     * Get the distance from the front to the front axle.
     *
     * @return
     */
    public double getFrontAxleDisplacement() {
        return frontAxleDisp;
    }

    /**
     * Get the distance from the front to the rear axle.
     *
     * @return
     */
    public double getRearAxleDisplacement() {
        return rearAxleDisp;
    }

    /**
     * Get the maximum acceleration.
     *
     * @return
     */
    public double getMaxAcc() {
        return maxAcceleration;
    }

    /**
     * Get the maximum retardation.
     *
     * @return
     */
    public double getMaxRet() {
        return maxDeceleration;
    }

    /**
     * Get the top speed.
     *
     * @return
     */
    public double getTopSpeed() {
        return topSpeed;
    }

    /**
     * @return the collisionBox
     */
    public CollisionBox getCollisionBox() {
        return collisionBox;
    }

    /**
     * Gets the center point of the car.
     *
     * @param pos the center point of the car
     * @param heading the heading of the car
     * @return rear point of the car
     */
    public Vector2D getCenterPoint(final Vector2D pos, final double heading) {
        return getCarPoint(pos, heading, length / 2 - frontAxleDisp);
    }

    public Vector2D getFrontPoint(final Vector2D pos, final double heading) {
        return getCarPoint(pos, heading, -frontAxleDisp);
    }

    public Vector2D getRearPoint(final Vector2D pos, final double heading) {
        return getCarPoint(pos, heading, length - frontAxleDisp);
    }

    public Vector2D getFrontAxelPoint(final Vector2D pos, final double heading) {
        return getCarPoint(pos, heading, 0);
    }

    public Vector2D getRearAxelPoint(final Vector2D pos, final double heading) {
        return getCarPoint(pos, heading, rearAxleDisp
                - frontAxleDisp);
    }

    private Vector2D getCarPoint(final Vector2D pos, final double heading,
            final double displacement) {
        final double xPos = pos.getX() - Math.cos(heading) * displacement;
        final double yPos = pos.getY() - Math.sin(heading) * displacement;
        return new Vector2D(xPos, yPos);
    }

}
