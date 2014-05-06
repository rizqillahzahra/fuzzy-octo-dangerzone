package fuzzy;

import java.text.DecimalFormat;

public abstract class Controller implements Runnable {

	protected boolean run = true;		// Run flag, if set to false the thread will quit
	protected String controllerName;	// Name of controller. Must be set for logging
	
	protected int pollingRate;			// Seconds between polls
	protected int currentSignal = 0;	// Current controller signal
	protected int lastSignal = 0;		// Previous controller signal

	protected DecimalFormat df = new DecimalFormat("#.####");	// Format for double output
	enum NotificationType {
		NONE, WARN, ERR, SUCCESS, NOTIFY
	}
	
	public void run() {
		while(this.run) {
			try {
				this.process();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				break;
			}
			this.lastSignal = this.currentSignal;
		}
		
		this.log("exiting");
		return;
	}
	
	/**
	 * This method will cause the controller to stop if called.
	 */
	public void stop() {
		this.run = false;
		this.log(">>!! T CTL: STOP SIGNAL RECIEVED");
	}
	
	/**
	 * Allows the polling rate of the controller to be set.
	 * 
	 * @param rate number of seconds in between polls
	 */
	public void setPollingRate(int rate) {
		this.pollingRate = rate;
	}
	
	/**
	 * Retrieves the current controller signal.
	 * 
	 * @return currentSignal the current controller signal
	 */
	public int getCurrentSignal() {
		return this.currentSignal;
	}
	
	/**
	 * Default minimal logging function. Will print message with notification type of
	 * NONE.
	 * 
	 * @param s string to print
	 * @see #log(String, NotificationType)
	 */
	protected void log(String s) {
		this.log(s, NotificationType.NONE);
	}
	
	/**
	 * Full implementation of logging function. Includes a notification type
	 * to be passed in.
	 * 
	 * @param s string to print
	 * @param type type of notification
	 */
	protected void log(String s, NotificationType type) {
		String prefix;
		if(type == NotificationType.WARN) {
			prefix = "=**=";
		} else if(type == NotificationType.ERR) {
			prefix = "!!  ";
		} else if(type == NotificationType.SUCCESS) {
			prefix = "##  ";
		} else if(type == NotificationType.NOTIFY) {
			prefix = "@@  ";
		} else {
			prefix = ">>";
		}
		System.out.println(prefix + " |" + this.controllerName + "| " + s);			
	}
	
	/**
	 * Main method to implement. Need to implement this method to make the
	 * controller do anything. This method will be called through the 
	 * {@link #run()} method until {@link #stop()} is called.
	 * @throws InterruptedException 
	 */
	protected abstract void process() throws InterruptedException;
}
