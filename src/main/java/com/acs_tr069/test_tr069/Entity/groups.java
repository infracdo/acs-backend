package com.acs_tr069.test_tr069.Entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Column;
import javax.persistence.Table;

@Entity
@Table(name = "\"groups\"")
public class groups { // superior to zeep ver, will retain
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

	@Column(name = "group_name")
    private String group_name;

	@Column(name = "location")
    private String location;

    @Column(name = "parent")
    private String parent;

	@Column(name = "child")
    private String child;

	@Column(name = "date_created")
    private String date_created;

	@Column(name = "date_modified")
    private String date_modified;

    public Long getId(){
        return id;
    }
    public String getgroup_name(){
        return group_name;
    }
    public String getlocation(){
        return location;
    }
    public String getparent(){
        return parent;
    }
    public String getchild(){
        return child;
    }
    public String getdate_created(){
        return date_created;
    }
    public String getdate_modified(){
        return date_modified;
    }

    public void setgroup_name(String group_name){
        this.group_name = group_name;
    }
    public void setlocation(String location){
        this.location = location;
    }
    public void setparent(String parent){
        this.parent = parent;
    }
    public void setchild(String child){
        this.child = child;
    }
    public void setdate_created(String date_created){
        this.date_created = date_created;
    }
    public void setdate_modified(String date_modified){
        this.date_modified = date_modified;
    }
    public groups() {
    }
    public groups(String group_name, String location, String parent, String child, String date_created, String date_modified) {
		this.group_name = group_name;
		this.location = location;
		this.parent = parent;
        this.child = child;
        this.date_created = date_created;
		this.date_modified = date_modified;
	}

}
