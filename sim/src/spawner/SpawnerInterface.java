package spawner;

public interface SpawnerInterface {
	public void tick(double diff);

	public void setOn(boolean isOn);
	
	public boolean isOn();
}
