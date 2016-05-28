package sim;

import map.intersection.*;
import sim.system.*;

import java.awt.geom.AffineTransform;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.regex.Pattern;

import javax.swing.JFrame;

public class Simulation {

    // public static final fields
    public static final boolean SHOW_TRACKS = false;
    public static final boolean DEBUG = false;
    public static final boolean COLLISION = false;

    /**
     * @return the SCALE_TICK
     */
    public static double getSCALE_TICK() {
        return SCALE_TICK;
    }

    /**
     * @return the TICKS_PER_SECOND
     */
    public static int getTICKS_PER_SECOND() {
        return TICKS_PER_SECOND;
    }
    private boolean WEAT;
    public static final int X = 0, Y = 1;
    public static final int[] WINDOW_SIZE = {1100, 900};
    public static final int HUD_SIZE = WINDOW_SIZE[X] - WINDOW_SIZE[Y];
    public static final int SIMULATION_SIZE = WINDOW_SIZE[Y];
    public static final int FPS = 60;
    public static final double SCALE = WINDOW_SIZE[Y] / Intersection.getSize();
    public static final AffineTransform SCALER = AffineTransform.
            getScaleInstance(SCALE, SCALE);
    // 1 = normal speed, 2 = double speed etc.
    private static double SCALE_TICK = 10;
    private static int TICKS_PER_SECOND = (int) (getSCALE_TICK()
            / Const.TIME_STEP);
    // Time between printing data (seconds)
    public static final double PRINT_TIME = 10 * 60;
    // private fields
    private double elapsedTime;
    private final JFrame window;
    private final SimDisplay simDisp;
    private final SystemHandler systemHandler;
    private int lastFps;
    private int lastTps;
    private DynamicControl dscs;
    private WirelessEfficientAT weat;

    private static void scaleTick(final double scale) {
        SCALE_TICK = scale;
        TICKS_PER_SECOND = (int) (SCALE_TICK
                / Const.TIME_STEP);
    }

    // main
    public static void main(final String[] args) {
        System.out.println("Loading...");

        Simulation simulation;
        if (args.length != 0) {
            simulation = new Simulation(args[0]);
        } else {
            simulation = new Simulation(null);
        }
        simulation.run();
    }
    private double[] freq;
    private double[] maxPhaseTime;

    private void processArg(final String arg) {
        if (arg.trim().equals("")) {
            return;
        }

        final String argLow = arg.toLowerCase();
        System.out.println("Arg: " + arg);
        if (argLow.startsWith("ics:")) {
            final String ics = argLow.substring(4);
            switch (ics) {
                case "weat":
                    WEAT = true;
                    break;
                case "dc":
                    WEAT = false;
                    break;
                default:
                    throw new IllegalArgumentException("ICS must be WEAT or DC.");
            }
        } else if (Pattern.matches("[nswe]:(\\d+.?\\d*|.?\\d+)", argLow)) {
            final String prefix = argLow.substring(0, 1);
            final double num = Double.valueOf(argLow.substring(2));

            switch (prefix) {

                case "n":
                    freq[Const.NORTH] = num;
                    break;
                case "s":
                    freq[Const.SOUTH] = num;
                    break;
                case "w":
                    freq[Const.WEST] = num;
                    break;
                case "e":
                    freq[Const.EAST] = num;
                    break;
            }

        } else if (Pattern.matches("p[0-4]:(\\d+.?\\d*|.?\\d+)", argLow)) {
            final int prefix = Integer.valueOf(argLow.substring(1, 2));
            final double num = Double.valueOf(argLow.substring(3));
            maxPhaseTime[prefix] = num;
        } else if (Pattern.matches("speed:(\\d+.?\\d*|.?\\d+)", argLow)) {
            final double num = Double.valueOf(argLow.substring(6));
            scaleTick(num);
            System.out.println("Speed change");
        } else {
            throw new IllegalArgumentException(argLow);
        }
    }

    // Constructor
    public Simulation(String settings) {
        this.WEAT = true;
        window = new JFrame(
                "WEAT Project - Autonomous Vehicle Intersection Controller");
        systemHandler = new SystemHandler();
        simDisp = new SimDisplay(this);
        freq = new double[]{0.45, 0.45, 0.45, 0.45};
        maxPhaseTime = new double[]{8.5, 6.5, 8.5, 6.5, 1};
        if (settings != null) {
            File file = new File(settings);
            BufferedReader reader = null;
            try {
                reader = new BufferedReader(new FileReader(file));
                String text = null;
                while ((text = reader.readLine()) != null) {
                    processArg(text);
                }
            } catch (Exception e) {
                e.printStackTrace();
                System.exit(1);
            }
        }

        initFrame();
        initSystems();
    }

    // Initializer
    private void initFrame() {
        simDisp.setBounds(0, 0, WINDOW_SIZE[Y], WINDOW_SIZE[Y]);
        window.setLayout(null);
        window.add(simDisp);
        window.setSize(WINDOW_SIZE[X], WINDOW_SIZE[Y]);
        window.setLocationRelativeTo(null); // Centers window
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setVisible(true);
        window.setResizable(false);

    }

    private void initSystems() {
        systemHandler.addSystem(new CollisionSystem());
        if (isWEAT()) {
            weat = new WirelessEfficientAT();
            systemHandler.addSystem(weat);
        } else {
            dscs = new DynamicControl(maxPhaseTime);
            systemHandler.addSystem(dscs);
        }
        systemHandler.addSystem(new SpawnSystem(this, freq));
        systemHandler.addSystem(new ACarSystem(this));
    }

    public DynamicControl getDSCS() {
        return dscs;
    }

    // public methods.
    public void run() {
        System.out.print("\nStarting simulation.\n\n");

        long nextTime = System.nanoTime();
        final long delay = (long) 1e9 / FPS;
        long now;
        int fps = 0;
        int tps = 0;
        long fpsTime = nextTime;
        long tpsTime = 0;
        long tickTime = System.nanoTime();

        double printTimer = PRINT_TIME;

        boolean pause = false;
        while (elapsedTime < 60 * 60 * 3 + 60) {
            now = System.nanoTime();

            nextTime = now + delay / 2;
            simDisp.render();
            fps++;
            // ticking
            while (now - tickTime >= 1e9 / getTICKS_PER_SECOND() && !pause) {
                try {
                    systemHandler.tick(Const.TIME_STEP);
                } catch (Exception e) {
                    pause = true;
                    System.err.println();
                    System.err.println("Exception: " + e.toString());
                    e.printStackTrace();
                }
                tps++;
                elapsedTime += Const.TIME_STEP;
                tickTime += 1e9 / getTICKS_PER_SECOND();

                if (printTimer - elapsedTime <= 0) {
                    final PerfDb.PerformanceData data = PerfDb.compileData();
                    if (data != null) {
                        System.out.printf("%d\t%d\t%.2f\t%.2f\t%.2f\t%.2f\n",
                                (int) (elapsedTime / 60), data.sampleSize(), data.
                                getMSCD(),
                                data.getMCD(), data.getVariance(), data.maxCD());
                    }
                    printTimer += PRINT_TIME;
                }

            }

            // TPS
            if (tpsTime <= now) {
                lastTps = tps;
                tpsTime += 1e9;
                tps = 0;
            }
            // FPS
            if (fpsTime <= now) {
                fpsTime += 1e9;
                lastFps = fps;
                fps = 0;
            }
        }

        System.exit(0);
    }

    public double getElapsedTime() {
        return elapsedTime;
    }

    public int getFps() {
        return lastFps;
    }

    public int getTps() {
        return lastTps;
    }

    public WirelessEfficientAT getWEAT() {
        return weat;
    }

    /**
     * @return the WEAT
     */
    public boolean isWEAT() {
        return WEAT;
    }
}
