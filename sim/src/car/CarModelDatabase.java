package car;

import java.awt.Color;
import java.util.Collection;
import java.util.HashMap;

public class CarModelDatabase {
	private static HashMap<String, CarModel> carModels = new HashMap<String, CarModel>();

	/**
	 * To prevent instantiation.
	 */
	private CarModelDatabase() {
	}

	/**
	 * Register a new car type, or override an older car type.
	 * 
	 * @param carModel
	 */
	public static void register(CarModel carModel) {
		carModels.put(carModel.getName(), carModel);
	}

	/**
	 * Get all car types.
	 * 
	 * @return
	 */
	public static Collection<String> getCarModelNames() {
		return carModels.keySet();
	}

	/**
	 * Get a car by name.
	 * 
	 * @param name
	 * @return
	 */
	public static CarModel getByName(String name) {
		return carModels.get(name);
	}

	// All cars need UNIQUE names!
	static {
		register(new CarModel("TeslaS", 4.970, 1.964, Color.cyan, 0.86, 3.95)); // Tesla
																					// Model
																					// S
		register(new CarModel("SpaceWagon", 4.680, 1.740, Color.white, 0.930,
				3.565)); // Mitsubishi Space Wagon
		register(new CarModel("AudiS5", 4.713, 1.854,
				Color.blue, .860, .860+2.811)); // Audi S5
		register(new CarModel("Mazda3", 4.415, 1.755,
				Color.blue, 0.93, 0.93 + 2.640)); // Mazda 3
	}
}
