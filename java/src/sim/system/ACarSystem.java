package sim.system;

import java.util.Iterator;

import sim.Const;
import sim.EntityDb;
import sim.PerfDb;
import sim.Simulation;

import car.AbstractCar;
import car.AutonomousCar;
import car.range.RangeData;
import java.awt.Color;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ACarSystem implements SimulationSystem {

    private final Simulation sim;

    public ACarSystem(Simulation sim) {
        this.sim = sim;
    }

    @Override
    public void tick(double diff) {
        ACarSystem.diff = diff;
        updateRange();
        handleCar();
        Iterator<AutonomousCar> it = EntityDb.getCars().iterator();
        it = EntityDb.getCars().iterator();
        while (it.hasNext()) {
            AutonomousCar car = it.next();
            if (car.isFinished()) {
                PerfDb.addData(sim.getElapsedTime(), car.getTravelData());
                it.remove();
            }

        }
    }

    private void updateRange() {
        Thread threads[] = new Thread[Const.THREAD_COUNT];
        BlockingQueue<AutonomousCar> queue = new LinkedBlockingQueue<>();
        queue.addAll(EntityDb.getCars());
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new Thread() {
                public void run() {
                    AutonomousCar car;
                    while ((car = queue.poll()) != null) {
                        car.updateRangeData();
                    }
                }
            };
            threads[i].start();
        }

        for (int i = 0; i < threads.length; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException ex) {
                Logger.getLogger(ACarSystem.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void handleCar() {
        Thread threads[] = new Thread[Const.THREAD_COUNT];
        BlockingQueue<AutonomousCar> queue = new LinkedBlockingQueue<>();
        queue.addAll(EntityDb.getCars());
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new Thread() {
                public void run() {
                    AutonomousCar car;
                    while ((car = queue.poll()) != null) {
                        carLogic(car);
                    }
                }
            };
            threads[i].start();
        }

        for (int i = 0; i < threads.length; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException ex) {
                Logger.getLogger(ACarSystem.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private static double diff = 0;

    private static void carLogic(AutonomousCar car) {
        if (!car.isAutonomous()) {
            car.setAutonomous(true);
        } else {
            car.setColor(Color.BLUE);
            RangeData rangeData;
            AbstractCar inFront;
            double dist;
            double carBreakDistance;
            double otherBreakDistance;
            rangeData = car.getRangeData();

            if (rangeData.getCar() == null) {
                car.setAcc(Const.ACCELERATION);
            } else {
                inFront = rangeData.getCar();
                dist = rangeData.distance() - Const.COLUMN_DISTANCE;
                otherBreakDistance = inFront.getBreakDistance();
                carBreakDistance = car.getBreakDistance();
                if (carBreakDistance > dist + otherBreakDistance) {
                    car.setAcc(-Const.DECELERATION);
                    car.setColor(Color.CYAN);
                } else if (carBreakDistance < dist + otherBreakDistance) {
                    car.setAcc(Const.ACCELERATION);
                } else {
                    car.setAcc(0);
                }

            }
        }
        car.setRangeData(null);
        car.tick(diff);
        if (car.getSpeed() < 0.01) {
            car.setColor(Color.MAGENTA);
        }
    }

}
