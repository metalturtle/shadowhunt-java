package handlers.basic;

import util.GameInfo;

public class Timer {
	long start,duration;
	
	public void set(long duration) {
		this.duration = duration;
		this.start = GameInfo.get_time_millis();
	}
	
	
	public boolean check() {
		return GameInfo.get_time_millis() >= this.duration + this.start;
	}
	
	public void extend(long addval) {
		this.duration+=addval;
	}
	
	public long get_start() {
		return this.start;
	}
	public long get_duration() {
		return this.duration;
	}
	
	public long get_finish_time() {
		return start+duration;
	}
	
	public long get_remaining_duration() {
		return Math.max(0,(this.duration + this.start)-GameInfo.get_time_millis());
	}
}
