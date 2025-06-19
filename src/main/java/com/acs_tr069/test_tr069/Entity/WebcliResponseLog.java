package com.acs_tr069.test_tr069.Entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "webcli_response_log")
public class WebcliResponseLog { // superior to zeep ver, will retain

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "device_sn")
    private String deviceSn;

    @Column(name = "command_output")
    private byte[] commandOutput;

    @Column(name = "command_used")
    private byte[] commandUsed;

    @Column(name = "time_saved")
    private java.sql.Timestamp timeSaved; // found in hive not in zeep

}
