package com.googlecode.syncnotes2google.dao;

import java.util.Calendar;

import com.googlecode.syncnotes2google.Constants;

public class BaseRecur {

	private int frequency = Constants.FREQ_NONE;
	private int interval = 0;
	private int count = 0;
	private Calendar until = null;
	private Calendar[] rdate = null;

	public void setFrequency(int frequency) {
		this.frequency = frequency;
	}

	public int getFrequency() {
		return frequency;
	}

	public void setInterval(int interval) {
		this.interval = interval;
	}

	public int getInterval() {
		return interval;
	}

	public void setUntil(Calendar until) {
		this.until = until;
	}

	public Calendar getUntil() {
		return until;
	}

	public void setRdate(Calendar[] rdate) {
		this.rdate = rdate;
	}

	public Calendar[] getRdate() {
		return rdate;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

}
