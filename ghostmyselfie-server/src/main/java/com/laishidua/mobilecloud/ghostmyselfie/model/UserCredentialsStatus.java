package com.laishidua.mobilecloud.ghostmyselfie.model;

import java.io.Serializable;

public class UserCredentialsStatus implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public enum UserCredentialsState {
		BOTH_AVAILABLE, USERNAME_AVAILABLE, EMAIL_AVAILABLE, NONE_AVAILABLE, USERNAME_NOT_AVAILABLE
	}

	private UserCredentialsState state;

	public UserCredentialsStatus(UserCredentialsState state) {
		super();
		this.state = state;
	}

	public UserCredentialsState getState() {
		return state;
	}

	public void setState(UserCredentialsState state) {
		this.state = state;
	}

}
