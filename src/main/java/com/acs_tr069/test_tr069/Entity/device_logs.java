package com.acs_tr069.test_tr069.Entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class device_logs {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)

    private Long id;
    private String serial_num;
    private String update_time;
    private String ontime;
    private String offtime;
    private String reason;
    private String type;

    public Long getid(){
        return id;
    }
    public String getserial_num(){
        return serial_num;
    }
    public String getupdate_time(){
        return update_time;
    }
    public String getontime(){
        return ontime;
    }
    public String getofftime(){
        return offtime;
    }
    public String getreason(){
        return reason;
    }
    public String gettype(){
        return type;
    }

    public void setserial_num(String serial_num){
        this.serial_num = serial_num;
    }
    public void setupdate_time(String update_time){
        this.update_time = update_time;
    }
    public void setontime(String ontime){
        this.ontime = ontime;
    }
    public void setofftime(String offtime){
        this.offtime = offtime;
    }
    public void setreason(String reason){
        this.reason = reason;
    }
    public void settype(String type){
        this.type = type;
    }

}
