package com.laishidua.model.mediator.webdata;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * This "Plain Ol' Java Object" (POJO) class represents meta-data of
 * interest downloaded in Json from the GhostMySelfie Service via the
 * GhostMySelfieServiceProxy.
 */
public class GhostMySelfie {
    /**
     * Various fields corresponding to data downloaded in Json from
     * the GhostMySelfie WebService.
     */
    private long id;
    private String title;
	private String location;
    private String contentType;
	private transient long votes;
	private transient long totalVote;
	private double averageVote;
    private transient long serverId;
    private List<String> filters = new ArrayList<String>();
	
    /**
     * No-op constructor
     */
    public GhostMySelfie() {
    }
    
    /**
     * Constructor that initializes title, duration, and contentType.
     */
    public GhostMySelfie(String title,
                 String contentType) {
        this.title = title;
        this.contentType = contentType;
    }

    /**
     * Constructor that initializes all the fields of interest.
     */
    public GhostMySelfie(long id,
                 String title,
                 String contentType) {
        this.id = id;
        this.title = title;
        this.contentType = contentType;
    }

    /*
     * Getters and setters to access GhostMySelfie.
     */

    /**
     * Get the Id of the GhostMySelfie.
     * 
     * @return id of video
     */
    public long getId() {
        return id;
    }

    /**
     * Get the GhostMySelfie by Id
     * 
     * @param id
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * Get the Title of GhostMySelfie.
     * 
     * @return title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Set the Title of GhostMySelfie.
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Get ContentType of GhostMySelfie.
     * 
     * @return contentType of GhostMySelfie.
     */
    public String getContentType() {
        return contentType;
    }

    /**
     * Set the ContentType of GhostMySelfie.
     */
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

	public double getAverageVote() {
		return averageVote;
	}

    public void setAverageVote(double averageVote) {
        this.averageVote = averageVote;
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


    /**
     * @return the textual representation of GhostMySelfie object.
     */
    @Override
    public String toString() {
        return "{" +
            "Id: "+ id + ", " +
            "ServerId: "+ serverId + ", " +
            "Title: "+ title + ", " +
            "ContentType: "+ contentType + ", " +
            "Votes: "+ getVotes() + ", " +
            "Average vote: "+ String.format("%.2f", getAverageVote()) +
            "}";
    }

    /** 
     * @return an Integer hash code for this object. 
     */
    @Override
    public int hashCode() {
        return Objects.hash(getTitle());
    }

    /**
     * @return Compares this GhostMySelfie instance with specified 
     *         GhostMySelfie and indicates if they are equal.
     */
    @Override
    public boolean equals(Object obj) {
        return (obj instanceof GhostMySelfie)
            && Objects.equals(getTitle(),
                              ((GhostMySelfie) obj).getTitle());
    }

	public long getServerId() {
		return serverId;
	}

	public void setServerId(long server_id) {
		this.serverId = server_id;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}
	
	public List<String> getFilters() {
		return filters;
	}

	public void setFilters(List<String> filters) {
		this.filters = filters;
	}		
	
}
