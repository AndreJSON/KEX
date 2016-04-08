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
		register(new CarModel("Tesla S", 196.0 * 0.0254, 77.3 * 0.0254,
				Color.cyan));
		register(new CarModel("Space Wagon", 4.6, 1.775, Color.white));
		register(new CarModel("Tesla X", 196.0 * 0.0254, 77.3 * 0.0254,
				Color.blue));
		register(new CarModel("Mazda3", 180.3 * 0.0254, 70.7 * 0.0254,
				Color.blue));
	}
}
