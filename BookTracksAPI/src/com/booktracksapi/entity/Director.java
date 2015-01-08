package com.booktracksapi.entity;

import java.util.Comparator;

import javax.jdo.annotations.Extension;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class Director {

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
	
	public Director() {}
	public Director(Long id)
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

	public static Comparator<Director> getComparator(SortParameter... sortParameters) {
		return new DirectorComparator(sortParameters);
	}

    private static class DirectorComparator implements Comparator<Director> {
        private SortParameter[] parameters;

        private DirectorComparator(SortParameter[] parameters) {
            this.parameters = parameters;
        }

        public int compare(Director o1, Director o2) {
        	int comparison;
        	for (SortParameter parameter : parameters) {
        		switch (parameter) {
        		case NAME_ASCENDING:
        			comparison = o1.namelc.compareTo(o2.namelc);
        			if (comparison != 0) return comparison;
        			break;
        		case NAME_DESCENDING:
        			comparison = o2.namelc.compareTo(o1.namelc);
        			if (comparison != 0) return comparison;
        			break;
        		}
        	}
        	return 0;
        }
    }
}
