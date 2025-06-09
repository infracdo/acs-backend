package com.acs_tr069.test_tr069.Entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class auto_complete { // same with zeep ver, will retain
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)

    private int id;
    private String device_model;
    private String command;
    private byte[] suggestion_list;

    public int get_id(){
        return id;
    }
    public String get_device_model(){
        return device_model;
    }
    public String get_command(){
        return command;
    }
    public byte[] get_suggestion_list(){
        return suggestion_list;
    }

    public void set_id(int id){
        this.id = id;
    }
    public void set_device_model(String device_model){
        this.device_model = device_model;
    }
    public void set_command(String command){
        this.command = command;
    }
    public void set_suggestion_list(byte[] suggestion_list){
        this.suggestion_list = suggestion_list;
    }
}
