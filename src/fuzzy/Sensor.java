package fuzzy;

public class Sensor {
	private Room room;
	
	public Sensor(Room room) {
		this.room = room;
	}
	
	public double getRoomTemp() {
		return this.room.temperature;
	}
}
