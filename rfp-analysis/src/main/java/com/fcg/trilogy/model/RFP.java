package com.fcg.trilogy.model;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
public class RFP {
	private Long id;
	private Date date;
	private String projectId;
	private String projectName;
	private String owner;
	private String serviceType;
	private String duration;

	public RFP() {
		// empty
	}

	public RFP(Long id, Date date, String projectId, String projectName,
			String owner, String serviceType, String duration) {
		super();
		this.id = id;
		this.date = date;
		this.projectId = projectId;
		this.projectName = projectName;
		this.owner = owner;
		this.serviceType = serviceType;
		this.duration = duration;
	}

	@Id
	@GeneratedValue
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Temporal(TemporalType.DATE)
	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getProjectId() {
		return projectId;
	}

	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public String getServiceType() {
		return serviceType;
	}

	public void setServiceType(String serviceType) {
		this.serviceType = serviceType;
	}

	public String getDuration() {
		return duration;
	}

	public void setDuration(String duration) {
		this.duration = duration;
	}

}
