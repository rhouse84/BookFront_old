package com.booktracksapi.entity;

import java.util.Comparator;
import java.util.Date;

import javax.jdo.annotations.Extension;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class Film {

    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	Long id;
    @Persistent
	private String title;
    @Persistent
	private Long directorId;
    @Persistent
	private Long userId;
    @Persistent
	private Long topicId;
    @Persistent
	private int year;
    @Persistent
	private byte rating;
    @Persistent
    @Extension(vendorName="datanucleus", key="gae.unindexed", value="true")
	private String notes;
    @Persistent
    @Extension(vendorName="datanucleus", key="gae.unindexed", value="true")
	private String stars;
    @Persistent
    @Extension(vendorName="datanucleus", key="gae.unindexed", value="true")
	private Date createDate;
    @Persistent
	private Date watchDate;
    @Persistent
    @Extension(vendorName="datanucleus", key="gae.unindexed", value="true")
	private String directorName;
    @Persistent
    @Extension(vendorName="datanucleus", key="gae.unindexed", value="true")
	private String topicDesc;
	
	public Film() {}
	public Film(Long id)
	{
		this.id = id;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public Long getDirectorId() {
		return directorId;
	}
	public void setDirectorId(Long directorId) {
		this.directorId = directorId;
	}
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public Long getTopicId() {
		return topicId;
	}
	public void setTopicId(Long topicId) {
		this.topicId = topicId;
	}
	public int getYear() {
		return year;
	}
	public void setYear(int year) {
		this.year = year;
	}
	public byte getRating() {
		return rating;
	}
	public void setRating(byte rating) {
		this.rating = rating;
	}
	public String getNotes() {
		return notes;
	}
	public void setNotes(String notes) {
		this.notes = notes;
	}
	public String getStars() {
		return stars;
	}
	public void setStars(String stars) {
		this.stars = stars;
	}
	public Date getCreateDate() {
		return createDate;
	}
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	public Date getWatchDate() {
		return watchDate;
	}
	public void setWatchDate(Date watchDate) {
		this.watchDate = watchDate;
	}
	public String getDirectorName() {
		return directorName;
	}
	public void setDirectorName(String directorName) {
		this.directorName = directorName;
	}
	public String getTopicDesc() {
		return topicDesc;
	}
	public void setTopicDesc(String topicDesc) {
		this.topicDesc = topicDesc;
	}
	public String getRatingDesc() {
		if (this.rating == 1) {
			return "Bad";
		} else if (this.rating == 2) {
			return "OK";
		} else if (this.rating == 3) {
			return "Good";
		} else {
			return "Great";
		}
	}

	public enum SortParameter {
		TITLE_ASCENDING,
		TITLE_DESCENDING,
		DIRECTOR_ASCENDING,
		DIRECTOR_DESCENDING,
		RATING_ASCENDING,
		RATING_DESCENDING,
		YEAR_ASCENDING,
		YEAR_DESCENDING,
		WATCH_ASCENDING,
		WATCH_DESCENDING
	}

	public static Comparator<Film> getComparator(SortParameter... sortParameters) {
		return new FilmComparator(sortParameters);
	}

    private static class FilmComparator implements Comparator<Film> {
        private SortParameter[] parameters;

        private FilmComparator(SortParameter[] parameters) {
            this.parameters = parameters;
        }

        public int compare(Film o1, Film o2) {
        	int comparison;
        	for (SortParameter parameter : parameters) {
        		switch (parameter) {
        		case TITLE_ASCENDING:
        			comparison = o1.title.compareTo(o2.title);
        			if (comparison != 0) return comparison;
        			break;
        		case TITLE_DESCENDING:
        			comparison = o2.title.compareTo(o1.title);
        			if (comparison != 0) return comparison;
        			break;
        		case DIRECTOR_ASCENDING:
        			comparison = o1.getDirectorName().toLowerCase().compareTo(o2.getDirectorName().toLowerCase());
        			if (comparison != 0) return comparison;
        			break;
        		case DIRECTOR_DESCENDING:
        			comparison = o2.getDirectorName().toLowerCase().compareTo(o1.getDirectorName().toLowerCase());
        			if (comparison != 0) return comparison;
        			break;
        		case RATING_ASCENDING:
        			comparison = o1.rating - o2.rating;
        			if (comparison != 0) return comparison;
        			break;
        		case RATING_DESCENDING:
        			comparison = o2.rating - o1.rating;
        			if (comparison != 0) return comparison;
        			break;
        		case YEAR_ASCENDING:
        			comparison = o1.year - o2.year;
        			if (comparison != 0) return comparison;
        			break;
        		case YEAR_DESCENDING:
        			comparison = o2.year - o1.year;
        			if (comparison != 0) return comparison;
        			break;
        		case WATCH_ASCENDING:
        			comparison = o1.watchDate.compareTo(o2.watchDate);
        			if (comparison != 0) return comparison;
        			break;
        		case WATCH_DESCENDING:
        			comparison = o2.watchDate.compareTo(o1.watchDate);
        			if (comparison != 0) return comparison;
        			break;
        		}
        	}
        	return 0;
        }
    }
}
