package com.acs_tr069.test_tr069.Entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class client_list {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)

    private Long id;
    private String serial_num;
    private String ip;
    private String macc;
    private String ssid;
    private String rssi;
    private String band;
    private String traffic;
    private String os;
    private String manufacturer;
    private String up;
    private String down;



    public Long getid(){
        return id;
    }
    public String getserial_num(){
        return serial_num;
    }
    public String getip(){
        return ip;
    }
    public String getmacc(){
        return macc;
    }
    public String getssid(){
        return ssid;
    }
    public String getrssi(){
        return rssi;
    }
    public String getband(){
        return band;
    }
    public String gettraffic(){
        return traffic;
    }
    public String getos(){
        return os;
    }
    public String getmanufacturer(){
        return manufacturer;
    }
    public String getup(){
        return up;
    }
    public String getdown(){
        return down;
    }



    public void setserial_num(String serial_num){
        this.serial_num = serial_num;
    }
    public void setip(String ip){
        this.ip = ip;
    }
    public void setmacc(String macc){
        this.macc = macc;
    }
    public void setssid(String ssid){
        this.ssid = ssid;
    }
    public void setrssi(String rssi){
        this.rssi = rssi;
    }
    public void setband(String band){
        this.band = band;
    }
    public void settraffic(String traffic){
        this.traffic = traffic;
    }
    public void setos(String os){
        this.os = os;
    }
    public void setmanufacturer(String manufacturer){
        this.manufacturer = manufacturer;
    }
    public void setup(String up){
        this.up = up;
    }
    public void setdown(String down){
        this.down = down;
    }
    

}
