package com.acs_tr069.test_tr069.Entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class httprequestlog {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)

    private Long id;
    private String serial_num;
    private String cookie;
    private java.sql.Timestamp lastRequest;
    private String device_status;

    public Long get_Id(){
        return id;
    }
    public String get_SN(){
        return serial_num;
    }
    public String get_cookie(){
        return cookie;
    }
    public java.sql.Timestamp get_lastRequest(){
        return lastRequest;
    }
    public String get_device_status(){
        return cookie;
    }

    public void set_SN(String sn){
        this.serial_num = sn;
    }
    public void set_cookie(String cookie){
        this.cookie = cookie;
    }
    public void set_lastRequest(java.sql.Timestamp lastRequest){
        this.lastRequest = lastRequest;
    }
    public void set_device_status(String device_status){
        this.device_status = device_status;
    }
}
