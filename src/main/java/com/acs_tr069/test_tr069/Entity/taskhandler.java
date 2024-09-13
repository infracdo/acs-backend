package com.acs_tr069.test_tr069.Entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class taskhandler {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)

    private Long id;
    private String serial_num;
    private String method;
    private String parameters;
    private String optional;

    public Long get_Id(){
        return id;
    }
    public String get_SN(){
        return serial_num;
    }
    public String get_method(){
        return method;
    }
    public String get_parameters(){
        return parameters;
    }
    public String get_optional(){
        return optional;
    }

    public void set_SN(String sn){
        this.serial_num = sn;
    }
    public void set_method(String method){
        this.method = method;
    }
    public void set_parameters(String parameters){
        this.parameters = parameters;
    }
    public void set_optional(String optional){
        this.optional = optional;
    }
}
