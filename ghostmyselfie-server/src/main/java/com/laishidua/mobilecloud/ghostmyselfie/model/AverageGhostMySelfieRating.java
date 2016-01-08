package com.laishidua.mobilecloud.ghostmyselfie.model;

public class AverageGhostMySelfieRating {

	private final double rating;

	private final long ghostMySelfieId;

	private final int totalRatings;

	public AverageGhostMySelfieRating(double rating, long ghostMySelfieId, int totalRatings) {
		super();
		this.rating = rating;
		this.ghostMySelfieId = ghostMySelfieId;
		this.totalRatings = totalRatings;
	}

	public double getRating() {
		return rating;
	}

	public long getGhostMySelfieId() {
		return ghostMySelfieId;
	}

	public int getTotalRatings() {
		return totalRatings;
	}

}
