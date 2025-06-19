package com.acs_tr069.test_tr069.Entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Column;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import lombok.Data;

@Data
@Entity
@Table(name = "`groups`") // used backticks to avoid conflict with mysql reserved keyword
public class Groups { // superior to zeep ver, will retain
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

	@Column(name = "group_name")
    private String groupName;

    private String location;
    private String parent;
    private String child;

    @CreationTimestamp
	@Column(name = "date_created")
    private String dateCreated;

    @UpdateTimestamp
	@Column(name = "date_modified")
    private String dateModified;

    public Groups() {
    }
    public Groups(String groupName, String location, String parent, String child, String dateCreated, String dateModified) {
		this.groupName = groupName;
		this.location = location;
		this.parent = parent;
        this.child = child;
        this.dateCreated = dateCreated;
		this.dateModified = dateModified;
	}

}
