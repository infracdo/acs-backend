package com.acs_tr069.test_tr069.Entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class radio_info {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)

    private Long id;
    private String sn;
    private String uploadTime;
    private String radioIndex;
    private String channel;
    private String gatherTime;
    private String utilization;
    private String power;
    private String bandWidth;

    public Long get_Id(){
        return id;
    }
    public String getsn(){
        return sn;
    }
    public String getuploadTime(){
        return uploadTime;
    }
    public String getradioIndex(){
        return radioIndex;
    }
    public String getgatherTime(){
        return gatherTime;
    }
    public String getutilization(){
        return utilization;
    }
    public String getchannel(){
        return channel;
    }
    public String getpower(){
        return power;
    }
    public String getbandWidth(){
        return bandWidth;
    }

    
    public void setsn(String sn){
        this.sn = sn;
    }
    public void setuploadTime(String uploadTime){
        this.uploadTime = uploadTime;
    }
    public void setradioIndex(String radioIndex){
        this.radioIndex = radioIndex;
    }
    public void setchannel(String channel){
        this.channel = channel;
    }
    public void setgatherTime(String gatherTime){
        this.gatherTime = gatherTime;
    }
    public void setutilization(String utilization){
        this.utilization = utilization;
    }
    public void setpower(String power){
        this.power = power;
    }
    public void setbandWidth(String bandWidth){
        this.bandWidth = bandWidth;
    }
}
