package com.acs_tr069.test_tr069.Entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "auto_complete")
public class AutoComplete { 

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Column(name = "device_model")
    private String deviceModel;

    private String command;

    @Column(name = "suggestion_list")
    private byte[] suggestionList;

}
