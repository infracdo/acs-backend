package com.acs_tr069.test_tr069.Entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class webcli_response_log { // superior to zeep ver, will retain
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)

    private Long id;
    private String device_sn;
    private byte[] CommandOutput;
    private byte[] CommandUsed;
    private java.sql.Timestamp time_saved; // found in hive not in zeep
    
    public Long get_Id(){
        return id;
    }
    public String get_device_sn(){
        return device_sn;
    }
    public byte[] get_CommandOutput(){
        return CommandOutput;
    }
    public byte[] get_CommandUsed(){
        return CommandUsed;
    }
    public java.sql.Timestamp get_time_saved(){ // found in hive not in zeep
        return time_saved;
    }

    public void set_device_sn(String device_sn){
        this.device_sn = device_sn;
    }
    public void set_CommandOutput(byte[] CommandOutput){
        this.CommandOutput = CommandOutput;
    }
    public void set_CommandUsed(byte[] CommandUsed){
        this.CommandUsed = CommandUsed;
    }
    public void set_time_saved(java.sql.Timestamp time_saved){ // found in hive not in zeep
        this.time_saved = time_saved;
    }

}
