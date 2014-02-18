/**
 * ProcessingUnit
 * 
 * v0.1
 * 
 * 02/16/2014
 * 
 * Processing unit class for fuzzy-octo-dangerzone.
 * The class to hold methods for a processing unit. This 
 * unit generates "heat" and needs to be "cooled" by one
 * or more fans.
 * 
 * @author epollitt
 *
 */

public class ProcessingUnit {

	private String id;
	private int temperature;	// In Celcius
	
	public ProcessingUnit(String id) {
		this.id = id;
		
		// TODO: Set to 30 temperature, but select random 35-45 future
		this.temperature = 30;
	}
	
	public String getId() {
		return this.id;
	}
	
	public int getTemperature() {
		// TODO: Random increase or decrease to temperature
		
		return this.temperature;
	}
	
	public double getHeatingFactor() {
		return 0.1;
	}
}
