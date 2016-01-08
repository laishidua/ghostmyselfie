package com.laishidua.model.mediator.webdata;

public class AverageGhostMySelfieRating {

	private final double rating;

	private final long ghostmyselfieId;

	private final int totalRatings;

	public AverageGhostMySelfieRating(double rating, long ghostmyselfieId, int totalRatings) {
		super();
		this.rating = rating;
		this.ghostmyselfieId = ghostmyselfieId;
		this.totalRatings = totalRatings;
	}

	public double getRating() {
		return rating;
	}

	public long getGhostMySelfieId() {
		return ghostmyselfieId;
	}

	public int getTotalRatings() {
		return totalRatings;
	}

}
