package com.booktracksapi.entity;

import java.util.Date;

import javax.jdo.annotations.Extension;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class UserAccount {

    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	Long id;
    @Persistent
	private String name;
    @Persistent
	private String emailAddress;
    @Persistent
    @Extension(vendorName="datanucleus", key="gae.unindexed", value="true")
	private Date lastLoginOn;
    @Persistent
    @Extension(vendorName="datanucleus", key="gae.unindexed", value="true")
	private Date lastActive;
    @Persistent
    @Extension(vendorName="datanucleus", key="gae.unindexed", value="true")
	private boolean adminUser;
    @Persistent
	private String uniqueId;
    @Persistent
    @Extension(vendorName="datanucleus", key="gae.unindexed", value="true")
	private boolean ultimateUser;
	
	public UserAccount() {}
	public UserAccount(String loginId, Integer loginProvider) {
		this();
		this.setUniqueId(loginId + "-" + loginProvider);
		this.setName(loginId);
	}
	public UserAccount(Long id) {
		this();
		this.setId(id);
	}

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getEmailAddress() {
		return emailAddress;
	}
	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}
	public Date getLastLoginOn() {
		return lastLoginOn;
	}
	public void setLastLoginOn(Date lastLoginOn) {
		this.lastLoginOn = lastLoginOn;
	}
	public Date getLastActive() {
		return lastActive;
	}
	public void setLastActive(Date lastActive) {
		this.lastActive = lastActive;
	}
	public boolean getAdminUser() {
		return adminUser;
	}
	public void setAdminUser(boolean adminUser) {
		this.adminUser = adminUser;
	}
	public String getUniqueId() {
		return uniqueId;
	}
	public void setUniqueId(String uniqueId) {
		this.uniqueId = uniqueId;
	}
	public boolean getUltimateUser() {
		return ultimateUser;
	}
	public void setUltimateUser(boolean ultimateUser) {
		this.ultimateUser = ultimateUser;
	}
}
