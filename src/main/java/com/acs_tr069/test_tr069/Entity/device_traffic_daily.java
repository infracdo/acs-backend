package com.acs_tr069.test_tr069.Entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class device_traffic_daily {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)

    private Long id;
    private String serial_num;
    private String date;
    private Integer rx;
    private Integer tx;

    public Long getid(){
        return id;
    }
    public String getserial_num(){
        return serial_num;
    }
    public String getdate(){
        return date;
    }
    public Integer getrx(){
        return rx;
    }
    public Integer gettx(){
        return tx;
    }


    public void setserial_num(String serial_num){
        this.serial_num = serial_num;
    }
    public void setdate(String date){
        this.date = date;
    }
    public void setrx(Integer rx){
        this.rx = rx;
    }
    public void settx(Integer tx){
        this.tx = tx;
    }
}
