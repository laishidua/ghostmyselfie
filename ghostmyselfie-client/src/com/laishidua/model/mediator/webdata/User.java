package com.laishidua.model.mediator.webdata;

public class User {
	
	private String email;
	private String password;
	private String username;
	
	public User() {
		super();
	}

	public static User create(String username, String password,
			String email) {
		return new User(username, password, email);
	}

	private User(String username, String password, String email) {
		this.username = username;
		this.password = password;
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public String getUsername() {
		return username;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setUsername(String username) {
		this.username = username;
	}
	
}