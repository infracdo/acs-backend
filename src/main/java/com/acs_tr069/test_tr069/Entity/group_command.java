package com.acs_tr069.test_tr069.Entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class group_command {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String command;
    private String description;
    private String model;
    private String parent;

    public Long getId(){
        return id;
    }
    public String getcommand(){
        return command;
    }
    public String getdescription(){
        return description;
    }
    public String getmodel(){
        return model;
    }
    public String getparent(){
        return parent;
    }

    public void setmodel(String model){
        this.model = model;
    }
    public void setparent(String parent){
        this.parent = parent;
    }
    public void setcommand(String command){
        this.command = command;
    }
    public void setdescription(String description){
        this.description = description;
    }
    public group_command() {
	}
    public group_command(String model, String description, String parent,String command) {
		this.model = model;
		this.description = description;
		this.parent = parent;
		this.command = command;
	}
}