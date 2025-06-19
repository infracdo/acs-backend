package com.acs_tr069.test_tr069.Entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "radio_info")
public class RadioInfo {

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

}
