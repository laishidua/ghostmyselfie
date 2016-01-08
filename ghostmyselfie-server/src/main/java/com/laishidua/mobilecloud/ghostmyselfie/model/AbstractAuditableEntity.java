package com.laishidua.mobilecloud.ghostmyselfie.model;

import java.util.Date;

import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToOne;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class AbstractAuditableEntity {

	@JsonIgnore
	@CreatedDate
    private Date createdDate;

	@JsonIgnore
	@LastModifiedDate
    private Date lastModifiedDate;
    
	@JsonIgnore
    @CreatedBy
    @OneToOne
    private User createdBy;
    
	@JsonIgnore
    @LastModifiedBy
    @OneToOne
    @JsonBackReference(value="lastModifiedBy")
    private User lastModifiedBy;

	@JsonIgnore
	public Date getCreatedDate() {
		return createdDate;
	}

	@JsonProperty
	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	@JsonIgnore
	public Date getLastModifiedDate() {
		return lastModifiedDate;
	}

	@JsonProperty
	public void setLastModifiedDate(Date lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}

	@JsonIgnore
	public User getCreatedBy() {
		return createdBy;
	}

	@JsonProperty
	public void setCreatedBy(User createdBy) {
		this.createdBy = createdBy;
	}

	@JsonIgnore
	public User getLastModifiedBy() {
		return lastModifiedBy;
	}

	@JsonProperty
	public void setLastModifiedBy(User lastModifiedBy) {
		this.lastModifiedBy = lastModifiedBy;
	}
	
}