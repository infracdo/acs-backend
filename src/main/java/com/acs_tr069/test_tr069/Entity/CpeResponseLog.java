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
@Table(name = "cpe_response_log")
public class CpeResponseLog { // same with zeep ver, will retain
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    @Column(name = "serial_num")
    private String serialNum;

    private String method;
    private String payload;

}
