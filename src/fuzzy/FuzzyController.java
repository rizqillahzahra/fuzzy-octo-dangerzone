package fuzzy;

public class FuzzyController implements Runnable {
	private double currentTemp;		// "Current" temperature
	private double prevTemp;			// Temperature at previous reading
	private int targetTemp;			// System target temperature	
	private int currentSignalOutput = 0;	// Current controller output
	private int pollingRate = 2;	// Time between input check in seconds
	private int pollingDuration = 300;	// Duration of test in seconds
	private TempController system = null;
	
	/**
	 * Default constructor for FuzzyController.
	 * 
	 * @param targetTemp integer target temperature for system
	 * @param system the room to monitor
	 */
	public FuzzyController(TempController system, int targetTemp) {
		this.targetTemp = targetTemp;
		this.system = system;
	}
	
	/**
	 * Overloaded constructor for FuzzyController. This method takes an
	 * override for the controller polling rate.
	 * 
	 * @param targetTemp integer target temperature for system
	 * @param system the room to monitor
	 * @param pollingTime time in seconds between input read
	 */
	public FuzzyController(TempController system, int targetTemp, int pollingRate) {
		this.targetTemp = targetTemp;
		this.system = system;
		this.pollingRate = pollingRate;
	}
	
	public void run() {
		int prevSignal = 0;
		int timePassed = 0;
		while(timePassed < this.pollingDuration) {
			try {
				Thread.sleep(this.pollingRate * 1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return;
			}
			
			int signal = calculateOutput();
			if(signal == prevSignal) {
				continue;
			}
			
			this.system.setSignal(signal);
			prevSignal = signal;
			this.currentSignalOutput = signal;
			
			System.out.println(">> FZ CTL: signal changed to " + Integer.toString(signal));
			System.out.println("CT: " + Double.toString(this.currentTemp) + "  PT: " + Double.toString(this.prevTemp));
			
			timePassed += this.pollingRate;
		}		
		
		System.out.println(">> FZ CTL: exiting");
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
		getInputs();	// Take reading of system
		
		String error = getError();
		String errorRC = getErrorRC();
		
		// Rule base goes below (TODO: Read rules from file!)
		if(error.matches("POS") && errorRC.matches("POS")) {
			// Its hot and getting hotter
			return -5;
		} else if(error.matches("POS") && errorRC.matches("NEG")) {
			// Its hot but cooling down
			return -3;
		} else if(error.matches("POS") && errorRC.matches("NC")) {
			// Its hot but not changing
			return -3;
		} else if(error.matches("NEG") && errorRC.matches("POS")) {
			// Its cold but warming up
			return 3;
		} else if(error.matches("NEG") && errorRC.matches("NC")) {
			// Its cold but not changing
			return 3;
		} else if(error.matches("NEG") && errorRC.matches("NEG")) {
			// Its cold and getting colder
			return 5;
		} else if(error.matches("NC") && errorRC.matches("NC")) {
			// It feels good and is staying good
			return 0;
		} else if(error.matches("NC") && errorRC.matches("NEG")) {
			// It feels good but getting colder
			return 3;			
		} else if(error.matches("NC") && errorRC.matches("POS")) {
			// It feels good but getting hotter
			return -3;			
		}
		return 0;
	}
	
	/**
	 * Reads current temperatures and returns the Fuzzy Linguistic value of the
	 * "error" input parameter.
	 * 
	 * POS - positive change
	 * NEG - Negative change
	 * NC  - No change
	 * 
	 * @return
	 */
	private String getError() {
		double err = this.targetTemp - this.currentTemp;		
		if(err > 1) {
			return "NEG";
		} else if (err < -1) {
			return "POS";
		}
		return "NC";
	}
	
	/**
	 * Reads current temperatures and returns the Fuzzy Linguistic value of the
	 * "error" input parameter.
	 * 
	 * POS - positive change
	 * NEG - Negative change
	 * NC  - No change
	 * 
	 * @return 
	 */
	private String getErrorRC() {
		// TODO: upgrade this to calculate actual rate of change over time (2+ historical values)
		double rateOfChange = 0;
		double tempChange = this.currentTemp - this.prevTemp;
		
		// Since the polling time is constant, we can simply divide by the
		// polling time
		rateOfChange = tempChange / this.pollingRate;
		if(rateOfChange > .25) {
			return "NEG";
		} else if (rateOfChange < -.25) {
			return "POS";
		} 		
		return "NC";
	}
	
	private void getInputs() {
		if(this.prevTemp == 0) {
			this.currentTemp = this.system.getRoomTemp();
			this.prevTemp = this.currentTemp;
		} else {
			this.prevTemp = (this.prevTemp + this.currentTemp) / 2;
			this.currentTemp = this.system.getRoomTemp();
		}		
	}
	
	public int getCurrentSignalOutput() {
		return this.currentSignalOutput;
	}
	
	public void setPollingRate(int rate) {
		this.pollingRate = rate;
	}
	
	public void setTargetTemp(int target) {
		this.targetTemp = target;
	}
}
