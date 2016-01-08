package com.laishidua.mobilecloud.ghostmyselfie.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Objects;

/**
 * A simple object to represent a ghostMySelfie and its URL for viewing.
 * 
 * You must annotate this object to make it a JPA entity.
 * 
 * 
 * Feel free to modify this with whatever other metadata that you want, such as
 * the
 * 
 * 
 * @author jules, mitchell
 */
@Entity
public class GhostMySelfie {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	private String title;
	private String location;
	private String contentType;

	// These fields allow vote operations without having
	// to scan the whole collection every time.
	private long votes;
	private long totalVote;
	private double averageVote;

	//Preventing serialization but not deserialization
	@JsonIgnore
	@ElementCollection
    private List<String> filters = new ArrayList<String>();

	// Collection of votes per ghostmyselfie, user being the key
	// Since we're allowed to use alternative solutions to
	// the OneToMany canonical one, here's my go, transcending
	// on real case considerations like performance.
	@JsonIgnore
	@ElementCollection
	private Map<String, Long> ratings = new ConcurrentHashMap<String, Long>();
	// We don't want to bother unmarshalling or marshalling
	// any owner data in the JSON. Why? We definitely don't
	// want the client trying to tell us who the owner is.
	// We also might want to keep the owner secret.
	@JsonIgnore
	private String owner;

	public GhostMySelfie() {
	}

	public GhostMySelfie(String owner, String title,
			long likes, Set<String> likedBy) {
		super();
		this.owner = owner;
		this.title = title;
	}	
	
	@JsonIgnore
	public List<String> getFilters() {
		return filters;
	}

	@JsonProperty
	public void setFilters(List<String> filters) {
		this.filters = filters;
	}	

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public long getId() {
		return id;
	}

	public double getAverageVote() {
		if (totalVote != 0)
			return ((double) totalVote / votes);
		else
			return 0;
	}

	public long getVotes() {
		return votes;
	}

	public void setVotes(long votes) {
		this.votes = votes;
	}

	public long getTotalVote() {
		return totalVote;
	}

	public void setTotalVote(long totalVote) {
		this.totalVote = totalVote;
	}


	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public Map<String, Long> getRatings() {
		return ratings;
	}
	
	/**
	 * Two GhostMySelfies will generate the same hashcode if they have exactly the same
	 * values for their name, url, and duration.
	 * 
	 */
	@Override
	public int hashCode() {
		// Google Guava provides great utilities for hashing
		return Objects.hashCode(title, owner);
	}

	/**
	 * Two GhostMySelfies are considered equal if they have exactly the same values for
	 * their name, url, and duration.
	 * 
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof GhostMySelfie) {
			GhostMySelfie other = (GhostMySelfie) obj;
			// Google Guava provides great utilities for equals too!
			return Objects.equal(title, other.title)
					&& Objects.equal(owner, other.owner);
		} else {
			return false;
		}
	}

}

