/* 
 **
 ** Copyright 2014, Jules White
 **
 ** 
 */
package com.laishidua.mobilecloud.ghostmyselfie.model;

public class GhostMySelfieStatus {

	public enum GhostMySelfieState {
		READY, PROCESSING
	}

	private GhostMySelfieState state;

	public GhostMySelfieStatus(GhostMySelfieState state) {
		super();
		this.state = state;
	}

	public GhostMySelfieState getState() {
		return state;
	}

	public void setState(GhostMySelfieState state) {
		this.state = state;
	}

}
