/**
 * ProcessingUnit
 * 
 * @version v0.1
 * 
 * 02/16/2014
 * 
 * Processing unit class for fuzzy-octo-dangerzone.
 * The class to hold methods for a processing unit. This 
 * unit generates "heat" and needs to be "cooled" by one
 * or more fans.
 * 
 * @author Ethan Pollitt
 *
 */

package FuzzyOctoDangerzone;

// Java imports
import java.util.Random;

// Package imports
import FuzzyOctoDangerzone.Constants;


public class ProcessingUnit {

	private String id;
	private double temperature;	// In Celcius
	private int timer = 0;
	private double heatFactor; 
	private double load = 10.0;	// Raise load to make unit generate more heat
	
	public ProcessingUnit(String id) {
		this.id = id;
		
		// TODO: Set to AmbientTemp + 7, but select random 5-10 for future
		this.temperature = Constants.AmbientTemp + 7.0;
	}
	
	public String getId() {
		return this.id;
	}
	
	public double getTemperature() {
		return this.temperature;
	}
	
	public void setTemperature(double temp) {
		this.temperature = temp;
	}
	
	public double getLoad() {
		return this.load;
	}
	
	public void setLoad(double newLoad) {
		this.load = newLoad;
	}
	
	public double getHeatingFactor() {
		// TODO: Random increase or decrease to heating heatFactor
		
		if(this.timer < 1) {
			// Reset timer
			Random rand = new Random();

		    // nextInt is normally exclusive of the top value,
		    // so add 1 to make it inclusive
			this.timer = rand.nextInt((30 - 600) + 1) + 30;
			
			// and regenerate heatFactor
			this.heatFactor = rand.nextInt((0 - 10) + 1) / load;
			if(rand.nextInt((0 - 10) + 1) > 5) {
				this.heatFactor = -this.heatFactor;
			}
		} else {
			// decrease timer
			this.timer--;
		}
		
		return this.heatFactor;
	}
}
