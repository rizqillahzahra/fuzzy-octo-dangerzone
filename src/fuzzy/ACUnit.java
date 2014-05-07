package fuzzy;

public class ACUnit extends Controller  {
 
	private final double coolingFactor = .02;		// Absolute temperature change (in F) at 100% usage
	private double currentLoad = 1;					// Load of the system (determined by inputSignal) 0 <= l <= 1
	private SystemMode systemMode = SystemMode.OFF;	// -1 for cool, 0 for off, 1 for heat
	
	private Room room;
	
	private enum SystemMode {
		COOL, HEAT, OFF
	}
	
	public ACUnit(String controllerName, Room room) {
		this.room = room;
		this.pollingRate = 1;
		this.controllerName = controllerName;
	}
	
	public void process() throws InterruptedException {			
		double newTemp = this.room.temperature;
		if(this.systemMode == SystemMode.COOL) {
			newTemp = this.room.temperature - (this.coolingFactor * this.currentLoad);
		} else if(this.systemMode == SystemMode.HEAT) {
			newTemp = this.room.temperature + (this.coolingFactor * this.currentLoad);			
		}
		this.room.temperature = newTemp;
			
		Thread.sleep(this.pollingRate * 1000);
	}
	
	public void setSignal(double signal) {
		this.currentSignal = signal;
		if(this.currentSignal == 0) {
			this.systemMode = SystemMode.OFF;
		} else if(this.currentSignal < 0) { 
			this.systemMode = SystemMode.COOL;
		} else if(this.currentSignal > 0) {
			this.systemMode = SystemMode.HEAT;
		}
		
		if(Math.abs(this.currentSignal) == 5) {
			this.currentLoad = 1;
		} else if(Math.abs(this.currentSignal) == 3) {
			this.currentLoad = .66;
		} else if(Math.abs(this.currentSignal) == 1.5) {
			this.currentLoad = .33;
		}
		// TODO: Add more input levels here
		
		this.log("recieved new signal!", NotificationType.NOTIFY);
	}
}
