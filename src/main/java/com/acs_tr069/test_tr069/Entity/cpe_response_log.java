package com.acs_tr069.test_tr069.Entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class cpe_response_log {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)

    private Long id;
    private String serial_num;
    private String method;
    private String payload;

    public Long get_Id(){
        return id;
    }
    public String get_SN(){
        return serial_num;
    }
    public String get_method(){
        return method;
    }
    public String get_payload(){
        return payload;
    }


    public void set_SN(String sn){
        this.serial_num = sn;
    }
    public void set_Method(String method){
        this.method = method;
    }
    public void set_Payload(String payload){
        this.payload = payload;
    }
}
