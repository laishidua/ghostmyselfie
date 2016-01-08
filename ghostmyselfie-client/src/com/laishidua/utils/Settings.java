package com.laishidua.utils;

public class Settings {

	public boolean isGhost() {
		return ghost;
	}

	public void setGhost(boolean ghost) {
		this.ghost = ghost;
	}

	public boolean isGray() {
		return gray;
	}

	public void setGray(boolean gray) {
		this.gray = gray;
	}

	public boolean isBlur() {
		return blur;
	}

	public void setBlur(boolean blur) {
		this.blur = blur;
	}

	public boolean isDark() {
		return dark;
	}

	public void setDark(boolean dark) {
		this.dark = dark;
	}
	
	public boolean isReminder() {
		return reminder;
	}

	public void setReminder(boolean reminder) {
		this.reminder = reminder;
	}	
	
	public int getReminderHour() {
		return reminderHour;
	}

	public void setReminderHour(int reminderHour) {
		this.reminderHour = reminderHour;
	}

	public int getReminderMinute() {
		return reminderMinute;
	}

	public void setReminderMinute(int reminderMinute) {
		this.reminderMinute = reminderMinute;
	}	

	private boolean ghost;
	private boolean gray;
	private boolean blur;
	private boolean dark;
	private boolean reminder;
	private int reminderHour;
	private int reminderMinute;

	public Settings() {
		this.ghost = false;
		this.gray = false;
		this.blur = false;
		this.dark = false;
		this.reminder = false;
		this.reminderHour = 0;
		this.reminderMinute = 0;
	}
	
	
	
}
