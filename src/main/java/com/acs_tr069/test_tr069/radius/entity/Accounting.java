package com.acs_tr069.test_tr069.radius.entity;

import java.time.OffsetDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Data
@Table(name = "accounting")
public class Accounting {

    @Column(name = "username")
    private String username;

    @Column(name = "time_stamp")
    private Long timestamp;

    @Column(name = "acctstatustype")
    private String acctStatusType;

    @Column(name = "acctdelaytime")
    private Long acctDelayTime;

    @Column(name = "acctinputoctets")
    private Long acctInputOctets;

    @Column(name = "acctoutputoctets")
    private Long acctOutputOctets;

    @Column(name = "acctsessionid")
    private String acctSessionId;

    @Column(name = "acctsessiontime")
    private Long acctSessionTime;

    @Column(name = "acctterminatecause")
    private String acctTerminateCause;

    @Column(name = "nasidentifier")
    private String nasIdentifier;

    @Column(name = "nasport")
    private Long nasPort;

    @Column(name = "framedipaddress")
    private String framedIpAddress;

    @Column(name = "auth_mode")
    private String authMode;

    @Column(name = "device")
    private String device;

    @Id
    @Column(name = "mac")
    private String mac;

    @Column(name = "created_at", columnDefinition = "timestamptz")
    private OffsetDateTime createdAt;

}
