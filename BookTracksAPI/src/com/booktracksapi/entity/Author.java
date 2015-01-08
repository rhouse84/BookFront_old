package com.booktracksapi.entity;

import java.util.Comparator;

import javax.jdo.annotations.Extension;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class Author {

    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	Long id;
    @Persistent
    @Extension(vendorName="datanucleus", key="gae.unindexed", value="true")
    private String name;
    @Persistent
	private String namelc;
    @Persistent
	private Long userId;
	
	public Author() {}
	public Author(Long id)
	{
		this.id = id;
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
	public String getNameLc() {
		return namelc;
	}
	public void setNameLc(String namelc) {
		this.namelc = namelc;
	}
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public enum SortParameter {
		NAME_ASCENDING,
		NAME_DESCENDING
	}

	public static Comparator<Author> getComparator(SortParameter... sortParameters) {
		return new AuthorComparator(sortParameters);
	}

    private static class AuthorComparator implements Comparator<Author> {
        private SortParameter[] parameters;

        private AuthorComparator(SortParameter[] parameters) {
            this.parameters = parameters;
        }

        public int compare(Author o1, Author o2) {
        	int comparison;
        	for (SortParameter parameter : parameters) {
        		switch (parameter) {
        		case NAME_ASCENDING:
        			comparison = o1.name.toLowerCase().compareTo(o2.name.toLowerCase());
        			if (comparison != 0) return comparison;
        			break;
        		case NAME_DESCENDING:
        			comparison = o2.name.toLowerCase().compareTo(o1.name.toLowerCase());
        			if (comparison != 0) return comparison;
        			break;
        		}
        	}
        	return 0;
        }
    }
}
