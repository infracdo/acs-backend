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
@Table(name = "device") // superior to zeep ver, will retain
public class Device {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "device_name")
    private String deviceName;
    
    @Column(name = "mac_address")
    private String macAddress; 

    @Column(name = "serial_number")
    private String serialNumber; 

    private String location;
    private String parent;

    @Column(name = "date_created")
    private String dateCreated;

    @Column(name = "date_modified")
    private String dateModified;

    @Column(name = "wan_ip")
    private String wanIp;

    @Column(name = "date_offline")
    private String dateOffline;

    private Boolean activated;
    private String status;
    private String model;

    @Column(name = "device_type")
    private String deviceType;

    @Column(name = "second_wan_mac")
    private String secondWanMac;
    
    public Device() {
    }
    
    public Device(String deviceName, String macAddress, String serialNumber, String location, String parent, String dateCreated, String dateModified , String dateOffline, String status, String model, String deviceType) {
        this.deviceName = deviceName;
        this.macAddress = macAddress;
        this.serialNumber = serialNumber;
        this.location = location;
        this.parent = parent;
        this.dateCreated = dateCreated;
        this.dateModified = dateModified;
        this.dateOffline = dateOffline;
        this.status = status;
        this.model = model;
        this.deviceType = deviceType;
    }
}
