package fuzzy;

public class FuzzyThermostat extends Controller {
	private double currentTemp;			// "Current" temperature
	private double prevTemp;			// Temperature at previous reading
	private double avgTemp;				// Average room temperature
	private int targetTemp;				// System target temperature
	
	private ACUnit system = null;
	private Sensor sensor;
	
	private double errThresh = this.pollingRate * .05;
	private double rocThresh = this.pollingRate * .0015;
	
	/**
	 * Default constructor for FuzzyController.
	 * 
	 * @param targetTemp integer target temperature for system
	 * @param system the room to monitor
	 */
	public FuzzyThermostat(String controllerName, ACUnit system, Sensor sensor, int targetTemp) {
		this.targetTemp = targetTemp;
		this.system = system;
		this.pollingRate = 5;
		this.controllerName = controllerName;
		this.sensor = sensor;
	}
	
	public void process() throws InterruptedException {		
		if(this.prevTemp != 0)
			Thread.sleep(this.pollingRate * 1000);
		
		int signal = calculateOutput();
		this.log("CUR-T: " + this.df.format(this.currentTemp) + 
				"    PR-T: " + this.df.format(this.prevTemp) + 
				"    AVG-T: " + this.df.format(this.avgTemp) +
				"    ERR THRESH: " + this.df.format(this.errThresh) +
				"    ERRDOT THRESH: " + this.df.format(this.rocThresh) + "\n" +
				"   Temperature changed by: " + this.df.format(this.currentTemp - this.prevTemp));
		
		// Skip if no change in system
		if(signal == this.lastSignal) {
			return;
		}

		this.log("signal changed to " + Integer.toString(signal), NotificationType.NOTIFY);
		this.system.setSignal(signal);
		this.lastSignal = this.currentSignal;
		this.currentSignal = signal;
	}
	
	/**
	 * Outputs control signal to control heating/cooling of the system. This
	 * output mimics an analog output signal that would control an external
	 * cooling device or controller.
	 * <p>
	 * This controller is valid for 1 input only. Modifications for 2+ inputs
	 * will be needed. This has to do with the rule-based structure.
	 * TODO: Make rules import-able from external file (.dat or Java structure)
	 * 
	 * -5 - high cool
	 * -3 - cool
	 * 0  - no change
	 * +3 - heat
	 * +5 - high heat
	 * 
	 * @return change control signal of change in system (values above)
	 */
	public int calculateOutput() {
		updateThermostat();	// Take reading of system
		
		String change = getTempChange();
		String changeMomentum = getTempChangeMomentum();
		
		// Rule base goes below (TODO: Read rules from file!)
		if(change.matches("POS") && changeMomentum.matches("POS")) {
			// Its hot and getting hotter
			return -5;
		} else if(change.matches("POS") && changeMomentum.matches("NEG")) {
			// Its hot but cooling down
			return -3;
		} else if(change.matches("POS") && changeMomentum.matches("NC")) {
			// Its hot but not changing
			return -3;
		} else if(change.matches("NEG") && changeMomentum.matches("POS")) {
			// Its cold but warming up
			return 3;
		} else if(change.matches("NEG") && changeMomentum.matches("NC")) {
			// Its cold but not changing
			return 3;
		} else if(change.matches("NEG") && changeMomentum.matches("NEG")) {
			// Its cold and getting colder
			return 5;
		} else if(change.matches("NC") && changeMomentum.matches("NC")) {
			// It feels good and is staying good
			return 0;
		} else if(change.matches("NC") && changeMomentum.matches("NEG")) {
			// It feels good but getting colder
			return 3;			
		} else if(change.matches("NC") && changeMomentum.matches("POS")) {
			// It feels good but getting hotter
			return -3;			
		}
		return 0;
	}
	
	/**
	 * Reads current temperatures and returns the Fuzzy Linguistic value of the
	 * "error" input parameter. This parameter controls how close the Fuzzy
	 * engine will try and keep the temperature to the target temp.
	 * 
	 * POS - positive change
	 * NEG - Negative change
	 * NC  - No change
	 * 
	 * @return
	 */
	private String getTempChange() {
		double err = this.targetTemp - this.currentTemp;		
		if(err > this.errThresh) {
			return "NEG";
		} else if (err < -this.errThresh) {
			return "POS";
		}
		return "NC";
	}
	
	/**
	 * Reads current temperatures and returns the Fuzzy Linguistic value of the
	 * "error rate of change" input parameter. This correlates to the "momentum"
	 * of change. 
	 * 
	 * POS - positive change
	 * NEG - Negative change
	 * NC  - No change
	 * 
	 * @return 
	 */
	private String getTempChangeMomentum() {
		// TODO: upgrade this to calculate actual rate of change over time (2+ historical values)
		
		// Since the polling time is constant, we can simply divide by the
		// polling time
		double tempChange = this.currentTemp - this.avgTemp;
		double rateOfChange = tempChange / this.pollingRate;
		if(rateOfChange > this.rocThresh) {
			return "NEG";
		} else if (rateOfChange < -this.rocThresh) {
			return "POS";
		} 		
		return "NC";
	}
	
	/**
	 * Retrieves input from sensors and updates temperatures.
	 */
	private void updateThermostat() {
		if(this.prevTemp == 0) {
			this.currentTemp = this.sensor.getRoomTemp();
			this.prevTemp = this.currentTemp;
			this.avgTemp = this.currentTemp;
		} else {
			this.prevTemp = this.currentTemp;
			this.currentTemp = this.sensor.getRoomTemp();
			this.avgTemp = (this.avgTemp + this.currentTemp) / 2;
		}		
	}
	
	/**
	 * Setter for target temperature of the thermostat.
	 * 
	 * @param target target temperature
	 */
	public void setTargetTemp(int target) {
		this.targetTemp = target;
	}
}
