/**
 * Fan class for fuzzy-octo-dangerzone
 * 
 * Implements a fan that is able to be controlled via
 * some function calls.
 * 
 * @author epollitt
 *
 */

public class Fan {

	private String id;
	private int cfm;		// Cubic Feet per minute fan can move at max RPM
	private int maxRPM;		
	private int currentRPM;
	
	public Fan(String id, int cfm, int max) {
		this.id = id;
		this.cfm = cfm;
		this.maxRPM = max;
	}
	
	public int fanUsePercentage() {
		return (int) (this.currentRPM / this.maxRPM) * 100;
	}
	
	public double getCoolingFactor() {
		// TODO: Calculate factor from top CFM and percent of use
		int percent = (int) (this.currentRPM / this.maxRPM);
		
		return percent * this.cfm;
	}
	
	
}
