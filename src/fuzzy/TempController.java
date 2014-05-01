package fuzzy;

public class TempController implements Runnable {
 
	private double coolingFactor = .25;	// Percentage temp change of a generic "room" system this can complete in one time cycle
	private int pollingRate = 1;		// Seconds for polling time
	private int pollingDuration = 300;	// Seconds to run test
	private int inputSignal;
	private Room room;
	
	public TempController(Room room) {
		this.room = room;
	}
	
	public void run() {
		int timePassed = 0;
		while(timePassed < this.pollingDuration) {
			try {
				Thread.sleep(this.pollingRate * 1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return;
			}
			
			double newTemp = this.room.temperature;
			if(this.inputSignal == -5) {
				newTemp = (this.room.temperature * (1 - this.coolingFactor / 100));
			} else if(this.inputSignal == -3) {
				newTemp = (this.room.temperature * (1 - ((this.coolingFactor * .5) / 100)));
			} else if(this.inputSignal == 3) {
				newTemp = (this.room.temperature * (1 + (this.coolingFactor / 100)));
			} else if(this.inputSignal == 5) {
				newTemp = (this.room.temperature * (1 + ((this.coolingFactor * .5)  / 100)));				
			}
			
			System.out.println(">> T CTL: room changed by: " + Double.toString(newTemp - this.room.temperature) + ". New temp is " + newTemp);
			this.room.temperature = newTemp;
			timePassed += this.pollingRate;
		}
		
		System.out.println(">> T CTL: exiting");
	}
	
	public void setSignal(int signal) {
		this.inputSignal = signal;
		System.out.println(">> T CTL: recieved new signal!");
	}
	
	public double getRoomTemp() {
		return this.room.temperature;
	}
}
