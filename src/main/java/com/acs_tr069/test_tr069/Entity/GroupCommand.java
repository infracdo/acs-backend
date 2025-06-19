package com.acs_tr069.test_tr069.Entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;

@Data
@Table(name = "group_command")
@Entity 
public class GroupCommand { // same with zeep ver, will retain

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String command;
    private String description;
    private String model;
    private String parent;

    public GroupCommand() {
	}
    
    public GroupCommand(String model, String description, String parent,String command) {
		this.model = model;
		this.description = description;
		this.parent = parent;
		this.command = command;
	}
}