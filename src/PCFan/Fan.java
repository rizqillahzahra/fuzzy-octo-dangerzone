/**
 * Fan
 * 
 * @version v0.1
 * 
 * 2/23/2014
 * 
 * Fan class for fuzzy-octo-dangerzone
 * 
 * Implements a fan that is able to be controlled via
 * some function calls.
 * 
 * @author Ethan Pollitt
 *
 */

package FuzzyOctoDangerzone;

public class Fan {

	private String id;
	private int cfm;		// Cubic Feet per minute fan can move at max RPM
	private int maxRPM;		
	private int currentRPM;
	private double effectFactor;	// (currently arbitrary) The effectiveness of the fan on cooling

	public Fan(String id, int cfm, int max) {
		this.id = id;
		this.cfm = cfm;
		this.maxRPM = max;
		this.effectFactor = 1.0;
	}
	
	public Fan(String id, int cfm, int max, double effectFactor) {
		this.id = id;
		this.cfm = cfm;
		this.maxRPM = max;
		
		// Validate input, truncate to: 0 < effectFactor < 1
		if(effectFactor > 1.0)
			this.effectFactor = 1.0;
		else if (effectFactor < 0)
			this.effectFactor = 0;
		else
			this.effectFactor = effectFactor;
	}
	
	public String getId() {
		return this.id;
	}
	
	public int getFanUsePercentage() {
		return (int) (this.currentRPM / this.maxRPM) * 100;
	}
	
	public double getCoolingFactor() {
		// Calculate factor from top CFM and percent of use
		int percent = (int) (this.currentRPM / this.maxRPM);
		
		return (percent * this.cfm) * this.effectFactor;
	}
	
	public void setFanSpeed(double percent) {
		this.currentRPM = (int) (percent / 100.0) * this.maxRPM;
	}
	
	public void setFanSpeed(int rpm) {
		this.currentRPM = rpm;
	}
}
