package fuzzy;

import java.text.DecimalFormat;

public abstract class Controller implements Runnable {

	protected boolean run = true;		// Run flag, if set to false the thread will quit
	protected String controllerName;	// Name of controller. Must be set for logging
	protected long startTime = System.currentTimeMillis();
	
	protected int pollingRate;			// Seconds between polls
	protected double currentSignal = 0;	// Current controller signal
	protected double lastSignal = 0;		// Previous controller signal

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
	public double getCurrentSignal() {
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
			prefix = ">>  ";
		}	
		System.out.println(prefix + " " + getRunningTimeString() + " |" + 
				this.controllerName + "| " + s);			
	}
	
	/**
	 * Calculates running time of process and returns a string in format of
	 * HH:MM:SS.
	 * 
	 * @return run time string formatted to HH:MM:SS
	 */
	public String getRunningTimeString() {
		long systimeSeconds = (System.currentTimeMillis() - startTime) / 1000;
		int hr = (int) (systimeSeconds / 3600);
		int rem = (int) (systimeSeconds % 3600);
		int mn = rem / 60;
		int sec = rem % 60;
		String hrStr = (hr < 10 ? "0" : "") + hr;
		String mnStr = (mn < 10 ? "0" : "") + mn;
		String secStr = (sec < 10 ? "0" : "") + sec;
		return hrStr + ":" + mnStr + ":" + secStr;
	}
	
	/**
	 * Return raw running time of process in milliseconds.
	 * 
	 * @return run time in raw Long format
	 */
	public Long getRunningTimeLong() {
		return (System.currentTimeMillis() - startTime);
	}
	
	/**
	 * Main method to implement. Need to implement this method to make the
	 * controller do anything. This method will be called through the 
	 * {@link #run()} method until {@link #stop()} is called.
	 * @throws InterruptedException 
	 */
	protected abstract void process() throws InterruptedException;
}
