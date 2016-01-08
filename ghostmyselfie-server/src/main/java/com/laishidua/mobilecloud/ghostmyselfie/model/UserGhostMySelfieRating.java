package com.laishidua.mobilecloud.ghostmyselfie.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

// You might want to annotate this with Jpa annotations, add an id field,
// and store it in the database...
//
// There are also plenty of other solutions that do not require
// persisting instances of this...
@Entity
public class UserGhostMySelfieRating {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;	
	
	private long ghostMySelfieId;

	private double rating;

	private String user;

	public UserGhostMySelfieRating() {
	}

	public UserGhostMySelfieRating(long ghostMySelfieId, double rating, String user) {
		super();
		this.ghostMySelfieId = ghostMySelfieId;
		this.rating = rating;
		this.user = user;
	}

	public long getGhostMySelfieId() {
		return ghostMySelfieId;
	}

	public void setGhostMySelfieId(long ghostMySelfieId) {
		this.ghostMySelfieId = ghostMySelfieId;
	}

	public double getRating() {
		return rating;
	}

	public void setRating(double rating) {
		this.rating = rating;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

}
