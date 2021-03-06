package car.model;

import java.awt.Color;
import java.util.Collection;
import java.util.HashMap;

public final class CarModels {

    private CarModels() {
        throw new AssertionError();
    }

    /**
     * Static methods for cars.
     */
    private static final HashMap<String, CarModel> CAR_MODELS
            = new HashMap<>();

    // static fields
    /**
     * Register a new car model.
     *
     * @param carModel
     */
    private static void register(final CarModel carModel) {
        CAR_MODELS.put(carModel.getName(), carModel);
    }

    /**
     * Get all car types.
     *
     * @return
     */
    public static Collection<String> getNames() {
        return CAR_MODELS.keySet();
    }

    /**
     * Get a car model by the car model name.
     *
     * @param name
     * @return
     */
    public static CarModel getCarModel(final String name) {
        if (!CAR_MODELS.containsKey(name)) {
            throw new IllegalArgumentException("Car model " + name
                    + " does not exist.");
        }
        return CAR_MODELS.get(name);
    }

    // compile time
    // All cars need UNIQUE names!
    static {
        /*
		 * // Tesla Model S register(new CarModel("TeslaS", 4.970, 1.964,
		 * Color.cyan, 0.86, 3.95));
		 * 
		 * // Mitsubishi Space Wagon register(new CarModel("SpaceWagon", 4.680,
		 * 1.740, Color.white, 0.930, 3.565));
		 * 
		 * // Audi S5 register(new CarModel("AudiS5", 4.713, 1.854, Color.blue,
		 * .860, .860+2.811));
         */

        // Mazda 3
        register(new CarModel("Mazda3", // Car name
                4.415, // Car lenght (m)
                1.755, // Car width (m)
                Color.blue, // Car color
                0.93, // Car front wheel displacement
                3.57, // Car rear wheel displacement
                2.6, // Max acceleration (m/s)
                7, // Max retardation (m/s)
                51.4 // Speed limit (m/s)
        ));

    }
}
