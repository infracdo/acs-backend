package com.acs_tr069.test_tr069.Entity;

import javax.persistence.*;
import lombok.Data;

@Data
@Table(name = "devices")
@Entity 
public class devices { 
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String serialNum;
    private String model;
    private String manufacturer;
    private String oui;
    private String hardwareVer;
    private String rootFsVer;
    private String firmwareVer;
    private String apMode;
    private String macAddress;
    private String osType;
    private String hostName;
    private String maxUsers;
    private String ip;
    private String lastReboot;
    private String lastBoot;
    private String rootDataModel;
    private String webAuth;
    private String groupPath;
    private String udpConReqUrl;
    private String secondWanMac;
    private String conReqUrl;
    private Boolean cwmpCycleEnd;
    private String managementIp;
    private String publicIp;
    private String softwareVer;
    private String deviceAlias;
    private String ssids;
    private String memoryUsage;
    private String cpuUsage;
}